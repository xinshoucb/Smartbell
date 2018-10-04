package com.smartbell.data;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.smartbell.LogView;
import com.smartbell.TestUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * BaseDataTask class
 *
 * @author hangwei
 * @date 2018/10/3
 */
public abstract class BaseDataTask {
    private static final String TAG = "BaseDataTask";

    public static final int EXCEPTION_IO = 0;
    public static final int EXCEPTION_DATA = 1;

    private static final int HANDLER_REFREASH_VIEW = 0;
    private static final int HANDLER_REACCEPT = 1;
    private static Callback mCallback;

    protected Context mContext;
    protected static Handler handler = new MyHandler();

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
                    if (mCallback != null) {
                        mCallback.update(((String) msg.obj));
                    }
                    break;
                case HANDLER_REACCEPT:
//                    isStop = true;
//                    reConnect();
                    Log.i(TAG, "reaccept = "+msg.arg1);
                    LogView.setLog(TAG + "reaccept = "+msg.arg1);
                    break;
                default:
                    break;
            }
        }
    }

    public BaseDataTask(Context context){
        mContext = context;
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
    public void registerCallback(Callback callback){
        mCallback = callback;
    }

    protected void update(String data){
        Message msg = Message.obtain();
        msg.what = HANDLER_REFREASH_VIEW;
        msg.obj = data;
        handler.sendMessage(msg);
    }

    protected void exception(int excptionId){
        Message msg = Message.obtain();
        msg.what = HANDLER_REACCEPT;
        msg.arg1 = excptionId;
        handler.sendMessage(msg);
    }

    /**
     * 数据接收任务
     */
    public interface Callback{
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
