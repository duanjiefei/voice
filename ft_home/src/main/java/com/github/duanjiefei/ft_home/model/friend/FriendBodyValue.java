package com.github.duanjiefei.ft_home.model.friend;


import com.github.duanjiefei.lib_base.BaseModel;
import com.github.duanjiefei.lib_base.audio.model.CommonAudioBean;

import java.util.ArrayList;

/**
 * @文件描述：朋友实体
 */
public class FriendBodyValue extends BaseModel {

  public int type;
  public String avatr;
  public String name;
  public String fans;
  public String text;
  public ArrayList<String> pics;
  public String videoUrl;
  public String zan;
  public String msg;
  public CommonAudioBean audioBean;
}
