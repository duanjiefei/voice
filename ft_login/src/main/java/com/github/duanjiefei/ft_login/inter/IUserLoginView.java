package com.github.duanjiefei.ft_login.inter;

public interface IUserLoginView {
    String getUserName();

    String getPassword();

    void finishActivity();

    void showLoginFailedView();

    void showLoadingView();

    void hideLoadingView();
}
