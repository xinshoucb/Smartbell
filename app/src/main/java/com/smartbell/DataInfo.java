package com.smartbell;

public class DataInfo {

    private String data;
    private long startTime;
    private long showTime;
    private boolean isFlash;

    public DataInfo(String data, long showTime, long startTime) {
        super();
        this.data = data;
        this.showTime = showTime;
        this.startTime = startTime;
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

}
