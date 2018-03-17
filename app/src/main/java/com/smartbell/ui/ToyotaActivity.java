package com.smartbell.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.smartbell.R;
import com.smartbell.bean.ItemConfig;
import com.smartbell.bean.ItemInfo;
import com.smartbell.bean.ToyotaDataInfo;

import java.util.ArrayList;
import java.util.List;

public class ToyotaActivity extends Activity {

    public static final int GRID_COLUMN_NUM = 4;
    private TextView logoTv;
    private RecyclerView dataRv;
    private RvDataAdapter mRvDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toyota);

        initView();
        initData();
    }

    private void initData() {
        mRvDataAdapter = new RvDataAdapter();
        dataRv.setAdapter(mRvDataAdapter);

        mRvDataAdapter.refreshData(fakeData());
    }

    private void initView() {
        logoTv = (TextView) findViewById(R.id.toyota_logo_tv);
        logoTv.setVisibility(View.GONE);

        dataRv = (RecyclerView) findViewById(R.id.toyota_data_rv);
        dataRv.setLayoutManager(new GridLayoutManager(this, GRID_COLUMN_NUM));
    }

    private List<ItemInfo> fakeData(){
        List<ItemInfo> lists = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.setDataInfo(new ToyotaDataInfo());
            itemInfo.setItemConfig(new ItemConfig());
            itemInfo.getDataInfo().setData(String.valueOf(i));
            itemInfo.getDataInfo().setBackgroundColor(itemInfo.getItemConfig().oneColor);
            lists.add(itemInfo);
        }

        return lists;
    }

}
