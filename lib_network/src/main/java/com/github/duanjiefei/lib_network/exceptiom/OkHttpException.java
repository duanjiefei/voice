package com.github.duanjiefei.lib_network.exceptiom;

public class OkHttpException  extends Exception{
    private int eCode;
    private Object eMsg;

    public OkHttpException(int eCode,Object eMsg){
        this.eCode = eCode;
        this.eMsg = eMsg;
    }

    public int getCode() {
        return eCode;
    }

    public Object getMsg() {
        return eMsg;
    }
}
