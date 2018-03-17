package com.smartbell.db;

import com.smartbell.bean.ItemConfig;

import io.realm.Realm;

/**
 * Created by Curry on 18/3/17.
 * 数据库工具
 */

public class DBManager {

    private static final int DEFAULT_INDEX = 20;

    /**
     * 获取指定item的数据
     * @param index item序列号
     * @return 返回item数据
     */
    public static ItemConfig getItemConfig(int index){
        Realm realm = Realm.getDefaultInstance();
        ItemConfig itemConfig = null;

        try {
            ItemConfig itemConfigDB = realm.where(ItemConfig.class).equalTo("index", index).findFirst();
            if (itemConfigDB != null) {
                itemConfig = itemConfigDB.copySelf();
            }
        } finally {
            realm.close();
        }

        return itemConfig;
    }

    /**
     * item数据更新到数据库
     * @param index item序号
     * @param oneTimeSec 绿色背景显示时间
     * @param twoTimeSec 黄色背景显示时间
     */
    public static void setItemConfig(final int index, final int oneTimeSec, final int twoTimeSec) {
        Realm realm = Realm.getDefaultInstance();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    ItemConfig itemConfig = realm.where(ItemConfig.class).equalTo("index", index).findFirst();
                    if (itemConfig == null) {
                        itemConfig = realm.createObject(ItemConfig.class);
                    }
                    itemConfig.index = index;
                    itemConfig.oneTimeSec = oneTimeSec;
                    itemConfig.twoTimeSec = twoTimeSec;
                }
            });
        } finally {
            realm.close();
        }
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
