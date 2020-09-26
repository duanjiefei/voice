package com.github.duanjiefei.lib_audio.app.service;

import android.app.Activity;
import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.github.duanjiefei.lib_audio.app.AudioHelper;
import com.github.duanjiefei.lib_audio.core.AudioController;
import com.github.duanjiefei.lib_base.audio.AudioService;
import com.github.duanjiefei.lib_base.audio.model.CommonAudioBean;

import java.util.ArrayList;

@Route(path = "/audio/audio_service")
public class AudioServiceImpl implements AudioService {
    @Override
    public void pauseAudio() {
        AudioController.getInstance().pause();
    }

    @Override
    public void resumeAudio() {
        AudioController.getInstance().resume();
    }

    @Override
    public void addAudio(Activity activity, CommonAudioBean audioBean) {
        AudioHelper.addAudio(activity,audioBean);
    }

    @Override
    public void startMusicService(ArrayList<CommonAudioBean> audioBeans) {
        AudioHelper.startMusicService(audioBeans);
    }


    @Override
    public void init(Context context) {

    }
}
