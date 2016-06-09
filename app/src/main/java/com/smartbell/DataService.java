package com.smartbell;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class DataService extends Service {
    private final static String TAG = "DataService --> ";

    private static final int DATA_MAX_COUNT = 4;

    private static final int HANDLER_REFREASH_VIEW = 0;
    private static final int HANDLER_REACCEPT = 1;
    private static final long TIME_OUT = 20 * 1000;

    private static final int PORT = 30001;
    private ServerSocket server;
    private Object mLock = new Object();

    private final IBinder binder = new MsgBinder();

    private boolean isStop = false;
    private String data;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_REFREASH_VIEW:
                    if (TestUtils.isTest) {
                        data = (String) msg.obj;
                    } else {
                        data = praseData((String) msg.obj);
                    }
                    noticeActivity();
                    break;
                case HANDLER_REACCEPT:
                    isStop = true;
                    Log.i(TAG, "chenbo reaccept");
                    connectAndGetData();
                    break;
                default:
                    break;
            }
        }
    };

    private String praseData(String data) {
        int lenght = data.length();
//		String rtn = data.substring(lenght - 19, lenght - 16);
        String rtn = data.substring(lenght - 20, lenght - 16);
        Log.d("chenbo", TAG + "praseData rtn = " + rtn);
        return rtn;
    }

    private void noticeActivity() {
        Log.d("chenbo", TAG + "noticeActivity mCallBack = " + mCallBack);
        if (data == null)
            return;

        if (mCallBack == null) {
            startMainActivity();
        } else {
            mCallBack.updateData(new String(data));
            data = null;
        }
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        Log.d("chenbo", TAG + " onCreate ");
        super.onCreate();

        if (null == server) {
            Log.d("chenbo", TAG + "server == null");
            try {
                // server = new ServerSocket(PORT);
                server = new ServerSocket();
                server.setReuseAddress(true);
                server.bind(new InetSocketAddress(PORT));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.d("chenbo", TAG + "new ServerSocket failed : " + e.toString());
                stopSelf();
                e.printStackTrace();
            }
        }

        connectAndGetData();
    }

    private void connectAndGetData() {
        if (TestUtils.isTest) {
            testData();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (mLock) { // һ��ֻ��һ���߳̽�������
                    isStop = false;
                    Looper.prepare();

                    try {
                        Log.d("chenbo", TAG + "accept before ");
                        Socket socket = server.accept();
                        Log.d("chenbo", TAG + "accept after");

                        BufferedReader reader = null;
                        DataOutputStream writer = null;
                        InputStream is = socket.getInputStream();

                        int bufLenght = 0;
                        char[] buf = null;

                        reader = new BufferedReader(new InputStreamReader(is));
                        String content = null;
                        while (!isStop) {
                            content = "";
                            bufLenght = is.available();
                            if (bufLenght > 0) {
                                buf = new char[bufLenght];
                                reader.read(buf);
                                if (4 == buf.length) {
                                    for (int i = 1; i < bufLenght; i++) {
                                        content += buf[i];
                                    }
                                } else {
                                    for (int i = 0; i < bufLenght; i++) {
                                        content += buf[i];
                                    }
                                }
                            }

                            Log.d("chenbo", TAG + "content = " + content + " bufLenght = " + bufLenght);
                            if (!content.equals("")) {
                                handler.removeMessages(HANDLER_REACCEPT);

                                if ((bufLenght == 4) || (bufLenght == 46)) {

                                } else {
                                    Log.d("chenbo", TAG + "���ݴ��� bufLenght = " + bufLenght);
                                    continue;
                                }

                                if (content.equals("ask")) {
                                    writer = new DataOutputStream(socket.getOutputStream());
                                    writer.write(0x55);
                                    writer.write(0x01);
                                    writer.write(0x00);
                                    writer.write(0xaa);
                                    writer.flush();
                                } else {
                                    Message msg = Message.obtain();
                                    msg.what = HANDLER_REFREASH_VIEW;
                                    msg.obj = content;
                                    handler.sendMessage(msg);
                                }
                            } else {
                                if (!handler.hasMessages(HANDLER_REACCEPT)) {
                                    handler.sendEmptyMessageDelayed(HANDLER_REACCEPT, TIME_OUT);
                                }
                                Thread.sleep(500);
                            }
                        }

                        if (null != writer)
                            writer.close();
                        reader.close();
                        Log.d("chenbo", TAG + " while end ");
                    } catch (Exception e) {
                        handler.sendEmptyMessage(HANDLER_REACCEPT);
                        Log.d("chenbo", TAG + "IOException " + e.toString());
                        e.printStackTrace();
                    } finally {
                        isStop = true;
                    }

                }
            }

        }).start();
    }

    int count = 0;
    String[] testData = {"#001", "#002", "#003", "#004", "#005", "#006", "-003", "-002"};

    private void testData() {

        final Timer mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message msg = Message.obtain();
                msg.what = HANDLER_REFREASH_VIEW;
                msg.obj = testData[count];
                handler.sendMessage(msg);

                count++;

                if (count >= testData.length)
                    mTimer.cancel();
            }
        }, 5000, 10000);
    }

    private boolean isMainActivityTop() {
        boolean rtn = false;

        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

        Log.d("chenbo", TAG + "pkg:" + cn.getPackageName() + " cls:" + cn.getClassName());
        if ("com.example.bell_x3.MainActivity".equals(cn.getClassName())) {
            rtn = true;
        }

        return rtn;
    }

    private void startMainActivity() {
        Log.d("chenbo", TAG + " start activity ");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("initData", data);
        startActivity(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("chenbo", TAG + " onStartCommand ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("chenbo", TAG + " onBind ");
        return binder;
    }

    public class MsgBinder extends Binder {

        public DataService getService() {
            return DataService.this;
        }
    }

    private CallBack mCallBack;

    public void setCallBack(CallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public void removeCallBack() {
        mCallBack = null;
    }

    public void getDatas() {
        noticeActivity();
    }

    @Override
    public void onDestroy() {
        Log.e("chenbo", TAG + " onDestroy ");
        super.onDestroy();
    }

    interface CallBack {
        public void updateData(String dataStr);
    }
}
