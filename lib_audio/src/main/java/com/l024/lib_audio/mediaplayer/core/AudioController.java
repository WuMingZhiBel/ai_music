package com.l024.lib_audio.mediaplayer.core;

import com.l024.lib_audio.mediaplayer.db.GreenDaoHelper;
import com.l024.lib_audio.mediaplayer.events.AudioFavouriteEvent;
import com.l024.lib_audio.mediaplayer.exception.AudioQueueEmptyException;
import com.l024.lib_audio.mediaplayer.model.AudioBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author wu_ming_zhi_bei
 * @date 2020/4/28 11:20
 * @Notes 控制音频播放类
 */
public class AudioController {

    //播放器
    private AudioPlayer mAudioPlayer;
    private ArrayList<AudioBean> mQueue;//歌曲队列
    private int mQueueIndex;//播放索引
    private PlayMode mPlayMode;//播放类型

    /**
     * 播放方式
     */
    public enum PlayMode{
        /**
         * 列表循环
         */
        LOOP,
        /**
         * 随机
         */
        RANDOM,
        /**
         * 单曲循环
         */
        REPEAT
    }

    /**
     * 初始化
     */
    private AudioController(){
        mAudioPlayer = new AudioPlayer();
        mQueue = new ArrayList<>();
        mQueueIndex = 0;
        mPlayMode = PlayMode.LOOP;
    }

    private static AudioController audioController;
    //单例模式
    public static AudioController getInstance() {
        if(audioController==null){
            synchronized (AudioController.class){
                if(audioController==null){
                    audioController =new AudioController();
                }
            }
        }
        return audioController;
    }


    /**
     * 获取歌曲队列
     */
    public ArrayList<AudioBean> getQueus(){
        return mQueue == null?new ArrayList<AudioBean>():mQueue;
    }

    /**
     * 添加队列
     */
    public void setQueue(ArrayList<AudioBean> queue){
        this.setQueue(queue,0);
    }

    /**
     * 获取播放对列
     * @return
     */
    public ArrayList<AudioBean> getQueue() {
        return mQueue == null ? new ArrayList<AudioBean>() : mQueue;
    }

    /**
     * 添加一首歌曲
     */
    public void addAudio(AudioBean audioBean){
        this.addAudio(0,audioBean);
    }
    public void addAudio(int index,AudioBean audioBean){
        if(mQueue==null){
            throw new AudioQueueEmptyException("当前播放列表为空");
        }
        //判断队列中是否有这首歌
        int query = queryAudio(audioBean);
        //没有添加过
        if(query <= -1){
            //添加并且设置当前播放
            addCustomAudio(index,audioBean);
            setPlayIndex(index);
        }else{
            //添加过,判断当前这首歌是否是当前播放的
            AudioBean bean = getNowPlaying();
            if(bean.getId().equals(audioBean.getId())){
                //说明是已经添加过并且在播放中
            }else{
                //不是当前播放，就要播放
                setPlayIndex(query);
            }
        }
    }

    /**
     * 添加歌曲到队列
     */
    private void addCustomAudio(int index, AudioBean audioBean) {
        if (mQueue == null) {
            throw new AudioQueueEmptyException("当前播放队列为空,请先设置播放队列.");
        }
        mQueue.add(index, audioBean);
    }

    /**
     * 判断队列中是否有这首歌
     * @param audioBean
     * @return
     */
    private int queryAudio(AudioBean audioBean) {
        return mQueue.indexOf(audioBean);
    }

    /**
     * 添加队列和索引 播放的歌曲
     */
    public void setQueue(ArrayList<AudioBean> queue,int index){
        mQueue.addAll(queue);
        mQueueIndex = index;
        System.out.println("当前索引:"+mQueueIndex);
    }

    /**
     * 设置播放模式
     */
    public PlayMode getPlayMode(){
        return mPlayMode;
    }
    public void setPlayMode(PlayMode playMode){
        mPlayMode = playMode;
    }

    /**
     * 设置播放索引
     */
    public void setPlayIndex(int index){
        if (mQueue == null){
            throw new AudioQueueEmptyException("当前播放队列为空,请先设置播放列表");
        }
        mQueueIndex = index;
        //设置好索引开始播放
        play();
    }

