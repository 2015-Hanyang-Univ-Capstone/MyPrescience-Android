package com.myprescience.dto;

import android.graphics.Bitmap;

/**
 * Created by hyeon-seob on 15. 3. 4..
 * 곡 정보를 저장하는 클래스
 */
public class MypTopSongData {
    public String id;
    public String albumUrl;
    public Bitmap albumArt;
    public String title;
    public String artist;
    public String genres;
    public String genre = "";
    public int user_rating;
    public float avg_rating;
    public float valenceProperty;
    public float speechinessProperty;
    public float energyProperty;
    public float danceablilityProperty;
    public float acousticProperty;
    public float instrumentalnessProperty;
    public float livenessProperty;
    public boolean vocalProperty;
    public boolean studioProperty;

    public void setAlbumArt(Bitmap mAlbumArt) { albumArt = mAlbumArt; }
    public String getGenre() { return this.genre; }
    public void setGenre(String _genre) { genre = _genre; }
}
