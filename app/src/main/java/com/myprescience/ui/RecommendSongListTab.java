package com.myprescience.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import static com.myprescience.util.Server.MODE;
import static com.myprescience.util.Server.RECOMMEND_API;
import static com.myprescience.util.Server.RECOMMEND_SONGS;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.WITH_USER;
import static com.myprescience.util.Server.getStringFromUrl;

import com.myprescience.R;
import com.myprescience.dto.UserData;
import com.myprescience.util.Indicator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@SuppressLint("ValidFragment")
public class RecommendSongListTab extends Fragment {

    private UserData userDTO = new UserData();

    Context mContext;

    private ListView mRecommendListView;
    private RecommendSongListAdapter mRecommendSongListAdapter;

    private Indicator mIndicator;

    private int totalListSize;
    private int mListCount;
    private int mListAddCount;
    private JSONArray mSongArray;
    private boolean mLockListView = false;

    public RecommendSongListTab(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_activity_recommend, null);

        mListCount = 0;
        mListAddCount = 5;

        mIndicator = new Indicator(mContext, view);

        mRecommendListView = (ListView) view.findViewById(R.id.recommendSongListView);
        mRecommendSongListAdapter = new RecommendSongListAdapter(mContext, userDTO.getId());
        mRecommendListView.setAdapter(mRecommendSongListAdapter);
        new getRecommendSongTask().execute(SERVER_ADDRESS+RECOMMEND_API+RECOMMEND_SONGS+WITH_USER+userDTO.getId());

        // 스크롤 했을 때 마지막 셀이 보인다면 추가로 로딩
        mRecommendListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 현재 가장 처음에 보이는 셀번호와 보여지는 셀번호를 더한값이
                // 전체의 숫자와 동일해지면 가장 아래로 스크롤 되었다고 가정
                if ( (totalItemCount+mListAddCount < totalListSize) && ((firstVisibleItem + visibleItemCount) == totalItemCount)
                        && (mLockListView == false) && (totalItemCount > 0) ) {
                    mListCount += mListAddCount;
//                        else if(totalItemCount+10 > totalListSize && !(totalItemCount >= totalListSize))
//                            mListCount = totalListSize - (10+1);
                    new getRecommendSongTask().execute(SERVER_ADDRESS+RECOMMEND_API+RECOMMEND_SONGS+WITH_USER+userDTO.getId());
                    mLockListView = true;
                } else if(totalItemCount+mListAddCount > totalListSize && totalListSize != 0) {
                    mListCount += mListAddCount;
                    mListAddCount =  totalListSize - mListCount;
                    new getRecommendSongTask().execute(SERVER_ADDRESS+RECOMMEND_API+RECOMMEND_SONGS+WITH_USER+userDTO.getId());
                    mRecommendListView.setOnScrollListener(null);
                }
            }
        });



//        mRecommendSongListAdapter.addItem("test", "albums/6gSCxFtdSR8Ig3VvntCBPE", "test", "test", 10);
//        mRecommendSongListAdapter.addItem("test", "albums/6gSCxFtdSR8Ig3VvntCBPE", "test", "test", 10);
//        mRecommendSongListAdapter.addItem("test", "albums/6gSCxFtdSR8Ig3VvntCBPE", "test", "test", 10);

        return view;
    }

    class getRecommendSongTask extends AsyncTask<String, String, String> {

        public getRecommendSongTask() {
        }

        @Override
        protected String doInBackground(String... url) {
            if(mSongArray == null)
                return getStringFromUrl(url[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String songJSON) {
            super.onPostExecute(songJSON);

            try {
                JSONParser jsonParser = new JSONParser();
                if(songJSON != null) {
                    mSongArray = (JSONArray) jsonParser.parse(songJSON);
                    totalListSize = mSongArray.size();
                }

                // mListCount는 추가 로드할 때 마다 10씩 증가
                for (int i = mListCount; i < mListCount + mListAddCount; i++) {
                    JSONObject song = (JSONObject) jsonParser.parse(mSongArray.get(i).toString());

                    String id = (String) song.get("id");
                    String title = (String) song.get("title");
                    String artist = (String) song.get("artist");
                    String spotifyAlbumID = "albums/" + (String) song.get("album_spotify_id");
                    String genres = (String) song.get("genres");
                    String song_type = (String) song.get("song_type");
                    String ratingStr = (String) song.get("rating");
                    int rating = 0;
                    if (ratingStr != null) {
                        rating = Integer.parseInt(ratingStr);
                    }

                    float valence = Float.parseFloat((String) song.get("valence"));
                    float danceability = Float.parseFloat((String) song.get("danceability"));
                    float energy = Float.parseFloat((String) song.get("energy"));
                    float liveness = Float.parseFloat((String) song.get("liveness"));
                    float speechiness = Float.parseFloat((String) song.get("speechiness"));
                    float acousticness = Float.parseFloat((String) song.get("acousticness"));
                    float instrumentalness = Float.parseFloat((String) song.get("instrumentalness"));

                    mRecommendSongListAdapter.addItem(id, spotifyAlbumID, title, artist, rating, genres, song_type,
                            valence, danceability, energy, liveness, speechiness, acousticness, instrumentalness);
                }
                if (mRecommendSongListAdapter.getCount() > 4) {
                    mRecommendSongListAdapter.notifyDataSetChanged();
                }
                if (mIndicator.isShowing())
                    mIndicator.hide();

                mLockListView = false;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if ( !mIndicator.isShowing())
                mIndicator.show();
        }
    }
}