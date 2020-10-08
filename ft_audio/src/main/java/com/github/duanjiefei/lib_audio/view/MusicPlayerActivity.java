package com.github.duanjiefei.lib_audio.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.transition.TransitionInflater;

import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;


import com.github.duanjiefei.lib_audio.GreenDaoHelper;
import com.github.duanjiefei.lib_audio.R;
import com.github.duanjiefei.lib_audio.core.AudioController;
import com.github.duanjiefei.lib_audio.core.CustomMediaPlayer;
import com.github.duanjiefei.lib_audio.event.AudioFavouriteEvent;
import com.github.duanjiefei.lib_audio.event.AudioLoadEvent;
import com.github.duanjiefei.lib_audio.event.AudioPauseEvent;
import com.github.duanjiefei.lib_audio.event.AudioPlayModeEvent;
import com.github.duanjiefei.lib_audio.event.AudioProgressEvent;
import com.github.duanjiefei.lib_audio.event.AudioStartEvent;
import com.github.duanjiefei.lib_audio.model.AudioBean;
import com.github.duanjiefei.lib_audio.utils.Utils;
import com.github.duanjiefei.lib_common_ui.BaseActivity;
import com.github.duanjiefei.lib_image_loader.ImageLoaderManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MusicPlayerActivity extends BaseActivity {

    private static final String TAG = "MusicPlayerActivity";
    // 根布局的背景
    private RelativeLayout rootView;
    private TextView mInfoView;
    private TextView mAuthorView;

    private AudioBean mAudioBean;
    private AudioController.PlayMode mPlayMode;
    private TextView mStartTimeView;
    private TextView mTotalTimeView;
    private SeekBar mProgressView;

    private Animator animator;
    private ImageView mPlayModeView;
    private ImageView mPreViousView;
    private ImageView mPlayView;
    private ImageView mNextView;
    private ImageView mFavouriteView;


    public static void start(Activity context){
        Log.d(TAG,"start ");
        Intent intent  = new Intent(context,MusicPlayerActivity.class);
        ActivityCompat.startActivity(context,intent, ActivityOptionsCompat.makeSceneTransitionAnimation(context).toBundle());
        //context.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate ");
        EventBus.getDefault().register(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.transition_bottom2top));
        }
        setContentView(R.layout.activity_music_service_layout);
        initData();
        initView();
    }

    private void initView() {
        Log.d(TAG,"Audio bean "+mAudioBean.album);
        Log.d(TAG,"Audio bean "+mAudioBean.albumInfo);
        Log.d(TAG,"Audio bean "+mAudioBean.albumPic);
        Log.d(TAG,"Audio bean "+mAudioBean.author);
        Log.d(TAG,"Audio bean "+mAudioBean.name);
        rootView = findViewById(R.id.root_layout);
        ImageLoaderManager.getInstance().displayImageForViewGroup(rootView, mAudioBean.albumPic);
        findViewById(R.id.back_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.title_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.share_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 弹出分享的对话框

            }
        });

        findViewById(R.id.show_list_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 弹出歌单列表
            }
        });

        mInfoView = findViewById(R.id.album_info);
        mInfoView.setText(mAudioBean.albumInfo);//专辑名字
        mInfoView.requestFocus();//跑马灯效果

        //专辑作者/歌手名字
        mAuthorView = findViewById(R.id.album_author);
        mAuthorView.setText(mAudioBean.author);

        mFavouriteView = findViewById(R.id.favourite_view);
        mFavouriteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 改变收藏的状态
            }
        });
        changeFavouriteStatus(false); //首次加载时改变收藏按钮的状态

        mStartTimeView = findViewById(R.id.start_time_view);
        mTotalTimeView = findViewById(R.id.total_time_view);
        mProgressView = findViewById(R.id.progress_view);
        mProgressView.setProgress(0);
        mProgressView.setEnabled(false);

        mPlayModeView = findViewById(R.id.play_mode_view);
        mPlayModeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mPlayMode){
                    case LOOP:
                        AudioController.getInstance().setPlayMode(AudioController.PlayMode.RANDOM);
                        break;
                    case RANDOM:
                        AudioController.getInstance().setPlayMode(AudioController.PlayMode.REPEAT);
                        break;
                    case REPEAT:
                        AudioController.getInstance().setPlayMode(AudioController.PlayMode.LOOP);
                        break;
                }
            }
        });

        updatePlayModeView();

        mPreViousView = findViewById(R.id.previous_view);
        mPreViousView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioController.getInstance().previous();
            }
        });
        mPlayView = findViewById(R.id.play_view);
        mPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioController.getInstance().playOrPause();
            }
        });
        mNextView = findViewById(R.id.next_view);
        mNextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioController.getInstance().next();
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        //更新notifacation为load状态
        mAudioBean = event.mAudioBean;
        ImageLoaderManager.getInstance().displayImageForViewGroup(rootView, mAudioBean.albumPic);
        //可以与初始化时的封装一个方法
        mInfoView.setText(mAudioBean.albumInfo);
        mAuthorView.setText(mAudioBean.author);
        changeFavouriteStatus(false);
        mProgressView.setProgress(0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        //更新activity为暂停状态
        showPauseView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        //更新activity为播放状态
        showPlayView();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPlayModeEvent(AudioPlayModeEvent event) {
        mPlayMode = event.mPlayMode;
        //更新播放模式
        updatePlayModeView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioFavouriteEvent(AudioFavouriteEvent event) {
        //更新activity收藏状态
        changeFavouriteStatus(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioProgessEvent(AudioProgressEvent event) {
        int totalTime = event.maxLength;
        int currentTime = event.progress;
        //更新时间
        mStartTimeView.setText(Utils.formatTime(currentTime));
        mTotalTimeView.setText(Utils.formatTime(totalTime));
        mProgressView.setProgress(currentTime);
        mProgressView.setMax(totalTime);
        if (event.mStatus == CustomMediaPlayer.Status.PAUSED) {
            showPauseView();
        } else {
            showPlayView();
        }
    }

    private void showPlayView() {
        mPlayView.setImageResource(R.drawable.audio_aj6);
    }

    private void showPauseView() {
        mPlayView.setImageResource(R.drawable.audio_aj7);
    }


    private void updatePlayModeView() {
        switch (mPlayMode){
            case LOOP:
                mPlayModeView.setImageResource(R.drawable.player_loop);
                break;
            case RANDOM:
                mPlayModeView.setImageResource(R.drawable.player_random);
                break;
            case REPEAT:
                mPlayModeView.setImageResource(R.drawable.player_once);
                break;
        }
    }

    private void changeFavouriteStatus(boolean animate) {
        if (GreenDaoHelper.selectFavourite(mAudioBean)!=null){
            mFavouriteView.setImageResource(R.drawable.audio_aeh);
        }else {
            mFavouriteView.setImageResource(R.drawable.audio_aef);
        }
        if (animate){
             if (animator != null)animator.cancel();

            PropertyValuesHolder animX = PropertyValuesHolder.ofFloat(View.SCALE_X.getName(),1.0F,1.2F,1.0F);
            PropertyValuesHolder animY = PropertyValuesHolder.ofFloat(View.SCALE_X.getName(),1.0F,1.2F,1.0F);
            animator = ObjectAnimator.ofPropertyValuesHolder(animX,animY);
            animator.setDuration(300);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.start();
        }
    }

    private void initData() {
        mAudioBean = AudioController.getInstance().getNowPlaying();
        mPlayMode =  AudioController.getInstance().getPlayMode();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
