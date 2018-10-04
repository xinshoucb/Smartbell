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
import android.text.TextUtils;
import android.util.Log;

import com.smartbell.DataPraser;
import com.smartbell.TestUtils;
import com.smartbell.util.TaskRunner;
import com.smartbell.util.ThreadPoolUtil;

import java.io.DataOutputStream;
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

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private UsbManager usbManager;
    private UsbDevice usbDevice;

    private TaskRunner taskRunner;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                if (intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED)) {
                    connectUsbDevice();
                }else{
                    Log.e(TAG, "no permission");
                }
                mContext.unregisterReceiver(broadcastReceiver);
            }
        }
    };

    public UsbDataTask(Context context) {
        super(context);
    }

    @Override
    public void startTask() {
        findUsbDevice();
        checkPermission();
    }

    @Override
    public void stopTask() {
        if (taskRunner != null) {
            taskRunner.cancel();
        }
    }

    private void findUsbDevice() {
        usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        if (usbManager == null) {
            Log.e(TAG, "获取usbManager失败");
            return;
        }

        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        for (UsbDevice device : deviceList.values()) {
            Log.i(TAG, "VendorId="+device.getVendorId()+"，ProductId="+device.getProductId());
            if (device.getVendorId() == 2588 && device.getProductId() == 9030) {
                usbDevice = device;
            }
        }
    }

    private void checkPermission(){
        if (usbManager.hasPermission(usbDevice)) {
            connectUsbDevice();
        }else{
            requestPermission();
        }
    }

    private void requestPermission(){
        Log.d(TAG, "正在获取权限...");

        mContext.registerReceiver(broadcastReceiver, new IntentFilter(ACTION_USB_PERMISSION));

        PendingIntent intent = PendingIntent.getBroadcast(mContext,0,new Intent(ACTION_USB_PERMISSION),0);
        usbManager.requestPermission(usbDevice, intent);
    }

    private void connectUsbDevice() {
        if (usbDevice == null) {
            Log.e(TAG, "没有找到设备");
            return;
        }

        taskRunner = new TaskRunner() {
            @Override
            public void run() {
                UsbInterface usbInterface = null;
                UsbDeviceConnection deviceConnection = null;
                UsbEndpoint usbEpIn = null;
                UsbEndpoint usbEpOut = null;

                // 1、创建连接
                UsbDeviceConnection connection = usbManager.openDevice(usbDevice);
                if (connection == null) {
                    Log.e(TAG, "设备连接为空");
                    return;
                }

                // 2、获取接口
                if (usbDevice.getInterfaceCount() <= 0) {
                    Log.e(TAG, "没有找到usbInterface");
                    return;
                }else{
                    //一个设备上面一般只有一个接口，有两个端点，分别接受和发送数据
                    usbInterface = usbDevice.getInterface(0);
                }

                // 3、声明接口
                if (connection.claimInterface(usbInterface, true)) {
                    deviceConnection = connection;
                } else {
                    connection.close();
                    Log.e(TAG, "claim usbInterface failed");
                    return;
                }

                // 4、获取输入输出端口
                for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                    UsbEndpoint ep = usbInterface.getEndpoint(i);
                    if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                            usbEpOut = ep;
                            Log.e(TAG, "获取发送数据的端点");
                        } else {
                            usbEpIn = ep;
                            Log.e(TAG, "获取接受数据的端点");
                        }
                    }
                }

                // 5、发送接收数据
                while (!isCancel){
                    byte[] receiveBytes = new byte[1024];
                    int length = deviceConnection.bulkTransfer(usbEpIn, receiveBytes, receiveBytes.length, 3000);
                    Log.e(TAG, "接受状态码：" + length);
                    if(length <= 0){
                        continue;
                    }

                    ArrayList<String> contents = DataPraser.buffer2String(receiveBytes);

                    for (String contentStr : contents) {
                        Log.d(TAG, "contentStr = " + contentStr + " bufLenght = " + contentStr.length());
                        if (!TextUtils.isEmpty(contentStr)) {
                            if (contentStr.length() == 3 && "ask".equals(contentStr)) {
                                byte[] sendData = new byte[4];
                                sendData[0] = (byte) 0x55;
                                sendData[1] = (byte) 0x01;
                                sendData[2] = (byte) 0x00;
                                sendData[3] = (byte) 0xaa;
                                int result = deviceConnection.bulkTransfer(usbEpOut, sendData, sendData.length, 3000);
                                Log.e(TAG, "发送状态码：" + result);
                            } else if(contentStr.length() == 44){
                                update(contentStr);
                            }else{
                                Log.e(TAG, "error contentStr=" + contentStr);
                            }
                        }
                    }
                }

                // 6、释放接口
                deviceConnection.releaseInterface(usbInterface);
            }
        };

        ThreadPoolUtil.getThreadPool().execute(taskRunner);
    }

}
