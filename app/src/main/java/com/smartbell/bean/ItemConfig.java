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

    public ItemConfig copySelf(){
        ItemConfig rtnObject = new ItemConfig();

        rtnObject.index = index;

        rtnObject.oneColor = oneColor;
        rtnObject.twoColor = twoColor;
        rtnObject.thrColor = thrColor;

        rtnObject.oneTimeSec = oneTimeSec;
        rtnObject.twoTimeSec = twoTimeSec;
        rtnObject.thrTimeSec = thrTimeSec;


        return rtnObject;
    }
}
