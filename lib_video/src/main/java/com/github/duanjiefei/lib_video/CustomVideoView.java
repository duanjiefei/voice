package com.github.duanjiefei.lib_video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;



public class CustomVideoView extends RelativeLayout implements View.OnClickListener
        , MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener
            , TextureView.SurfaceTextureListener {
    
    public AudioManager audioManager;

    private MediaPlayer mediaPlayer;
    private boolean isMute;



    private int mWidth, mHeight;
    private RelativeLayout relativeLayout;


    private TextureView mVideoView;
    private Surface videoSurface;
    private Button smallPlayButton;
    private ImageView smallToFullButton;
    private ImageView mLoadingView;

    private int mCurrentCount;
    private String mUrl;
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PLAYING = 1;
    private static final int STATE_PAUSING = 2;
    private static final int LOAD_TOTAL_COUNT = 3;



    private int playerState = STATE_IDLE;
    private boolean canPlay = true;
    private boolean mIsRealPause;
    private boolean mIsComplete;



    private ADVideoPlayerListener listener;
    private ScreenEventReceiver mScreenReceiver;

    public CustomVideoView(Context context){
        super(context);
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        initData();
        initView();
        registerBroadcastReceiver();
    }



    private void initView() {
        LayoutInflater layoutInflater =  LayoutInflater.from(this.getContext());
        relativeLayout = (RelativeLayout) layoutInflater.inflate(R.layout.video_player_layout,this);
        mVideoView = relativeLayout.findViewById(R.id.xadsdk_player_video_textureView);
        mVideoView.setOnClickListener(this);
        mVideoView.setKeepScreenOn(true);
        mVideoView.setSurfaceTextureListener(this);
        initSmallLayoutMode();
    }

    private void initData() {

        DisplayMetrics dm  =  new DisplayMetrics();
        WindowManager wm  = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        mWidth = dm.widthPixels;
        mHeight = (int) (mWidth * (9 /16.0f));



    }

    private void initSmallLayoutMode() {
        LayoutParams layoutParams = new LayoutParams(mWidth,mHeight);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        relativeLayout.setLayoutParams(layoutParams);

        smallPlayButton  = relativeLayout.findViewById(R.id.xadsdk_small_play_btn);
        smallToFullButton = relativeLayout.findViewById(R.id.xadsdk_to_full_view);
        mLoadingView = relativeLayout.findViewById(R.id.loading_bar);

        smallPlayButton.setOnClickListener(this);
        smallToFullButton.setOnClickListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        if (listener !=null){
            listener.onAdVideoLoadComplete();
        }
        
        playBack();
        setIsComplete(true);
        setIsRealPause(true);
    }

    //播放完成后回到初始状态
    private void playBack() {
        setCurrentStatus(STATE_PAUSING);
        if (mediaPlayer != null){
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
        }

        this.showPauseView(false);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        this.playerState = STATE_ERROR;
        if (mCurrentCount > LOAD_TOTAL_COUNT){
            showPauseView(false);
            if (listener != null){
                listener.onAdVideoLoadFailed();
            }
        }
        this.stop(); //重新加载
        return true;
    }

    // 加载错误时，重新加载
    private void stop() {
        if (mediaPlayer != null){
            mediaPlayer.reset();
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        setCurrentStatus(STATE_IDLE);
        if (mCurrentCount < LOAD_TOTAL_COUNT){
            mCurrentCount = mCurrentCount +1;
            load();  //重新加载
        }else {
            showPauseView(false);  //加载3次后依然错误，显示暂停状态
        }
    }


    //加载资源
    private void load() {
        if (playerState != STATE_IDLE){
            return;
        }

        try {
            showLoadingView();
            setCurrentStatus(STATE_IDLE);
            checkMediaPlayer();
            mute(true);
            mediaPlayer.setDataSource(mUrl);
            mediaPlayer.prepareAsync();
        }catch (Exception e){
            stop();
        }
    }

    // true is no voice
    public void mute(boolean b) {
        isMute = b;
        if (mediaPlayer != null && this.audioManager!=null){
            float volume = isMute ? 0.0f :1.0f;
            mediaPlayer.setVolume(volume,volume);
        }
    }


    private synchronized void checkMediaPlayer() {
        if (mediaPlayer == null){
            mediaPlayer =  new MediaPlayer();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    // 加载过程中显示的view
    private void showLoadingView() {
        smallToFullButton.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
        AnimationDrawable animationDrawable = (AnimationDrawable) mLoadingView.getBackground();
        animationDrawable.start();

        smallPlayButton.setVisibility(View.GONE);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        showPlayingView();
        mediaPlayer =  mp;
        if (mediaPlayer!=null){
            mCurrentCount = 0;
            if (listener !=null){
                listener.onAdVideoLoadSuccess();
            }
            setCurrentStatus(STATE_PAUSING);
            resume();
        }

    }

    public void resume() {
        if (this.playerState != STATE_PAUSING){
            return;
        }
        
        if (!isPlaying()){//没有在播放,进入播放状态
            entryResumeState();
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.start();
            showPauseView(true);
        }else{//正在播放
            showPauseView(false);
        }
    }

    private void entryResumeState() {
        canPlay = true;
        setCurrentStatus(STATE_PLAYING);
        setIsRealPause(false);
        setIsComplete(false);
    }

    public void setIsComplete(boolean b) {
        mIsComplete = b;
    }

    public void setIsRealPause(boolean b) {
        mIsRealPause = b;
    }

    public boolean getIsComplete(){
        return  mIsComplete;
    }

    public boolean getIsRealPause(){
        return  mIsRealPause;
    }

    public void setDataSource(String url){
        mUrl = url;
    }

    public void setListener(ADVideoPlayerListener listener){
        this.listener = listener;
    }

    public int getPosition(){
        if (mediaPlayer != null){
            return  mediaPlayer.getCurrentPosition();
        }

        return  0;
    }

    private void showPauseView(boolean show) {
        smallToFullButton.setVisibility(show? View.VISIBLE : View.GONE);
        smallPlayButton.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoadingView.clearAnimation();
        mLoadingView.setVisibility(View.GONE);
    }

    public void isShowFullBtn(boolean isShow) {
        smallToFullButton.setImageResource(isShow ? R.drawable.xadsdk_ad_mini : R.drawable.xadsdk_ad_mini_null);
        smallToFullButton.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private boolean isPlaying() {
        if (mediaPlayer!=null&& mediaPlayer.isPlaying()){
            return true;
        }
        return false;
    }

    private void setCurrentStatus(int state) {
        playerState = state;
    }

    private void showPlayingView() {
        mLoadingView.clearAnimation();
        mLoadingView.setVisibility(View.GONE);
        smallPlayButton.setVisibility(View.GONE);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        videoSurface =  new Surface(surface);
        checkMediaPlayer();
        mediaPlayer.setSurface(videoSurface);
        load();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onClick(View v) {
        if (v == this.smallPlayButton){
            if (playerState == STATE_PAUSING){
                resume();
                listener.onClickPlay();
            }else {
                load();
            }
        }else if (v == this.smallToFullButton){
            listener.onClickFullScreenBtn();
        }else if (v == mVideoView){
            listener.onClickVideo();
        }
    }

    private void registerBroadcastReceiver() {
        if (mScreenReceiver == null) {
            mScreenReceiver = new ScreenEventReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            getContext().registerReceiver(mScreenReceiver, filter);
        }
    }

    private void unRegisterBroadcastReceiver() {
        if (mScreenReceiver != null) {
            getContext().unregisterReceiver(mScreenReceiver);
        }
    }

    public void pause() {
        if (this.playerState != STATE_PLAYING) {
            return;
        }
        setCurrentStatus(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.pause();
            if (!this.canPlay) {
                this.mediaPlayer.seekTo(0);
            }
        }
        this.showPauseView(false);
    }

    //全屏不显示暂停状态,后续可以整合，不必单独出一个方法
    public void pauseForFullScreen() {
        if (playerState != STATE_PLAYING) {
            return;
        }
        setCurrentStatus(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.pause();
            if (!this.canPlay) {
                mediaPlayer.seekTo(0);
            }
        }
    }

    //跳到指定点暂停视频
    public void seekAndPause(int position) {
        if (this.playerState != STATE_PLAYING) {
            return;
        }
        showPauseView(false);
        setCurrentStatus(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.seekTo(position);
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mediaPlayer.pause();
                }
            });
        }
    }

    //跳到指定点播放视频
    public void seekAndResume(int position) {
        if (mediaPlayer != null) {
            showPauseView(true);
            entryResumeState();
            mediaPlayer.seekTo(position);
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
        }
    }


    public interface ADVideoPlayerListener {

        void onBufferUpdate(int time);

        void onClickFullScreenBtn();

        void onClickVideo();

        void onClickBackBtn();

        void onClickPlay();

        void onAdVideoLoadSuccess();

        void onAdVideoLoadFailed();

        void onAdVideoLoadComplete();
    }

    public interface ADFrameImageLoadListener {

        void onStartFrameLoad(String url, ImageLoaderListener listener);
    }

    public interface ImageLoaderListener {
        /**
         * 如果图片下载不成功，传null
         */
        void onLoadingComplete(Bitmap loadedImage);
    }

    /**
     * 监听锁屏事件的广播接收器
     */
    private class ScreenEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //主动锁屏时 pause, 主动解锁屏幕时，resume
            switch (intent.getAction()) {
                case Intent.ACTION_USER_PRESENT:
                    if (playerState == STATE_PAUSING) {
                        if (mIsRealPause) {
                            //手动点的暂停，回来后还暂停
                            pause();
                        } else {
                            resume();
                        }
                    }
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    if (playerState == STATE_PLAYING) {
                        pause();
                    }
                    break;
            }
        }
    }

    public void destroy() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.setOnSeekCompleteListener(null);
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        setCurrentStatus(STATE_IDLE);
        mCurrentCount = 0;
        setIsComplete(false);
        setIsRealPause(false);
        unRegisterBroadcastReceiver();
        showPauseView(false); //除了播放和loading外其余任何状态都显示pause
    }
}
