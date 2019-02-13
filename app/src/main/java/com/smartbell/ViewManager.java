package com.smartbell;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewManager {
    private MainActivity mAc;

    private static final boolean isChangeSizeRolling = false;

    private int rollingCnt = 0;

    private final static int SHOW_VIEW_COUNT_MAX = 6;

    private LayoutItemViewHolder[] mViewHolders = new LayoutItemViewHolder[SHOW_VIEW_COUNT_MAX];
    private ArrayList<DataInfo> curShowDatas = new ArrayList<DataInfo>();
    private ArrayList<DataInfo> tmpShowDatas = new ArrayList<DataInfo>();
    private int curShowViewCount = 0;

    private int layoutInitWidth = 0;
    private int layoutInitHeight = 0;
    private float layoutInitX = 0;
    private float layoutInitY = 0;

    private float textSizeBig = 0;
    private float textSizeMid = 0;
    private float textSizeSmall = 0;

    private TextView logoTv;
    private ImageView logoTvImg;
    private RelativeLayout mRootView;

    private int gap = 8;

    public ViewManager(MainActivity mAc) {
        super();
        this.mAc = mAc;

        initView();
    }

    private void initView() {
        logoTv = (TextView) mAc.findViewById(R.id.logo_tv);
        logoTvImg = (ImageView) mAc.findViewById(R.id.logo_tv_img);
        BellUtils.setAntiAliasFlag(logoTv);
        logoTv.setSelected(true);

        mRootView = (RelativeLayout) mAc.findViewById(R.id.rootView);

        for (int i = 0; i < SHOW_VIEW_COUNT_MAX; i++) {
            mViewHolders[i] = new LayoutItemViewHolder(mAc, i);
        }
    }

    public void initParam() {
        layoutInitWidth = mRootView.getWidth() - 2 * gap;
        layoutInitHeight = mRootView.getHeight() - 2 * gap;

        layoutInitX = gap;
        layoutInitY = gap;

        textSizeBig = layoutInitHeight / 4;
        textSizeMid = layoutInitHeight / 5;
        textSizeSmall = layoutInitHeight / 6;

        logoTv.setTextSize((layoutInitHeight / 7));
    }

    public void resetView() {
        curShowDatas.clear();
        refreshView(curShowDatas);
    }

    public void refreshView() {
        refreshView(curShowDatas);
    }

    public void refreshView(ArrayList<DataInfo> datas) {
        if (null == datas)
            return;

        curShowDatas = datas;

        int dataCount = datas.size();

        // show logo page when there is no data
        if (dataCount > 0) {
            logoTv.setVisibility(View.GONE);
            logoTvImg.setVisibility(View.GONE);

            int showViewCount = Math.max(Math.min(SHOW_VIEW_COUNT_MAX, dataCount), 0);

            // layout view
            if (curShowViewCount != showViewCount) {
                layoutView(showViewCount);
            }

            // fill view
            for (int i = 0; i < showViewCount; i++) {
                mViewHolders[i].getContentTv().setTextSize(BellUtils.px2sp(mAc, textSizeBig));
                mViewHolders[i].setData(curShowDatas.get(i));
            }
        } else {
//            logoTv.setVisibility(View.VISIBLE);
            logoTvImg.setVisibility(View.VISIBLE);
        }

    }

    private void layoutView(int showViewCount) {
        curShowViewCount = showViewCount;

        Log.e("chenbo", "layoutView = " + curShowViewCount);
        for (int i = 0; i < mViewHolders.length; i++) {
            if (i < curShowViewCount) {
                mViewHolders[i].getLayoutItem().setVisibility(View.VISIBLE);
            } else {
                mViewHolders[i].getLayoutItem().setVisibility(View.GONE);
            }
            mViewHolders[i].getIndexTv().setVisibility(View.INVISIBLE);
        }

        if (3 >= curShowViewCount) {
            layoutView(layoutInitX, layoutInitY, layoutInitWidth, layoutInitHeight, 1, curShowViewCount);
        } else if (6 >= curShowViewCount) {
            int viewHeght = (layoutInitHeight - gap) / 2;

            layoutView(layoutInitX, layoutInitY, layoutInitWidth, viewHeght, 1, 3);

            layoutView(layoutInitX, layoutInitY + (gap + viewHeght), layoutInitWidth, viewHeght, 2, curShowViewCount - 3);
        }

    }

    private void layoutView(float initX, float initY, int widthSum, int heightSum, int level, int viewCount) {
        int viewWidth = (widthSum - gap * (viewCount - 1)) / viewCount;
        int viewHeight = heightSum;

        View viewTmp = null;
        int offset = 3 * (level - 1);
        for (int i = 0; i < viewCount; i++) {
            viewTmp = mViewHolders[i + offset].getLayoutItem();

            LayoutParams laParams = (LayoutParams) viewTmp.getLayoutParams();
            laParams.width = viewWidth;
            laParams.height = viewHeight;
            viewTmp.setLayoutParams(laParams);

            viewTmp.setX(initX + i * (viewWidth + gap));
            viewTmp.setY(initY);
        }
    }

}
