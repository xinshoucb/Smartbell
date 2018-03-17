package com.smartbell;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.smartbell.bean.ItemConfig;
import com.smartbell.db.DBManager;

public class SettingDialog extends Dialog {

    private MainActivity mAC;

    //绿色显示时间
    private SeekBar mGreenSeekBar;
    private TextView mGreenTimeTv;
    private int mGreenProgress;
    private int initGreenProgress = 5;

    //红色显示时间
    private SeekBar mYellowSeekBar;
    private TextView mYellowTimeTv;
    private int mYellowProgress;
    private int initYellowProgress = 5;

    private int index = 0;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public SettingDialog(MainActivity mAC) {
        super(mAC, R.style.dialog);
        this.mAC = mAC;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.setting_dialog);
        setTitle("Settings");
        initView();
        Log.d("chenbo", "SettingDialog  +++ onCreate");
    }

    private void initView() {
        //green
        mGreenSeekBar = (SeekBar) findViewById(R.id.green_dlg_seerbar);
        mGreenSeekBar.setOnSeekBarChangeListener(seekListener);
        mGreenTimeTv = (TextView) findViewById(R.id.green_dlg_time_tv);

        //yellow
        mYellowSeekBar = (SeekBar) findViewById(R.id.yellow_dlg_seerbar);
        mYellowSeekBar.setOnSeekBarChangeListener(seekListener);
        mYellowTimeTv = (TextView) findViewById(R.id.yellow_dlg_time_tv);
    }

    @Override
    public void show() {
        super.show();
        Log.d("chenbo", "SettingDialog  +++ show index="+index);
        readDataFromDB();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mAC.notifyBackgroudColorChange(index);
    }

    OnSeekBarChangeListener seekListener = new OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            saveDataToDB();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // TODO Auto-generated method stub
            int seekBarId = seekBar.getId();
            switch (seekBarId) {
                case R.id.green_dlg_seerbar:
                    setGreenProgress(progress);
                    break;
                case R.id.yellow_dlg_seerbar:
                    setYellowProgress(progress);
                    break;
                default:
                    break;
            }
        }
    };

    private void setGreenProgress(int progress) {
        mGreenProgress = progress;
        mGreenTimeTv.setText(mGreenProgress + "Sec");
    }

    private void setYellowProgress(int progress) {
        mYellowProgress = progress;
        mYellowTimeTv.setText(mYellowProgress + "Sec");
    }

    private void saveDataToDB() {
        DBManager.setItemConfigAndDefault(index,mGreenProgress,mYellowProgress);
    }

    private void readDataFromDB(){
        ItemConfig itemConfig = DBManager.getItemConfigHasDefault(index);
        if (itemConfig != null && itemConfig.isValid()) {
            mGreenProgress = initGreenProgress = itemConfig.oneTimeSec;
            mYellowProgress = initYellowProgress = itemConfig.twoTimeSec;
        }else {
            mGreenProgress = initGreenProgress = 0;
            mYellowProgress = initYellowProgress = 0;
        }

        mGreenSeekBar.setProgress(initGreenProgress);
        mYellowSeekBar.setProgress(initYellowProgress);
        setGreenProgress(initGreenProgress);
        setYellowProgress(initYellowProgress);
    }

}
