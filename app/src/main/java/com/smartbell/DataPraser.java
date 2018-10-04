package com.smartbell;

import android.util.Log;

import java.util.ArrayList;

/**
 * DataPraser class
 *
 * @author hangwei
 * @date 2018/10/4
 */
public class DataPraser {
    public static final String TAG = "DataPraser";

    public static ArrayList<String> buffer2String(byte[] buf){
        if (buf == null) {
            return null;
        }

        ArrayList<String> contents = new ArrayList<String>();
        String content = "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            content += (char)buf[i];

            Log.d(TAG, "buf="+buf[i]+"  "+sb.length());
            if(buf[i] == -1 || buf[i] == 1){
                Log.d(TAG, "buf[i] == -1 || buf[i] == 1  "+sb.length());
                if (sb.length() > 0) {
                    contents.add(sb.toString());
                    sb = new StringBuilder();
                }
            }else{
                sb.append((char)buf[i]);
            }
        }

        if (sb.length() > 0) {
            contents.add(sb.toString());
        }

        Log.d(TAG, "conten =" + content + " bufLenght=" + buf.length+"，contents.size="+contents.size());

        return contents;
    }

    public static String rawStr2Ctrl(String raw) {
        int lenght = raw.length();
        String rtn = raw.substring(lenght - 20, lenght - 15);
        Log.d(TAG, "rawStr2Ctrl rtn = " + rtn);
        return rtn;
    }
}
