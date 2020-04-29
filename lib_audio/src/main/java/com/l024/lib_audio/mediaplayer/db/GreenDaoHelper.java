package com.l024.lib_audio.mediaplayer.db;

import android.database.sqlite.SQLiteDatabase;

import com.l024.lib_audio.app.AudioHelper;
import com.l024.lib_audio.mediaplayer.model.AudioBean;
import com.l024.lib_audio.mediaplayer.model.Favourite;

/**
 * @author wu_ming_zhi_bei
 * @date 2020/4/28 16:16
 * @Notes GreenDao数据库帮助类
 */
public class GreenDaoHelper {
    private static final String DB_NAME = "music_db";
    //数据库帮助类
    private static DaoMaster.DevOpenHelper mHelper;
    //创建好的数据库
    private static SQLiteDatabase mDb;
    //管理数据库
    private static DaoMaster mDaoMaster;
    //管理表
    private static DaoSession mDaoSession;
    /**
     * 初始化GreenDao
     */
    public static void initDatabase(){
        mHelper = new DaoMaster.DevOpenHelper(AudioHelper.getContext(),DB_NAME);
        mDb = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(mDb);
        mDaoSession = mDaoMaster.newSession();
    }

    /**
     * 添加收藏
     * @param audioBean
     */
    public static void addFavourite(AudioBean audioBean){
        FavouriteDao favouriteDao = mDaoSession.getFavouriteDao();
        Favourite favourite = new Favourite();
        favourite.setAudioId(audioBean.id);
        favourite.setAudioBean(audioBean);
        //如果有更新 没有插入
        favouriteDao.insertOrReplace(favourite);
    }

    /**
     * 移除收藏
     */
    public static void removeFavourite(AudioBean audioBean){
        FavouriteDao dao = mDaoSession.getFavouriteDao();
        //查找
        Favourite favourite = selectFavourite(audioBean);
        if(favourite!=null){
            //删除
            dao.delete(favourite);
        }
    }

    /**
     * 查询收藏
     */
    public static Favourite selectFavourite(AudioBean audioBean){
        FavouriteDao dao = mDaoSession.getFavouriteDao();
        //查找
        Favourite favourite = dao.queryBuilder().where(FavouriteDao.Properties.AudioId.eq(audioBean.getId())).unique();
        return favourite;
    }
}
