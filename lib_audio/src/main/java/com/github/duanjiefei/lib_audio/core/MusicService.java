package com.github.duanjiefei.lib_audio.core;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.github.duanjiefei.lib_audio.app.AudioHelper;
import com.github.duanjiefei.lib_audio.event.AudioFavouriteEvent;
import com.github.duanjiefei.lib_audio.event.AudioLoadEvent;
import com.github.duanjiefei.lib_audio.event.AudioPauseEvent;
import com.github.duanjiefei.lib_audio.event.AudioReleaseEvent;
import com.github.duanjiefei.lib_audio.event.AudioStartEvent;
import com.github.duanjiefei.lib_audio.model.AudioBean;
import com.github.duanjiefei.lib_audio.view.NotificationHelper;

import androidx.annotation.Nullable;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class MusicService extends Service implements NotificationHelper.NotificationHelperListener {


    private static final String TAG = "MusicService";
    private static final String ACTION_START = "ACTION_START";
    private static final String DATA_AUDIOS = "DATA_AUDIOS";
    private NotificationReceiver notificationReceiver;
    private ArrayList<AudioBean> list;

    public static void startMusicService(ArrayList<AudioBean> list){
        Intent intent = new Intent(AudioHelper.getContext(),MusicService.class);
        intent.setAction(ACTION_START);
        intent.putExtra(DATA_AUDIOS,list);
        AudioHelper.getContext().startService(intent);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        EventBus.getDefault().register(this);
        registerBroadcastReceiver();
    }

    private void registerBroadcastReceiver() {
        if (notificationReceiver == null){
            notificationReceiver = new NotificationReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(NotificationReceiver.ACTION_STATUS_BAR);
            registerReceiver(notificationReceiver,filter);
        }
    }

    private void unRegisterBroadcastReceiver(){
        if (notificationReceiver!=null){
            unregisterReceiver(notificationReceiver);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        if (intent!=null){
            list = (ArrayList<AudioBean>) intent.getSerializableExtra(DATA_AUDIOS);
            if (ACTION_START.equals(intent.getAction())){
                playMusic();
                NotificationHelper.getInstance().init(this);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

   


    private void playMusic() {
        AudioController.getInstance().setQueue(list);
        AudioController.getInstance().play();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        EventBus.getDefault().unregister(this);
        unRegisterBroadcastReceiver();
    }

    //接收到播放事件时  弹出通知，并更新状态
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        //更新notifacation为load状态
        NotificationHelper.getInstance().showLoadStatus(event.mAudioBean);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        //更新notifacation为暂停状态
        NotificationHelper.getInstance().showPauseStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        //更新notifacation为播放状态
        NotificationHelper.getInstance().showPlayStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioFavouriteEvent(AudioFavouriteEvent event) {
        //更新notifacation收藏状态
        NotificationHelper.getInstance().changeFavouriteStatus(event.isFavourite);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioReleaseEvent(AudioReleaseEvent event) {
        //移除notifacation
    }


    @Override
    public void onNotificationInit() {
        startForeground(NotificationHelper.NOTIFICATION_ID,NotificationHelper.getInstance().getNotification());
    }

    public static class  NotificationReceiver extends BroadcastReceiver{

        public static final String ACTION_STATUS_BAR = AudioHelper.getContext().getPackageName()+".NOTIFICATION_ACTIONS";
        public static final  String EXTRA = "extra";
        public static final String EXTRA_PLAY = "play_pause";
        public static final String EXTRA_NEXT = "play_next";
        public static  final  String EXTRA_PRE = "play_pre";
        public static final String EXTRA_FAV = "play_fav";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || TextUtils.isEmpty(intent.getAction())){
                return;
            }

            String extraString = intent.getStringExtra(EXTRA);
            switch (extraString){
                case EXTRA_PLAY:
                    AudioController.getInstance().playOrPause();
                    break;
                case EXTRA_NEXT:
                    AudioController.getInstance().next();
                    break;
                case EXTRA_PRE:
                    AudioController.getInstance().previous();
                    break;
                case EXTRA_FAV:
                    break;
            }
        }
    }
}
