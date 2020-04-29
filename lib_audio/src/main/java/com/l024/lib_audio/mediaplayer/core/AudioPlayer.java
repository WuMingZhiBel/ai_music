package com.l024.lib_audio.mediaplayer.core;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.l024.lib_audio.app.AudioHelper;
import com.l024.lib_audio.mediaplayer.events.AudioCompleteEvent;
import com.l024.lib_audio.mediaplayer.events.AudioErrorEvent;
import com.l024.lib_audio.mediaplayer.events.AudioLoadEvent;
import com.l024.lib_audio.mediaplayer.events.AudioPauseEvent;
import com.l024.lib_audio.mediaplayer.events.AudioReleaseEvent;
import com.l024.lib_audio.mediaplayer.events.AudioStartEvent;
import com.l024.lib_audio.mediaplayer.model.AudioBean;

import org.greenrobot.eventbus.EventBus;

/**
 * @author wu_ming_zhi_bei
 * @date 2020/4/27 15:14
 * @Notes 播放各种类型事件
 * 1.播放音频
 * 2.对外发送
 */
public class AudioPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        AudioFocusManager.AudioFocusListener {
    private static final String TAG = "AudioPlayer";
    private static final int TIME_MSG = 0x01;
    public static final int H_PROGRESS = 1000;
    private static final int TIME_INVAL = 100;

    private OnMusicProgressListener mOnMusicProgressListener;
    //是否因为音频失败焦点停止播放
    private boolean isPauseByFocusLossTransient = false;

