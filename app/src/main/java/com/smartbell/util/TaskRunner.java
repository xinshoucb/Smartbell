package com.smartbell.util;

/**
 * TaskRunner class
 *
 * @author hangwei
 * @date 2018/10/4
 */
public abstract class TaskRunner implements Runnable {
    protected boolean isCancel = false;

    public void cancel(){
        isCancel = true;
    }

    public boolean isCancel(){
        return isCancel;
    }
}