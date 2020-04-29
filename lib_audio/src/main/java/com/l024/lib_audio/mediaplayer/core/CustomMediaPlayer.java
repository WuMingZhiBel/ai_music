package com.l024.lib_audio.mediaplayer.core;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * @author wu_ming_zhi_bei
 * @date 2020/4/27 14:55
 * @Notes 自定义的带状态的MediaPlayer 修改系统的MediaPlayer
 */
public class CustomMediaPlayer extends MediaPlayer implements MediaPlayer.OnCompletionListener{
    //当前状态
    private Status mState;
    //状态
    public enum Status{
        IDEL,INITALIZED,STARTED,PAUSED,STOPPTED,COMPLETED
    }
    //监听
    private OnCompletionListener mCompletionListener;

    public CustomMediaPlayer(){
        super();
        mState = Status.IDEL;
        super.setOnCompletionListener(this);
    }

    /**
     * 播放监听完成时是在一首歌曲播放完之后会得到调用
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        mState = Status.COMPLETED;
    }

    /**
     *重置方法
     */
    @Override
    public void reset() {
        super.reset();
        mState = Status.IDEL;
    }

    /**
     *设置资源
     */
    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
        super.setDataSource(path);
        mState = Status.INITALIZED;
    }

    /**
     * 开始播放
     * @throws IllegalStateException
     */
    @Override
    public void start() throws IllegalStateException {
        super.start();
        mState = Status.STARTED;
    }

    /**
     * 暂停
     * @throws IllegalStateException
     */
    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        mState = Status.PAUSED;
    }

    /**
     * 停止
     * @throws IllegalStateException
     */
    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        mState = Status.STOPPTED;
    }

    /**
     * 获取当前状态
     * @return
     */
    public Status getStatus(){
        return mState;
    }

    /**
     * 是否播放完成
     */
    public boolean isComplete(){
        return mState == Status.COMPLETED ;
    }

    /**
     * 设置播放完毕监听
     */
    public void setCompleteListener(OnCompletionListener listener){
        this.mCompletionListener = listener;
    }

}
