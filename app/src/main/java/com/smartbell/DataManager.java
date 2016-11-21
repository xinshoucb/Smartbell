package com.smartbell;

import android.util.Log;

import java.util.ArrayList;

public class DataManager {
    private final static String TAG = "DataManager == ";
    private final static int DATA_COUNT_MAX = 80;

    private MainActivity mAc;
    private CallBack mCallBack;

    private final ArrayList<DataInfo> mDataList = new ArrayList<DataInfo>(DATA_COUNT_MAX);

    private DataService.CallBack dataServiceCallBack = new DataService.CallBack() {

        @Override
        public void updateData(String dataStr) {
            if (dataStr == null || dataStr.equals("")) {
                return;
            }

            String ctrlStr = dataStr.substring(0, 1);
            String bellStr = dataStr.substring(1);

            if ("#".equals(ctrlStr)) {
                addData(bellStr);
            } else if ("+".equals(ctrlStr)) {
                // flashData(bellStr);
            } else if ("-".equals(ctrlStr)) {
//                cancelData(bellStr);
                cancelData();
            }

            Log.d("chenbo", TAG + "ctrlStr = " + ctrlStr + " bellStr = " + bellStr + " dataStr = " + dataStr);
        }

    };

    public DataManager(MainActivity mAc, final CallBack mCallBack) {
        super();
        this.mAc = mAc;
        this.mCallBack = mCallBack;
    }

    private void cancelData(String bellStr) {
        if(null == bellStr)
            return;

        int postion = -1;
        for (int i = 0; i < mDataList.size(); i++) {
            if(bellStr.equals(mDataList.get(i).getData())){
                postion = i;
                break;
            }
        }

        if(postion < mDataList.size() && postion >= 0){
            mDataList.remove(postion);
            mCallBack.refreshView(mDataList);
        }
    }

    private void cancelData() {
        if(0 < mDataList.size()){
            mDataList.remove(0);
            mCallBack.refreshView(mDataList);
        }
    }

    private void addData(String dataStr) {
        int dataSize = mDataList.size();

        if (dataStr.equals("") || dataSize >= DATA_COUNT_MAX) {
            return;
        }

        if (dataSize > 0 && isContains(dataStr) >= 0) {
            return;
        }

        DataInfo dataInfo = new DataInfo(dataStr, 0, System.currentTimeMillis());
        mDataList.add(dataInfo);
        mCallBack.refreshView(mDataList);
    }

    private int isContains(String dataStr) {
        int rtn = -1;

        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).getData().equals(dataStr)) {
                rtn = i;
                break;
            }
        }

        return rtn;
    }

    public DataService.CallBack getDataServiceCallBack() {
        return dataServiceCallBack;
    }

    public interface CallBack {
        public void refreshView(ArrayList<DataInfo> datas);
    }

}
