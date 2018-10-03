package com.smartbell;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.widget.TextView;

public class BellUtils {
    private static final String TAG = "BellUtils";
    private static MainActivity mAc;

    private static final long GREEN_TIME_GAP = 5 * 60 * 1000;
    private static final long YELLOW_TIME_GAP = 10 * 60 * 1000;

    public static void init(MainActivity mMainActivity) {
        mAc = mMainActivity;
    }

    public static int getLayoutbackgroundIdByTime(DataInfo mDataInfo) {
        int rtn = R.drawable.tv_bg_green;
        long currentTime = System.currentTimeMillis();
        long timeGap = currentTime - mDataInfo.getStartTime();

        if (timeGap < 0) {
            mDataInfo.setStartTime(currentTime);
            timeGap = 0;
        }

        if (mAc == null) {
            if (timeGap < GREEN_TIME_GAP) {
                rtn = R.drawable.tv_bg_green;
            } else if (timeGap < YELLOW_TIME_GAP) {
                rtn = R.drawable.tv_bg_yellow;
            } else {
                rtn = R.drawable.tv_bg_red;
            }
        } else {
            int greenShowTime = mAc.getGreenShowTime() * 60 * 1000;
            int yellowShowTime = mAc.getYellowShowTime() * 60 * 1000;
            if (timeGap < greenShowTime) {
                rtn = R.drawable.tv_bg_green;
            } else if (timeGap < greenShowTime + yellowShowTime) {
                rtn = R.drawable.tv_bg_yellow;
            } else {
                rtn = R.drawable.tv_bg_red;
            }
        }

        return rtn;
    }

    public static void setAntiAliasFlag(TextView tv) {
        int flags = tv.getPaintFlags() | Paint.ANTI_ALIAS_FLAG;
        tv.setPaintFlags(flags);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 把字符转化成数字
     * @param value
     * @return 返回-1表示转换失败
     */
    public static int str2Int(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }


    private boolean isMainActivityTop(Context context) {
        if (context == null) {
            return false;
        }

        boolean rtn = false;

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

        if ("com.example.bell_x3.MainActivity".equals(cn.getClassName())) {
            rtn = true;
        }

        return rtn;
    }


    private void startMainActivity(Context context) {
        if (context == null) {
            return;
        }
        Log.d(TAG, " start activity ");
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("initData", data);
        context.startActivity(intent);
    }

}
