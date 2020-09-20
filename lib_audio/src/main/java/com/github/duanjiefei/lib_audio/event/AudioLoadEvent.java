package com.github.duanjiefei.lib_audio.event;


import com.github.duanjiefei.lib_audio.model.AudioBean;

public class AudioLoadEvent {
  public AudioBean mAudioBean;

  public AudioLoadEvent(AudioBean audioBean) {
    this.mAudioBean = audioBean;
  }
}
