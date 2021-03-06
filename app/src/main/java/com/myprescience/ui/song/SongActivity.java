package com.myprescience.ui.song;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.myprescience.R;
import com.myprescience.dto.UserData;
import com.myprescience.ui.album.AlbumActivity;
import com.myprescience.ui.artist.ArtistActivity;
import com.myprescience.util.Indicator;
import com.myprescience.util.InsertUpdateQuery;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.myprescience.util.Server.INSERT_RATING;
import static com.myprescience.util.Server.RATING_API;
import static com.myprescience.util.Server.SELECT_SONG_AVG_RATING;
import static com.myprescience.util.Server.SELECT_SONG_RATING;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.SONG_API;
import static com.myprescience.util.Server.SONG_WITH_ID;
import static com.myprescience.util.Server.SPOTIFY_API;
import static com.myprescience.util.Server.VIDEO_MOST_VIEW;
import static com.myprescience.util.Server.WITH_USER;
import static com.myprescience.util.Server.YOUTUBE_API;
import static com.myprescience.util.Server.YOUTUBE_API_KEY;
import static com.myprescience.util.Server.YOUTUBE_DEVELOPMENT_KEY;
import static com.myprescience.util.Server.YOUTUBE_RESULT_FIVE;
import static com.myprescience.util.Server.getStringFromUrl;


public class SongActivity extends YouTubeBaseActivity {

    private UserData userDTO;

    String SONG_URL = SERVER_ADDRESS+SONG_API+SONG_WITH_ID;
    String SONG_ID;

    Indicator mIndicator;

    private Activity mActivity = this;

    private ScrollView scrollView;
    private ImageView albumArtView;
    private TextView titleTextView, artistTextView, genreTextView, ratingTextView, ratindCountTextView,
                        songModeTextView, songKeyTextView, tempoTextView, timeSignatureTextView, durationTextView,
                        albumNameTextView, trackNumTextView, userRatingTextView;
    private TextView valanceTextview, danceablilityTextview, energyTextview,
                        livenessTextview, speechinessTextview, acousticnessTextview, instrumentalnessTextview;
    private TextView songType1Textview, songType2Textview, songType3Textview, songType4Textview, previewTextview,
                        mPlaylistNo1, mPlaylistNo2, mPlaylistNo3, mPlaylistNo4, mPlaylistNo5;
    private RatingBar mSongActivityRatingBar;
    private ProgressBar popularityProgressBar;
    private LinearLayout mArtistButton, mAlbumButton, mEvaluationButton, mPreviewClick;
    private ImageButton previewButton;
    private MediaPlayer mPlayer;
    private YouTubePlayerView youTubeView;
    private RelativeLayout mRatingRelativeLayout;
    private Button mCloseButton, mPlayAllButton;

    private String[] modes = {"Minor", "Major"};
    private String[] keys = {"C", "C#", "D", "E♭", "E", "F", "F#", "G", "A♭", "A", "B♭", "B"};
    private ArrayList<TextView> mPlaylist = new ArrayList<TextView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        userDTO = new UserData(getApplicationContext());

        Intent intent = getIntent();
        SONG_ID = intent.getExtras().getString("song_id");

        mIndicator = new Indicator(this);

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.scrollTo(0, 0);

        albumArtView = (ImageView) findViewById(R.id.albumArtView);

