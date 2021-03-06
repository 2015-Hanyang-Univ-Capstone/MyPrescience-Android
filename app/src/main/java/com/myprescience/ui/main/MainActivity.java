package com.myprescience.ui.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.meetme.android.horizontallistview.HorizontalListView;
import com.myprescience.R;
import com.myprescience.dto.UserData;
import com.myprescience.search.SearchListActivity;
import com.myprescience.ui.album.AlbumListAdapter;
import com.myprescience.ui.album.LatestAlbumListActivity;
import com.myprescience.ui.mix_play.MixThemeActivity;
import com.myprescience.ui.song.MyPTopSongListActivity;
import com.myprescience.ui.song.SongActivity;
import com.myprescience.ui.song.SongListActivity;
import com.myprescience.util.BackPressCloseHandler;
import com.myprescience.util.ImageLoad;
import com.myprescience.util.Indicator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

import static com.myprescience.util.PixelUtil.getProperImage;
import static com.myprescience.util.Server.ALBUM_API;
import static com.myprescience.util.Server.FIRST_MODE;
import static com.myprescience.util.Server.MYP_HOT_SONGS;
import static com.myprescience.util.Server.SELECT_MAIN_LATEST_ALBUMS;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.SONG_API;
import static com.myprescience.util.Server.SPOTIFY_API;
import static com.myprescience.util.Server.SYNC_MODE;
import static com.myprescience.util.Server.TODAY_SONG_MODE;
import static com.myprescience.util.Server.getStringFromUrl;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private BackPressCloseHandler backPressCloseHandler;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private UserData userDTO;

    private Activity mActivity;
    private Indicator mIndicater;
    private ViewGroup mMyPTop1_FrameLayout, mMyPTop_LinearLayout1, mMyPTop_LinearLayout2, mMyPTop_LinearLayout3;
    private ArrayList<ViewGroup> mMyTopList;

    private CardView mFeatureDetailLayout;
    private LinearLayout mMypTop100Button, mMyPrescienceButton, mLatestAlbumButton, mMixPlayButton;
    private Button mMypTopMoreButton, mMoreLatestAlbumButton, mFeatureDetailButton;

    private HorizontalListView mHorizontalListView;
    private AlbumListAdapter mHorizontalListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userDTO = new UserData(getApplicationContext());

        errorHandle();

        if(userDTO.getRatingSongCount() == 0)
            DialogInitChoice();

        mIndicater = new Indicator(this);
        backPressCloseHandler = new BackPressCloseHandler(this);
        mActivity = this;

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mMyPTop1_FrameLayout = (ViewGroup) findViewById(R.id.MyPHot_FrameLayout1);
        mMyPTop_LinearLayout1 = (ViewGroup) findViewById(R.id.MyPTop_LinearLayout1);
        mMyPTop_LinearLayout2 = (ViewGroup) findViewById(R.id.MyPTop_LinearLayout2);
        mMyPTop_LinearLayout3 = (ViewGroup) findViewById(R.id.MyPTop_LinearLayout3);

        mMyTopList = new ArrayList<ViewGroup>();
        mMyTopList.add(mMyPTop_LinearLayout1);
        mMyTopList.add(mMyPTop_LinearLayout2);
        mMyTopList.add(mMyPTop_LinearLayout3);

        mFeatureDetailLayout = (CardView) findViewById(R.id.featureDetailLayout);
        mFeatureDetailButton = (Button) findViewById(R.id.featureDetailButton);
        mFeatureDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFadeOut(mFeatureDetailButton);
                viewFadeIn(mFeatureDetailLayout);
            }
        });

        mMypTop100Button = (LinearLayout) findViewById(R.id.mypTop100Button);
        mMypTop100Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyPTopSongListActivity.class);
                startActivity(intent);
            }
        });

        mMypTopMoreButton = (Button) findViewById(R.id.mypTopMoreButton);
        mMypTopMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyPTopSongListActivity.class);
                startActivity(intent);
            }
        });

        mMyPrescienceButton = (LinearLayout) findViewById(R.id.myPrescienceButton);
        mMyPrescienceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyPrescienceActivity.class);
                startActivity(intent);
            }
        });

        mLatestAlbumButton = (LinearLayout) findViewById(R.id.latestAlbumButton);
        mLatestAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LatestAlbumListActivity.class);
                startActivity(intent);
            }
        });

        mMoreLatestAlbumButton = (Button) findViewById(R.id.moreLatestAlbumButton);
        mMoreLatestAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LatestAlbumListActivity.class);
                startActivity(intent);
            }
        });

        mMixPlayButton = (LinearLayout) findViewById(R.id.mixPlayButton);
        mMixPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MixThemeActivity.class);
                startActivity(intent);
            }
        });

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mHorizontalListView = (HorizontalListView) findViewById(R.id.HorizontalListView);
        mHorizontalListAdapter = new AlbumListAdapter(getApplicationContext(), userDTO.getId());
        mHorizontalListView.setAdapter(mHorizontalListAdapter);

        initSetting();
        
    }

    public void errorHandle() {
        if(userDTO.getId() == 0) {
            Toast.makeText(this, getString(R.string.main_error), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void initSetting() {
        new getMyPHotSongs().execute(SERVER_ADDRESS + SONG_API + MYP_HOT_SONGS);
        new getLatestAlbums().execute(SERVER_ADDRESS+ALBUM_API+SELECT_MAIN_LATEST_ALBUMS);
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                Intent section2 = new Intent(this, MyPrescienceActivity.class);
                startActivity(section2);
                break;
            case 3:
                Intent section3 = new Intent(this, MixThemeActivity.class);
                startActivity(section3);
                break;
            case 4:
                Intent section4 = new Intent(this, SongListActivity.class);
                section4.putExtra("mode", TODAY_SONG_MODE);
                startActivity(section4);
                break;
            case 5:
                Intent section5 = new Intent(this, MyPageActivity.class);
                startActivity(section5);
                break;
            case 6:
                Uri uri = Uri.parse("mailto:humanbrain.info@gmail.com");
                Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(it);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setTitle(mTitle);
        actionBar.setTitle("");
        actionBar.show();

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        actionBar.setCustomView(R.layout.actionbar_title);
//        mTitleTextView = (TextView) findViewById(R.id.customActionbarTitle);
//        mTitleTextView.setTypeface(Typeface.createFromAsset(getAssets(), "NEOTERIC.ttf"));
//        mTitleTextView.setText(mTitle);
//        actionBar.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_search, menu);
            restoreActionBar();
            return true;
        } else {
            getSupportActionBar().hide();
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchListActivity.class);
            startActivity(intent);
            return true;
        }
//        else if (id == R.id.action_bell) {
//            Log.e("Bell", "아티스트에 대한 소식을 알림.");
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_activity, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    public void setMypTop1View(ViewGroup framelayout, int rank, float avg, int count, String title, String artist) {

        ViewGroup linearLayout = (ViewGroup) framelayout.getChildAt(2);
        ViewGroup inLinearLayout = (ViewGroup) linearLayout.getChildAt(0);
        RatingBar avgRatingBar = (RatingBar) inLinearLayout.getChildAt(0);
        TextView avgTextView = (TextView) inLinearLayout.getChildAt(1);
        ViewGroup inLinearLayout2 = (ViewGroup) linearLayout.getChildAt(1);
        TextView countTextView = (TextView) inLinearLayout2.getChildAt(0);
        TextView titleTextView = (TextView) linearLayout.getChildAt(2);
        TextView artistTextView = (TextView) linearLayout.getChildAt(3);

        LayerDrawable stars = (LayerDrawable) avgRatingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#FFD700"), PorterDuff.Mode.SRC_ATOP);

        int avg_rating = Math.round(avg);

        avgRatingBar.setProgress(avg_rating);
        avgTextView.setText(String.format("(%.1f)", avg / 2));
        countTextView.setText(count+" ");
        titleTextView.setText(title);
        artistTextView.setText(artist);
    }

    public ImageView getMypTop1ImageView(ViewGroup framelayout) {
        return (ImageView) framelayout.getChildAt(0);
    }

    public void setMypTopListView(ViewGroup linearLayout, float avg, int count, String title, String artist) {

        ViewGroup inLinearLayout = (ViewGroup) linearLayout.getChildAt(1);
        TextView titleTextView = (TextView) inLinearLayout.getChildAt(0);
        TextView artistTextView = (TextView) inLinearLayout.getChildAt(1);

        ViewGroup ininLinearLayout = (ViewGroup) inLinearLayout.getChildAt(2);
        TextView avgRatingTextView = (TextView) ininLinearLayout.getChildAt(1);

        ViewGroup ininLinearLayout2 = (ViewGroup) inLinearLayout.getChildAt(3);
        TextView countTextView = (TextView) ininLinearLayout2.getChildAt(0);

        titleTextView.setText(title);
        artistTextView.setText(artist);

        avgRatingTextView.setText(String.format("%.1f", avg/2.0));
        countTextView.setText(count + " ");
    }

    public ImageView getMypTopListImageView(ViewGroup linearLayout) {
        return (ImageView) linearLayout.getChildAt(0);
    }

    class getMyPHotSongs extends AsyncTask<String, String, String> {

        public getMyPHotSongs(){
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String myphotJSON) {
            super.onPostExecute(myphotJSON);

            JSONParser jsonParser = new JSONParser();
            JSONArray hots = null;
            try {
                hots = (JSONArray) jsonParser.parse(myphotJSON);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            for(int i = 0; i < 4; i++) {

                JSONObject hot = (JSONObject) hots.get(i);
                final String id = (String) hot.get("id");
                String artist_spotify_id = (String) hot.get("artist_spotify_id");
                String title = (String) hot.get("title");
                String artist = (String) hot.get("artist");
                float avg = Float.parseFloat((String) hot.get("avg"));
                int rating_count = Integer.parseInt((String) hot.get("rating_count"));

                if(i == 0) {
                    setMypTop1View(mMyPTop1_FrameLayout, 1, avg, rating_count, title, artist);

                    ImageView albumArt = getMypTop1ImageView(mMyPTop1_FrameLayout);
                    albumArt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), SongActivity.class);
                            intent.putExtra("song_id", id);
                            startActivity(intent);
                        }
                    });

                    if (!artist_spotify_id.equals(""))
                        new getSpotifyArtistImage(mMyPTop1_FrameLayout).execute(SPOTIFY_API + "artists/" + artist_spotify_id);
                } else {
                    setMypTopListView(mMyTopList.get(i - 1), avg, rating_count, title, artist);

                    ImageView albumArt = getMypTopListImageView(mMyTopList.get(i - 1));
                    albumArt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), SongActivity.class);
                            intent.putExtra("song_id", id);
                            startActivity(intent);
                        }
                    });
                    if (!artist_spotify_id.equals(""))
                    new getSpotifyArtistImage(mMyTopList.get(i-1)).execute(SPOTIFY_API + "artists/" + artist_spotify_id);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            if(!mIndicater.isShowing())
                mIndicater.show();
        }
    }

    public class getSpotifyArtistImage extends AsyncTask<String, String, String> {

        private ViewGroup myPHot_FrameLayout;

        public getSpotifyArtistImage(ViewGroup _myPHot_FrameLayout) {
            this.myPHot_FrameLayout = _myPHot_FrameLayout;
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String artistJSON) {
            super.onPostExecute(artistJSON);

            JSONParser jsonParser = new JSONParser();
            JSONObject artist = null;
            try {
                artist = (JSONObject) jsonParser.parse(artistJSON);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            JSONArray images = (JSONArray) artist.get("images");
            if(images.size() != 0) {
                JSONObject image = getProperImage(images, getMypTop1ImageView(myPHot_FrameLayout).getWidth());
                String url = (String) image.get("url");
                new ImageLoad(mActivity, mIndicater, url, getMypTop1ImageView(myPHot_FrameLayout)).execute();
            } else {
                getMypTop1ImageView(myPHot_FrameLayout).setImageResource(R.drawable.not_exist);
            }
        }
    }

    class getLatestAlbums extends AsyncTask<String, String, String> {

        public getLatestAlbums(){
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String albumJSON) {
            super.onPostExecute(albumJSON);

            JSONParser jsonParser = new JSONParser();
            JSONArray albums = null;
            try {
                albums = (JSONArray) jsonParser.parse(albumJSON);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            for(int i = 0; i < albums.size(); i++) {
                JSONObject album = (JSONObject) albums.get(i);
                final String id = (String) album.get("id");
                String name = (String) album.get("name");
                String artist = (String) album.get("artist");
                String release_date = (String) album.get("release_date");
                String image_300 = (String) album.get("image_300");

                mHorizontalListAdapter.addItem(id, name, artist, release_date, image_300);
            }
            mHorizontalListAdapter.notifyDataSetChanged();

        }

        @Override
        protected void onPreExecute() {
            if(!mIndicater.isShowing())
                mIndicater.show();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed(0);

    }

    private void viewFadeIn(View layout) {
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.abc_fade_in);
        layout.startAnimation(animation);
        layout.setVisibility(View.VISIBLE);
    }

    private void viewFadeOut(View layout) {
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.abc_fade_out);
        layout.startAnimation(animation);
        layout.setVisibility(View.GONE);
    }

    private void DialogInitChoice(){
        final CharSequence[] mainMenu = {getString(R.string.main_init_menu1), getString(R.string.main_init_menu2)};
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(MainActivity.this);
        alt_bld.setTitle(getString(R.string.main_init_message));
//        alt_bld.setMessage(getString(R.string.main_init_message));
        alt_bld.setItems(mainMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    new syncLocalMP3File(MainActivity.this).execute(100);
                } else if (item == 1) {
                    Intent intent = new Intent(MainActivity.this, SelectGenreActivity2.class);
                    intent.putExtra("mode", FIRST_MODE);
                    startActivity(intent);
                }
            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

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
                    MediaStore.Video.Media.ARTIST};
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

            Intent intent = new Intent(MainActivity.this, SongListActivity.class);
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
            } else if (progress[0].equals("max")) {
                mDlg.setMax(Integer.parseInt(progress[1]));
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            mDlg.dismiss();
        }
    }

}
