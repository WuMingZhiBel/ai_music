package com.l024.lib_audio.app;

import android.app.Activity;
import android.content.Context;

import com.l024.lib_audio.app.service.MusicService;
import com.l024.lib_audio.mediaplayer.core.AudioController;
import com.l024.lib_audio.mediaplayer.db.GreenDaoHelper;
import com.l024.lib_audio.mediaplayer.model.AudioBean;

import java.util.ArrayList;

/**
 * @author wu_ming_zhi_bei
 * @date 2020/4/27 15:39
 * @Notes 外观模式 唯一与外界通信的帮助类
 */
public class AudioHelper {
    //SDK全局Context, 供子模块用
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
        //初始化本地数据库
        GreenDaoHelper.initDatabase();
    }

    //外部启动MusicService方法
    public static void startMusicService(ArrayList<AudioBean> audios) {
        MusicService.startMusicService(audios);

    }

    public static void addAudio(Activity activity, AudioBean bean) {
        AudioController.getInstance().addAudio(bean);
        //MusicPlayerActivity.start(activity);
    }

    public static void pauseAudio() {
        AudioController.getInstance().pause();
    }

    public static void resumeAudio() {
        AudioController.getInstance().resume();
    }

    public static Context getContext() {
        return mContext;
    }

    /**
     * 获取进度
     */

}
