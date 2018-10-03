package com.smartbell.data;

import android.util.Log;

import com.smartbell.LogView;
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

    private ServerSocket server;
    private static final int PORT = 30006;

    private Object mLock = new Object();
    private boolean isStop = false;

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
        ThreadPoolUtil.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                synchronized (mLock) {
                    isStop = false;

                    try {
                        Log.d(TAG, "accept before ");
                        LogView.setLog(TAG + "accept before");
                        Socket socket = server.accept();
                        Log.d(TAG, "accept after isStop="+isStop);
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
                                exception(EXCEPTION_IO);

                                buf = new byte[bufLenght];
                                reader.read(buf);
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < bufLenght; i++) {
                                    content += (char)buf[i];

                                    Log.d(TAG, "buf="+buf[i]+"  "+sb.length());
                                    if(buf[i] == -1 || buf[i] == 1){
                                        Log.d(TAG, "buf[i] == -1 || buf[i] == 1  "+sb.length());
                                        if (sb.length() > 0) {
                                            contents.add(sb.toString());
                                            sb = new StringBuilder();
                                            Log.d(TAG, "create new content");
                                        }
                                    }else{
                                        sb.append((char)buf[i]);
                                    }
                                }

                                if (sb.length() > 0) {
                                    contents.add(sb.toString());
                                }

                                Log.d(TAG, "content = " + content + " bufLenght = " + bufLenght+"  "+contents.size());

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
                                            update(contentStr);
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
                    } finally {
                        isStop = true;
                    }

                }
            }
        });
    }

    private String data;

    private String praseData(String data) {
        int lenght = data.length();
        String rtn = data.substring(lenght - 20, lenght - 15);
        Log.d("chenbo", TAG + "praseData rtn = " + rtn);
        return rtn;
    }

}
