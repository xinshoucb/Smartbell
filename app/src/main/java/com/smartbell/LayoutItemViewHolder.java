package com.smartbell;

import android.app.Activity;
import android.graphics.Color;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LayoutItemViewHolder {
    private int[] viewsId = {R.id.item_1, R.id.item_2, R.id.item_3, R.id.item_4, R.id.item_5, R.id.item_6
    ,R.id.item_7, R.id.item_8, R.id.item_9, R.id.item_10, R.id.item_11, R.id.item_12};


    private RelativeLayout layoutItem;
    private TextView contentTv;
    private TextView indexTv;
    private DataInfo setDataInfo;

    public LayoutItemViewHolder(Activity mAc, int index) {
        layoutItem = (RelativeLayout) mAc.findViewById(viewsId[index]);

        contentTv = (TextView) layoutItem.findViewById(R.id.content);
        indexTv = (TextView) layoutItem.findViewById(R.id.index);
        indexTv.setText("" + (index + 1));
        BellUtils.setAntiAliasFlag(contentTv);
    }

    public void setData(DataInfo setDataInfo) {
        this.setDataInfo = setDataInfo;
        refreshView();
    }

    public void refreshView() {
        contentTv.setTextColor(setDataInfo.getTextColor());
        layoutItem.setBackgroundResource(setDataInfo.getBackgroundColor());

        contentTv.setText(setDataInfo.getData());

        contentTv.clearAnimation();
    }

    public RelativeLayout getLayoutItem() {
        return layoutItem;
    }

    public void setLayoutItem(RelativeLayout layoutItem) {
        this.layoutItem = layoutItem;
    }

    public TextView getContentTv() {
        return contentTv;
    }

    public void setContentTv(TextView contentTv) {
        this.contentTv = contentTv;
    }

    public TextView getIndexTv() {
        return indexTv;
    }

    public void setIndexTv(TextView indexTv) {
        this.indexTv = indexTv;
    }
}
