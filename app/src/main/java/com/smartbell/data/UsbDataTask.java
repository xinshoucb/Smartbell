package com.smartbell.data;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.smartbell.DataPraser;
import com.smartbell.LogView;
import com.smartbell.util.TaskRunner;
import com.smartbell.util.ThreadPoolUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * UsbDataTask class
 *
 * @author hangwei
 * @date 2018/10/3
 */
public class UsbDataTask extends BaseDataTask {
    private static final String TAG = "UsbDataTask";
    public static final String ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE";

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private UsbManager usbManager;
//    private UsbDevice usbDevice;
    private UsbSerialPort mUsbSerialPort;

    private TaskRunner taskRunner;

    private BroadcastReceiver permissionBcReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_USB_PERMISSION.equals(intent.getAction())) {
                Bundle bundle = intent.getExtras();
                if (bundle != null && bundle.getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED)) {
                    connectUsbDevice();
                }else{
                    Log.e(TAG, "no permission");
                }
            }
        }
    };

    private BroadcastReceiver usbDeviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if(!isTargetDevice(device)){
                return;
            }

            String action=intent.getAction();
            if (action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                findUsbDevice();

                Toast.makeText(mContext, "usb device attached", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "BroadcastReceiver usb device attached." );
                LogView.setLog("usb device attached.");
            }else if(action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)){
                if (taskRunner != null) {
                    taskRunner.cancel();
                }

                Toast.makeText(mContext, "usb device detached", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "BroadcastReceiver usb device detached." );
                LogView.setLog("usb device detached.");
            }else{
                Toast.makeText(mContext, action, Toast.LENGTH_SHORT).show();
            }
        }
    };

    public UsbDataTask(Context context) {
        super(context);
    }

    @Override
    public void startTask() {
        Log.v(TAG, "startTask");
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager. ACTION_USB_DEVICE_DETACHED);
        mContext.registerReceiver(usbDeviceReceiver, filter);

        mContext.registerReceiver(permissionBcReceiver, new IntentFilter(ACTION_USB_PERMISSION));

        findUsbDevice();
    }

    @Override
    public void stopTask() {
        Log.v(TAG, "stopTask");
        mContext.unregisterReceiver(usbDeviceReceiver);
        mContext.unregisterReceiver(permissionBcReceiver);

        if (taskRunner != null) {
            taskRunner.cancel();
        }
    }

    private void findUsbDevice() {
        if (usbManager == null) {
            usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        }

        if (usbManager == null) {
            Log.e(TAG, "获取usbManager失败");
            LogView.setLog("get usbManager failed.");
            return;
        }else{
            LogView.setLog("get usbManager success.");
        }

        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        LogView.setLog("usb device size="+deviceList.size());
        for (UsbDevice device : deviceList.values()) {
            Log.i(TAG, "VendorId="+device.getVendorId()+"，ProductId="+device.getProductId());
            LogView.setLog("select usb device VendorId="+device.getVendorId()+"，ProductId="+device.getProductId());
            if (isTargetDevice(device)) {
                if (mUsbSerialPort == null) {
                    mUsbSerialPort = probeDevicea(device);
                }else if(mUsbSerialPort != device){
                    synchronized (mUsbSerialPort) {
                        mUsbSerialPort = probeDevicea(device);
                    }
                }

                checkPermission();
                break;
            }
        }
    }

    private UsbSerialPort probeDevicea(UsbDevice device){
        UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(device);
        if (driver == null || driver.getPorts() == null) {
            return null;
        }

        return driver.getPorts().get(0);
    }

    private boolean isTargetDevice(UsbDevice device){
//        if (device.getVendorId() == 2588 && device.getProductId() == 9030) {
        if (true) {
            return true;
        }

        return false;
    }

    private void checkPermission(){
        if (usbManager == null || mUsbSerialPort == null) {
            LogView.setLog("checkPermission but has no usbDevice.");
            return;
        }

        if (usbManager.hasPermission(mUsbSerialPort.getDriver().getDevice())) {
            connectUsbDevice();
        }else{
            requestPermission();
        }
    }

    private void requestPermission(){
        Log.d(TAG, "正在获取权限...");
        PendingIntent intent = PendingIntent.getBroadcast(mContext,0,new Intent(ACTION_USB_PERMISSION),0);
        usbManager.requestPermission(mUsbSerialPort.getDriver().getDevice(), intent);
    }

    private void connectUsbDevice() {
        LogView.setLog("connectUsbDevice start.");
        if (mUsbSerialPort == null) {
            Log.e(TAG, "没有找到设备");
            return;
        }

        if (taskRunner != null) {
            taskRunner.cancel();
        }

        taskRunner = new TaskRunner() {
            @Override
            public void run() {
                UsbInterface usbInterface = null;
                UsbDeviceConnection deviceConnection = null;
                UsbEndpoint usbEpIn = null;
                UsbEndpoint usbEpOut = null;

                try{
                    synchronized (mUsbSerialPort) {
                        // 1、创建连接
                        UsbDeviceConnection connection = usbManager.openDevice(mUsbSerialPort.getDriver().getDevice());
                        if (connection == null) {
                            Log.e(TAG, "设备连接为空");
                            LogView.setLog("UsbDeviceConnection failed.");
                            return;
                        }

                        try {
                            mUsbSerialPort.open(connection);
                            mUsbSerialPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);


                            while (!isCancel){
                                byte[] receiveBytes = new byte[1024];
                                int numBytesRead = mUsbSerialPort.read(receiveBytes, 1000);
                                Log.d(TAG, "Read " + numBytesRead + " bytes.");

                                Log.e(TAG, "接受数据长度：" + numBytesRead);
                                if(numBytesRead <= 0){
                                    LogView.setLog("read data from usb device is empty .");
                                    continue;
                                }

                                ArrayList<String> contents = DataPraser.buffer2String(receiveBytes, numBytesRead);

                                for (String contentStr : contents) {
                                    LogView.setLog("contentStr="+contentStr);
                                    Log.d(TAG, "update data contentStr = " + contentStr + " bufLenght = " + contentStr.length());
                                    if (!TextUtils.isEmpty(contentStr)) {
                                        if (contentStr.length() == 3 && "ask".equals(contentStr)) {
                                            byte[] sendData = new byte[4];
                                            sendData[0] = (byte) 0x55;
                                            sendData[1] = (byte) 0x01;
                                            sendData[2] = (byte) 0x00;
                                            sendData[3] = (byte) 0xaa;

                                            int result = mUsbSerialPort.write(sendData, 3000);
                                            Log.e(TAG, "发送状态码：" + result);
                                        } else if(contentStr.length() == 44){
                                            update(DataPraser.rawStr2Ctrl(contentStr));
                                        }else{
                                            Log.e(TAG, "error contentStr=" + contentStr);
                                        }
                                    }
                                }
                            }
                        } catch (IOException e) {
                            Log.d(TAG, "IOException:"+e.toString());
                        } finally {
                            LogView.setLog("close usb device connect.");
                            mUsbSerialPort.close();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    LogView.setLog(e.toString());
                }
            }
        };

        ThreadPoolUtil.getThreadPool().execute(taskRunner);
    }

}
