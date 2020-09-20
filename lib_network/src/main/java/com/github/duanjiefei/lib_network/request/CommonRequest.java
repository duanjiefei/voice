package com.github.duanjiefei.lib_network.request;

import android.util.Log;

import java.io.File;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CommonRequest {

    public static Request createPostRequest(String url, RequestParams bodyParams, RequestParams headParams){
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        if (bodyParams!=null){
            for (Map.Entry<String,String> entry : bodyParams.urlParams.entrySet()){
                bodyBuilder.add(entry.getKey(),entry.getValue());
            }
        }

        Headers.Builder headBuilder = new Headers.Builder();
        if (headParams!=null){
            for (Map.Entry<String,String> entry: headParams.urlParams.entrySet()){
                headBuilder.add(entry.getKey(),entry.getValue());
            }
        }

        FormBody formBody = bodyBuilder.build();
        Headers headers = headBuilder.build();

        Request request = new Request.Builder().url(url)
                .post(formBody)
                .headers(headers)
                .build();

        return request;
    }

    public static Request createPostRequest(String url,RequestParams bodyParams){
        return createPostRequest(url,bodyParams,null);
    }

    public static Request createGetRequest(String url,RequestParams params,RequestParams headParams){
        StringBuilder stringBuilder = new StringBuilder(url).append("?");
        if (params!=null){
            for (Map.Entry<String,String> entry : params.urlParams.entrySet()){
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }

        Headers.Builder headBuilder = new Headers.Builder();
        if (headParams!=null){
            for (Map.Entry<String,String> entry: headParams.urlParams.entrySet()){
                headBuilder.add(entry.getKey(),entry.getValue());
            }
        }

        Headers headers = headBuilder.build();
        Request request = new Request.Builder()
                .url(stringBuilder.substring(0,stringBuilder.length()-1))
                .headers(headers)
                .get()
                .build();

        return  request;
    }

    public static Request createGetRequest(String url,RequestParams params){
        return createGetRequest(url,params,null);
    }
    /**
     * 文件上传请求
     *
     * @return
     */
    private static final MediaType FILE_TYPE = MediaType.parse("application/octet-stream");

    public static Request createMultiPostRequest(String url, RequestParams params) {
        MultipartBody.Builder requestBody = new MultipartBody.Builder();
        requestBody.setType(MultipartBody.FORM);
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.fileParams.entrySet()) {
                if (entry.getValue() instanceof File) {
                    requestBody.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create(FILE_TYPE, (File) entry.getValue()));
                } else if (entry.getValue() instanceof String) {

                    requestBody.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create(null, (String) entry.getValue()));
                }
            }
        }
        return new Request.Builder().url(url).post(requestBody.build()).build();
    }

}
