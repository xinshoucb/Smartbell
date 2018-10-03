package com.smartbell.data;

import android.os.Message;
import android.util.Log;

import com.smartbell.TestUtils;

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

    private ServerSocket server;
    private static final int PORT = 30001;


    @Override
    public void startTask() {
        if (null == server) {
            Log.d(TAG, "server == null");
            try {
                // server = new ServerSocket(PORT);
                server = new ServerSocket();
                server.setReuseAddress(true);
                server.bind(new InetSocketAddress(PORT));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.d(tag, TAG + "new ServerSocket failed : " + e.toString());
                stopSelf();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stopTask() {

    }

    @Override
    public void registerCallback() {

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
                        Socket socket = server.accept();
                        Log.d(tag, TAG + "accept after");

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
                                    msg.arg1 = 0;
                                    handler.sendMessageDelayed(msg,TIME_OUT);
                                }
                            }

                            Thread.sleep(500);
                        }

                        if (null != writer)
                            writer.close();
                        reader.close();
                        Log.d(tag, TAG + " while end ");
                    } catch (Exception e) {
                        Log.d(tag, TAG + "IOException " + e.toString());
                        e.printStackTrace();
                    } finally {
                        isStop = true;

                        Message msg = Message.obtain();
                        msg.what = HANDLER_REACCEPT;
                        msg.arg1 = 1;
                        handler.sendMessage(msg);
                    }

                }
            }

        }).start();
    }

}
