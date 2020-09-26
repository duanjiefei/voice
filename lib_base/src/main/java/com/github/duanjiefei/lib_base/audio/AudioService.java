package com.github.duanjiefei.lib_base.audio;

import android.app.Activity;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.github.duanjiefei.lib_base.audio.model.CommonAudioBean;

import java.util.ArrayList;

public interface AudioService extends IProvider {
    void pauseAudio();
    void resumeAudio();
    void addAudio(Activity activity, CommonAudioBean audioBean);

    void startMusicService(ArrayList<CommonAudioBean> audioBeans);
}
