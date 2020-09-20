package com.github.duanjiefei.lib_base.audio;

import com.alibaba.android.arouter.facade.template.IProvider;

public interface AudioService extends IProvider {
    void pauseAudio();
    void resumeAudio();
}
