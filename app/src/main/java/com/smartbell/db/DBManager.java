package com.smartbell.db;

import android.util.Log;

import com.smartbell.bean.ItemConfig;

/**
 * Created by Curry on 18/3/17.
 * 数据库工具
 */

public class DBManager {

    private static final int DEFAULT_INDEX = 20;
    public static final String SP_KEY_PREFIX_ONE_TIME = "one_color_time_";
    public static final String SP_KEY_PREFIX_TWO_TIME = "two_color_time_";

    /**
     * 获取指定item的数据
     * @param index item序列号
     * @return 返回item数据
     */
    public static ItemConfig getItemConfig(int index){
        ItemConfig itemConfig = new ItemConfig();
        itemConfig.index = index;
        itemConfig.oneTimeSec = SPUtil.getPreference(SP_KEY_PREFIX_ONE_TIME+index, 0);
        itemConfig.twoTimeSec = SPUtil.getPreference(SP_KEY_PREFIX_TWO_TIME+index, 0);

        Log.d("getItemConfig","index="+index+" one="+itemConfig.oneTimeSec+" two="+itemConfig.twoTimeSec);

        return itemConfig;
    }

    /**
     * item数据更新到数据库
     * @param index item序号
     * @param oneTimeSec 绿色背景显示时间
     * @param twoTimeSec 黄色背景显示时间
     */
    public static void setItemConfig(final int index, final int oneTimeSec, final int twoTimeSec) {
        Log.d("setItem","index="+index+" one="+oneTimeSec+" two="+twoTimeSec);
        SPUtil.savePreference(SP_KEY_PREFIX_ONE_TIME+index, oneTimeSec);
        SPUtil.savePreference(SP_KEY_PREFIX_TWO_TIME+index, twoTimeSec);
    }

    public static ItemConfig getDefaultItemConfig(){
        return getItemConfig(DEFAULT_INDEX);
    }

    public static void setDefaultItemConfig(final int oneTimeSec, final int twoTimeSec){
        setItemConfig(DEFAULT_INDEX, oneTimeSec, twoTimeSec);
    }

    /**
     * 获取指定item的数据，如果没有就获取默认数据
     * @param index item序列号
     * @return 返回item数据
     */
    public static ItemConfig getItemConfigHasDefault(int index){
        ItemConfig itemConfig = getItemConfig(index);
        if (itemConfig != null) {
            return itemConfig;
        }

        return getItemConfig(DEFAULT_INDEX);
    }

    /**
     * item数据更新到数据库，并且同时刷新默认值
     * @param index item序号
     * @param oneTimeSec 绿色背景显示时间
     * @param twoTimeSec 黄色背景显示时间
     */
    public static void setItemConfigAndDefault(final int index, final int oneTimeSec, final int twoTimeSec){
        setItemConfig(index, oneTimeSec, twoTimeSec);
        setItemConfig(DEFAULT_INDEX, oneTimeSec, twoTimeSec);
    }

}
