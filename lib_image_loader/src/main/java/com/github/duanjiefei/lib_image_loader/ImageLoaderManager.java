package com.github.duanjiefei.lib_image_loader;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;

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

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ImageLoaderManager {

    private ImageLoaderManager(){}

    private static class ImageHolder{
        private static ImageLoaderManager instance = new ImageLoaderManager();
    }

    public static ImageLoaderManager getInstance(){
        return ImageHolder.instance;
    }


    public void displayImageForView(ImageView imageView, String url){
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url) //加载图片的URL
                .apply(initCommonRequestOptions()) //设置初始化参数信息
                .transition(BitmapTransitionOptions.withCrossFade()) //设置加载效果
                .into(imageView);
    }

    /**
     * 为ImageView 设置圆形背景图片
     * @param imageView
     * @param url
     */
    public void displayImageForCircle(final ImageView imageView, String url){
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOptions())
                .transition(BitmapTransitionOptions.withCrossFade())
                .into(new BitmapImageViewTarget(imageView){
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable  drawable = RoundedBitmapDrawableFactory.create(imageView.getResources(),resource);
                        drawable.setCircular(true);
                        imageView.setImageDrawable(drawable);
                    }
                });
    }

    /**
     * 为ViewGroup 设置背景
     */
    public void displayImageForViewGroup(final ViewGroup viewGroup, String url){
            Glide.with(viewGroup.getContext())
                    .asBitmap()
                    .apply(initCommonRequestOptions())
                    .load(url)
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            final Bitmap  res =  resource;
                            Observable.just(resource)
                                    .map(new Function<Bitmap, Drawable>() {
                                        @Override
                                        public Drawable apply(Bitmap bitmap) {
                                            Drawable drawable = new BitmapDrawable(Utils.doBlur(res,100,true));
                                            return drawable;
                                        }
                                    })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Drawable>() {
                                        @Override
                                        public void accept(Drawable drawable) throws Exception {
                                            viewGroup.setBackground(drawable);
                                        }
                                    });
                        }
                    });
    }

    /**
     * 为通知设置背景
     * @param context
     * @param rv
     * @param id  notification 中显示图片资源的id
     * @param notification
     * @param NOTFICATION_ID  notification id
     * @param url
     */
    public void displayImageForNotfication(Context context, RemoteViews rv, int id, Notification notification,int NOTFICATION_ID,String url){
        this.displayImageForTarget(context,initNotificationTarget(context,rv,  id, notification,NOTFICATION_ID),url);
    }

    private Target initNotificationTarget(Context context,RemoteViews rv, int id, Notification notification, int notfication_id) {
        NotificationTarget target = new NotificationTarget(context,id,rv,notification,notfication_id);
        return target;
    }

    /**
     * t通用的为Target 设置背景的方法
     * @param context
     * @param target
     * @param url
     */
    private void displayImageForTarget(Context context,Target target,String url){
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOptions())
                .transition(BitmapTransitionOptions.withCrossFade())
                .fitCenter()
                .into(target);

    }
    private RequestOptions initCommonRequestOptions() {
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.b4y)
                .error(R.drawable.b4y)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.NORMAL)
                .skipMemoryCache(false);
        return options;
    }
}
