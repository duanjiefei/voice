package com.github.duanjiefei.lib_base.ft_home.impl;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.github.duanjiefei.lib_base.ft_home.HomeService;


public class HomeImpl {

    private static HomeImpl mHomeImpl;
    @Autowired(name = "/ft_home/home_service")
    protected HomeService mHomeService;

    public static HomeImpl getInstance(){
        if (mHomeImpl ==  null){
            synchronized (HomeImpl.class){
                if (mHomeImpl == null){
                    mHomeImpl = new HomeImpl();
                }
            }
        }
        return mHomeImpl;
    }
    private HomeImpl(){
        ARouter.getInstance().inject(this);
    }

    public void startHomeActivity(Context context){
        mHomeService.startHomeActivity(context);
    }
}
