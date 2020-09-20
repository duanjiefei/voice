package com.github.duanjiefei.lib_network.listener;

public class DisposeDataHandle {
    public DisposeDataListener mListener = null;
    public Class<?> mClass = null;
    public String mSource = null; //文件下载时存储的路径

    public DisposeDataHandle(DisposeDataListener listener){
        this.mListener = listener;
    }

    public DisposeDataHandle(DisposeDataListener listener,Class<?> mClass){
        this.mListener = listener;
        this.mClass = mClass;
    }


    public DisposeDataHandle(DisposeDataListener listener,String mSource){
        this.mListener = listener;
        this.mSource = mSource;
    }


}
