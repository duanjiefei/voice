package com.github.duanjiefei.voice.friend;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.github.duanjiefei.lib_common_ui.recyclerview.LoadMoreWrapper;
import com.github.duanjiefei.lib_network.listener.DisposeDataListener;
import com.github.duanjiefei.lib_network.utils.ResponseEntityToModule;
import com.github.duanjiefei.voice.R;
import com.github.duanjiefei.voice.api.MockData;
import com.github.duanjiefei.voice.api.RequestCenter;
import com.github.duanjiefei.voice.model.friend.BaseFriendModel;
import com.github.duanjiefei.voice.model.friend.FriendBodyValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;



import java.util.ArrayList;
import java.util.List;


/**
 * 朋友fragment
 */
public class FriendFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener, LoadMoreWrapper.OnLoadMoreListener{

    private static final String TAG = "FriendFragment";
    private Context context;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private FriendRecyclerViewAdapter mAdapter;
    private LoadMoreWrapper loadMoreWrapper;

    private BaseFriendModel mRecommandData;
    private List<FriendBodyValue> mDatas = new ArrayList<>();

    public static Fragment newInstance() {
        FriendFragment fragment = new FriendFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        Log.d(TAG, "onCreate: ");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_discory_layout,null);
        refreshLayout = rootView.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_red_light));
        refreshLayout.setOnRefreshListener(this);

        recyclerView = rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        Log.d(TAG, "onCreateView: ");
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestData();
        Log.d(TAG, "onViewCreated: ");
    }

    private void requestData() {
        Log.d(TAG, "requestData: ");
        RequestCenter.requestFriendData(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                mRecommandData = (BaseFriendModel) responseObj;

                updateView();
            }

            @Override
            public void onFailure(Object reasonObj) {
                onSuccess(ResponseEntityToModule.parseJsonToModule(MockData.FRIEND_DATA,BaseFriendModel.class));
            }
        });
    }

    private void updateView() {

        refreshLayout.setRefreshing(false);//停止刷新
        mDatas = mRecommandData.data.list;
        mAdapter = new FriendRecyclerViewAdapter(context,mDatas);

        loadMoreWrapper  = new LoadMoreWrapper(mAdapter);
        loadMoreWrapper.setLoadMoreView(R.layout.default_loading);
        loadMoreWrapper.setOnLoadMoreListener(this);

        recyclerView.setAdapter(loadMoreWrapper);
    }

    @Override
    public void onRefresh() {
        //发请求更新UI
        Log.d(TAG, "onRefresh: ");
        requestData();
    }

    @Override
    public void onLoadMoreRequested() {
        Log.d(TAG, "onLoadMoreRequested: ");
        loadMore();
    }

    private void loadMore() {
        RequestCenter.requestFriendData(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                BaseFriendModel moreData = (BaseFriendModel) responseObj;
                //追加数据到adapter中
                mDatas.addAll(moreData.data.list);
                loadMoreWrapper.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Object reasonObj) {
                //显示请求失败View,显示mock数据
                onSuccess(
                        ResponseEntityToModule.parseJsonToModule(MockData.FRIEND_DATA, BaseFriendModel.class));
            }
        });
    }
}
