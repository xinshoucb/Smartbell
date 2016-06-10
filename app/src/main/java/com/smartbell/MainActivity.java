package com.smartbell;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private final static String SERVICE_NAME = "com.smartbell.DataService";

    private int level = 1;

    private int rollingTime = 5;
    private int sleepTimeLevel = 2;
    private int greenShowTime = 5;
    private int yellowShowTime = 5;
    private int redShowTime = 5;

    private DataManager mDataManager;
    private ViewManager mViewManager;

    private SharedPreferences sharedPreferences;
    private final String[] multiChoiceItems = {"Level 1", "Level 2", "Level 3", "Level 4"};

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
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("bell", Context.MODE_PRIVATE);
        initSetValue();

        BellUtils.init(this);

        mDataManager = new DataManager(this, mDataManagerCallBack);
        mViewManager = new ViewManager(this);

        final String initData = getIntent().getStringExtra("initData");
        findViewById(R.id.rootView).post(new Runnable() {

            @Override
            public void run() {
                mViewManager.initParam();
                if (initData != null)
                    mDataManager.getDataServiceCallBack().updateData(initData);
            }
        });
        Intent intent = new Intent(this, DataService.class);
        startService(intent);
    }

    private void showDialog() {

        if (mSettingDialog == null) {
            mSettingDialog = new SettingDialog(this);
        }

        mSettingDialog.show();
    }

    private DataService mDataService;

    ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            Log.d("chenbo", "activity onServiceDisconnected ");
            mDataService.removeCallBack();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            Log.d("chenbo", "activity Service Connected ");
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
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        saveSetValue("level", level);
    }

    public int getRollingTime() {
        return rollingTime;
    }

    public int getSleepTimeLevel() {
        return sleepTimeLevel;
    }

    public int getGreenShowTime() {
        return greenShowTime;
    }

    public int getYellowShowTime() {
        return yellowShowTime;
    }

    public int getRedShowTime() {
        return redShowTime;
    }

    public void setSleepTimeLevel(int sleepTimeLevel) {
        this.sleepTimeLevel = sleepTimeLevel;
        saveSetValue("sleepTimeLevel", sleepTimeLevel);
    }

    public void setGreenShowTime(int greenShowTime) {
        this.greenShowTime = greenShowTime;
        saveSetValue("green_show_time", greenShowTime);
    }

    public void setYellowShowTime(int yellowShowTime) {
        this.yellowShowTime = yellowShowTime;
        saveSetValue("yellow_show_time", yellowShowTime);
    }

    public void setRedShowTime(int redShowTime) {
        this.redShowTime = redShowTime;
        saveSetValue("red_show_time", redShowTime);
    }

    public void setRollingTime(int rollingTime) {
        this.rollingTime = rollingTime;
        saveSetValue("rolling_time", rollingTime);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        int tmpLevel = 0;

        switch (itemId) {
            case R.id.action_level_1:
                tmpLevel = 1;
                break;
            case R.id.action_level_2:
                tmpLevel = 2;
                break;
            case R.id.action_level_3:
                tmpLevel = 3;
                break;
            case R.id.action_level_4:
                tmpLevel = 4;
                break;
            default:
                break;
        }

        showDialog();
        return super.onOptionsItemSelected(item);
    }

    // "level"
    private void saveSetValue(String key, int level) {
        Editor editor = sharedPreferences.edit();
        editor.putInt(key, level);
        editor.commit();
    }

    private void initSetValue() {
        level = sharedPreferences.getInt("level", 1);
        rollingTime = sharedPreferences.getInt("rolling_time", 10);
        sleepTimeLevel = sharedPreferences.getInt("sleepTimeLevel", 2);

        greenShowTime = sharedPreferences.getInt("green_show_time", 5);
        yellowShowTime = sharedPreferences.getInt("yellow_show_time", 5);
        redShowTime = sharedPreferences.getInt("red_show_time", 5);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        boolean rnt = true;

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            showDialog();
        } else {
            rnt = super.onKeyDown(keyCode, event);
        }
        return rnt;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
