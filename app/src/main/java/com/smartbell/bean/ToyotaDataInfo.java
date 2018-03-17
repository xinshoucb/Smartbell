package com.smartbell.bean;

/**
 * Created by Curry on 18/3/17.
 * TOYOTA 项目的数据
 */
public class ToyotaDataInfo {
    private String data;
    private long startTime;
    private String textColor;
    private String backgroundColor;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
