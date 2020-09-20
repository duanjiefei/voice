package com.github.duanjiefei.lib_audio.core;




import android.util.Log;

import com.github.duanjiefei.lib_audio.exception.AudioQueueEmptyException;
import com.github.duanjiefei.lib_audio.event.AudioCompleteEvent;
import com.github.duanjiefei.lib_audio.event.AudioErrorEvent;
import com.github.duanjiefei.lib_audio.event.AudioPlayModeEvent;
import com.github.duanjiefei.lib_audio.model.AudioBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Random;

public class AudioController {
    private static final String TAG = "AudioController";


    public enum PlayMode{
        LOOP,
        RANDOM,
        REPEAT
    }
    public static AudioController getInstance(){
        return SingletonHolder.mAudioController;
    }

    private static class SingletonHolder{
        private static AudioController mAudioController = new AudioController();
    }

    private ArrayList<AudioBean> mQueue = new ArrayList<>();
    private PlayMode mPlayMode;
    private int queueIndex;
    private AudioPlayer mAudioPlayer;
    private AudioController(){
        EventBus.getDefault().register(this);
        mAudioPlayer = new AudioPlayer();
        mQueue = new ArrayList<>();
        mPlayMode  = PlayMode.LOOP;
        queueIndex = 0;
    }

    private int queryAudio(AudioBean audioBean){
        return mQueue.indexOf(audioBean);
    }

    private void load(AudioBean bean){
        mAudioPlayer.load(bean);
    }

    //获取当前播放器的状态
    private CustomMediaPlayer.Status getStatus(){
        Log.d(TAG, "getStatus: "+mAudioPlayer.getStatus().name());
        return mAudioPlayer.getStatus();
    }

    private AudioBean getPlaying(int index){
        Log.d(TAG, "getPlaying: "+index);
        if (mQueue!=null&&!mQueue.isEmpty()&& index>=0 && index < mQueue.size()){
            return  mQueue.get(index);
        }else {
            throw new AudioQueueEmptyException("当前播放队列为空,请先设置播放队列.");
        }
    }

    //获取下一首的播放歌曲
    private AudioBean  getNextPlaying(){
        switch (mPlayMode){
            case LOOP://循环播放
                queueIndex =  (queueIndex + 1)%mQueue.size();
                return getPlaying(queueIndex);
            case RANDOM:
                queueIndex = new Random().nextInt(mQueue.size())%mQueue.size();
                return getPlaying(queueIndex);
            case REPEAT:
                return getPlaying(queueIndex);
        }
        return null;
    }
    //获取前一首带播放的歌曲
    private AudioBean getPreviousPlaying(){
        switch (mPlayMode){
            case LOOP://循环播放
                Log.d(TAG, "getPreviousPlaying: before "+queueIndex);
                queueIndex =  (queueIndex - 1 + mQueue.size())%mQueue.size();
                Log.d(TAG, "getPreviousPlaying: after "+queueIndex);
                return getPlaying(queueIndex);
            case RANDOM:
                queueIndex = new Random().nextInt(mQueue.size())%mQueue.size();
                return getPlaying(queueIndex);
            case REPEAT:
                return getPlaying(queueIndex);
        }
        return null;
    }



    private void addCustomAudio(int index, AudioBean bean) {
        if (mQueue == null) {
            throw new AudioQueueEmptyException("当前播放队列为空,请先设置播放队列.");
        }
        mQueue.add(index, bean);
    }

    /**
     * 对外提供是否处于播放的状态
     * @return
     */
    public boolean isStartState(){
        return getStatus() == CustomMediaPlayer.Status.STARTED;
    }

    /**
     * 对外提供是否处于暂停的状态
     * @return
     */
    public boolean isPauseState(){
        return getStatus() == CustomMediaPlayer.Status.PAUSED;
    }

    /**
     * 对外提供获取歌曲列表的方法
     * @return
     */
    public ArrayList<AudioBean> getQueue(){
        return mQueue == null ? new ArrayList<AudioBean>() : mQueue;
    }

    public void setQueue(ArrayList<AudioBean> queue,int index){
        mQueue.addAll(queue);
        queueIndex = index;
    }

    /**
     * 对外提供的设置播放队列的方法
     * @param queue
     */
    public void setQueue(ArrayList<AudioBean> queue){
        setQueue(queue,0);
    }

    /**
     * 根据索引设置需要播放的歌曲
     * @param index
     */
    public void setPlayIndex(int index){
        if (mQueue == null){
            throw  new AudioQueueEmptyException("the queue is empty");
        }
        
        queueIndex = index;
        play();
    }

    public PlayMode getPlayMode() {
        return mPlayMode;
    }

    public void setPlayMode(PlayMode mode){
        mPlayMode = mode;
        EventBus.getDefault().post(new AudioPlayModeEvent(mode));
    }


    /**
     * 队列头添加播放哥曲
     */
    public void addAudio(AudioBean bean) {
        this.addAudio(0, bean);
    }

    public void addAudio(int index, AudioBean bean) {
        if (mQueue == null) {
            throw new AudioQueueEmptyException("当前播放队列为空,请先设置播放队列.");
        }
        int query = queryAudio(bean);
        if (query <= -1) {
            //没添加过此id的歌曲，添加且直播番放
            addCustomAudio(index, bean);
            setPlayIndex(index);
        } else {
            AudioBean currentBean = getNowPlaying();
            if (!currentBean.id.equals(bean.id)) {
                //添加过且不是当前播放，播，否则什么也不干
                setPlayIndex(query);
            }
        }
    }
    /**
     *  播放当前歌曲
     */
    public void play() {
        AudioBean bean = getPlaying(queueIndex);
        load(bean);
    }

    /**
     * 加载next index歌曲
     */
    public void next() {
        AudioBean bean = getNextPlaying();
        load(bean);
    }

    /**
     * 加载previous index歌曲
     */
    public void previous() {
        AudioBean bean = getPreviousPlaying();
        load(bean);
    }

    public AudioBean getNowPlaying() {
        return getPlaying(queueIndex);
    }


    public void resume() {
        mAudioPlayer.resume();
    }

    public void pause() {
        mAudioPlayer.pause();
    }

    public void release() {
        mAudioPlayer.release();
        EventBus.getDefault().unregister(this);
    }


    /**
     * 播放/暂停切换
     */
    public void playOrPause() {
        if (isStartState()) {
            pause();
        } else if (isPauseState()) {
            resume();
        }
    }


    public int getQueueIndex(){
        return queueIndex;
    }


    //插放完毕事件处理
    @Subscribe(threadMode = ThreadMode.MAIN) public void onAudioCompleteEvent(
            AudioCompleteEvent event) {
        next();
    }

    //播放出错事件处理
    @Subscribe(threadMode = ThreadMode.MAIN) public void onAudioErrorEvent(AudioErrorEvent event) {
        next();
    }
}
