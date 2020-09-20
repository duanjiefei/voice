package com.github.duanjiefei.voice.utils;

import com.github.duanjiefei.voice.model.user.User;

public class UserManager {

    private static UserManager instance = null;
    private User mUser;
    private UserManager(){}


    /**
     * 双检查机制单例模式
     * @return
     */
    public static UserManager getInstance(){
        if (instance == null){
            synchronized (UserManager.class){
                if (instance == null){
                    instance = new UserManager();
                }
            }
        }
        return instance;
    }


    public void saveUser(User user){
        mUser = user;
        saveLocal(user);
    }

    private void saveLocal(User user){

    }

    public User getUser(){
        return mUser;
    }

    public void removeUser(){
        mUser = null;
    }

    public boolean hasLogin(){
        return  getUser() == null ? false:true;
    }
}
