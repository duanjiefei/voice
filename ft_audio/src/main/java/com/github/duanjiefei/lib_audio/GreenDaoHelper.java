package com.github.duanjiefei.lib_audio;


import android.database.sqlite.SQLiteDatabase;

import com.github.duanjiefei.lib_audio.app.AudioHelper;
import com.github.duanjiefei.lib_audio.mediaplayer.db.DaoMaster;
import com.github.duanjiefei.lib_audio.mediaplayer.db.DaoSession;
import com.github.duanjiefei.lib_audio.mediaplayer.db.FavouriteDao;
import com.github.duanjiefei.lib_audio.model.AudioBean;
import com.github.duanjiefei.lib_audio.model.Favourite;

public class GreenDaoHelper {

    private static final String DB_NAME = "music_db";
    private static DaoMaster.DevOpenHelper mHelper;
    private static SQLiteDatabase mDb;
    private static DaoMaster daoMaster;
    private static DaoSession mDaoSession;

    public static void initDatabase(){
        mHelper = new DaoMaster.DevOpenHelper(AudioHelper.getContext(),DB_NAME,null);
        mDb = mHelper.getWritableDatabase();
        daoMaster = new DaoMaster(mDb);
        mDaoSession = daoMaster.newSession();
    }

    public static void addFavourite(AudioBean bean){
        FavouriteDao favouriteDao = mDaoSession.getFavouriteDao();
        Favourite favourite = new Favourite();
        favourite.setAudioId(bean.id);
        favourite.setAudioBean(bean);
        favouriteDao.insertOrReplace(favourite);
    }

    public static void deleteFavourite(AudioBean bean){
        FavouriteDao favouriteDao = mDaoSession.getFavouriteDao();
        Favourite fa = favouriteDao.queryBuilder().where(FavouriteDao.Properties.AudioId.eq(bean.id)).unique();
        favouriteDao.delete(fa);
    }

    public static Favourite selectFavourite(AudioBean bean){
        FavouriteDao favouriteDao = mDaoSession.getFavouriteDao();
        Favourite fa = favouriteDao.queryBuilder().where(FavouriteDao.Properties.AudioId.eq(bean.id)).unique();
        return fa;
    }



}
