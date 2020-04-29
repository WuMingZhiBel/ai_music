package com.l024.lib_image_loader.app;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.l024.lib_image_loader.R;
import com.l024.lib_image_loader.image.Utils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author wu_ming_zhi_bei
 * @date 2020/4/26 17:37
 * @Notes 图片加载类
 */
public class ImageLoaderManager {
    private static ImageLoaderManager imageLoaderManager;
    private ImageLoaderManager(){

    }
    public static ImageLoaderManager getInstance(){
        if(imageLoaderManager==null){
            synchronized (ImageLoaderManager.class){
                if(imageLoaderManager==null){
                    imageLoaderManager = new ImageLoaderManager();
                }
            }
        }
        return imageLoaderManager;
    }

    /**
     * 一些图片设置
     * @return
     */
    private RequestOptions initCommonRequestOption(){
        RequestOptions options = new RequestOptions();
        options.placeholder(R.mipmap.b4y)
                .error(R.mipmap.b4y)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .priority(Priority.NORMAL);
        return options;
    }

    /**
     * 给View加载图片
     */
    public void displayImageForView(ImageView imageView,String url){
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .transition(BitmapTransitionOptions.withCrossFade())
                .into(imageView);
    }

    /**
     * 为ImageView加载圆形图片
     */
    public void displayImageForCircie(final ImageView imageView, String url){
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .transition(BitmapTransitionOptions.withCrossFade())
                .into(new BitmapImageViewTarget(imageView){
                    //将imageView包装成target
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        //设置为圆形
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory
                                .create(imageView.getResources(),resource);
                        drawable.setCircular(true);
                        imageView.setImageDrawable(drawable);
                    }
                });
    }

    /**
     * 为任意的ViewGroud设置背景并模糊处理
     */
    public void displayImageForViewGroup(final ViewGroup viewGroup,String url){
        Glide.with(viewGroup.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .into(new SimpleTarget<Bitmap>(){
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Observable.just(resource).map(new Function<Bitmap, Object>() {
                            @Override
                            public Drawable apply(Bitmap bitmap) throws Exception {
                                //将bitmap进行模糊处理在进行转换为drawable
                                Drawable drawable = new BitmapDrawable(Utils.doBlur(bitmap,100,true));
                                return drawable;
                            }
                        }).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(Object o) throws Exception {
                                viewGroup.setBackground((Drawable)(o));
                            }
                        });
                    }
                });
    }

    /**
     *为Notification中的id控件加载图片
     * @param context
     * @param rv
     * @param id 要加载图片的控件id
     * @param notification
     * @param NOTIFICATION_ID
     * @param url
     */
    public void displayImageForNotification(Context context, RemoteViews rv,
                                            int id, Notification notification,
                                            int NOTIFICATION_ID,String url){
        NotificationTarget target = initNotificationTarget(context, rv, id, notification, NOTIFICATION_ID);
        this.displayImageFroTarget(context,target,url);
    }

    /**
     * 生成Notification
     * @param context
     * @param rv
     * @param id
     * @param notification
     * @param NOTIFICATION_ID
     * @return
     */
    private NotificationTarget initNotificationTarget(Context context, RemoteViews rv,
                                                      int id,Notification notification,
                                                      int NOTIFICATION_ID){
        NotificationTarget target = new NotificationTarget(context,id,rv,notification,NOTIFICATION_ID);
        return target;
    }
    /**
     * 为非View设置背景图
     */
    private void displayImageFroTarget(Context context, Target target,String url){
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .transition(BitmapTransitionOptions.withCrossFade())
                .fitCenter()
                .into(target);
    }
}
