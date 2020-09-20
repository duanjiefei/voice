package com.github.duanjiefei.voice.login;

import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import com.github.duanjiefei.lib_common_ui.BaseActivity;
import com.github.duanjiefei.lib_network.listener.DisposeDataListener;
import com.github.duanjiefei.lib_pullalive.AliveJobService;
import com.github.duanjiefei.voice.R;
import com.github.duanjiefei.voice.api.RequestCenter;
import com.github.duanjiefei.voice.model.user.User;
import com.github.duanjiefei.voice.utils.UserManager;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.Nullable;

public class LoginActivity extends BaseActivity implements DisposeDataListener{

    private static final String TAG = LoginActivity.class.getSimpleName();

    public static  void start(Context context){
        Intent intent = new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        findViewById(R.id.login_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestCenter.login(LoginActivity.this);
            }
        });
        AliveJobService.start(this);
    }

    @Override
    public void onSuccess(Object responseObj) {
        Log.d(TAG, "onSuccess: ");
        User user = (User) responseObj;
        UserManager.getInstance().saveUser(user);
        EventBus.getDefault().post(new LoginEvent());
        finish();
    }

    @Override
    public void onFailure(Object reasonObj) {
        Log.d(TAG, "onFailure: ");
        //finish();
    }
}
