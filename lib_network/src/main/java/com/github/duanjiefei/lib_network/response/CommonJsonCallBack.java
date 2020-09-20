package com.github.duanjiefei.lib_network.response;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.github.duanjiefei.lib_network.exceptiom.OkHttpException;
import com.github.duanjiefei.lib_network.listener.DisposeDataHandle;
import com.github.duanjiefei.lib_network.listener.DisposeDataListener;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CommonJsonCallBack implements Callback {

    private final static String  TAG = "CommonJsonCallBack";

    protected final int NETWORK_ERROR = -1; // the network relative error
    protected final int JSON_ERROR = -2; // the JSON relative error
    protected final int OTHER_ERROR = -3; // the unknow error


    protected final String ERROR_MSG = "eMsg";
    protected final String EMPTY_MSG = "";
    protected final String RESULT_CODE = "ecode"; // 有返回则对于http请求来说是成功的，但还有可能是业务逻辑上的错误
    protected final int RESULT_CODE_VALUE = 0;

    private DisposeDataListener mListener;
    private Class<?> mClass;
    private Handler mDeliverHandler;

    public CommonJsonCallBack(DisposeDataHandle disposeDataHandle){
        mListener = disposeDataHandle.mListener;
        mClass = disposeDataHandle.mClass;
        mDeliverHandler = new Handler(Looper.getMainLooper());

    }
    @Override
    public void onFailure(Call call, final IOException e) {
        mDeliverHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(new OkHttpException(NETWORK_ERROR,e));
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final String result = response.body().string();
        mDeliverHandler.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(result);
            }
        });
    }

    private void handleResponse(Object result) {
        //如果返回结果为空，抛出消息为空的异常
        if (result==null|| result.toString().trim().equals("")){
            Log.d(TAG, "handleResponse: result == null");
            mListener.onFailure(new OkHttpException(NETWORK_ERROR,EMPTY_MSG));
        }
        //根据传入的Class 对象是否为空，确定返回个客户端是实体对象还是原始数据
        Log.d("djf", "handleResponse: "+result);
        try {
            JSONObject jsonObject = new JSONObject(String.valueOf(result));

            if (mClass == null){
                mListener.onSuccess(jsonObject);
            }else{
                Object object = new Gson().fromJson(String.valueOf(result),mClass);
                if (object!=null){
                    mListener.onSuccess(object);
                }else {
                    mListener.onFailure(new OkHttpException(JSON_ERROR,EMPTY_MSG));
                }
            }
        }catch (Exception e){
                mListener.onFailure(new OkHttpException(OTHER_ERROR,e.getMessage()));
                e.printStackTrace();
        }
    }
}
