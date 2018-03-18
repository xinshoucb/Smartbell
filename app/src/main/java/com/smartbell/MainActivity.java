package com.smartbell;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private final static String SERVICE_NAME = "com.smartbell.DataService";

    private int greenShowTime = 5;
    private int yellowShowTime = 5;

    private DataManager mDataManager;
    private ToyotaViewManager mViewManager;

    private SharedPreferences sharedPreferences;

    private SettingDialog mSettingDialog;

    private DataManager.CallBack mDataManagerCallBack = new DataManager.CallBack() {

        @Override
        public void refreshView(ArrayList<DataInfo> datas) {
            mViewManager.refreshView(datas);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_toyota);
        sharedPreferences = getSharedPreferences("bell", Context.MODE_PRIVATE);

        LogView.setLogView((TextView) findViewById(R.id.log_tv));
        initSetValue();

        BellUtils.init(this);

        mDataManager = new DataManager(this, mDataManagerCallBack);
        mViewManager = new ToyotaViewManager(this);

        final String initData = getIntent().getStringExtra("initData");
        findViewById(R.id.rootView).post(new Runnable() {

            @Override
            public void run() {
                mViewManager.initParam();
                if (initData != null) mDataManager.getDataServiceCallBack().updateData(initData);

            }
        });
        Intent intent = new Intent(this, DataService.class);
        startService(intent);
    }

    public void showSettingDialog(int index) {
        if (mSettingDialog == null) {
            mSettingDialog = new SettingDialog(this);
        }
        mSettingDialog.setIndex(index);

        mSettingDialog.show();
    }

    public void notifyBackgroudColorChange(int index){
        mViewManager.notifyBackgroudColorChange(index);
    }

    private DataService mDataService;

    ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            Log.d("chenbo", "activity onServiceDisconnected ");
            LogView.setLog("activity -> data service onServiceDisconnected");
            mDataService.removeCallBack();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            Log.d("chenbo", "activity Service Connected "+mDataManager.getDataServiceCallBack());
            LogView.setLog("activity -> data service Connected");
            mDataService = ((DataService.MsgBinder) service).getService();
            mDataService.setCallBack(mDataManager.getDataServiceCallBack());
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        // mDataManager.stop();
        Log.d("chenbo", "activity onPause ");
        if (null != mDataService) {
            mDataService.removeCallBack();
        }
        unbindService(conn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // mDataManager.start();
        Log.d("chenbo", "activity onResume ");
        Intent intent = new Intent(this, DataService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

        if(mDataService != null){
            mDataService.setCallBack(mDataManager.getDataServiceCallBack());
        }
    }

    public int getGreenShowTime() {
        return greenShowTime;
    }

    public int getYellowShowTime() {
        return yellowShowTime;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showSettingDialog(-1);
        return super.onOptionsItemSelected(item);
    }

    private void initSetValue() {
        greenShowTime = sharedPreferences.getInt("green_show_time", 5);
        yellowShowTime = sharedPreferences.getInt("yellow_show_time", 5);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        boolean rnt = true;

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            showSettingDialog(-1);
        } else {
            rnt = super.onKeyDown(keyCode, event);
        }
        return rnt;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dialog, menu);
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        if (mSettingDialog != null && mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
        }
        Log.d("chenbo", "activity finished ");
    }

}