    /**
     * 获取当前播放的实体类
     * @param index
     * @return
     */
    private AudioBean getPlaying(int index) {
        if (mQueue != null && !mQueue.isEmpty() && index >= 0 && index < mQueue.size()) {
            return mQueue.get(index);
        } else {
            throw new AudioQueueEmptyException("获取当前播放实体类当前播放队列为空,请先设置播放队列."+index+"***mQueueIndex***"+mQueueIndex);
        }
    }

    /**
     * 播放下一首
     */
    public void next(){
        AudioBean bean = getNextPlaying();
        mAudioPlayer.load(bean);
    }

    /**
     * 播放上一首
     */
    public void previous(){
        AudioBean bean = getPreviousPlaying();
        mAudioPlayer.load(bean);
    }

    /**
     * 获取当前播放
     * @return
     */
    public AudioBean getNowPlaying() {
        return getPlaying(mQueueIndex);
    }

    /**
     * 获取下一首播放的实体类
     * @return
     */
    private AudioBean getNextPlaying() {
        switch (mPlayMode){
            case LOOP:
                //列表
                mQueueIndex = (mQueueIndex+1) % mQueue.size();
                System.out.println("mQueueIndex"+mQueueIndex+"mQueue.size()"+mQueue.size());
                break;
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                //随机
                break;
            case REPEAT:
                //单曲
                break;
        }
        return getPlaying(mQueueIndex);
    }

    /**
     * 获取上一首播放的实体类
     * @return
     */
    private AudioBean getPreviousPlaying() {
        switch (mPlayMode){
            case LOOP:
                //列表
                //mQueueIndex=mQueueIndex==0?( mQueue.size()-1):(mQueueIndex-=1);
                if(mQueueIndex==0){
                    mQueueIndex = mQueue.size()-1;
                }else{
                    mQueueIndex-=1;
                }
                break;
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                //随机
                break;
            case REPEAT:
                //单曲
                break;
        }
        return getPlaying(mQueueIndex);
    }

    /**
     * 播放
     */
    public void play() {
        AudioBean bean = getNowPlaying();
        mAudioPlayer.load(bean);
    }

    /**
     * 自动切换播放暂停
     */
    public void playOrPause(){
        if(isStartState()){
            pause();
        }else if(isPauseState()){
            resume();
        }
    }

    /**
     * 停止
     */
    public void pause(){
        mAudioPlayer.pause();
    }

    /**
     * 恢复播放
     */
    public void resume(){
        mAudioPlayer.resume();
    }

    /**
     * 释放资源
     */
    public void release(){
        mAudioPlayer.release();
        EventBus.getDefault().unregister(this);
    }

    public int getPlayIndex(){
        return mQueueIndex;
    }

    /**
     * 对外提供是否播放中状态
     */
    public boolean isStartState(){
        return CustomMediaPlayer.Status.STARTED == getStatus();
    }

    /**
     * 对外提供是否停止状态
     */
    public boolean isPauseState(){
        return CustomMediaPlayer.Status.PAUSED == getStatus();
    }

    /*
     * 获取播放器当前状态
     */
    private CustomMediaPlayer.Status getStatus() {
        return mAudioPlayer.getStatus();
    }

    /**
     * 添加/移除到收藏
     */
    public void changeFavourite() {
        if (null != GreenDaoHelper.selectFavourite(getNowPlaying())) {
            //已收藏，移除
            GreenDaoHelper.removeFavourite(getNowPlaying());
            EventBus.getDefault().post(new AudioFavouriteEvent(false));
        } else {
            //未收藏，添加收藏
            GreenDaoHelper.addFavourite(getNowPlaying());
            EventBus.getDefault().post(new AudioFavouriteEvent(true));
        }
    }

    /**
     * 获取进度
     */
    public void getDuration(AudioPlayer.OnMusicProgressListener onMusicProgressListener){
        //触发Hanlder
        mAudioPlayer.setOnProgressListener(onMusicProgressListener);
    }
}
