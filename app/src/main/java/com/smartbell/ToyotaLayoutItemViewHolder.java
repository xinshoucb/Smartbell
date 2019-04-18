package com.smartbell;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartbell.bean.ItemConfig;
import com.smartbell.db.DBManager;

public class ToyotaLayoutItemViewHolder {
    private int[] lineLayoutId = {R.id.layout_line_item1, R.id.layout_line_item2, R.id.layout_line_item3, R.id.layout_line_item4};
    private int[] viewsId = {R.id.item_1, R.id.item_2, R.id.item_3, R.id.item_4};

    private RelativeLayout layoutItem;
    private TextView contentTv;
    private TextView indexTv;
    private DataInfo setDataInfo;
    private ItemConfig mItemConfig;

    public ToyotaLayoutItemViewHolder(final Activity mAc, final int index) {
//        mItemConfig = DBManager.getItemConfigHasDefault(index);
        if (mItemConfig == null) {
            mItemConfig = new ItemConfig();
        }
        LinearLayout linearLayout = (LinearLayout) mAc.findViewById(lineLayoutId[index/4]);
        layoutItem = (RelativeLayout) linearLayout.findViewById(viewsId[index%4]);

        contentTv = (TextView) layoutItem.findViewById(R.id.content);
        indexTv = (TextView) layoutItem.findViewById(R.id.index);
        indexTv.setText("" + (index + 1));
        indexTv.setVisibility(View.INVISIBLE);
        BellUtils.setAntiAliasFlag(contentTv);

        layoutItem.setClickable(true);
        layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)mAc).showSettingDialog(index);
            }
        });
    }

    public void setData(DataInfo setDataInfo) {
        this.setDataInfo = setDataInfo;
    }

    public void refreshView() {
        if (setDataInfo != null) {
            contentTv.setTextColor(Color.RED);
            layoutItem.setBackgroundColor(Color.WHITE);

            contentTv.setText(setDataInfo.getData());
            contentTv.clearAnimation();
        }else{
//            contentTv.setTextColor(Color.GRAY);
            layoutItem.setBackgroundColor(Color.WHITE);

            contentTv.setText("");
            contentTv.clearAnimation();
        }
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

    public ItemConfig getItemConfig() {
        return mItemConfig;
    }

    public void setItemConfig(ItemConfig itemConfig) {
        this.mItemConfig = itemConfig;
    }

    private String getBackgroudColorFromItemConfig(){
        String rtnColor = mItemConfig.oneColor;

        int showTimeSec = (int) ((System.currentTimeMillis() - setDataInfo.getStartTime()) / 1000);

        if(mItemConfig.oneTimeSec > 0 ){
            if(mItemConfig.twoTimeSec > 0 && showTimeSec > (mItemConfig.oneTimeSec+mItemConfig.twoTimeSec)){
                rtnColor = mItemConfig.thrColor;
            }else if (showTimeSec > mItemConfig.oneTimeSec){
                rtnColor = mItemConfig.twoColor;
            }
        }

        return rtnColor;
    }

    private String getBackgroudColorFromDataConfig(){
        ItemConfig itemConfig = DBManager.getItemConfigHasDefault(setDataInfo.getData());
        setDataInfo.setItemConfig(itemConfig);

        String rtnColor = itemConfig.oneColor;

        int showTimeSec = (int) ((System.currentTimeMillis() - setDataInfo.getStartTime()) / 1000);

        if(itemConfig.oneTimeSec > 0 ){
            if(itemConfig.twoTimeSec > 0 && showTimeSec > (itemConfig.oneTimeSec+itemConfig.twoTimeSec)){
                rtnColor = itemConfig.thrColor;
            }else if (showTimeSec > itemConfig.oneTimeSec){
                rtnColor = itemConfig.twoColor;
            }
        }

        return rtnColor;
    }
}
