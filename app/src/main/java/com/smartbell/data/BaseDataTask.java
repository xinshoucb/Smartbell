package com.smartbell.data;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.smartbell.TestUtils;

/**
 * BaseDataTask class
 *
 * @author hangwei
 * @date 2018/10/3
 */
public abstract class BaseDataTask {
    private static final String TAG = "BaseDataTask";

    private static final int HANDLER_REFREASH_VIEW = 0;
    private static final int HANDLER_REACCEPT = 1;

    private static Handler handler = new MyHandler();

    static class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case HANDLER_REFREASH_VIEW:
//                    if (TestUtils.isTest) {
//                        data = (String) msg.obj;
//                    } else {
//                        data = praseData((String) msg.obj);
//                    }
//                    noticeActivity();
                    break;
                case HANDLER_REACCEPT:
//                    if(msg.arg1 == 1){
//                        connectAndGetData();
//                    }else if(msg.arg1 == 0){
//                        isStop = true;
//                    }
                    Log.i(TAG, "reaccept = "+msg.arg1);
                    break;
                default:
                    break;
            }
        }
    }

    public BaseDataTask(){

    }

    /**
     * 开启任务
     */
    public abstract void startTask();

    /**
     * 停止任务
     */
    public abstract void stopTask();

    /**
     * 注册回调
     */
    public abstract void registerCallback();

    /**
     * 数据接收任务
     */
    interface Callback{
        /**
         * 接收到新数据，通知界面更新
         * @param data 新数据
         */
        void update(String data);

        /**
         * 异常状况回调
         * @param exceptionId 异常码
         */
        void exception(int exceptionId);
    }
}
