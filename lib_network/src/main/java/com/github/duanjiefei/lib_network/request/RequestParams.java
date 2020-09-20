package com.github.duanjiefei.lib_network.request;



import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestParams {

    public static ConcurrentHashMap<String,String> urlParams = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String,Object>  fileParams = new ConcurrentHashMap<>();

    public RequestParams(){
        this(null);
    }

    public RequestParams(Map<String,String> source){
        if (source!=null){
            for(Map.Entry<String,String> entry:source.entrySet()){
                put(entry.getKey(),entry.getValue());
            }
        }
    }


    public RequestParams(final String key, final String value){
        this(new HashMap<String, String>(){
            {
                put(key,value);
            }
        });
    }

    private void put(String key, String value) {
        if (key!=null&&value!=null){
            urlParams.put(key,value);
        }
    }

    public void put(String key, Object object){

        if (key != null) {
            fileParams.put(key, object);
        }
    }


}
