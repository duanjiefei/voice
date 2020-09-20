package com.github.duanjiefei.lib_audio.event;

import com.github.duanjiefei.lib_audio.core.AudioController;

public class AudioPlayModeEvent {
    public AudioController.PlayMode mPlayMode;
    public AudioPlayModeEvent(AudioController.PlayMode mPlayMode) {
        this.mPlayMode = mPlayMode;
    }
}
