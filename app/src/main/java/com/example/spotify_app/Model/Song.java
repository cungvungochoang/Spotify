package com.example.spotify_app.Model;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.util.Size;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Song implements Serializable {

    //    public static int Internet = 0;
    //    public static int Local = 1;
    @SerializedName("SONG_ID")
    private int SONG_ID;
    @SerializedName("TITLE")
    private String TITLE;
    @SerializedName("ARTIST_NAMES")
    private String ARTIST_NAMES;

    @SerializedName("IMAGE")
    private String IMAGE;

    @SerializedName("SONG_URL")
    private String SONG_URL;

    @SerializedName("RELEASE_DATE")
    private String RELEASE_DATE;

    Long SONG_IDV2;
    Bitmap imgBitmap;
    Uri SONG_URI;
    boolean liked;
    int type = 0;


    public Long getSONG_IDV2() {
        return SONG_IDV2;
    }

    public void setSONG_IDV2(Long SONG_IDV2) {
        this.SONG_IDV2 = SONG_IDV2;
    }
    public int getSONG_ID() {
        return SONG_ID;
    }

    public void setSONG_ID(int SONG_ID) {
        this.SONG_ID = SONG_ID;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }

    public String getARTIST_NAMES() {
        return ARTIST_NAMES;
    }

    public void setARTIST_NAMES(String ARTIST_NAMES) {
        this.ARTIST_NAMES = ARTIST_NAMES;
    }

    public String getIMAGE() {
        return IMAGE;
    }

    public void setIMAGE(String IMAGE) {
        this.IMAGE = IMAGE;
    }


    public void setImgBitmap(Bitmap imgBitmap) {
        this.imgBitmap = imgBitmap;
    }

    public String getSONG_URL() {
        return SONG_URL;
    }

    public void setSONG_URL(String SONG_URL) {
        this.SONG_URL = SONG_URL;
    }

    public Uri getSONG_URI() {
        return SONG_URI;
    }

    public void setSONG_URI(Uri SONG_URI) {
        this.SONG_URI = SONG_URI;
    }

    public String getRELEASE_DATE() {
        return RELEASE_DATE;
    }

    public void setRELEASE_DATE(String RELEASE_DATE) {
        this.RELEASE_DATE = RELEASE_DATE;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean checkSimilar(Song item)
    {
        if(getSONG_ID() != item.getSONG_ID())
            return false;
        if(!getTITLE().equals(item.getTITLE()))
            return false;
        if(!getARTIST_NAMES().equals(item.getARTIST_NAMES()))
            return false;
        if(!getSONG_URL().equals(item.getSONG_URL()))
            return false;
        return true;
    }

    public Song() {
        this.SONG_ID = 0;
        this.IMAGE = "Image";
        this.TITLE = "Title";
        this.ARTIST_NAMES = "Artist Names";
    }

    public Song(String title, String artistNames, String image, String songUrl) {
        this.TITLE = title;
        this.ARTIST_NAMES = artistNames;
        this.IMAGE = image;
        this.SONG_URL = songUrl;
    }

    public Song(String SONG_URL, Long SONG_IDV2, String TITLE, String ARTIST_NAMES, String IMAGE, int type) {
        this.SONG_URL = SONG_URL;
        this.SONG_IDV2 = SONG_IDV2;
        this.TITLE = TITLE;
        this.ARTIST_NAMES = ARTIST_NAMES;
        this.IMAGE = IMAGE;
        this.type = type;
    }

    public Song(int SONG_ID, String TITLE, String ARTIST_NAMES, String IMAGE, String SONG_URL, String RELEASE_DATE) {
        this.SONG_ID = SONG_ID;
        this.TITLE = TITLE;
        this.ARTIST_NAMES = ARTIST_NAMES;
        this.IMAGE = IMAGE;
        this.SONG_URL = SONG_URL;
        this.RELEASE_DATE = RELEASE_DATE;
    }

    public Song (Song t)
    {
        this.SONG_ID = t.SONG_ID;
        this.SONG_URL = t.SONG_URL;
        this.SONG_IDV2 = t.SONG_IDV2;
        this.TITLE = t.TITLE;
        this.ARTIST_NAMES = t.ARTIST_NAMES;
        this.IMAGE = t.IMAGE;
        this.type = t.type;
    }

    public int findIndexItem(ArrayList<Song> lstSong)
    {
        for(int i=0 ; i < lstSong.size();i++)
        {
            if(checkSimilar(lstSong.get(i)))
                return i;
        }
        return -1;
    }



    public void loadThumbnailToTarget(ContentResolver contentResolver, ImageView img, Size size)
    {
        if(!getIMAGE().isEmpty())
        {
            if(getType() == TypeSong.Local)
            {
                Picasso.get().load(new File(getIMAGE())).into(img);
            }
            else
            {
                Picasso.get().load(getIMAGE()).into(img);
            }
        }
        else
        {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                try {
                    Bitmap thumbnail =
                            contentResolver.loadThumbnail(
                                    getSONG_URI(), size, null);
                    img.setImageBitmap(thumbnail);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if(img.getDrawable() != null)
            TypeSong.imgBitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();

        if(img.getDrawable() != null)
            setImgBitmap(((BitmapDrawable) img.getDrawable()).getBitmap());
    }

    public Bitmap getImgBitmap() {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(getSONG_URL());
        byte[] artwork = retriever.getEmbeddedPicture();

        if (artwork != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(artwork, 0, artwork.length);
            imgBitmap = bitmap;
        } else {

        }
        return imgBitmap;
    }

    public static void optimalData(ArrayList<Song> dataPrimaryLstSong, ArrayList<FavoriteSong> dataLstFavoriteSongs)
    {
        for(int i = 0; i < dataPrimaryLstSong.size() ; i++)
        {
            for(FavoriteSong item : dataLstFavoriteSongs)
            {
                if(item.getSONG_ID() == dataPrimaryLstSong.get(i).getSONG_ID())
                {
                    dataPrimaryLstSong.get(i).setLiked(true);
                    break;
                }
            }

        }
    }

}
