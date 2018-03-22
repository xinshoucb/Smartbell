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
     * @param key item序列号
     * @return 返回item数据
     */
    public static ItemConfig getItemConfig(String key){
        ItemConfig itemConfig = new ItemConfig();
//        itemConfig.index = index;
        itemConfig.oneTimeSec = SPUtil.getPreference(SP_KEY_PREFIX_ONE_TIME+key, 0);
        itemConfig.twoTimeSec = SPUtil.getPreference(SP_KEY_PREFIX_TWO_TIME+key, 0);

        Log.d("getItemConfig","key="+key+" one="+itemConfig.oneTimeSec+" two="+itemConfig.twoTimeSec);

        return itemConfig;
    }

    /**
     * item数据更新到数据库
     * @param key 数字串
     * @param oneTimeSec 绿色背景显示时间
     * @param twoTimeSec 黄色背景显示时间
     */
    public static void setItemConfig(final String key, final int oneTimeSec, final int twoTimeSec) {
        Log.d("setItem","key="+key+" one="+oneTimeSec+" two="+twoTimeSec);
        SPUtil.savePreference(SP_KEY_PREFIX_ONE_TIME+key, oneTimeSec);
        SPUtil.savePreference(SP_KEY_PREFIX_TWO_TIME+key, twoTimeSec);
    }

    public static ItemConfig getDefaultItemConfig(){
        return getItemConfig(String.valueOf(DEFAULT_INDEX));
    }

    public static void setDefaultItemConfig(final int oneTimeSec, final int twoTimeSec){
        setItemConfig(String.valueOf(DEFAULT_INDEX), oneTimeSec, twoTimeSec);
    }

    /**
     * 获取指定item的数据，如果没有就获取默认数据
     * @param index item序列号
     * @return 返回item数据
     */
    public static ItemConfig getItemConfigHasDefault(String index){
        ItemConfig itemConfig = getItemConfig(index);
        if (itemConfig != null) {
            return itemConfig;
        }

        return getItemConfig(String.valueOf(DEFAULT_INDEX));
    }

    /**
     * item数据更新到数据库，并且同时刷新默认值
     * @param key item序号
     * @param oneTimeSec 绿色背景显示时间
     * @param twoTimeSec 黄色背景显示时间
     */
    public static void setItemConfigAndDefault(final String key, final int oneTimeSec, final int twoTimeSec){
        setItemConfig(key, oneTimeSec, twoTimeSec);
        setItemConfig(String.valueOf(DEFAULT_INDEX), oneTimeSec, twoTimeSec);
    }

}
