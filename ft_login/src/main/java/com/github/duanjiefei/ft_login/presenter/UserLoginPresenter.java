package com.github.duanjiefei.ft_login.presenter;

import com.github.duanjiefei.ft_login.api.MockData;
import com.github.duanjiefei.ft_login.api.RequestCenter;
import com.github.duanjiefei.lib_base.ft_login.event.LoginEvent;
import com.github.duanjiefei.ft_login.inter.IUserLoginPresenter;
import com.github.duanjiefei.ft_login.inter.IUserLoginView;
import com.github.duanjiefei.ft_login.manager.UserManager;
import com.github.duanjiefei.lib_base.ft_login.modle.User;
import com.github.duanjiefei.lib_network.listener.DisposeDataListener;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

public class UserLoginPresenter implements IUserLoginPresenter, DisposeDataListener {

    private IUserLoginView  mLoginView;

    public UserLoginPresenter(IUserLoginView mLoginView) {
        this.mLoginView = mLoginView;
    }

    @Override
    public void login(String userName, String passWord) {
        mLoginView.showLoadingView();
        RequestCenter.login(this);
    }

    @Override
    public void onSuccess(Object responseObj) {
        mLoginView.hideLoadingView();
        User user = (User) responseObj;
        //TODO
        UserManager.getInstance().saveUser(user);
        EventBus.getDefault().post(new LoginEvent());
        mLoginView.finishActivity();
    }

    @Override
    public void onFailure(Object reasonObj) {
        mLoginView.hideLoadingView();
        onSuccess(new Gson().fromJson(MockData.LOGIN_DATA,User.class));
        mLoginView.showLoginFailedView();
    }
}
