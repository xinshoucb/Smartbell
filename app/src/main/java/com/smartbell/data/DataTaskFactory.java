package com.smartbell.data;

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

    public static BaseDataTask getDataTask(int type){
        BaseDataTask task;
        switch (type){
            case TASK_TYPE_WIFI:
                task = new WifiDataTask();
                break;
            case TASK_TYPE_USB:
                task = new UsbDataTask();
                break;
            case TASK_TYPE_FAKE:
            default:
                task = new FakeDateTask();
        }

        return task;
    }
}
