package com.smartbell;

import android.app.Activity;
import android.widget.TextView;

/**
 * Created by Curry on 18/3/18.
 * 输出日志
 */

public class LogView {

    private static TextView logView;
    private static Activity mContext;

    public static void setLogView(TextView logView) {
        LogView.logView = logView;
    }

    public static void setActivity(Activity activity) {
        LogView.mContext = activity;
    }

    public static void setLog(final String log){
        if (logView != null) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logView.setText(logView.getText()+"\n"+log);
                }
            });
        }
    }
}
