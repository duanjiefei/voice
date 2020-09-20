package com.github.duanjiefei.ft_login.api;

import com.github.duanjiefei.lib_base.ft_login.modle.User;
import com.github.duanjiefei.lib_network.CommonOkHttpClient;
import com.github.duanjiefei.lib_network.listener.DisposeDataHandle;
import com.github.duanjiefei.lib_network.listener.DisposeDataListener;
import com.github.duanjiefei.lib_network.request.CommonRequest;
import com.github.duanjiefei.lib_network.request.RequestParams;

public class RequestCenter {
    static class HttpConstants {
        //private static final String ROOT_URL = "http://imooc.com/api";
        private static final String ROOT_URL = "http://39.97.122.129";

        /**
         * 登陆接口
         */
        public static String LOGIN = ROOT_URL + "/module_voice/login_phone";
    }

    //根据参数发送所有post请求
    public static void getRequest(String url, RequestParams params, DisposeDataListener listener,
                                  Class<?> clazz) {
        CommonOkHttpClient.get(CommonRequest.
                createGetRequest(url, params), new DisposeDataHandle(listener, clazz));
    }
    /**
     * 用户登陆请求
     */
    public static void login(DisposeDataListener listener) {

        RequestParams params = new RequestParams();
        params.put("mb", "18734924592");
        params.put("pwd", "999999q");
        RequestCenter.getRequest(HttpConstants.LOGIN, params, listener, User.class);
    }
}

