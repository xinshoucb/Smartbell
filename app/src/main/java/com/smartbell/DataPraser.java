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
    public static StringBuilder lastSb;

    public final static int DATA_LENGTH = 44;

    public static ArrayList<String> buffer2String(byte[] buf, int length){
        if (buf == null) {
            return null;
        }

        ArrayList<String> contents = new ArrayList<String>();
        String content = "";

        StringBuilder sb = lastSb == null ? new StringBuilder() : lastSb;
        LogView.setLog("lastStr="+sb.toString()+",raw conten =" + new String(buf));

        for (int i = 0; i < length; i++) {
            content += (char)buf[i];

            if(buf[i] == -1 || buf[i] == 1){
                if (sb.length() > 0) {
                    contents.add(sb.toString());
                    sb = new StringBuilder();
                }
            }else{
                sb.append((char)buf[i]);
            }
        }

        if (sb.length() > 0) {
            String tmpStr = sb.toString();
            if(!"ask".equals(tmpStr) && sb.length() != DATA_LENGTH){
                lastSb = sb;
            }else{
                contents.add(tmpStr);
                lastSb = null;
            }
        }

        Log.d(TAG, "conten =" + content + " bufLenght=" + buf.length+"，contents.size="+contents.size());

        return contents;
    }

    public static String rawStr2Ctrl(String raw) {
        int lenght = raw.length();
        String rtn = raw.substring(lenght - 20, lenght - 16);
        Log.d(TAG, "rawStr2Ctrl rtn = " + rtn);
        return rtn;
    }
}
