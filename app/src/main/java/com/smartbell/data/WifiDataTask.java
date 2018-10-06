package com.smartbell.data;

import android.content.Context;
import android.util.Log;

import com.smartbell.DataPraser;
import com.smartbell.LogView;
import com.smartbell.util.TaskRunner;
import com.smartbell.util.ThreadPoolUtil;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * WifiDataTask class
 *
 * @author hangwei
 * @date 2018/10/3
 */
public class WifiDataTask extends BaseDataTask {
    public static final String TAG = "WifiDataTask";
    private static final long TIME_OUT = 20 * 1000;

    private final Object mLock = new Object();
    private static final int PORT = 30006;

    private ServerSocket server;
    private TaskRunner taskRunner;

    public WifiDataTask(Context context) {
        super(context);
    }

    @Override
    public void startTask() {
        try {
            if (null != server) {
                server.close();
            }

            server = new ServerSocket();
            server.setReuseAddress(true);
            server.bind(new InetSocketAddress(PORT));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.d(TAG, "new ServerSocket failed : " + e.toString());
            LogView.setLog(TAG + "new ServerSocket failed : " + e.toString());
//            stopSelf();
            exception(EXCEPTION_IO);
            e.printStackTrace();
        }

        connectAndGetData();
    }

    @Override
    public void stopTask() {
        try {
            if (server != null) {
                server.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectAndGetData() {
        taskRunner = new TaskRunner() {
            @Override
            public void run() {
                synchronized (mLock) {
                    try {
                        Log.d(TAG, "accept before ");
                        LogView.setLog(TAG + "accept before");
                        Socket socket = server.accept();

                        BufferedInputStream reader = null;
                        DataOutputStream writer = null;
                        InputStream is = socket.getInputStream();

                        int bufLenght = 0;
                        byte[] buf = null;

                        reader = new BufferedInputStream(is);
                        while (!isCancel) {
                            bufLenght = is.available();
                            if (bufLenght > 0) {
                                buf = new byte[bufLenght];
                                reader.read(buf);
                                ArrayList<String> contents = DataPraser.buffer2String(buf, bufLenght);

                                for (String contentStr : contents) {
                                    Log.d(TAG, "contentStr = " + contentStr + " bufLenght = " + contentStr.length());
                                    if (!contentStr.equals("")) {
                                        if ((contentStr.length() == 3) || (contentStr.length() == 44)) {
                                            Log.d(TAG, "msg bufLenght = " + bufLenght);
                                        } else {
                                            Log.d(TAG, "msg mix bufLenght = " + bufLenght);
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
                                            update(DataPraser.rawStr2Ctrl(contentStr));
                                        }
                                    } else {

                                    }
                                }
                            }else{
//                                if (!handler.hasMessages(HANDLER_REACCEPT)) {
//                                    Message msg = Message.obtain();
//                                    msg.what = HANDLER_REACCEPT;
//                                    msg.arg1 = 1;
//                                    handler.sendMessageDelayed(msg,TIME_OUT);
//                                }
                            }

                            Thread.sleep(500);
                        }

                        if (null != writer) {
                            writer.close();
                        }
                        reader.close();
                        Log.d(TAG, " while end ");
                    } catch (Exception e) {

//                        Message msg = Message.obtain();
//                        msg.what = HANDLER_REACCEPT;
//                        msg.arg1 = 1;
//                        handler.sendMessageDelayed(msg,1000);
//                        Log.d(TAG, "IOException " + e.toString());
//                        LogView.setLog(TAG + "IOException " + e.toString());
                        e.printStackTrace();
                    }

                }
            }
        };

        ThreadPoolUtil.getThreadPool().execute(taskRunner);
    }

}
