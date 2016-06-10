package com.smartbell;

import android.graphics.Color;

public class DataInfo {

    private String data;
    private long startTime;
    private long showTime;
    private boolean isFlash;
    private int textColor;
    private int backgroundColor;

    public DataInfo(String data, long showTime, long startTime) {
        super();
        this.data = data;
        this.showTime = showTime;
        this.startTime = startTime;

        initColorByData();
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getShowTime() {
        return showTime;
    }

    public void setShowTime(long showTime) {
        this.showTime = showTime;
    }

    public boolean isFlash() {
        return isFlash;
    }

    public void setFlash(boolean isFlash) {
        this.isFlash = isFlash;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    private void initColorByData(){
        textColor = Color.BLACK;
        backgroundColor = R.color.green;  //default value

        if(data == null){
            return;
        }

        int dataInt = BellUtils.str2Int(data);

        if(dataInt >= 0){ //纯数字
            int hundredInt = dataInt / 100;
            switch (hundredInt){
                case 2:
                    backgroundColor = R.color.blue;
                    break;
                case 3:
                    int tenInt = (dataInt - hundredInt * 100) / 10;
                    if(tenInt < 3){
                        backgroundColor = R.color.white;
                    }else{
                        backgroundColor = R.color.blue;
                    }
                    break;
                case 4:
                    backgroundColor = R.color.white;
                    break;
                case 5:
                case 6:
                    backgroundColor = R.color.yellow;
                    break;
                default:
            }
        }else{ //含字母

            String hundedStr = data.substring(0,1);
            String postTwoStr = data.substring(1,3);
            int hundredInt = BellUtils.str2Int(hundedStr);
            int postTwoInt = BellUtils.str2Int(postTwoStr);

            if(hundredInt < 0 && postTwoInt >= 0) { //百位是字母,个位十位都是数字
                hundedStr = hundedStr.toUpperCase();
                if (hundedStr.startsWith("A") || hundedStr.startsWith("B") || hundedStr.startsWith("C")){
                    backgroundColor = R.color.orange;
                }else if (hundedStr.startsWith("D") || hundedStr.startsWith("E") || hundedStr.startsWith("F")){
                    backgroundColor = R.color.gray;
                }
            }
        }

    }
}
