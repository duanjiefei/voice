package com.github.duanjiefei.lib_audio.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.widget.RemoteViews;

import com.github.duanjiefei.lib_audio.model.AudioBean;
import com.github.duanjiefei.lib_audio.core.AudioController;
import com.github.duanjiefei.lib_audio.app.AudioHelper;
import com.github.duanjiefei.lib_audio.core.MusicService;
import com.github.duanjiefei.lib_audio.R;
import com.github.duanjiefei.lib_image_loader.ImageLoaderManager;





public class NotificationHelper {

    public static final String CHANNEL_ID = "channel_id_audio";
    public static final String CHANNEL_NAME = "channel_name_audio";
    public static final int NOTIFICATION_ID = 0x111;


    private Notification notification;  // 通知
    private RemoteViews bigRemoteViews;  //展开的布局
    private RemoteViews smallRemoteViews; //缩小的布局

    private NotificationManager notificationManager;
    private NotificationHelperListener notificationHelperListener;

    private String packageName;
    private AudioBean audioBean;



    public static NotificationHelper getInstance(){
        return SingletonHolder.notificationHelper;
    }

    private static class SingletonHolder{
        private static NotificationHelper notificationHelper = new NotificationHelper();
    }

    public void init(NotificationHelperListener listener){
        notificationManager = (NotificationManager) AudioHelper.getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        packageName = AudioHelper.getContext().getPackageName();
        audioBean = AudioController.getInstance().getNowPlaying();
        initNotification();
        notificationHelperListener = listener;
        if (notificationHelperListener != null){
            notificationHelperListener.onNotificationInit();
        }
    }

    private void initNotification() {
        if (notification == null){
            initRemoteViews();

            Intent intent = new Intent(AudioHelper.getContext(), MusicPlayerActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(AudioHelper.getContext(),
                    0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            //8.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(false);
                channel.enableVibration(false);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(AudioHelper.getContext(),
                    CHANNEL_ID).setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setCustomBigContentView(bigRemoteViews)
                    .setContent(smallRemoteViews);

            notification = builder.build();
            showLoadStatus(audioBean);
        }
    }



    private void initRemoteViews() {
        int bigLayoutId = R.layout.notification_big_layout;
        bigRemoteViews = new RemoteViews(packageName,bigLayoutId);
        bigRemoteViews.setTextViewText(R.id.title_view,audioBean.name);
        bigRemoteViews.setTextViewText(R.id.tip_view,audioBean.album);
        bigRemoteViews.setImageViewResource(R.id.favourite_view,R.drawable.note_btn_love_white);

        int smallLayoutId = R.layout.notification_small_layout;
        smallRemoteViews  = new RemoteViews(packageName,smallLayoutId);
        smallRemoteViews.setTextViewText(R.id.title_view,audioBean.name);
        smallRemoteViews.setTextViewText(R.id.tip_view,audioBean.album);

        //点击播放按钮的广播
        Intent playIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        playIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_PLAY);

        PendingIntent playPendingIntent = PendingIntent.getBroadcast(AudioHelper.getContext(),
        1,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);//始终执行
        //分别为通知的小布局和大布局 添加PendingIntent;
        smallRemoteViews.setOnClickPendingIntent(R.id.play_view,playPendingIntent);
        smallRemoteViews.setImageViewResource(R.id.play_view,R.drawable.note_btn_play_white);
        bigRemoteViews.setOnClickPendingIntent(R.id.play_view,playPendingIntent);
        bigRemoteViews.setImageViewResource(R.id.play_view,R.drawable.note_btn_play_white);



        //点击上一首按钮广播
        Intent previousIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        previousIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_PRE);
        PendingIntent previousPendingIntent =
                PendingIntent.getBroadcast(AudioHelper.getContext(), 2, previousIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        bigRemoteViews.setOnClickPendingIntent(R.id.previous_view, previousPendingIntent);
        bigRemoteViews.setImageViewResource(R.id.previous_view, R.drawable.note_btn_pre_white);

        //点击下一首按钮广播
        Intent nextIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        nextIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_NEXT);
        PendingIntent nextPendingIntent =
                PendingIntent.getBroadcast(AudioHelper.getContext(), 3, nextIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        bigRemoteViews.setOnClickPendingIntent(R.id.next_view, nextPendingIntent);
        bigRemoteViews.setImageViewResource(R.id.next_view, R.drawable.note_btn_next_white);
        smallRemoteViews.setOnClickPendingIntent(R.id.next_view, nextPendingIntent);
        smallRemoteViews.setImageViewResource(R.id.next_view, R.drawable.note_btn_next_white);

        //点击收藏按钮广播
        Intent favouriteIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        favouriteIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_FAV);
        PendingIntent favouritePendingIntent =
                PendingIntent.getBroadcast(AudioHelper.getContext(), 4, favouriteIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        bigRemoteViews.setOnClickPendingIntent(R.id.favourite_view, favouritePendingIntent);
    }


    public void showLoadStatus(AudioBean bean) {
        audioBean = bean;
        if (bigRemoteViews != null) {
            bigRemoteViews.setImageViewResource(R.id.play_view, R.drawable.note_btn_pause_white);
            bigRemoteViews.setTextViewText(R.id.title_view, audioBean.name);
            bigRemoteViews.setTextViewText(R.id.tip_view, audioBean.album);
            ImageLoaderManager.getInstance()
                    .displayImageForNotfication(AudioHelper.getContext(),bigRemoteViews, R.id.image_view,notification,NOTIFICATION_ID,audioBean.albumPic);

            //更新收藏view
//            if (null != GreenDaoHelper.selectFavourite(mAudioBean)) {
//                mRemoteViews.setImageViewResource(R.id.favourite_view, R.mipmap.note_btn_loved);
////            } else {
////                mRemoteViews.setImageViewResource(R.id.favourite_view, R.mipmap.note_btn_love_white);
////            }

            //小布局也要更新
            smallRemoteViews.setImageViewResource(R.id.play_view, R.drawable.note_btn_pause_white);
            smallRemoteViews.setTextViewText(R.id.title_view, audioBean.name);
            smallRemoteViews.setTextViewText(R.id.tip_view, audioBean.album);
            ImageLoaderManager.getInstance()
                    .displayImageForNotfication(AudioHelper.getContext(), smallRemoteViews, R.id.image_view, notification, NOTIFICATION_ID, audioBean.albumPic);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    public void showPlayStatus() {
        if (bigRemoteViews != null) {
            bigRemoteViews.setImageViewResource(R.id.play_view, R.drawable.note_btn_pause_white);
            smallRemoteViews.setImageViewResource(R.id.play_view, R.drawable.note_btn_pause_white);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    public void showPauseStatus() {
        if (bigRemoteViews != null) {
            bigRemoteViews.setImageViewResource(R.id.play_view, R.drawable.note_btn_play_white);
            smallRemoteViews.setImageViewResource(R.id.play_view, R.drawable.note_btn_play_white);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    public void changeFavouriteStatus(boolean isFavourite) {
        if (bigRemoteViews != null) {
            bigRemoteViews.setImageViewResource(R.id.favourite_view,
                    isFavourite ? R.drawable.note_btn_loved : R.drawable.note_btn_love_white);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    public Notification getNotification(){
        return notification;
    }
    /**
     * 与音乐service的回调通信
     */
    public interface NotificationHelperListener {
        void onNotificationInit();
    }
}
