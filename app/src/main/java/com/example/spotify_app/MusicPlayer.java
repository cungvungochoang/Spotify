package com.example.spotify_app;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;


import com.example.spotify_app.Model.Song;

import java.util.ArrayList;
import java.util.Random;


// Audio controller
public class MusicPlayer {

    private static MusicPlayer _Ins;

    public static MusicPlayer Ins()
    {
        if(_Ins == null)
            _Ins = new MusicPlayer();
        return _Ins;
    }

    protected MusicPlayer()
    {

    }

    protected int indexCurSong;

    protected Song precentlySong;

    protected Song curSong;
    protected Song previousSong;
    protected Song nextSong;
    protected ArrayList<Song> lstSong;
    int curProgress = 0;

    boolean isRandom = false;



    protected MediaPlayer media;



    public static int NONE_REPEAT = -1;
    public static int REPEAT_ONE = 0;
    public static int REPEAT_ALL = 1;


    public int getIndexCurSong() {
        return indexCurSong;
    }

    public void setIndexCurSong(int indexCurSong) {
        if(indexCurSong < 0)
            indexCurSong = this.indexCurSong;
        this.indexCurSong = indexCurSong;
    }

    public Song getCurSong() {
        return curSong;
    }

    public void setCurSong(Song curSong) {
        if(this.curSong != null)
        {
            this.precentlySong = this.curSong;
        }
        this.curSong = curSong;

        int index = lstSong.indexOf(curSong);
        setIndexCurSong(index);

        setPreviousSong(index - 1);
        setNextSong(index + 1);
    }

    public Song getPrecentlySong() {
        if(precentlySong == null)
            precentlySong = new Song();
        return precentlySong;
    }


    public void setCurSong(int index) {
        if(index > lstSong.size() - 1)
            index = 0;
        if(index < 0)
            index = lstSong.size() - 1;
        if(this.curSong != null)
        {
            this.precentlySong = this.curSong;
        }
        this.curSong = lstSong.get(index);

        setIndexCurSong(index);

        setPreviousSong(index - 1);
        setNextSong(index + 1);
    }

    public Song getPreviousSong() {
        return previousSong;
    }

    public void setPreviousSong(int index) {
        if(index < 0)
        {
            index = lstSong.size() - 1;
        }
        this.previousSong = lstSong.get(index);
    }

    public Song getNextSong() {
        return nextSong;
    }

    public void setNextSong(int index) {
        if(index > lstSong.size() - 1)
        {
            index = 0;
        }
        this.nextSong = lstSong.get(index);
    }

    public ArrayList<Song> getLstSong() {
        if(lstSong == null)
            lstSong = new ArrayList<>();
        return lstSong;
    }

    public void setLstSong(ArrayList<Song> lstSong) {
        this.lstSong = lstSong;
    }

    public int getCurProgress() {
        if(media != null)
            return media.getCurrentPosition();
        return curProgress;
    }

    public void setCurProgress(int curProgress) {
        this.curProgress = curProgress;
    }


    public boolean isRandom() {
        return isRandom;
    }

    public void setRandom(boolean random) {
        isRandom = random;
    }

    public void goToTheNextSong()
    {
        if(getLstSong().size() > 1 && isRandom())
        {
            int rand = indexCurSong;
            while(rand == indexCurSong)
            {
                rand = new Random().nextInt(getLstSong().size());
            }
            indexCurSong = rand;
        }
        else
        {
            indexCurSong ++;
        }
        setCurSong(indexCurSong);
    }

    public void goToThePreviousSong()
    {
        indexCurSong --;
        setCurSong(indexCurSong);
    }

    public boolean isPlayingMusic() {

        return media.isPlaying();
    }

    public MediaPlayer getMedia() {
        return media;
    }

    public void playAudio()
    {
        if(this.media != null)
            if(!this.media.isPlaying())
                this.media.start();
    }

    public void pauseAudio()
    {
        if(this.media != null)
            if(this.media.isPlaying())
                this.media.pause();
    }

    public void prepareSong(Context context, Uri songUri) {
        Song precentlySong = MusicPlayer.Ins().getPrecentlySong();
        Song curSong = MusicPlayer.Ins().getCurSong();

        if (!curSong.checkSimilar(precentlySong))
        {
            MediaPlayer newMedia = MediaPlayer.create(context, songUri);
            setMedia(newMedia);
//            try {
//
//                String audioUrl = curSong.getSONG_URL();
//                newMedia.setDataSource(audioUrl);
//                newMedia.prepare();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    public void setMedia(MediaPlayer media) {
        releaseMedia();
        this.media = media;
    }

    public void releaseMedia()
    {
        if(this.media != null)
        {
            this.media.stop();
            this.media.release();
            this.media = null;
        }
    }

    public Song findSongIndex(Song target)
    {
        for(int i = 0; i< getLstSong().size(); i++)
        {
            Song item = getLstSong().get(i);
            if(item.getSONG_ID() == target.getSONG_ID())
                return item;
        }
        return null;
    }



}
