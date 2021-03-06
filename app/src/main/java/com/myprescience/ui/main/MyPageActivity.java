package com.myprescience.ui.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.myprescience.R;
import com.myprescience.dto.UserData;
import com.myprescience.ui.album.MyAlbumListActivity;
import com.myprescience.ui.artist.MyArtistListActivity;
import com.myprescience.ui.song.MySongListActivity;
import com.myprescience.ui.song.SongListActivity;
import com.myprescience.util.Indicator;

import java.util.ArrayList;

import static com.myprescience.util.Server.SYNC_MODE;

/**
 * Created by dongjun on 15. 4. 6..
 */
public class MyPageActivity extends ActionBarActivity {

    private UserData userDTO;
    private GridView gridView;
    private int userId;
    private boolean mLockListView = false;

    private LinearLayout mMyInfoButton, mMp3SyncButton, mAnalizeButton,
                        mMySongButton, mMyAlbumButton, mMyArtistButton;

    Indicator mIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        setActionBar(R.string.title_activity_MyPage);
        userDTO = new UserData(getApplicationContext());

        mIndicator = new Indicator(this);

//        facebook_profile = (ImageView) findViewById(R.id.profileImageView);
//        facebook_profile.setImageDrawable(getFACEBOOK_PROFILE_BITMAP());

        mMyInfoButton = (LinearLayout) findViewById(R.id.myInfoButton);
        mMyInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MyPageActivity.this);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                });
                alert.setMessage("추후 추가 예정입니다.");
                alert.show();
                return;
            }
        });

        mMp3SyncButton = (LinearLayout) findViewById(R.id.mp3SyncButton);
        mMp3SyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncLocalMusicFile();
            }
        });

        mAnalizeButton = (LinearLayout) findViewById(R.id.analizeSongButton);
        mAnalizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MyPageActivity.this);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                });
                alert.setMessage("추후 추가 예정입니다.");
                alert.show();
                return;
            }
        });

        mMySongButton = (LinearLayout) findViewById(R.id.mySongButton);
        mMySongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, MySongListActivity.class);
                startActivity(intent);
            }
        });

        mMyAlbumButton = (LinearLayout) findViewById(R.id.myAlbumButton);
        mMyAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, MyAlbumListActivity.class);
                startActivity(intent);
            }
        });

        mMyArtistButton = (LinearLayout) findViewById(R.id.myArtistButton);
        mMyArtistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, MyArtistListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setActionBar(int title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        // 뒤로가기 버튼
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView TitleTextView = (TextView) findViewById(R.id.toolbar_title);
        TitleTextView.setText(title);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "Steinerlight.ttf");
        TitleTextView.setTypeface(face);

    }

    private void syncLocalMusicFile() {

        new syncLocalMP3File(MyPageActivity.this).execute(100);

//        ProgressDialog progressDialog = new ProgressDialog(MyPageActivity.this);
//
//        new syncLocalMP3File(progressDialog).execute();

//        AlertDialog.Builder alert = new AlertDialog.Builder(MyPageActivity.this);
//        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {


//                try {
//                    new LocalMusicSyncThread(getApplicationContext(), SERVER_ADDRESS+RATING_API+INSERT_LOCAL_FILE_RATING
//                            +WITH_USER+userDTO.getId()+"&title="+URLEncoder.encode(title,"utf-8")+"&artist="+URLEncoder.encode(artist,"utf-8") ).start();
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }

                //                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        public void run() {
//                            new RecommendThread(getApplicationContext(), SERVER_ADDRESS + RECOMMEND_API + EXEC_RECOMMEND_ALGORITHM + WITH_USER + userDTO.getId()).start();
//                        }
//                    }, 5000);
//                dialog.dismiss();     //닫기
//            }
//        });
//        alert.setMessage("보유하고 계신 음악을 동기화합니다.");
//        alert.show();

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main, menu);
//        return true;
//    }

    // 뒤로가기 버튼
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
//            case R.id.action_reset:
//                // NavUtils.navigateUpFromSameTask(this);
//                new InsertUpdateQuery(getApplicationContext()).execute(SERVER_ADDRESS + USER_API + RESET_DATE + WITH_USER + userDTO.getId());
//                userDTO.setRatingSongCount(0);
//                triger = 15;
//                return true;
        }
        return super.onOptionsItemSelected(item);
    };

    public class syncLocalMP3File extends AsyncTask< Integer//excute()실행시 넘겨줄 데이터타입
            , String//진행정보 데이터 타입 publishProgress(), onProgressUpdate()의 인수
            , Integer//doInBackground() 종료시 리턴될 데이터 타입 onPostExecute()의 인수
            > {
        private ProgressDialog mDlg;
        private Context mContext;
        private Cursor mMusiccursor;
        private int count;

        public syncLocalMP3File(Context context) {
            mContext = context;

            String[] songFile = {
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Video.Media.ARTIST };
            mMusiccursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    songFile, null, null, null);

            count = 0;
        }

        @Override
        protected void onPreExecute() {
            mDlg = new ProgressDialog(mContext);
            mDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDlg.setMessage(getString(R.string.main_mp3_sync_start));
            publishProgress("max", Integer.toString(mMusiccursor.getCount()));
            mDlg.show();
            Toast.makeText(mContext, getString(R.string.main_mp3_sync_explain), Toast.LENGTH_LONG).show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... params) {

            int taskCnt = mMusiccursor.getCount();

            ArrayList<String> titles = new ArrayList<>();
            ArrayList<String> artists = new ArrayList<>();


            boolean delay = false;

            while (!delay && mMusiccursor.moveToNext()) {
                delay = true;
                count++;
                int music_column_index = mMusiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                final String title = mMusiccursor.getString(music_column_index);

                music_column_index = mMusiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                final String artist = mMusiccursor.getString(music_column_index);

                if (artist.equals("<unknown>") || artist.equals("FaceBook") || title.indexOf("Hangouts") != -1) {

                } else {
                    titles.add(title);
                    artists.add(artist);
                }

                publishProgress("progress", Integer.toString(count), title + "\n    " + artist);

                try {
                    Thread.sleep(30);
                    delay = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Intent intent = new Intent(MyPageActivity.this, SongListActivity.class);
            intent.putExtra("syncTitle", titles);
            intent.putExtra("syncArtist", artists);
            intent.putExtra("mode", SYNC_MODE);
            startActivity(intent);

            return taskCnt;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            if (progress[0].equals("progress")) {
                mDlg.setProgress(Integer.parseInt(progress[1]));
                mDlg.setMessage(progress[2]);

                Log.e("background", progress[2]);
            }
            else if (progress[0].equals("max")) {
                mDlg.setMax(Integer.parseInt(progress[1]));
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            mDlg.dismiss();
        }
    }



}
