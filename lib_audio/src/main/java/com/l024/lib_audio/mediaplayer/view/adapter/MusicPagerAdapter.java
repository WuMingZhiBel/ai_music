package com.l024.lib_audio.mediaplayer.view.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.l024.lib_audio.R;
import com.l024.lib_audio.mediaplayer.core.AudioController;
import com.l024.lib_audio.mediaplayer.model.AudioBean;
import com.l024.lib_image_loader.app.ImageLoaderManager;

import java.util.ArrayList;

/**
 * @author wu_ming_zhi_bei
 * @date 2020/4/29 10:55
 * @Notes
 */
public class MusicPagerAdapter extends PagerAdapter {

    private Context mContext;
    /*
     * data
     */
    private ArrayList<AudioBean> mAudioBeans;
    private SparseArray<ObjectAnimator> mAnims = new SparseArray<>();
    private Callback mCallback;

    public MusicPagerAdapter(ArrayList<AudioBean> audioBeans, Context context, Callback callback) {
        mAudioBeans = audioBeans;
        mContext = context;
        mCallback = callback;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.indictor_item_view, null);
        ImageView imageView = rootView.findViewById(R.id.circle_view);
        container.addView(rootView);
        ImageLoaderManager.getInstance()
                .displayImageForCircie(imageView, mAudioBeans.get(position).albumPic);
        //只在无动化时创建
        mAnims.put(position,createAnim(rootView)); // 将动画缓存起来
        return rootView;
    }

    @Override
    public int getCount() {
        return mAudioBeans == null ? 0 : mAudioBeans.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /**
     * 创建动画
     * @param view
     * @return
     */
    private ObjectAnimator createAnim(View view) {
        view.setRotation(0);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ROTATION.getName(), 0, 360);
        animator.setDuration(10000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);
        if (AudioController.getInstance().isStartState()) {
            animator.start();
        }
        return animator;
    }

    /**
     * 获取动画
     * @param pos
     * @return
     */
    public ObjectAnimator getAnim(int pos) {
        return mAnims.get(pos);
    }

    /**
     * 与IndictorView回调,暂时没用到
     */
    public interface Callback {
        void onPlayStatus();

        void onPauseStatus();
    }
}