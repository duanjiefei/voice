package com.github.duanjiefei.lib_network.response;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;


import com.github.duanjiefei.lib_network.exceptiom.OkHttpException;
import com.github.duanjiefei.lib_network.listener.DisposeDataHandle;
import com.github.duanjiefei.lib_network.listener.DisposeDownloadListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CommonFileCallBack implements Callback {

    protected final int NETWORK_ERROR = -1; // the network relative error
    protected final int IO_ERROR = -2; // the JSON relative error
    protected final String EMPTY_MSG = "";

    private DisposeDownloadListener downloadListener;
    private Handler mDeliverHandler;
    private String path;
    private int mProgress;
    private static final int PROGRESS_MSG = 0x01;


    public CommonFileCallBack(DisposeDataHandle disposeDataHandle) {
        this.downloadListener = (DisposeDownloadListener) disposeDataHandle.mListener;
        this.path = disposeDataHandle.mSource;
        mDeliverHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage( Message msg) {
                switch (msg.what){
                    case PROGRESS_MSG:
                        downloadListener.onProgress((int)msg.obj);
                        break;
                }
            }
        };
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        mDeliverHandler.post(new Runnable() {
            @Override
            public void run() {
                downloadListener.onFailure(new OkHttpException(NETWORK_ERROR,e));
            }
        });

    }

    //回调是在子线程
    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final File file = handleResponse(response);
        mDeliverHandler.post(new Runnable() {
           @Override
           public void run() {
               //主线程
                if (file != null){
                    downloadListener.onSuccess(file);
                }else {
                    downloadListener.onFailure(new OkHttpException(IO_ERROR,EMPTY_MSG));
                }
           }
       });
    }

    //子线程
    private File handleResponse(Response response) {
        if (response == null) {
            return null;
        }
        byte[] buffer = new byte[2048];
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        File file = null;

        int length;
        int currentLength = 0;
        double sumLength;

        try{
            checkLockFilePath(path);
            file = new File(path);
            // 文件的写入输出流
            outputStream = new FileOutputStream(file);


            //从回调回来的数据获取输入流
            inputStream = response.body().byteStream();
            sumLength = response.body().contentLength();

            while ((length = inputStream.read(buffer))!= -1){
                outputStream.write(buffer,0,length);
                currentLength += length;
                mProgress = (int) (currentLength/sumLength*100);
                //将进度返回到主线程
                mDeliverHandler.obtainMessage(PROGRESS_MSG,mProgress).sendToTarget();
            }
        }catch (Exception e){
            downloadListener.onFailure(new OkHttpException(IO_ERROR,e.getMessage()));
        }finally {
            if (inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return  file;
    }

    private void checkLockFilePath(String localPath) {
        File path = new File(localPath.substring(0,localPath.lastIndexOf("/")+1));
        File file = new File(localPath);

        if (!path.exists()){
            path.mkdirs();
        }

        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
