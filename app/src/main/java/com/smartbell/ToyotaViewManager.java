package com.smartbell;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartbell.bean.ItemConfig;
import com.smartbell.db.DBManager;

import java.util.ArrayList;

public class ToyotaViewManager {
    private MainActivity mAc;

    private final static int SHOW_VIEW_COUNT_MAX = 20;

    private ToyotaLayoutItemViewHolder[] mViewHolders = new ToyotaLayoutItemViewHolder[SHOW_VIEW_COUNT_MAX];
    private ArrayList<DataInfo> curShowDatas = new ArrayList<DataInfo>();
    private ArrayList<DataInfo> tmpShowDatas = new ArrayList<DataInfo>();
    private int curShowViewCount = 0;

    private int layoutInitHeight = 0;
    private float textSizeSmall = 0;

    private RelativeLayout logoLayout;
    private TextView logoTv;
    private RelativeLayout mRootView;

    public ToyotaViewManager(MainActivity mAc) {
        super();
        this.mAc = mAc;

        initView();
    }

    private void initView() {
        logoLayout = (RelativeLayout) mAc.findViewById(R.id.logo_layout);
        logoTv = (TextView) mAc.findViewById(R.id.logo_tv);
        BellUtils.setAntiAliasFlag(logoTv);
        logoTv.setSelected(true);
        logoTv.setClickable(true);

        mRootView = (RelativeLayout) mAc.findViewById(R.id.rootView);

        for (int i = 0; i < SHOW_VIEW_COUNT_MAX; i++) {
            mViewHolders[i] = new ToyotaLayoutItemViewHolder(mAc, i);
        }
    }

    public void initParam() {

        layoutInitHeight = mRootView.getHeight();

        textSizeSmall = layoutInitHeight / 8;

        logoTv.setTextSize((layoutInitHeight / 7));
    }

    public void resetView() {
        curShowDatas.clear();
        refreshView(curShowDatas);
    }

    public void notifyBackgroudColorChange(int index) {
        if (null != curShowDatas && index >= 0 && curShowDatas.size() > index){
            ItemConfig itemConfig = DBManager.getItemConfig(index);
            if (itemConfig != null) {
                mViewHolders[index].setItemConfig(itemConfig);
                mViewHolders[index].refreshView();
            }

            curShowDatas.get(index).setItemConfig(itemConfig);
        }
    }

    public void refreshView() {
        refreshView(curShowDatas);
    }

    public void refreshView(ArrayList<DataInfo> datas) {
        if (null == datas) {
            return;
        }

        curShowDatas = datas;

        int dataCount = datas.size();

        // show logo page when there is no data
        if (dataCount > 0) {
            logoLayout.setVisibility(View.GONE);

            // fill view
            for (int i = 0; i < SHOW_VIEW_COUNT_MAX; i++) {
                mViewHolders[i].getContentTv().setTextSize(BellUtils.px2sp(mAc, textSizeSmall));
                if(i < dataCount){
                    mViewHolders[i].setData(curShowDatas.get(i));

                    if (curShowDatas.get(i).getStartTime() == 0){
                        curShowDatas.get(i).setStartTime(System.currentTimeMillis());
                    }
                }else{
                    mViewHolders[i].setData(null);
                }

                mViewHolders[i].refreshView();
                //clean shuju
            }
        } else {
            logoLayout.setVisibility(View.VISIBLE);
        }
    }

}
