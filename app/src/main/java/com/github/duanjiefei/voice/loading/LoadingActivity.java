package com.github.duanjiefei.voice.loading;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.github.duanjiefei.lib_common_ui.BaseActivity;
import com.github.duanjiefei.lib_pullalive.AliveJobService;
import com.github.duanjiefei.voice.R;

import com.github.duanjiefei.voice.home.HomeActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LoadingActivity  extends BaseActivity {


   @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent(LoadingActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        avoidLauncherAgain();
        setContentView(R.layout.activity_loading_layout);
        pullAliveService();
//        if (hasPermission(Constant.WRITE_READ_EXTERNAL_PERMISSION)) {
//            doSDCardPermission();
//        } else {
//            requestPermission(Constant.WRITE_READ_EXTERNAL_CODE, Constant.WRITE_READ_EXTERNAL_PERMISSION);
//        }
        doSDCardPermission();
    }

    private void pullAliveService() {
        AliveJobService.start(this);
    }

    //避免从桌面启动程序后，实例化多个入口activity
    private void avoidLauncherAgain() {
        if (!this.isTaskRoot()){
            Intent intent = getIntent();
            if (intent != null){
                String action = intent.getAction();

                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER)&& Intent.ACTION_MAIN.equals(action)){
                    finish();
                }
            }
        }
    }

    @Override
    public void doSDCardPermission() {
        super.doSDCardPermission();
        handler.sendEmptyMessageDelayed(0,3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
