package com.smartbell;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.smartbell.data.BaseDataTask;
import com.smartbell.data.DataTaskFactory;

public class DataService extends Service {
    private final static String TAG = "DataService --> ";

    private final IBinder binder = new MsgBinder();
    private CallBack mCallBack;
    private BaseDataTask mDataTask;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, " onCreate ");
        mDataTask = DataTaskFactory.getDataTask(DataTaskFactory.TASK_TYPE_USB, this);

        mDataTask.startTask();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, " onDestroy ");
        if (mDataTask != null) {
            mDataTask.stopTask();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, " onStartCommand ");

        if (mDataTask != null) {
            mDataTask.registerCallback(new BaseDataTask.Callback() {
                @Override
                public void update(String data) {
                    if (mCallBack != null) {
                        mCallBack.updateData(data);
                    }
                }

                @Override
                public void exception(int exceptionId) {

                }
            });
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, " onBind ");
        return binder;
    }

    public class MsgBinder extends Binder {

        public DataService getService() {
            return DataService.this;
        }
    }

    public void setCallBack(CallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public void removeCallBack() {
        mCallBack = null;
    }

    interface CallBack {
        void updateData(String dataStr);
    }
}