        userRatingTextView = (TextView) findViewById(R.id.userRatingTextView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        artistTextView = (TextView) findViewById(R.id.artistTextView);
        genreTextView = (TextView) findViewById(R.id.genreTextView);
        ratingTextView = (TextView) findViewById(R.id.ratingTextView);

        mSongActivityRatingBar = (RatingBar) findViewById(R.id.songActivityRatingBar);
        LayerDrawable stars = (LayerDrawable) mSongActivityRatingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.color_base_theme), PorterDuff.Mode.SRC_ATOP);

        ratindCountTextView = (TextView) findViewById(R.id.ratingCountTextView);
        albumNameTextView = (TextView) findViewById(R.id.albumNameTextView);
        trackNumTextView = (TextView) findViewById(R.id.trackNumTextView);
        popularityProgressBar = (ProgressBar) findViewById(R.id.popularityProgressBar);

        mArtistButton = (LinearLayout) findViewById(R.id.artistButton);
        mAlbumButton = (LinearLayout) findViewById(R.id.albumButton);
        mEvaluationButton = (LinearLayout) findViewById(R.id.evaluationButton);

        songModeTextView = (TextView) findViewById(R.id.songModeTextView);
        songKeyTextView = (TextView) findViewById(R.id.songKeyTextView);
        tempoTextView = (TextView) findViewById(R.id.tempoTextView);
        timeSignatureTextView = (TextView) findViewById(R.id.timeSignatureTextView);
        durationTextView = (TextView) findViewById(R.id.durationTextView);

        valanceTextview= (TextView) findViewById(R.id.valanceTextview);
        danceablilityTextview = (TextView) findViewById(R.id.danceablilityTextview);
        energyTextview = (TextView) findViewById(R.id.energyTextview);
        livenessTextview = (TextView) findViewById(R.id.livenessTextview);
        speechinessTextview = (TextView) findViewById(R.id.speechinessTextview);
        acousticnessTextview = (TextView) findViewById(R.id.acousticnessTextview);
        instrumentalnessTextview = (TextView) findViewById(R.id.instrumentalnessTextview);

        songType1Textview = (TextView) findViewById(R.id.songType1Textview);
        songType2Textview = (TextView) findViewById(R.id.songType2Textview);
        songType3Textview = (TextView) findViewById(R.id.songType3Textview);
        songType4Textview = (TextView) findViewById(R.id.songType4Textview);
        previewTextview = (TextView) findViewById(R.id.previeTextview);

        mPlaylistNo1 = (TextView) findViewById(R.id.playlistTrack1);
        mPlaylistNo2 = (TextView) findViewById(R.id.playlistTrack2);
        mPlaylistNo3 = (TextView) findViewById(R.id.playlistTrack3);
        mPlaylistNo4 = (TextView) findViewById(R.id.playlistTrack4);
        mPlaylistNo5 = (TextView) findViewById(R.id.playlistTrack5);

        mPlaylist.add(mPlaylistNo1);
        mPlaylist.add(mPlaylistNo2);
        mPlaylist.add(mPlaylistNo3);
        mPlaylist.add(mPlaylistNo4);
        mPlaylist.add(mPlaylistNo5);

        mPreviewClick = (LinearLayout) findViewById(R.id.previewClick);
        previewButton = (ImageButton) findViewById(R.id.previewButton);

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtubeView);

        mRatingRelativeLayout = (RelativeLayout) findViewById(R.id.ratingRelativeLayout);
        mCloseButton = (Button) findViewById(R.id.closeButton);
        mPlayAllButton = (Button) findViewById(R.id.playAllButton);

        new getSongTask().execute(SONG_URL+SONG_ID);
    }

    // 초 -> 00 : 00
    public String convertMS(int duration) {
        int M = duration / 60;
        int S = duration % 60;
        return String.format("%02d", M) + " : " + String.format("%02d", S);
    }

    class getRatingTask extends AsyncTask<String, String, String> {

        public getRatingTask(){
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String resultJSON) {
            super.onPostExecute(resultJSON);

            JSONParser jsonParser = new JSONParser();
            JSONArray response = null;
            try {
                response = (JSONArray) jsonParser.parse(resultJSON);
                if(response.size() != 0) {
                    JSONObject rating = (JSONObject) response.get(0);
                    if(rating.get("rating") != null) {
                        int user_rating = Integer.parseInt((String) rating.get("rating"));
                        if(user_rating > 10)
                            userRatingTextView.setText("보유곡");
                        else
                            userRatingTextView.setText(String.format("%.1f", user_rating / 2.0));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }

    class getAvgRatingTask extends AsyncTask<String, String, String> {

        public getAvgRatingTask(){
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String resultJSON) {
            super.onPostExecute(resultJSON);

            JSONParser jsonParser = new JSONParser();
            JSONArray response = null;
            try {
                response = (JSONArray) jsonParser.parse(resultJSON);
                if(response.size() != 0) {
                    JSONObject rating = (JSONObject) response.get(0);
                    if(rating.get("avg") != null) {
                        float avg = Float.parseFloat((String) rating.get("avg"));
                        ratingTextView.setText(String.format("%.1f", avg / 2.0));

                        int rating_count = Integer.parseInt((String) rating.get("rating_count"));
                        ratindCountTextView.setText(rating_count + " " + getString(R.string.main_rating_count));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }

    /* Album JSON
       Parameter - String(url)
       Callback - genres, images */
    class getAlbumTask extends AsyncTask<String, String, String> {

        public getAlbumTask(){
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String spotifyAlbumJSON) {
            super.onPostExecute(spotifyAlbumJSON);

            JSONParser jsonParser = new JSONParser();
            JSONObject album = null;
            try {
                album = (JSONObject) jsonParser.parse(spotifyAlbumJSON);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            JSONArray genres = (JSONArray) album.get("genres");

            String genre = "";
            if(genres.size() != 0) {
                for(int i = 0; i < genres.size()-1; i++) {
                    genre += genres.get(i) + ", ";
                }
                genre += genres.get(genres.size()-1);
            }
            genreTextView.setText(genre);

            JSONArray images = (JSONArray) album.get("images");
            JSONObject image = (JSONObject) images.get(1);
            // Image 역시 UI Thread에서 바로 작업 불가.
            new ImageLoadTask((String)image.get("url"), albumArtView).execute();
            albumNameTextView.setText((String)album.get("name"));
        }
    }

    /* Track JSON
       Parameter - String(url)
       Callback - popularity, track_number, preview */
    class getTrackTask extends AsyncTask<String, String, String> {

        public getTrackTask(){
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String spotifyTrackJSON) {
            super.onPostExecute(spotifyTrackJSON);

            JSONParser jsonParser = new JSONParser();
            JSONObject track = null;
            try {
                track = (JSONObject) jsonParser.parse(spotifyTrackJSON);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long popularity = (long) track.get("popularity");
            popularityProgressBar.setProgress((int) popularity);

            trackNumTextView.setText("Track." + track.get("track_number"));
            mPlayer = new MediaPlayer();

            String preview = (String) track.get("preview_url");
            if(preview != null) {
                new setSourceTask(mPlayer).execute(preview);
            } else {
                previewButton.setImageResource(R.drawable.icon_x_mark);
                previewTextview.setText("Preview ");
                mPreviewClick.setVisibility(View.VISIBLE);
            }

        }

    }

    /* set Preview Source
       Parameter - String(url)  */
    class setSourceTask extends AsyncTask<String, String, Void> {

        private MediaPlayer mPlayer;

        public setSourceTask(MediaPlayer mPlayer){
            this.mPlayer = mPlayer;
        }

        @Override
        protected Void doInBackground(String... url) {
            try {
                mPlayer.setDataSource(url[0]);
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    previewTextview.setText("Preview ");
                    previewButton.setImageResource(R.drawable.icon_play);

                    mPreviewClick.setVisibility(View.VISIBLE);
                    mPreviewClick.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!mPlayer.isPlaying()) {
                                mPlayer.start();
                                previewButton.setImageResource(R.drawable.icon_pause);
                            } else {
                                mPlayer.pause();
                                previewButton.setImageResource(R.drawable.icon_play);
                            }
                        }
                    });
                }
            });

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    previewButton.setImageResource(R.drawable.icon_play);
                }
            });
        }
    }

    /* Song JSON
       Parameter - String(url)
       Callback - song detail,
                  spotifyTrackID, spotifyArtistID, spotifyAlbumID (Async Task) */
    class getSongTask extends AsyncTask<String, String, String> {

        public getSongTask(){
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String songJSON) {
            super.onPostExecute(songJSON);

            try {
                JSONParser jsonParser = new JSONParser();
                JSONArray songArray = (JSONArray) jsonParser.parse(songJSON);
                JSONObject song = (JSONObject) jsonParser.parse(songArray.get(0).toString());

                String title = (String)song.get("title");
                String artist = (String)song.get("artist");
                String songType = (String)song.get("song_type");

                double tempo = Math.round(Double.parseDouble((String) song.get("tempo")) * 100) / 100.0;
                int timeSignature = Integer.parseInt((String)song.get("time_signature"));
                double duration = Math.round(Double.parseDouble((String)song.get("duration"))*100) / 100.0;
                int songMode = Integer.parseInt((String)song.get("song_mode"));
                int songKey = Integer.parseInt((String) song.get("song_key"));

                float valence = (float)0.5;
                if((String) song.get("valence") != null)
                    valence = Float.parseFloat((String) song.get("valence"));


                float danceability = (float)0.5;
                if((String) song.get("danceability") != null)
                    danceability = Float.parseFloat((String) song.get("danceability"));

                float energy = (float)0.5;
                if((String) song.get("energy") != null)
                    energy = Float.parseFloat((String) song.get("energy"));

                float liveness = (float)0.5;
                if((String) song.get("liveness") != null)
                    liveness = Float.parseFloat((String) song.get("liveness"));

                float speechiness = (float)0.5;
                if((String) song.get("speechiness") != null)
                    speechiness = Float.parseFloat((String) song.get("speechiness"));

                float acousticness = (float)0.5;
                if((String) song.get("acousticness") != null)
                    acousticness = Float.parseFloat((String) song.get("acousticness"));

                float instrumentalness = (float)0.5;
                if((String) song.get("instrumentalness") != null)
                    instrumentalness = Float.parseFloat((String) song.get("instrumentalness"));

                titleTextView.setText(title);
                artistTextView.setText(artist);
//                songTypeTextView.setText(songType);
                String[] songTypes = songType.split(",");
                for(int i = 0; i < songTypes.length; i++) {
                    if(songTypes[i].equals("studio"))
                        setActivePropertyView(songType1Textview, 3);
                    if(songTypes[i].equals("christmas"))
                        setActivePropertyView(songType2Textview, 3);
                    if(songTypes[i].equals("electric"))
                        setActivePropertyView(songType3Textview, 3);
                    if(songTypes[i].equals("vocal"))
                        setActivePropertyView(songType4Textview, 3);
                }

                tempoTextView.setText(Double.toString(tempo) + " bpm");
                timeSignatureTextView.setText(timeSignature + "");
                durationTextView.setText(convertMS((int)duration));
                songModeTextView.setText(modes[songMode]);
                songKeyTextView.setText(keys[songKey]);

                setSongPropertyText(valanceTextview, valence);
                setSongPropertyText(danceablilityTextview, danceability);
                setSongPropertyText(energyTextview, energy);
                setSongPropertyText(livenessTextview, liveness);
                setSongPropertyText(speechinessTextview, speechiness);
                setSongPropertyText(acousticnessTextview, acousticness);
                setSongPropertyText(instrumentalnessTextview, instrumentalness);

                String spotifyTrackID = "tracks/"+(String)song.get("track_spotify_id");
                if(spotifyTrackID.equals("tracks/")) {
                    trackNumTextView.setText(getString(R.string.error_not_found));
                    popularityProgressBar.setProgress(0);
                    previewButton.setImageResource(R.drawable.icon_x_mark);
                    previewTextview.setText("Preview ");
                    mPreviewClick.setVisibility(View.VISIBLE);
                } else {
                    new getTrackTask().execute(SPOTIFY_API+spotifyTrackID);
                }

                final String spotifyArtistID = "artists/"+(String)song.get("artist_spotify_id");
//                String spotifyArtistJSON = new getArtistTask().execute(spotifyAPI+spotifyArtistID).get();

                mArtistButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(spotifyArtistID.equals("artists/")) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(SongActivity.this);
                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });
                            alert.setMessage(getString(R.string.song_no_artist));
                            alert.show();
                        } else {
                            Intent intent = new Intent(SongActivity.this, ArtistActivity.class);
                            intent.putExtra("spotifyArtistID", spotifyArtistID);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        }
                    }
                });

                final String spotifyAlbumID = "albums/"+(String)song.get("album_spotify_id");
                if(spotifyAlbumID.equals("albums/")) {
                    genreTextView.setText(getString(R.string.error_not_found));
                    albumNameTextView.setText(getString(R.string.error_not_found));
                    albumArtView.setImageResource(R.drawable.image_not_exist_600);

                    mAlbumButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(SongActivity.this);
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });
                            alert.setMessage(getString(R.string.song_no_album));
                            alert.show();
                        }
                    });
                    mIndicator.hide();
                } else {
                    new getAlbumTask().execute(SPOTIFY_API+spotifyAlbumID);

                    mAlbumButton.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SongActivity.this, AlbumActivity.class);
                            intent.putExtra("spotifyAlbumID", spotifyAlbumID );
                            startActivity(intent);
                            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        }
                    });
                }

                new getRatingTask().execute(SERVER_ADDRESS + RATING_API + SELECT_SONG_RATING + SONG_ID + WITH_USER + userDTO.getId());
                new getAvgRatingTask().execute(SERVER_ADDRESS + RATING_API + SELECT_SONG_AVG_RATING + SONG_ID);

                mEvaluationButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Animation fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_fade_in);
                        mRatingRelativeLayout.setVisibility(View.VISIBLE);
                        mRatingRelativeLayout.startAnimation(fadeInAnimation);
                    }
                });

                mSongActivityRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        int ratingInt = (int)(rating*2);
                        new InsertUpdateQuery(getApplicationContext()).execute(SERVER_ADDRESS + RATING_API + INSERT_RATING +
                                "user_id=" + userDTO.getId() + "&song_id=" + SONG_ID + "&rating=" + ratingInt +
                                "&artist_id=" + spotifyArtistID + "&album_id=" + spotifyAlbumID.substring(7));
                        Toast toast = Toast.makeText(getApplicationContext(), rating + getString(R.string.song_rating_score), Toast.LENGTH_SHORT);
                        toast.show();

                        userDTO.addRatingSoungCount(SONG_ID, ratingInt);

                        new getRatingTask().execute(SERVER_ADDRESS + RATING_API + SELECT_SONG_RATING + SONG_ID + WITH_USER + userDTO.getId());
                        new getAvgRatingTask().execute(SERVER_ADDRESS + RATING_API + SELECT_SONG_AVG_RATING + SONG_ID);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                mCloseButton.callOnClick();
                            }
                        }, 1000);
                    }
                });

                mCloseButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Animation fadeOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_fade_out);
                        mRatingRelativeLayout.setVisibility(View.GONE);
                        mRatingRelativeLayout.startAnimation(fadeOutAnimation);
                    }
                });

                String keyword = title+" "+artist;
                new setYouTubeTask(youTubeView).execute(YOUTUBE_API + URLEncoder.encode(keyword, "utf-8") + YOUTUBE_RESULT_FIVE+YOUTUBE_API_KEY+VIDEO_MOST_VIEW);
