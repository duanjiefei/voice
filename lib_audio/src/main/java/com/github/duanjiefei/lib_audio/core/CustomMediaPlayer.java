package com.github.duanjiefei.lib_audio.core;

import android.media.MediaPlayer;

import java.io.IOException;

public class CustomMediaPlayer extends MediaPlayer implements MediaPlayer.OnCompletionListener {

    public enum  Status {
        IDEL,INITALIZED,STARTED,PAUSED,STOPED,COMPLETED;
    }
    private MediaPlayer.OnCompletionListener completionListener;
    private Status mState = Status.IDEL;
    public CustomMediaPlayer(){
        super();
        mState = Status.IDEL;
        super.setOnCompletionListener(this);
    }

    @Override
    public void reset() {
        super.reset();
        mState = Status.IDEL;
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
        super.setDataSource(path);
        mState = Status.INITALIZED;
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        mState = Status.STARTED;
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        mState = Status.PAUSED;
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        mState = Status.STOPED;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mState = Status.COMPLETED;
    }

    public Status getState(){
        return mState;
    }

    public boolean isCompleted(){
        return mState  == Status.COMPLETED;
    }


    public void setCompleteListener(MediaPlayer.OnCompletionListener listener){
        completionListener = listener;
    }
}
