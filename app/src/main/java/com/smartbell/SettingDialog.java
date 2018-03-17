package com.smartbell;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

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


    public SettingDialog(MainActivity mAC, int index) {
        super(mAC, R.style.dialog);
        // TODO Auto-generated constructor stub
        this.mAC = mAC;
        Log.d("chenbo", "SettingDialog  +++ SettingDialog");
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
        Log.d("chenbo", "SettingDialog  +++ show");
        mGreenProgress = initGreenProgress = 120;// mAC.getGreenShowTime();
        mYellowProgress = initYellowProgress = 120;//mAC.getYellowShowTime();
        mGreenSeekBar.setProgress(initGreenProgress);
        mYellowSeekBar.setProgress(initYellowProgress);
    }

    OnSeekBarChangeListener seekListener = new OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

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

}
