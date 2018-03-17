package com.smartbell.ui;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartbell.R;
import com.smartbell.bean.ItemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Curry on 18/3/17.
 *
 */
public class RvDataAdapter extends RecyclerView.Adapter {

    private final int mLayoutId = R.layout.layout_item;
    private List<ItemInfo> mList = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =   LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        RvDataItemHolder holder = new RvDataItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((RvDataItemHolder) holder).getContentTv().setText(mList.get(position).getDataInfo().getData());
        ((RvDataItemHolder) holder).getContentTv().setBackgroundColor(Color.parseColor(mList.get(position).getDataInfo().getBackgroundColor()));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void refreshData(List<ItemInfo> dataList){
        mList.clear();
        mList.addAll(dataList);
    }
}
