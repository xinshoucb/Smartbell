package com.smartbell;

import android.widget.TextView;

/**
 * Created by Curry on 18/3/18.
 * 输出日志
 */

public class LogView {

    private static TextView logView;

    public static void setLogView(TextView logView) {
        LogView.logView = logView;
    }

    public static void setLog(String log){
        if (logView != null) {
            logView.setText(logView.getText()+"\n"+log);
        }
    }
}
