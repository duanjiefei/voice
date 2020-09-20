package com.github.duanjiefei.lib_audio.core;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;

import com.github.duanjiefei.lib_audio.app.AudioHelper;
import com.github.duanjiefei.lib_audio.event.AudioCompleteEvent;
import com.github.duanjiefei.lib_audio.event.AudioErrorEvent;
import com.github.duanjiefei.lib_audio.event.AudioLoadEvent;
import com.github.duanjiefei.lib_audio.event.AudioPauseEvent;
import com.github.duanjiefei.lib_audio.event.AudioProgressEvent;
import com.github.duanjiefei.lib_audio.event.AudioReleaseEvent;
import com.github.duanjiefei.lib_audio.event.AudioStartEvent;
import com.github.duanjiefei.lib_audio.model.AudioBean;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;


/**
 * 1 对外提供播放音频的方法
 * 2 对外发送各种事件
 */
public class AudioPlayer
        implements MediaPlayer.OnCompletionListener,MediaPlayer.OnBufferingUpdateListener
,MediaPlayer.OnErrorListener,MediaPlayer.OnPreparedListener,AudioFocusManager.AudioFocusListener {

    private static final String TAG  = "AudioPlayer";
    private static final int TIMEZ_MSG = 0x01;
    private static final int TIME_INVAL = 100;

    //真正负责播放的核心类
    private CustomMediaPlayer mCustomMediaPlayer;
    private WifiManager.WifiLock mWifiLock;
    private AudioFocusManager mAudioFocusManager;

    //private

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case TIMEZ_MSG:
                    if (getStatus() == CustomMediaPlayer.Status.STARTED
                            || getStatus() == CustomMediaPlayer.Status.PAUSED) {
                        //UI类型处理事件
                        EventBus.getDefault()
                                .post(new AudioProgressEvent(getStatus(), getCurrentPosition(), getDuration()));
                        sendEmptyMessageDelayed(TIMEZ_MSG, TIME_INVAL);
                    }
                    break;
            }
        }
    };

    public AudioPlayer(){
        init();
    }

    //初始化音频播放器
    private void init() {
        mCustomMediaPlayer = new CustomMediaPlayer();
        mCustomMediaPlayer.setWakeMode(AudioHelper.getContext(), PowerManager.PARTIAL_WAKE_LOCK);

        mCustomMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mCustomMediaPlayer.setCompleteListener(this);
        mCustomMediaPlayer.setOnPreparedListener(this);
        mCustomMediaPlayer.setOnBufferingUpdateListener(this);
        mCustomMediaPlayer.setOnErrorListener(this);

        // 初始化wifi锁
        mWifiLock = ((WifiManager) AudioHelper.getContext()
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
        // 初始化音频焦点管理器
        mAudioFocusManager = new AudioFocusManager(AudioHelper.getContext(), this);
    }

    //内部提供的播放音频的方法
    private void start() {
        //保证音频播放器有获取到焦点
        if (!mAudioFocusManager.requestAudioFocus()){
            return;
        }

        mCustomMediaPlayer.start();
        mWifiLock.acquire();//启用wifi锁

        //更新进度
        mHandler.sendEmptyMessage(TIMEZ_MSG);
        //发送start事件，UI类型处理事件
        EventBus.getDefault().post(new AudioStartEvent());
    }

    //设置播放器的音量
    private void setVolume(float l, float r) {
        if (mCustomMediaPlayer!=null){
            mCustomMediaPlayer.setVolume(l,r);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        EventBus.getDefault().post(new AudioCompleteEvent());
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        EventBus.getDefault().post(new AudioErrorEvent());
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        start();
    }

    private boolean isPausedByFocusLossTransient;
    /**
     * 获得焦点回调处理
     */
    @Override
    public void audioFocusGrant(){
        setVolume(1.0f,1.0f);
        if (isPausedByFocusLossTransient){
            resume();
        }
        isPausedByFocusLossTransient = false;
    }
    /**
     * 永久失去焦点回调处理
     */
    @Override
    public void audioFocusLoss(){
        if (mCustomMediaPlayer!=null){
            pause();
        }
    }

    /**
     * 短暂失去焦点回调处理
     */
    @Override
    public void audioFocusLossTransient(){
        if (mCustomMediaPlayer!=null){
            pause();
        }
        isPausedByFocusLossTransient = true;
    }
    /**
     * 瞬间失去焦点回调
     */
    @Override
    public void audioFocusLossDuck(){
        setVolume(0.5f,0.5f);
    }


    /**
     * 获取 audioplayer 的播放 状态
     * @return
     */
    public CustomMediaPlayer.Status getStatus(){
        if (mCustomMediaPlayer!=null){
            return mCustomMediaPlayer.getState();
        }else {
            return CustomMediaPlayer.Status.STOPED;
        }
    }


    /**
     * 对外提供加载音频的方法
     * @param bean
     */
    public void load(AudioBean bean){
        try {
            mCustomMediaPlayer.reset();
            mCustomMediaPlayer.setDataSource(bean.mUrl);
            mCustomMediaPlayer.prepareAsync();

            //发送音频加载事件
            EventBus.getDefault().post(new AudioLoadEvent(bean));
        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new AudioErrorEvent());
        }
    }

    /**
     * 对外提供的播放的方法
     */
    public void resume(){
        if (getStatus() == CustomMediaPlayer.Status.PAUSED){
            start();
        }
    }

    /**
     * 对外提供的暂定的方法
     */
    public void pause(){
        if (getStatus() == CustomMediaPlayer.Status.STARTED){
            mCustomMediaPlayer.pause();

            if (mWifiLock.isHeld()){
                mWifiLock.release();
            }

            if (mAudioFocusManager!=null){
                //释放音频的焦点
                mAudioFocusManager.abandonAudioFocus();
            }
            //发送暂停播放的事件
            EventBus.getDefault().post(new AudioPauseEvent());
        }
    }

    /**
     * 释放唯一的audioPlayer 实例，只有在app推出时使用
     */
    public void release(){
        if (mCustomMediaPlayer==null){
            return;
        }
        mCustomMediaPlayer.release();
        mCustomMediaPlayer = null;

        if (mAudioFocusManager!=null){
            mAudioFocusManager.abandonAudioFocus();
        }
        if (mWifiLock.isHeld()){
            mWifiLock.release();
        }

        mWifiLock = null;
        mAudioFocusManager = null;

        //发送销毁播放器的事件
        EventBus.getDefault().post(new AudioReleaseEvent());
    }


    public int getCurrentPosition() {
        if (getStatus() == CustomMediaPlayer.Status.STARTED
                || getStatus() == CustomMediaPlayer.Status.PAUSED) {
            return mCustomMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 获取当前音乐总时长,更新进度用
     */
    public int getDuration() {
        if (getStatus() == CustomMediaPlayer.Status.STARTED
                || getStatus() == CustomMediaPlayer.Status.PAUSED) {
            return mCustomMediaPlayer.getDuration();
        }
        return 0;
    }
}
