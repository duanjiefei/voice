package com.github.duanjiefei.lib_base.ft_login.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.github.duanjiefei.lib_base.ft_login.modle.User;

public interface LoginService extends IProvider {

    public void init(Context context);
    User getUserInfo();
    void removeUser();
    boolean hasLogin();
    void login(Context context);
}