    //负责音频的播放
    private CustomMediaPlayer mMediaPlayer;
    private WifiManager.WifiLock mWifiLock;
    //音频焦点监听器
    private AudioFocusManager mAudioFocusManager;
    //Looper.getMainLooper保证每次都在主线程
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case TIME_MSG:{
                    break;
                }
                case H_PROGRESS:{
                    if(mOnMusicProgressListener!=null){
                        //获取当前时长
                        int currentPosition = getCurrentPosition();
                        int duration = getDuration();
                        int pos = (int) (((float)currentPosition)/((float)duration)*100);
                        mOnMusicProgressListener.onProgress(currentPosition,duration,pos);
                        //每隔一秒执行
                        mHandler.sendEmptyMessageDelayed(H_PROGRESS,1000);
                    }
                    break;
                }
            }
        }
    };

    /**
     * 初始化
     */
    public AudioPlayer(){
        init();
    }

    /**
     * 初始化播放器
     */
    private void init() {
        mMediaPlayer = new CustomMediaPlayer();
        //电量低也播放
        mMediaPlayer.setWakeMode(AudioHelper.getContext(), PowerManager.PARTIAL_WAKE_LOCK);
        // 指定流媒体类型
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //当装载流媒体完毕的时候回调。
        mMediaPlayer.setOnPreparedListener(this);
        //网络流媒体的缓冲变化时回调 **
        mMediaPlayer.setOnBufferingUpdateListener(this);
        //发生错误时回调 **
        mMediaPlayer.setOnErrorListener(this);
        //获取wifilock 使用wifi锁
        mWifiLock = ((WifiManager) AudioHelper.getContext()
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
        mAudioFocusManager = new AudioFocusManager(AudioHelper.getContext(),this);
    }

    /**
     *  对外提供的加载音频
     */
    public void load(AudioBean audioBean){
        System.out.println("开始加载--"+audioBean.mUrl);
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(audioBean.mUrl);
            //异步加载
            mMediaPlayer.prepareAsync();
            //对外发送load事件
            EventBus.getDefault().post(new AudioLoadEvent(audioBean));
        }catch (Exception e){
            //对外发送error事件
            EventBus.getDefault().post(new AudioErrorEvent());
        }
    }

    /**
     * 内部播放音乐
     */
    private void start(){
        //判断是音频否有焦点
        if(!mAudioFocusManager.requestAudioFocus()){
            Toast.makeText(AudioHelper.getContext(), "有其他程序在占用音频", Toast.LENGTH_SHORT).show();
        }else{
            //播放音乐
            mMediaPlayer.start();
            //wifi锁
            mWifiLock.acquire();
            //对方发送start事件
            EventBus.getDefault().post(new AudioStartEvent());
            //开始播放
            mHandler.sendEmptyMessage(H_PROGRESS);
        }
    }

    /**
     * 暂停
     */
    public void  pause(){
        //播放状态
        if(getStatus()==CustomMediaPlayer.Status.STARTED){
            mMediaPlayer.pause();
            //如果wifi锁占用。释放掉
            if(mWifiLock.isHeld()){
                mWifiLock.release();
            }
            //释放音频焦点
            if(mAudioFocusManager!=null){
                mAudioFocusManager.abandonAudioFocus();
            }
            //发送暂停事件
            EventBus.getDefault().post(new AudioPauseEvent());
            //移除
            mHandler.removeMessages(H_PROGRESS);
        }
    }

    /**
     * 获取当前状态
     * @return
     */
    public CustomMediaPlayer.Status getStatus(){
        if(mMediaPlayer!=null){
            return mMediaPlayer.getStatus();
        }
        return CustomMediaPlayer.Status.STOPPTED;
    }
    /**
     * 恢复
     */
    public void  resume(){
        if(getStatus()==CustomMediaPlayer.Status.PAUSED){
            //开始播放
            start();
        }
    }

    /**
     * 释放资源
     */
    public void release(){
        if (mMediaPlayer!=null){
            mMediaPlayer.release();
            mMediaPlayer = null;
            if(mAudioFocusManager!=null){
                mAudioFocusManager.abandonAudioFocus();
            }
            if(mWifiLock.isHeld()){
                mWifiLock.release();
            }
            mWifiLock = null;
            mAudioFocusManager = null;
            //发送release事件
            EventBus.getDefault().post(new AudioReleaseEvent());
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //缓存进度 视频播放的时候缓存
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //播放完毕回调
        EventBus.getDefault().post(new AudioCompleteEvent());
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //播放错误 返回true就不会回调onCompletion
        EventBus.getDefault().post(new AudioErrorEvent());
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //加载资源准备播放
        start();
    }

    //再次获取音频焦点
    @Override
    public void audioFocusGrant() {
       //设置音量
        setVolumn(1.0f,1.0f);
        //判断是否因为音频失去焦点停止播放
        if(isPauseByFocusLossTransient){
            //回复播放
            resume();
        }
        isPauseByFocusLossTransient = false;
    }

    @Override
    public void audioFocusLoss() {
        //永久失去焦点
        pause();
    }

    @Override
    public void audioFocusLossTransient() {
        //短暂性失去焦点 比如接电话
        pause();
        isPauseByFocusLossTransient = true;
    }

    @Override
    public void audioFocusLossDuck() {
        //瞬间失去焦点 比如有声音的通知 短信
        //减低声音
        setVolumn(0.5f,0.5f);
    }

    //设置音量 左声道和右声道
    private void setVolumn(float leftV, float rightV) {
        if(mMediaPlayer!=null){
            mMediaPlayer.setVolume(leftV,rightV);
        }
    }

    /**
     * 获取播放总时长
     */
    public int getDuration(){
        return mMediaPlayer.getDuration();
    }

    /**
     * 获取当前位置
     */
    public int getCurrentPosition(){
        return mMediaPlayer.getCurrentPosition();
    }

    /**
     * 监听播放进度
     */
    public void setOnProgressListener(OnMusicProgressListener listener){
        //开始发送进度
        mHandler.sendEmptyMessage(H_PROGRESS);
        mOnMusicProgressListener = listener;
    }

    //播放进度对外接口
    public interface OnMusicProgressListener{
        void onProgress(int currentPosition,int duration,int pos);
    }


}
