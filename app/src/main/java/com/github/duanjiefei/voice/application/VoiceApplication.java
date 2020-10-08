package com.github.duanjiefei.voice.application;


import com.alibaba.android.arouter.launcher.ARouter;
import com.github.duanjiefei.lib_audio.app.AudioHelper;
import com.qihoo360.replugin.RePluginApplication;

public class VoiceApplication extends RePluginApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        AudioHelper.init(this);
        ARouter.init(this);
    }
}
