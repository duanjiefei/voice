package com.github.duanjiefei.lib_audio.app;


import android.app.Activity;
import android.content.Context;

import com.github.duanjiefei.lib_audio.GreenDaoHelper;
import com.github.duanjiefei.lib_audio.core.AudioController;
import com.github.duanjiefei.lib_audio.core.MusicService;
import com.github.duanjiefei.lib_audio.model.AudioBean;
import com.github.duanjiefei.lib_audio.view.MusicPlayerActivity;

import java.util.ArrayList;

/**
 * Created by qndroid on 19/5/20.
 *
 * @function 唯一与外界通信的帮助类
 */
public final class AudioHelper {

  //SDK全局Context, 供子模块用
  private static Context mContext;

  public static void init(Context context) {
    mContext = context;
    //初始化本地数据库
    GreenDaoHelper.initDatabase();
  }
  public static Context getContext() {
    return mContext;
  }

  //外部启动MusicService方法
  public static void startMusicService(ArrayList<AudioBean> audios) {
    MusicService.startMusicService(audios);
  }

  public static void addAudio(Activity activity, AudioBean bean) {
    AudioController.getInstance().addAudio(bean);
    MusicPlayerActivity.start(activity);
  }

}
