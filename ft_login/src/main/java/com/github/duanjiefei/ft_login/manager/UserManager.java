package com.github.duanjiefei.ft_login.manager;


import com.github.duanjiefei.lib_base.ft_login.modle.User;

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
