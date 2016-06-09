package com.smartbell;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SettingDialog extends Dialog {

    private MainActivity mAC;

    private Button btnSure;
    private Button btnCancel;

    //ѭ��ʱ������
    private SeekBar mSeekBar;
    private TextView timeTv;
    private int mProgress;
    private int initProgress = 50;

    //��ɫʱ������
    private SeekBar mGreenSeekBar;
    private TextView mGreenTimeTv;
    private int mGreenProgress;
    private int initGreenProgress = 5;

    //��ɫʱ������
    private SeekBar mYellowSeekBar;
    private TextView mYellowTimeTv;
    private int mYellowProgress;
    private int initYellowProgress = 5;

    //��ɫʱ������
    private SeekBar mRedSeekBar;
    private TextView mRedTimeTv;
    private int mRedProgress;
    private int initRedProgress = 5;

    //�ȼ�����
    private RadioGroup levelRadioGroup;
    private RadioButton[] mRadioButtons = new RadioButton[4];
    private int[] mRadioButtonIds = {R.id.dlg_radio1, R.id.dlg_radio2, R.id.dlg_radio3, R.id.dlg_radio4};
    private int mLevle;
    private int initLevel = 3;

    //����ʱ������
    private RadioGroup sleepRadioGroup;
    private RadioButton[] sleepRadioButtons = new RadioButton[4];
    private int[] sleepRadioButtonIds = {R.id.dlg_sleep_radio1, R.id.dlg_sleep_radio2, R.id.dlg_sleep_radio3, R.id.dlg_sleep_radio4};
    private int sleepLevel;
    private int initSleepLevel = 2;

    public SettingDialog(MainActivity mAC) {
        super(mAC);
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
        btnSure = (Button) findViewById(R.id.dialog_sure);
        btnSure.setOnClickListener(mOnClickListener);
        btnCancel = (Button) findViewById(R.id.dialog_cancel);
        btnCancel.setOnClickListener(mOnClickListener);

        mSeekBar = (SeekBar) findViewById(R.id.dlg_seerbar);
        mSeekBar.setOnSeekBarChangeListener(seekListener);
        timeTv = (TextView) findViewById(R.id.dlg_time_tv);

        //green
        mGreenSeekBar = (SeekBar) findViewById(R.id.green_dlg_seerbar);
        mGreenSeekBar.setOnSeekBarChangeListener(seekListener);
        mGreenTimeTv = (TextView) findViewById(R.id.green_dlg_time_tv);

        //yellow
        mYellowSeekBar = (SeekBar) findViewById(R.id.yellow_dlg_seerbar);
        mYellowSeekBar.setOnSeekBarChangeListener(seekListener);
        mYellowTimeTv = (TextView) findViewById(R.id.yellow_dlg_time_tv);

        //red
        mRedSeekBar = (SeekBar) findViewById(R.id.red_dlg_seerbar);
        mRedSeekBar.setOnSeekBarChangeListener(seekListener);
        mRedTimeTv = (TextView) findViewById(R.id.red_dlg_time_tv);

        levelRadioGroup = (RadioGroup) findViewById(R.id.dlg_radioGroup);
        levelRadioGroup.setOnCheckedChangeListener(mRadioGroupListener);

        for (int i = 0; i < mRadioButtonIds.length; i++) {
            mRadioButtons[i] = (RadioButton) levelRadioGroup.findViewById(mRadioButtonIds[i]);
        }

        sleepRadioGroup = (RadioGroup) findViewById(R.id.dlg_sleep_radioGroup);
        sleepRadioGroup.setOnCheckedChangeListener(mRadioGroupListener);

        for (int i = 0; i < mRadioButtonIds.length; i++) {
            sleepRadioButtons[i] = (RadioButton) sleepRadioGroup.findViewById(sleepRadioButtonIds[i]);
        }
    }

    RadioGroup.OnCheckedChangeListener mRadioGroupListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // TODO Auto-generated method stub
            if (group.equals(levelRadioGroup)) {
                switch (checkedId) {
                    case R.id.dlg_radio1:
                        mLevle = 1;
                        break;
                    case R.id.dlg_radio2:
                        mLevle = 2;
                        break;
                    case R.id.dlg_radio3:
                        mLevle = 3;
                        break;
                    case R.id.dlg_radio4:
                        mLevle = 4;
                        break;
                    default:
                        break;
                }
            } else if (group.equals(sleepRadioGroup)) {
                switch (checkedId) {
                    case R.id.dlg_sleep_radio1:
                        sleepLevel = 1;
                        break;
                    case R.id.dlg_sleep_radio2:
                        sleepLevel = 2;
                        break;
                    case R.id.dlg_sleep_radio3:
                        sleepLevel = 3;
                        break;
                    case R.id.dlg_sleep_radio4:
                        sleepLevel = 4;
                        break;
                    default:
                        break;
                }
            }

        }
    };

    @Override
    public void show() {
        super.show();
        Log.d("chenbo", "SettingDialog  +++ show");
        initLevel = mAC.getLevel();
        initProgress = mAC.getRollingTime();
        initSleepLevel = mAC.getSleepTimeLevel();
        mGreenProgress = initGreenProgress = mAC.getGreenShowTime();
        mYellowProgress = initYellowProgress = mAC.getYellowShowTime();
        mRedProgress = initRedProgress = mAC.getRedShowTime();

        mLevle = initLevel;
        mProgress = initProgress;
        sleepLevel = initSleepLevel;

        mSeekBar.setProgress(initProgress);
        mGreenSeekBar.setProgress(initGreenProgress);
        mYellowSeekBar.setProgress(initYellowProgress);
        mRedSeekBar.setProgress(initRedProgress);
        mRadioButtons[initLevel - 1].setChecked(true);
        sleepRadioButtons[initSleepLevel - 1].setChecked(true);
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
                case R.id.dlg_seerbar:
                    setRollingProgress(progress);
                    break;
                case R.id.green_dlg_seerbar:
                    setGreenProgress(progress);
                    break;
                case R.id.yellow_dlg_seerbar:
                    setYellowProgress(progress);
                    break;
                case R.id.red_dlg_seerbar:
                    setRedProgress(progress);
                    break;
                default:
                    break;
            }
        }
    };

    private void setRollingProgress(int progress) {
        if (progress % 2 == 0) {
            mProgress = progress;
        } else {

            if (mProgress > progress) {
                mProgress = progress - 1;
            } else {
                mProgress = progress + 1;
            }
            mSeekBar.setProgress(mProgress);
        }
        timeTv.setText(mProgress + "s");
    }

    private void setGreenProgress(int progress) {
        mGreenProgress = progress;
        mGreenTimeTv.setText(mGreenProgress + "min");
    }

    private void setYellowProgress(int progress) {
        mYellowProgress = progress;
        mYellowTimeTv.setText(mYellowProgress + "min");
    }

    private void setRedProgress(int progress) {
        mRedProgress = progress;
        mRedTimeTv.setText(mRedProgress + "min");
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            switch (id) {
                case R.id.dialog_sure:
                    settingValue();
                    dismiss();
                    break;
                case R.id.dialog_cancel:
                    dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    private void settingValue() {
        if (mLevle != initLevel) {
            mAC.setLevel(mLevle);
        }

        if (mProgress != initProgress) {
            mAC.setRollingTime(mProgress);
        }

        if (sleepLevel != initSleepLevel) {
            mAC.setSleepTimeLevel(sleepLevel);
        }

        if (mGreenProgress != initGreenProgress) {
            mAC.setGreenShowTime(mGreenProgress);
        }

        if (mYellowProgress != initYellowProgress) {
            mAC.setYellowShowTime(mYellowProgress);
        }

        if (mRedProgress != initRedProgress) {
            mAC.setRedShowTime(mRedProgress);
        }
    }

}
