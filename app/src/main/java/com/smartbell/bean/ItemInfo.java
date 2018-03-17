package com.smartbell.bean;


/**
 * Created by Curry on 18/3/17.
 */
public class ItemInfo {
    private ToyotaDataInfo dataInfo;
    private ItemConfig itemConfig;

    public ToyotaDataInfo getDataInfo() {
        return dataInfo;
    }

    public void setDataInfo(ToyotaDataInfo dataInfo) {
        this.dataInfo = dataInfo;
    }

    public ItemConfig getItemConfig() {
        return itemConfig;
    }

    public void setItemConfig(ItemConfig itemConfig) {
        this.itemConfig = itemConfig;
    }
}
