package com.github.duanjiefei.lib_video;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.github.duanjiefei.lib_base.audio.AudioService;

public class VideoAdSlot implements CustomVideoView.ADVideoPlayerListener {


    private Context context;
    private CustomVideoView customVideoView;
    private ViewGroup mParentView;

    private SDKSlotListener sdkSlotListener;
    private String adVideoUrl;

    @Autowired(name = "/audio/audio_service")
    protected AudioService mAudioService;

    public VideoAdSlot(String adVideoUrl,SDKSlotListener sdkSlotListener) {
        ARouter.getInstance().inject(this);
        this.adVideoUrl = adVideoUrl;
        this.sdkSlotListener =  sdkSlotListener;
        mParentView = sdkSlotListener.getAdParent();
        context = mParentView.getContext();
        initVideoView();
    }

    private void initVideoView() {
        customVideoView = new CustomVideoView(context);
        if (adVideoUrl != null){
            customVideoView.setDataSource(adVideoUrl);
            customVideoView.setListener(this);
        }
        RelativeLayout backGroundView = new RelativeLayout(context);
        backGroundView.setBackgroundColor(context.getResources().getColor(android.R.color.black));
        backGroundView.setLayoutParams(customVideoView.getLayoutParams());
        mParentView.addView(backGroundView);
        mParentView.addView(customVideoView);
    }

    public void destroy(){
        customVideoView.destroy();
        customVideoView = null;
        context = null;
        adVideoUrl = null;
    }

    @Override
    public void onBufferUpdate(int time) {

    }

    @Override
    public void onClickFullScreenBtn() {
        Bundle bundle = Utils.getViewProperty(mParentView);
        mParentView.removeView(customVideoView);

        VideoFullDialog dialog = new VideoFullDialog(context,customVideoView,adVideoUrl,customVideoView.getPosition());
        dialog.setListener(new VideoFullDialog.FullToSmallListener() {
            @Override
            public void getCurrentPlayPosition(int position) {
                backToSmallMode(position);
            }

            @Override
            public void playComplete() {
                bigPlayComplete();
            }
        });

        dialog.setViewBundle(bundle);
        dialog.setSlotListener(sdkSlotListener);
        dialog.show();

        //TODO
        mAudioService.pauseAudio();
    }

    private void bigPlayComplete() {
        if(customVideoView.getParent() == null){
            mParentView.addView(customVideoView);
        }
        customVideoView.setTranslationY(0);
        customVideoView.isShowFullBtn(true);
        customVideoView.mute(true);
        customVideoView.setListener(this);
        customVideoView.seekAndPause(0);
    }

    private void backToSmallMode(int position) {
        if(customVideoView.getParent() == null){
            mParentView.addView(customVideoView);
        }
        customVideoView.setTranslationY(0);
        customVideoView.isShowFullBtn(true);
        customVideoView.mute(true);
        customVideoView.setListener(this);
        customVideoView.seekAndResume(position);

        //TODO
        mAudioService.resumeAudio();
    }

    @Override
    public void onClickVideo() {

    }

    @Override
    public void onClickBackBtn() {

    }

    @Override
    public void onClickPlay() {

    }

    @Override
    public void onAdVideoLoadSuccess() {
        if (sdkSlotListener !=null){
            sdkSlotListener.onVideoLoadSuccess();
        }
    }

    @Override
    public void onAdVideoLoadFailed() {
        if (sdkSlotListener!=null){
            sdkSlotListener.onVideoFailed();
        }
    }

    @Override
    public void onAdVideoLoadComplete() {
        if (sdkSlotListener !=null){
            sdkSlotListener.onVideoComplete();
        }
        customVideoView.setIsRealPause(true);
    }


    //传递消息到appcontext层
    public interface SDKSlotListener {

        ViewGroup getAdParent();

        void onVideoLoadSuccess();

        void onVideoFailed();

        void onVideoComplete();
    }
}