//                new setYouTubeTask(youTubeView).execute(YOUTUBE_API + URLEncoder.encode(keyword + " lyrics", "utf-8") + YOUTUBE_RESULT_ONE+YOUTUBE_API_KEY);
//                new setYouTubeTask(youTubeView).execute(YOUTUBE_API + URLEncoder.encode(keyword + " concert live", "utf-8") + YOUTUBE_RESULT_ONE+YOUTUBE_API_KEY);

            } catch (ParseException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
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

    /* Image Load - ImageView
       Parameter - String(url), ImageView
       Callback - Image.set*/
    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;
//        private Activity mActivity;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            if (mIndicator.isShowing())
                mIndicator.hide();
        }
    }

    class setYouTubeTask extends AsyncTask<String, String, String> implements YouTubePlayer.OnInitializedListener {

        private YouTubePlayerView mYouTubePlayerView;
        private ArrayList<String> youtubes_id, youtubes_title;
        private int mCurrent_Video;

        public setYouTubeTask(YouTubePlayerView _YouTubePlayerView){
            this.mYouTubePlayerView = _YouTubePlayerView;
            youtubes_id = new ArrayList<String>();
            youtubes_title = new ArrayList<String>();
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String youtubeJSON) {
            super.onPostExecute(youtubeJSON);

            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject youtube = (JSONObject) jsonParser.parse(youtubeJSON);
                JSONArray items = (JSONArray) youtube.get("items");
                if(items.size()!= 0) {
                    for(int i = 0; i < items.size(); i ++) {
                        JSONObject id = (JSONObject) ((JSONObject) items.get(i)).get("id");
                        String video_id = (String) id.get("videoId");
                        youtubes_id.add(video_id);

                        JSONObject snippet = (JSONObject) ((JSONObject) items.get(i)).get("snippet");
                        String video_title = (String) snippet.get("title");
                        youtubes_title.add(video_title);
                    }
                }
                mYouTubePlayerView.initialize(YOUTUBE_DEVELOPMENT_KEY, this);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider,
                                            YouTubeInitializationResult errorReason) {
            if (errorReason.isUserRecoverableError()) {
                errorReason.getErrorDialog(mActivity, 1).show();
            } else {
                String errorMessage = String.format(
                        getString(R.string.YouTubeError), errorReason.toString());
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                            final YouTubePlayer player, boolean wasRestored) {
            mCurrent_Video = 0;

            if (!wasRestored && youtubes_id.size() != 0) {
                player.cueVideos(youtubes_id);
                for(int i = 0; i < youtubes_title.size(); i++) {
                    mPlaylist.get(i).setText( (i+1) + ". " + youtubes_title.get(i) );
                    mPlaylist.get(i).setTag(youtubes_id.get(i));
                    final int finalI = i;
                    mPlaylist.get(i).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for(int j = 0; j < youtubes_title.size(); j ++)
                                mPlaylist.get(j).setTextColor(getResources().getColor(R.color.darker_gray));
                            mCurrent_Video = finalI;
                            player.loadVideo((String) v.getTag());
                            TextView textView = (TextView) v;
                            textView.setTextColor(getResources().getColor(R.color.WhiteSmoke));
                        }
                    });
                }
            }

            mPlayAllButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    player.cueVideos(youtubes_id);
                    for(int j = 0; j < youtubes_title.size(); j ++)
                        mPlaylist.get(j).setTextColor(getResources().getColor(R.color.darker_gray));
                    mPlaylist.get(0).setTextColor(getResources().getColor(R.color.WhiteSmoke));
                }
            });

            player.setPlaylistEventListener(new YouTubePlayer.PlaylistEventListener() {
                @Override
                public void onPrevious() {
                    mPlaylist.get(mCurrent_Video).setTextColor(getResources().getColor(R.color.darker_gray));
                    if(mCurrent_Video != 0)
                        mCurrent_Video--;
                    mPlaylist.get(mCurrent_Video).setTextColor(getResources().getColor(R.color.WhiteSmoke));
                }

                @Override
                public void onNext() {
                    mPlaylist.get(mCurrent_Video).setTextColor(getResources().getColor(R.color.darker_gray));
                    if(mCurrent_Video != youtubes_id.size()-1)
                        mCurrent_Video++;
                    mPlaylist.get(mCurrent_Video).setTextColor(getResources().getColor(R.color.WhiteSmoke));
                }

                @Override
                public void onPlaylistEnded() {

                }
            });

            player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                @Override
                public void onLoading() {

                }

                @Override
                public void onLoaded(String s) {

                }

                @Override
                public void onAdStarted() {

                }

                @Override
                public void onVideoStarted() {
                    mPlaylist.get(mCurrent_Video).setTextColor(getResources().getColor(R.color.WhiteSmoke));
                }

                @Override
                public void onVideoEnded() {
//                    mPlaylist.get(mCurrent_Video).setTextColor(getResources().getColor(R.color.darker_gray));
//                    if(mCurrent_Video != youtubes_id.size()-1)
//                        mCurrent_Video++;
                }

                @Override
                public void onError(YouTubePlayer.ErrorReason errorReason) {

                }
            });
        }
    }

    private void setSongPropertyText(TextView propertyTextView, float property) {

        int px = 0;
        Log.e("Tag", (String) propertyTextView.getTag());
        String[] propertyText = ((String) propertyTextView.getTag()).split("#");

        if(property >= 0.5) {
            if (property >= 0.8) {
                propertyTextView.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_DeviceDefault_Large);
                px = 10;
            } else if (property >= 0.6 && property < 0.8) {
                propertyTextView.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_DeviceDefault_Medium);
                px = 5;
            } else {
                propertyTextView.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_DeviceDefault_Small);
                px = 1;
            }
            propertyTextView.setText(propertyText[0]);
            setActiveView(propertyTextView, px);
        } else {
            if(property < 0.2) {
                propertyTextView.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_DeviceDefault_Large);
                px = 10;
            } else if(property >= 0.2 && property < 0.4) {
                propertyTextView.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_DeviceDefault_Medium);
                px = 5;
            } else {
                propertyTextView.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_DeviceDefault_Small);
                px = 1;
            }
            propertyTextView.setText(propertyText[1]);
            setNotActiveView(propertyTextView, px);
        }
        propertyTextView.setTextColor(Color.parseColor("#FFFFFF"));
    }

    private void setActiveView(View view, int px) {
        int bottom = view.getPaddingBottom();
        int top = view.getPaddingTop();
        int right = view.getPaddingRight();
        int left = view.getPaddingLeft();
        view.setBackgroundResource(R.drawable.button_active_background);
        view.setPadding(left + px, top + px, right + px, bottom + px);
    }

    private void setActivePropertyView(View view, int px) {
        int bottom = view.getPaddingBottom();
        int top = view.getPaddingTop();
        int right = view.getPaddingRight();
        int left = view.getPaddingLeft();
        view.setBackgroundResource(R.drawable.rectangle_active);
        view.setPadding(left + px, top + px, right + px, bottom + px);
    }

    private void setNotActiveView(View view, int px) {
        int bottom = view.getPaddingBottom();
        int top = view.getPaddingTop();
        int right = view.getPaddingRight();
        int left = view.getPaddingLeft();
        view.setBackgroundResource(R.drawable.button_property2_background);
        view.setPadding(left + px, top + px, right + px, bottom + px);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
