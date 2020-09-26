package com.github.duanjiefei.ft_home.view.friend;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.duanjiefei.ft_home.R;
import com.github.duanjiefei.ft_home.model.friend.FriendBodyValue;
import com.github.duanjiefei.lib_audio.app.AudioHelper;
import com.github.duanjiefei.lib_base.ft_login.service.impl.LoginImpl;
import com.github.duanjiefei.lib_common_ui.MultiImageViewLayout;
import com.github.duanjiefei.lib_common_ui.recyclerview.ItemViewDelegate;
import com.github.duanjiefei.lib_common_ui.recyclerview.MultiItemTypeAdapter;
import com.github.duanjiefei.lib_common_ui.recyclerview.ViewHolder;
import com.github.duanjiefei.lib_image_loader.ImageLoaderManager;
import com.github.duanjiefei.lib_video.VideoAdContext;


import java.util.List;


public class FriendRecyclerViewAdapter extends MultiItemTypeAdapter {


    public static final int MUSIC_TYPE = 0x01; //音乐类型
    public static final int VIDEO_TYPE = 0x02; //音乐类型
    private Context mContext;
    public FriendRecyclerViewAdapter(Context context, List<FriendBodyValue> datas) {
        super(context, datas);
        mContext = context;
        addItemViewDelegate(MUSIC_TYPE,new MusicItemDelegate());
        addItemViewDelegate(VIDEO_TYPE,new VideoItemDelegate());
    }


    private class MusicItemDelegate implements ItemViewDelegate<FriendBodyValue> {
        private static final String TAG = "MusicItemDelegate";
        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_friend_list_picture_layout;
        }

        @Override
        public boolean isForViewType(FriendBodyValue item, int position) {
            Log.d(TAG, "isForViewType: "+item.type);
            Log.d(TAG, "isForViewType: "+FriendRecyclerViewAdapter.MUSIC_TYPE);
            return item.type == FriendRecyclerViewAdapter.MUSIC_TYPE;
        }

        @Override
        public void convert(ViewHolder holder, final FriendBodyValue friendBodyValue, int position) {
            holder.setText(R.id.name_view, friendBodyValue.name + " 分享单曲:");
            holder.setText(R.id.fansi_view, friendBodyValue.fans + "粉丝");
            holder.setText(R.id.text_view, friendBodyValue.text);
            holder.setText(R.id.zan_view, friendBodyValue.zan);
            holder.setText(R.id.message_view, friendBodyValue.msg);
            holder.setText(R.id.audio_name_view, friendBodyValue.audioBean.name);
            holder.setText(R.id.audio_author_view, friendBodyValue.audioBean.album);
            holder.setOnClickListener(R.id.album_layout, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //调用播放器装饰类
                    AudioHelper.addAudio((Activity) mContext, friendBodyValue.audioBean);
                }
            });
            holder.setOnClickListener(R.id.guanzhu_view, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!LoginImpl.getInstance().hasLogin()) {
                        //goto login
                        LoginImpl.getInstance().login(mContext);
                    }
                }
            });
            ImageView avatar = holder.getView(R.id.photo_view);
            ImageLoaderManager.getInstance().displayImageForCircle(avatar, friendBodyValue.avatr);
            ImageView albumPicView = holder.getView(R.id.album_view);
            ImageLoaderManager.getInstance()
                    .displayImageForView(albumPicView, friendBodyValue.audioBean.albumPic);

            MultiImageViewLayout imageViewLayout = holder.getView(R.id.image_layout);
            imageViewLayout.setList(friendBodyValue.pics);
        }
    }

    /**
     * 视频类型item
     */
    private class VideoItemDelegate implements ItemViewDelegate<FriendBodyValue> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.item_friend_list_video_layout;
        }

        @Override
        public boolean isForViewType(FriendBodyValue item, int position) {
            return item.type == FriendRecyclerViewAdapter.VIDEO_TYPE;
        }

        @Override
        public void convert(ViewHolder holder, FriendBodyValue recommandBodyValue, int position) {
            RelativeLayout videoGroup = holder.getView(R.id.video_layout);
            VideoAdContext mAdsdkContext = new VideoAdContext(videoGroup, recommandBodyValue.videoUrl);
            holder.setText(R.id.fansi_view, recommandBodyValue.fans + "粉丝");
            holder.setText(R.id.name_view, recommandBodyValue.name + " 分享视频");
            holder.setText(R.id.text_view, recommandBodyValue.text);
            holder.setOnClickListener(R.id.guanzhu_view, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!LoginImpl.getInstance().hasLogin()) {
                        //goto login
                        LoginImpl.getInstance().login(mContext);
                    }
                }
            });
            ImageView avatar = holder.getView(R.id.photo_view);
            ImageLoaderManager.getInstance().displayImageForCircle(avatar, recommandBodyValue.avatr);
        }
    }
}
