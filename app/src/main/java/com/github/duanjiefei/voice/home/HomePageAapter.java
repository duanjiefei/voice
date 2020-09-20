package com.github.duanjiefei.voice.home;



import com.github.duanjiefei.voice.model.CHANNEL;
import com.github.duanjiefei.voice.discovery.DiscoveryFragment;
import com.github.duanjiefei.voice.friend.FriendFragment;
import com.github.duanjiefei.voice.mine.MineFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class HomePageAapter extends FragmentPagerAdapter {

    private CHANNEL[] mList;
    public HomePageAapter(FragmentManager fm, CHANNEL[] data) {
        super(fm);
        mList = data;
    }

    @Override
    public Fragment getItem(int i) {
       int type = mList[i].getValue();
       switch (type){
           case  CHANNEL.MINE_ID:
               return MineFragment.newInstance();
           case  CHANNEL.DISCORY_ID:
               return  DiscoveryFragment.newInstance();
           case  CHANNEL.FRIEND_ID:
               return FriendFragment.newInstance();
       }
        return null;
    }

    @Override
    public int getCount() {
        return mList.length;
    }
}
