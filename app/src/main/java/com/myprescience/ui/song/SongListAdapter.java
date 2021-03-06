package com.myprescience.ui.song;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.myprescience.R;
import com.myprescience.dto.SongData;
import com.myprescience.dto.UserData;
import com.myprescience.util.InsertUpdateQuery;
import com.myprescience.util.ViewHolder;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.myprescience.util.Server.INSERT_RATING;
import static com.myprescience.util.Server.RATING_API;
import static com.myprescience.util.Server.SERVER_ADDRESS;

/**
 * Created by hyeon-seob on 15. 3. 4..
 * 리스트 뷰에 곡 정보를 추가하기 위한 어댑터 클래스
 * addItem()으로 리스트에 곡 추가
 */

public class SongListAdapter extends BaseAdapter{

    private UserData userDTO;

    private int userId;
    private Context mContext = null;
    private ArrayList<SongData> mListData = new ArrayList<>();
    private ViewHolder holder;
    private ProgressBar progressBar;
    private int ratingCount;
    private TextView textView;
    private ImageButton rightButton;

    public SongListAdapter(Context mContext, int _ratingCount, ProgressBar _progressBar, TextView _textView, ImageButton _rightButton, int _userId) {
        super();
        this.mContext = mContext;
        progressBar = _progressBar;
        ratingCount = _ratingCount;
        textView = _textView;
        rightButton = _rightButton;
        this.userId = _userId;
        userDTO = new UserData(mContext);
    }

    public void addItem(String _id, String _artist_id, String _albumArtURL, String _title, String _artist, int _rating){
        SongData temp = new SongData();
        temp.id = _id;
        temp.artist_id = _artist_id;
        temp.title = _title;
        temp.artist = _artist;
        temp.rating = _rating;
        temp.albumUrl = _albumArtURL;
        temp.albumArt = null;
        mListData.add(temp);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_song, null);

            holder.albumImageView = (ImageView) convertView.findViewById(R.id.albumArtView);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
            holder.artistTextView = (TextView) convertView.findViewById(R.id.artistTextView);
            holder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);

            LayerDrawable stars = (LayerDrawable) holder.ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(mContext.getResources().getColor(R.color.color_base_theme), PorterDuff.Mode.SRC_ATOP);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final SongData mData = mListData.get(position);
        holder.albumImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SongActivity.class);
                intent.putExtra("song_id", mData.id);
                v.getContext().startActivity(intent);
            }
        });
        holder.titleTextView.setText(mData.title);
        holder.artistTextView.setText(mData.artist);
        holder.ratingBar.setProgress(mData.rating);

        holder.position = position;

        // 앨범아트 가져오기
        // Spotify에 앨범아트 정보가 있을 경우
        if(!(mData.albumUrl).equals("")) {
            if(mData.albumArt == null) {
                holder.albumImageView.setImageResource(R.drawable.image_loading);
                try {
                    new LoadAlbumArt(position, holder, mData).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mData.albumUrl);
                } catch (Exception e) { e.printStackTrace(); }
            }
            else
                holder.albumImageView.setImageBitmap(mData.albumArt);

        } else {
            holder.albumImageView.setImageResource(R.drawable.image_not_exist_64);
        }

        holder.ratingBar.setTag(position);
        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(!fromUser) return;

                int index = (Integer)ratingBar.getTag();
                if(mListData.get(index).rating == 0) {
                    ratingCount++;
                    setProgress(ratingCount);
                    Log.e("ratingCount", ratingCount + "");

                    textView.invalidate();

                    if(ratingCount >= SongListActivity.MIN_SELECTED_SONG)
                        rightButton.setVisibility(ImageButton.VISIBLE);
                }

                mListData.get(index).rating = (int)(rating*2);

                new InsertUpdateQuery(mContext).execute(SERVER_ADDRESS+RATING_API+INSERT_RATING+
                        "user_id=" + userId + "&song_id=" + mListData.get(index).id + "&rating=" + mListData.get(index).rating +
                        "&artist_id=" + mListData.get(index).artist_id + "&album_id=" + mListData.get(index).albumUrl.substring(7));

                userDTO.addRatingSoungCount(mListData.get(index).id, mListData.get(index).rating);

                Toast toast = Toast.makeText(mContext, rating + mContext.getString(R.string.song_rating_score), Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        return convertView;
    }

    public void setProgress(int count) {
        progressBar.setProgress((int)(Math.min(1, count/(double)SongListActivity.MIN_SELECTED_SONG)*100));
        progressBar.invalidate();
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public SongData getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class LoadAlbumArt extends AsyncTask<String, String, Bitmap> {

        private int mPosition;
        private ViewHolder mHolder = null;
        private SongData songData;

        public LoadAlbumArt(int positon, ViewHolder holder, SongData mSongData){
            this.mPosition = positon;
            this.mHolder = holder;
            this.songData = mSongData;
        }

        @Override
        protected Bitmap doInBackground(String... url) {

            Bitmap myBitmap = null;

            try {
                URL urlConnection = new URL(url[0]);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();

                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = 1;
                option.inPurgeable = true;
                option.inDither = true;

                myBitmap = BitmapFactory.decodeStream(input, null, option);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return myBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap mAlbumArt) {
            super.onPostExecute(mAlbumArt);
            songData.setAlbumArt(mAlbumArt);
            if (mHolder.position == mPosition) {
                mHolder.albumImageView.setImageBitmap(mAlbumArt);
            }
        }
    }
}
