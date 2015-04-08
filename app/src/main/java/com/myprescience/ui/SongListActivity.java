package com.myprescience.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.myprescience.R;
import com.myprescience.util.Indicator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.myprescience.util.JSON.BBT_WITH_GENRE;
import static com.myprescience.util.JSON.BILLBOARDTOP_API;
import static com.myprescience.util.JSON.SERVER_ADDRESS;
import static com.myprescience.util.JSON.SPOTIFY_API;
import static com.myprescience.util.JSON.USER_API;
import static com.myprescience.util.JSON.USER_ID_WITH_FACEBOOK_ID;
import static com.myprescience.util.JSON.getStringFromUrl;

/**
 * 곡 리스트 출력하는 액티비티
 */


public class SongListActivity extends Activity {
    public static Activity sSonglistActivity;
    // 추천 받을 최소 곡 수
    public static int MIN_SELECTED_SONG = 5;
//    public static String BBT_API = "http://166.104.245.89/MyPrescience/db/BillboardTop.php?query=selectGenreTop&genres=";
//    public static String BBT_API = "http://218.37.215.185/MyPrescience/db/BillboardTop.php?query=selectGenreTop&genres=";
    final private String BBT_API = SERVER_ADDRESS+BILLBOARDTOP_API+BBT_WITH_GENRE;
    private int userId;
    private String genres;
    private int totalListSize;

    private Indicator mIndicator;

    private int selectCount = 0;
    private ImageButton rightButton;
    private TextView textView;
    private ListView songListView;
    private SongListAdapter songListAdapter;
    private ProgressBar progressBar;
    private boolean mLockListView = false;

    private int mListCount;
    private int mListAddCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        Session session = Session.getActiveSession();
        if(session != null){
            if(session.isOpened()){
                Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                    // callback after Graph API response with user object
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        new searchUserTask().execute(SERVER_ADDRESS+USER_API+USER_ID_WITH_FACEBOOK_ID+user.getId());
                    }
                });
            }
        }

        mListCount = 0;
        mListAddCount = 5;
        mIndicator = new Indicator(this);

        rightButton = (ImageButton) findViewById(R.id.nextButton);
        rightButton.setVisibility(ImageButton.INVISIBLE);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sSonglistActivity = SongListActivity.this;
                Intent intent = new Intent(SongListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        textView = (TextView) findViewById(R.id.top);
        textView.setText("최소 "+ MIN_SELECTED_SONG +"곡 이상 평가해주세요.");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        Intent intent = getIntent();
        ArrayList<String> selectGenre = intent.getExtras().getStringArrayList("selectGenre");
        genres = TextUtils.join(",", selectGenre);

        new getSimpleSongTask().execute(BBT_API+genres);

        songListAdapter = new SongListAdapter(SongListActivity.this, selectCount, progressBar, textView, rightButton, userId);
        songListView = (ListView) findViewById(R.id.songListView);
        songListView.setAdapter(songListAdapter);

        // 스크롤 했을 때 마지막 셀이 보인다면 추가로 로딩
        songListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 현재 가장 처음에 보이는 셀번호와 보여지는 셀번호를 더한값이
                // 전체의 숫자와 동일해지면 가장 아래로 스크롤 되었다고 가정
                if ( (totalItemCount+mListAddCount < totalListSize) && ((firstVisibleItem + visibleItemCount) == totalItemCount) && (mLockListView == false) ) {
                    mListCount += mListAddCount;
//                        else if(totalItemCount+10 > totalListSize && !(totalItemCount >= totalListSize))
//                            mListCount = totalListSize - (10+1);
                    new getSimpleSongTask().execute(BBT_API + genres);
                    mLockListView = true;
                } else if(totalItemCount+mListAddCount > totalListSize && totalListSize != 0) {
                    mListAddCount =  totalListSize - (mListCount + 1);
                    songListView.setOnScrollListener(null);
                }
            }
        });
    }

    class searchUserTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... url) {
            String userIdJSON = getStringFromUrl(url[0]);
            JSONParser jsonParser = new JSONParser();
            JSONArray users = null;
            try {
                users = (JSONArray) jsonParser.parse(userIdJSON);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(users != null) {
                JSONObject user = (JSONObject) users.get(0);
                userId = Integer.parseInt((String)user.get("user_id"));
                Log.e("userId", userId+"");

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    class getSimpleSongTask extends AsyncTask<String, String, String> {

        public getSimpleSongTask(){
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String songJSON) {
            super.onPostExecute(songJSON);
//            mLockListView = true;

            try {
                JSONParser jsonParser = new JSONParser();
                JSONArray songArray = (JSONArray) jsonParser.parse(songJSON);
                totalListSize = songArray.size();

                // mListCount는 추가 로드할 때 마다 10씩 증가
                Log.e("mListCount", mListCount+"");
                Log.e("mListAddCount", mListAddCount+"");
                for(int i = mListCount; i < mListCount+mListAddCount; i ++) {

                    JSONObject song = (JSONObject) jsonParser.parse(songArray.get(i).toString());

                    String id = (String)song.get("id");
                    String title = (String)song.get("title");
                    String artist = (String)song.get("artist");
                    String spotifyAlbumID = "albums/"+(String)song.get("album_spotify_id");

                    songListAdapter.addItem(id, spotifyAlbumID, title, artist, 0);

                    if(songListAdapter.getCount() > 4) {
                        songListAdapter.notifyDataSetChanged();

                        if ( mIndicator.isShowing())
                            mIndicator.hide();
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            mLockListView = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                if ( !mIndicator.isShowing())
                    mIndicator.show();
        }
    }

}
