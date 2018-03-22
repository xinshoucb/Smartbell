package com.smartbell;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DataService extends Service {
    private final static String TAG = "DataService --> ";

    private static final int DATA_MAX_COUNT = 4;

    private static final int HANDLER_REFREASH_VIEW = 0;
    private static final int HANDLER_REACCEPT = 1;
    private static final long TIME_OUT = 20 * 1000;

    private static final int PORT = 30006;
    private ServerSocket server;
    private Object mLock = new Object();

    private final IBinder binder = new MsgBinder();

    private boolean isStop = false;
    private String data;

    public static final int ACCEPTTING = 1;
    public static final int ACCEPTTED = 2;
    private int serverStatus;

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
                    reConnect();
                    Log.i(TAG, "reaccept = "+msg.arg1);
                    LogView.setLog(TAG + "reaccept = "+msg.arg1);
                    break;
                default:
                    break;
            }
        }
    };
    private String tag = "chenbo";

    private String praseData(String data) {
        int lenght = data.length();
//		String rtn = data.substring(lenght - 19, lenght - 16);
        String rtn = data.substring(lenght - 20, lenght - 15);
        Log.d("chenbo", TAG + "praseData rtn = " + rtn);
        return rtn;
    }

    private void noticeActivity() {
        Log.d(tag, TAG + "noticeActivity mCallBack = " + mCallBack);
        if (data == null) {
            return;
        }

        if (mCallBack == null) {
            isStop = true;
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startMainActivity();
                    connectAndGetData();
                }
            },1000);
        } else {
            mCallBack.updateData(new String(data));
            data = null;
        }
    }



    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        Log.d(tag, TAG + " onCreate ");
        LogView.setLog(TAG + "onCreate");
        super.onCreate();

        reConnect();
    }

    private void reConnect(){
        Log.d(tag, TAG + "reConnect server");
        try {
            if (null != server) {
                server.close();
            }

            // server = new ServerSocket(PORT);
            server = new ServerSocket();
            server.setReuseAddress(true);
            server.bind(new InetSocketAddress(PORT));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.d(tag, TAG + "new ServerSocket failed : " + e.toString());
            LogView.setLog(TAG + "new ServerSocket failed : " + e.toString());
            stopSelf();
            e.printStackTrace();
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
                synchronized (mLock) { 
                    isStop = false;
//                    Looper.prepare();

                    try {
                        Log.d(tag, TAG + "accept before ");
                        LogView.setLog(TAG + "accept before");
                        Socket socket = server.accept();
                        Log.d(tag, TAG + "accept after isStop="+isStop);
                        LogView.setLog(TAG + "accept after isStop="+isStop);

                        BufferedInputStream reader = null;
                        DataOutputStream writer = null;
                        InputStream is = socket.getInputStream();

                        int bufLenght = 0;
                        byte[] buf = null;

                        reader = new BufferedInputStream(is);
                        String content = null;

                        while (!isStop) {
                            content = "";
                            ArrayList<String> contents = new ArrayList<String>();
                            bufLenght = is.available();
                            if (bufLenght > 0) {
                                handler.removeMessages(HANDLER_REACCEPT);

                                buf = new byte[bufLenght];
                                reader.read(buf);
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < bufLenght; i++) {
                                    content += (char)buf[i];

                                    Log.d(tag, TAG + "buf="+buf[i]+"  "+sb.length());
                                    if(buf[i] == -1 || buf[i] == 1){
                                        Log.d(tag, TAG + "buf[i] == -1 || buf[i] == 1  "+sb.length());
                                        if (sb.length() > 0) {
                                            contents.add(sb.toString());
                                            sb = new StringBuilder();
                                            Log.d(tag, TAG + "create new content");
                                        }
                                    }else{
                                        sb.append((char)buf[i]);
                                    }
                                }

                                if (sb.length() > 0) {
                                    contents.add(sb.toString());
                                }

                                Log.d(tag, TAG + "content = " + content + " bufLenght = " + bufLenght+"  "+contents.size());

                                for (String contentStr : contents) {
                                    Log.d(tag, TAG + "contentStr = " + contentStr + " bufLenght = " + contentStr.length());
                                    if (!contentStr.equals("")) {
                                        if ((contentStr.length() == 3) || (contentStr.length() == 44)) {
                                            Log.d(tag, TAG + "msg bufLenght = " + bufLenght);
                                        } else {
                                            Log.d(tag, TAG + "msg mix bufLenght = " + bufLenght);
                                            continue;
                                        }

                                        if (contentStr.equals("ask")) {
                                            writer = new DataOutputStream(socket.getOutputStream());
                                            writer.write(0x55);
                                            writer.write(0x01);
                                            writer.write(0x00);
                                            writer.write(0xaa);
                                            writer.flush();
                                        } else {
                                            Message msg = Message.obtain();
                                            msg.what = HANDLER_REFREASH_VIEW;
                                            msg.obj = contentStr;
                                            handler.sendMessage(msg);
                                        }
                                    } else {

                                    }
                                }
                            }else{
                                if (!handler.hasMessages(HANDLER_REACCEPT)) {
                                    Message msg = Message.obtain();
                                    msg.what = HANDLER_REACCEPT;
                                    msg.arg1 = 1;
                                    handler.sendMessageDelayed(msg,TIME_OUT);
                                }
                            }

                            Thread.sleep(500);
                        }

                        if (null != writer) {
                            writer.close();
                        }
                        reader.close();
                        Log.d(tag, TAG + " while end ");
                    } catch (Exception e) {

                        Message msg = Message.obtain();
                        msg.what = HANDLER_REACCEPT;
                        msg.arg1 = 1;
                        handler.sendMessageDelayed(msg,1000);
                        Log.d(tag, TAG + "IOException " + e.toString());
                        LogView.setLog(TAG + "IOException " + e.toString());
                        e.printStackTrace();
                    } finally {
                        isStop = true;
                    }

                }
            }

        }).start();
    }


    int count = 0;
    String[] testData = {"#334","#201","#2d2","#2d2","-201"};
//            "#200","#2d0","#2d5","#300",
//            "#500","#600","#400","#336",
//            "#359","#2011","#2d22","#2d23",
//            "#2004","#2d05","#2d56","#3007",
//            "#5060","#6040"};//, "#2d2", "#303", "#334", "#405", "#506", "-2d2", "-334", "#603", "#702", "-405", "-506", "#aa6", "#A23", "#d02", "-702", "-702"};

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
        }, 5000, 5000);
    }

    private boolean isMainActivityTop() {
        boolean rtn = false;

        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

        Log.d(tag, TAG + "pkg:" + cn.getPackageName() + " cls:" + cn.getClassName());
        if ("com.example.bell_x3.MainActivity".equals(cn.getClassName())) {
            rtn = true;
        }

        return rtn;
    }

    private void startMainActivity() {
        Log.d(tag, TAG + " start activity ");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("initData", data);
        startActivity(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(tag, TAG + " onStartCommand ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(tag, TAG + " onBind ");
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
        Log.e(tag, TAG + " onDestroy ");
        isStop = true;
        super.onDestroy();
    }

    interface CallBack {
        public void updateData(String dataStr);
    }
}
