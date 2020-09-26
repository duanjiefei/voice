package com.github.duanjiefei.ft_home.view.home;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.github.duanjiefei.ft_home.R;
import com.github.duanjiefei.ft_home.model.CHANNEL;
import com.github.duanjiefei.lib_audio.app.AudioHelper;
import com.github.duanjiefei.lib_audio.model.AudioBean;
import com.github.duanjiefei.lib_base.audio.impl.AudioImpl;
import com.github.duanjiefei.lib_base.audio.model.CommonAudioBean;
import com.github.duanjiefei.lib_base.ft_login.event.LoginEvent;
import com.github.duanjiefei.lib_base.ft_login.service.impl.LoginImpl;
import com.github.duanjiefei.lib_common_ui.BaseActivity;
import com.github.duanjiefei.lib_common_ui.Constant;
import com.github.duanjiefei.lib_image_loader.ImageLoaderManager;


import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

public class HomeActivity extends BaseActivity implements View.OnClickListener{


    private static final CHANNEL[] CHANNELS = new CHANNEL[]{
            CHANNEL.MY,CHANNEL.DISCOVERY,CHANNEL.FRIEND
    };

    private static final  String TAG = "HomeActivity";
    private DrawerLayout mDrawerLayout;
    private View mToggleView;
    private View mSearchView;
    private View unLoginLayout;
    private ImageView mPhotoView;
    private ViewPager mViewPager;
    private HomePageAapter homePageAapter;


    /*
     * data
     */
    private ArrayList<CommonAudioBean> mLists = new ArrayList<>();

    public static void start(Context context) {
        Intent intent = new Intent(context,HomeActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        mLists.add(new CommonAudioBean("100001", "http://sp-sycdn.kuwo.cn/resource/n2/85/58/433900159.mp3",
                "以你的名字喊我", "周杰伦", "七里香", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698076304&di=e6e99aa943b72ef57b97f0be3e0d2446&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fblog%2F201401%2F04%2F20140104170315_XdG38.jpeg",
                "4:30"));
        mLists.add(
                new CommonAudioBean("100002", "http://sq-sycdn.kuwo.cn/resource/n1/98/51/3777061809.mp3", "勇气",
                        "梁静茹", "勇气", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698193627&di=711751f16fefddbf4cbf71da7d8e6d66&imgtype=jpg&src=http%3A%2F%2Fimg0.imgtn.bdimg.com%2Fit%2Fu%3D213168965%2C1040740194%26fm%3D214%26gp%3D0.jpg",
                        "4:40"));
        mLists.add(
                new CommonAudioBean("100003", "http://sp-sycdn.kuwo.cn/resource/n2/52/80/2933081485.mp3", "灿烂如你",
                        "汪峰", "春天里", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698239736&di=3433a1d95c589e31a36dd7b4c176d13a&imgtype=0&src=http%3A%2F%2Fpic.zdface.com%2Fupload%2F201051814737725.jpg",
                        "3:20"));
        mLists.add(
                new CommonAudioBean("100004", "http://sr-sycdn.kuwo.cn/resource/n2/33/25/2629654819.mp3", "小情歌",
                        "五月天", "小幸运", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698289780&di=5146d48002250bf38acfb4c9b4bb6e4e&imgtype=0&src=http%3A%2F%2Fpic.baike.soso.com%2Fp%2F20131220%2Fbki-20131220170401-1254350944.jpg",
                        "2:45"));
        AudioImpl.getInstance().startMusicService(mLists);
    }

    private void initView() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggleView = findViewById(R.id.toggle_view);
        mToggleView.setOnClickListener(this);
        mSearchView = findViewById(R.id.search_view);
        mSearchView.setOnClickListener(this);
        findViewById(R.id.online_music_view).setOnClickListener(this);
        findViewById(R.id.check_update_view).setOnClickListener(this);


        mViewPager = findViewById(R.id.view_pager);
        homePageAapter = new HomePageAapter(getSupportFragmentManager(),CHANNELS);
        mViewPager.setAdapter(homePageAapter);
        initMagicIndicator();

        unLoginLayout = findViewById(R.id.unloggin_layout);
        unLoginLayout.setOnClickListener(this);
        mPhotoView = findViewById(R.id.avatr_view);

        //findViewById(R.id.bottom_view).setVisibility(View.INVISIBLE);
    }

    private void initMagicIndicator() {
        MagicIndicator magicIndicator = findViewById(R.id.magic_indicator);
        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return CHANNELS == null ? 0 : CHANNELS.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView =  new SimplePagerTitleView(context);
                simplePagerTitleView.setText(CHANNELS[index].getKey());
                simplePagerTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                simplePagerTitleView.setNormalColor(Color.parseColor("#99999999"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#33ff3333"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }

            @Override
            public float getTitleWeight(Context context, int index) {
                return 1.0f;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator,mViewPager);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        if (id == R.id.search_view){
            return;
        }

        if (id == R.id.toggle_view){
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }else {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
            return;
        }


        if (id == R.id.unloggin_layout){
            if (!LoginImpl.getInstance().hasLogin()){
                LoginImpl.getInstance().login(this);
            }else {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
            return;
        }

        if (id == R.id.home_music){
            return;
        }

        if (id == R.id.online_music_view){
            Log.d(TAG, "onClick: online_music_view");
            gotoWebView("https://www.imooc.com");
            return;
        }

        if (id == R.id.check_update_view){
            return;
        }
    }

    private void gotoWebView(String s) {
        ARouter.getInstance()
                .build(Constant.Router.ROUTER_WEB_ACTIVIYT)
                .withString("url",s)
                .navigation();
    }

    @Subscribe(threadMode =  ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent loginEvent){
        Log.d(TAG, "onLoginEvent: ");
        unLoginLayout.setVisibility(View.GONE);
        mPhotoView.setVisibility(View.VISIBLE);
        ImageLoaderManager.getInstance().displayImageForCircle(mPhotoView,LoginImpl.getInstance().getUserInfo().data.photoUrl);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
