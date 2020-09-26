package com.github.duanjiefei.ft_home.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.github.duanjiefei.ft_home.view.home.HomeActivity;
import com.github.duanjiefei.lib_base.ft_home.HomeService;

@Route(path = "/ft_home/home_service")
public class HomeServiceImpl implements HomeService {
    @Override
    public void startHomeActivity(Context context) {
        HomeActivity.start(context);
    }

    @Override
    public void init(Context context) {
        Log.d(HomeServiceImpl.class.getSimpleName(), "init: ");
    }
}
