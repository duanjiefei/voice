package com.github.duanjiefei.lib_video;

import android.view.ViewGroup;

public class VideoAdContext implements VideoAdSlot.SDKSlotListener{

    private ViewGroup mParentView;

    private VideoAdSlot videoAdSlot;
    private  String videoUrl;


    private VideoContextInterface mListener;

    public VideoAdContext(ViewGroup mParentView, String videoUrl) {
        this.mParentView = mParentView;
        this.videoUrl = videoUrl;
        load();
    }

    private void load() {
        if (videoUrl != null){
            videoAdSlot  = new VideoAdSlot(videoUrl,this);
        }else {
            videoAdSlot = new VideoAdSlot(null,this);
            if (mListener != null){
                mListener.onVideoFailed();
            }
        }
    }

    public void setAdResultListener(VideoContextInterface listener){
        this.mListener = listener;
    }
    public void destroy(){
        videoAdSlot.destroy();
    }

    @Override
    public ViewGroup getAdParent() {
        return mParentView;
    }

    @Override
    public void onVideoLoadSuccess() {
        if (mListener != null){
            mListener.onVideoSuccess();
        }
    }

    @Override
    public void onVideoFailed() {
        if (mListener != null){
            mListener.onVideoFailed();
        }
    }

    @Override
    public void onVideoComplete() {
        if (mListener != null){
            mListener.onVideoComplete();
        }
    }
}
