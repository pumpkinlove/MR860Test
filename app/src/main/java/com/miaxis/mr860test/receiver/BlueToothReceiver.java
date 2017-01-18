package com.miaxis.mr860test.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.miaxis.mr860test.domain.BlueToothEvent;

import org.greenrobot.eventbus.EventBus;
import org.xutils.common.util.LogUtil;

public class BlueToothReceiver extends BroadcastReceiver {
    private static final String TAG = "BlueToothReceiver";
    public BlueToothReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        EventBus.getDefault().post(new BlueToothEvent());
        Log.e(TAG, "onReceive---------");
        switch(intent.getAction()){
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch(blueState){
                    case BluetoothAdapter.STATE_TURNING_ON:
                        LogUtil.e("onReceive---------STATE_TURNING_ON");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        LogUtil.e("onReceive---------STATE_ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        LogUtil.e("onReceive---------STATE_TURNING_OFF");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        LogUtil.e("onReceive---------STATE_OFF");
                        break;
                }
                break;
        }
    }
}
