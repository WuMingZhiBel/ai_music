package com.l024.mwzb_voice.application;

import android.app.Application;

import com.l024.lib_audio.app.AudioHelper;

/**
 * @author wu_ming_zhi_bei
 * @date 2020/4/28 14:57
 * @Notes APP
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //音频SDK初始化
        AudioHelper.init(this);
    }
}
