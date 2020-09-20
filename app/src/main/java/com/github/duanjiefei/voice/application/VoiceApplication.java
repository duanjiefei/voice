package com.github.duanjiefei.voice.application;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.github.duanjiefei.lib_audio.app.AudioHelper;

public class VoiceApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AudioHelper.init(this);
        ARouter.init(this);
    }
}
