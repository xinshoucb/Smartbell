package com.smartbell.bean;

import io.realm.RealmObject;

/**
 * Created by Curry on 18/3/17.
 */

public class ItemConfig extends RealmObject {
//public class ItemConfig {

    public int index;

    // setBackgroundColor(Color.parseColor("#3FE2C5"));//使用颜色的16进制类型值
    public String oneColor = "#90EE90";
    public String twoColor = "#EEEE00";
    public String thrColor = "#FF4500";

    // 0 ~ 999
    public int oneTimeSec = 0;
    public int twoTimeSec = 0;
    public int thrTimeSec = 0;
}
