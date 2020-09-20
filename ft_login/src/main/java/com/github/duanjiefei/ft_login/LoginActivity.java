package com.github.duanjiefei.ft_login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.github.duanjiefei.ft_login.inter.IUserLoginView;
import com.github.duanjiefei.ft_login.presenter.UserLoginPresenter;
import com.github.duanjiefei.lib_common_ui.BaseActivity;

import androidx.annotation.Nullable;

public class LoginActivity extends BaseActivity implements IUserLoginView {
    private UserLoginPresenter mLoginPresenter;

    public static void start(Context context) {
        Intent intent = new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        mLoginPresenter = new UserLoginPresenter(this);
        findViewById(R.id.login_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginPresenter.login(getUserName(),getPassword());
            }
        });
    }

    @Override
    public String getUserName() {
        return "18734924592";
    }

    @Override
    public String getPassword() {
        return "999999q";
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void showLoginFailedView() {

    }

    @Override
    public void showLoadingView() {

    }

    @Override
    public void hideLoadingView() {

    }
}
