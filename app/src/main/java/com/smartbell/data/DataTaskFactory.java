package com.smartbell.data;

import android.content.Context;

/**
 * DataTaskFactory class
 *
 * @author hangwei
 * @date 2018/10/3
 */
public class DataTaskFactory {
    public static final int TASK_TYPE_FAKE = 0;
    public static final int TASK_TYPE_WIFI = 1;
    public static final int TASK_TYPE_USB = 2;

    public static BaseDataTask getDataTask(int type, Context context){
        BaseDataTask task;
        switch (type){
            case TASK_TYPE_WIFI:
                task = new WifiDataTask(context);
                break;
            case TASK_TYPE_USB:
                task = new UsbDataTask(context);
                break;
            case TASK_TYPE_FAKE:
            default:
                task = new FakeDateTask(context);
        }

        return task;
    }
}
