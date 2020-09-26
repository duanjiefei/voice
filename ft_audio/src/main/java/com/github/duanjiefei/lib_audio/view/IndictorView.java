package com.github.duanjiefei.lib_audio.view;

import android.animation.ObjectAnimator;
import android.content.Context;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.duanjiefei.lib_audio.model.AudioBean;
import com.github.duanjiefei.lib_audio.core.AudioController;
import com.github.duanjiefei.lib_audio.R;
import com.github.duanjiefei.lib_audio.event.AudioLoadEvent;
import com.github.duanjiefei.lib_audio.event.AudioPauseEvent;
import com.github.duanjiefei.lib_audio.event.AudioStartEvent;
import com.github.duanjiefei.lib_audio.view.adapter.MusicPagerAdapter;





import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import androidx.viewpager.widget.ViewPager;

public class IndictorView extends RelativeLayout implements ViewPager.OnPageChangeListener {

    private Context mContext;
    private AudioBean mAudioBean;
    private ArrayList<AudioBean> mQueue;
    private ImageView imageView;
    private ViewPager viewPager;
    private MusicPagerAdapter musicPagerAdapter;

    public IndictorView(Context context) {
        this(context,null);
    }

    public IndictorView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public IndictorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        EventBus.getDefault().register(this);
        initData();
    }

    private void initData() {
        mAudioBean = AudioController.getInstance().getNowPlaying();
        mQueue = AudioController.getInstance().getQueue();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void initView() {
        View root = LayoutInflater.from(mContext).inflate(R.layout.indictor_view,this);
        imageView = root.findViewById(R.id.tip_view);
        viewPager = root.findViewById(R.id.view_pager);
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);//去掉滑动到边缘时的回弹效果
        musicPagerAdapter = new MusicPagerAdapter(mQueue,mContext,null);
        viewPager.setAdapter(musicPagerAdapter);

        showLoadingView(false);

        viewPager.addOnPageChangeListener(this);
    }

    private void showLoadingView(boolean isSmooth) {
        viewPager.setCurrentItem(mQueue.indexOf(mAudioBean),isSmooth);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        AudioController.getInstance().setPlayIndex(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state){
            case ViewPager.SCROLL_STATE_IDLE://滑动结束
                showPlayingView();
                break;
            case ViewPager.SCROLL_STATE_DRAGGING://正在被用户滑动
                showPauseView();
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        //更新viewpager为load状态
        mAudioBean = event.mAudioBean;
        showLoadingView(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        //更新activity为暂停状态
        showPauseView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        //更新activity为播放状态
        showPlayingView();
    }

    // 暂停旋转动画
    private void showPauseView() {
        ObjectAnimator animator = musicPagerAdapter.getAnim(viewPager.getCurrentItem());
        if (animator!=null) animator.pause();
    }

    //主要是用来更新中间旋转卡片的状态，音乐的播放状态在onPageSelected中处理
    private void showPlayingView() {
        ObjectAnimator animator = musicPagerAdapter.getAnim(viewPager.getCurrentItem());
        if (animator!=null){
            if (animator.isPaused()){
                animator.resume();
            }else {
                animator.start();
            }
        }
    }
}
