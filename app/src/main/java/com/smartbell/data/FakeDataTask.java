package com.smartbell.data;

import android.content.Context;

import java.util.Timer;
import java.util.TimerTask;

/**
 * FakeDateTask class
 *
 * @author hangwei
 * @date 2018/10/3
 */
public class FakeDataTask extends BaseDataTask {
    int count = 0;
//    String[] testData = {"#201","#334","#201","#2d2","#2d2","-333"};
//            "#200","#2d0","#2d5","#300",
//            "#500","#600","#400","#336",
//            "#359","#2011","#2d22","#2d23",
//            "#2004","#2d05","#2d56","#3007",
//            "#5060","#6040"};//,
String[] testData = {"#2d2", "#303", "#334", "#405", "#506", "-2d2", "-334", "#603", "#702", "-405", "-506", "#aa6", "#A23", "#d02", "-702", "-702"};


    private Timer mTimer;

    public FakeDataTask(Context context) {
        super(context);
    }

    @Override
    public void startTask() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                update(testData[count]);

                count++;

                if (count >= testData.length) {
                    mTimer.cancel();
                }
            }
        }, 5000, 5000);
    }

    @Override
    public void stopTask() {
        if (mTimer != null) {
            mTimer.cancel();
        }
    }
}
