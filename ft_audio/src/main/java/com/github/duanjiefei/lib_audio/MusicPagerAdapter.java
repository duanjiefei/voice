package com.github.duanjiefei.lib_audio;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.github.duanjiefei.lib_audio.core.AudioController;
import com.github.duanjiefei.lib_audio.model.AudioBean;
import com.github.duanjiefei.lib_image_loader.ImageLoaderManager;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;



import java.util.List;

public class MusicPagerAdapter extends PagerAdapter {
    private SparseArray<ObjectAnimator> animations = new SparseArray<>();



    private List<AudioBean> beanList;
    private Context mContext;
    private Callback callback;

    public MusicPagerAdapter(List<AudioBean> beanList, Context mContext, Callback callback) {
        this.beanList = beanList;
        this.mContext = mContext;
        this.callback = callback;
    }


    @Override
    public int getCount() {
        return beanList == null ? 0 : beanList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.indictor_item_view,null);
        ImageView imageView = view.findViewById(R.id.circle_view);
        ImageLoaderManager.getInstance().displayImageForCircle(imageView,beanList.get(position).albumPic);
        container.addView(view);
        //为每个view 创建旋转的属性动画
        animations.put(position,createAnimation(view));
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    private ObjectAnimator createAnimation(View view) {
        view.setRotation(0);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,View.ROTATION.getName(),0,360);
        objectAnimator.setDuration(10000);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatCount(-1);
        if (AudioController.getInstance().isStartState()){
            objectAnimator.start();
        }
        return objectAnimator;
    }

    public ObjectAnimator getAnim(int pos) {
        return animations.get(pos);
    }
    /**
     * 与IndictorView回调,暂时没用到
     */
    public interface Callback {
        void onPlayStatus();

        void onPauseStatus();
    }
}
