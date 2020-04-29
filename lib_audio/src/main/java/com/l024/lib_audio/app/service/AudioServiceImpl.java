package com.l024.lib_audio.app.service;

import com.l024.lib_audio.mediaplayer.core.AudioController;

/**
 * @author wu_ming_zhi_bei
 * @date 2020/4/28 15:33
 * @Notes
 */
public class AudioServiceImpl implements AudioService {

    @Override
    public void pauseAudio() {
        AudioController.getInstance().pause();
    }

    @Override
    public void resumeAudio() {
        AudioController.getInstance().resume();
    }

}
