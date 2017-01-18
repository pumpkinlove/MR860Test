package com.miaxis.mr860test.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.ResultEvent;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.reflect.Method;

@ContentView(R.layout.activity_blue_tooth)
public class BlueToothActivity extends BaseTestActivity implements RadioGroup.OnCheckedChangeListener {

    private EventBus bus;
    private BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
    private static final int DISCOVERED_ENABLE = 3;

    @ViewInject(R.id.rg_open)       private RadioGroup rg_open;
    @ViewInject(R.id.rg_discover)   private RadioGroup rg_discover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initData();
        initView();
    }

    @Override
    protected void initData() {
        bus = EventBus.getDefault();
    }

    @Override
    protected void initView() {
        rg_open.setOnCheckedChangeListener(this);
        rg_discover.setOnCheckedChangeListener(this);
        ((RadioButton)rg_discover.getChildAt(1)).setChecked(true);
        ((RadioButton)rg_open.getChildAt(0)).setChecked(true);
    }

    @Event(R.id.tv_test)
    private void onTest(View view) {
        try {
            NotificationManager manger = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification();
            notification.defaults=Notification.DEFAULT_SOUND;
            manger.notify(1, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Event(R.id.tv_pass)
    private void onPass(View view) {
        bus.post(new ResultEvent(Constants.ID_BT, Constants.STATUS_PASS));
        finish();
    }

    @Event(R.id.tv_deny)
    private void onDeny(View view) {
        bus.post(new ResultEvent(Constants.ID_BT, Constants.STAUTS_DENIED));
        finish();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.rg_open:
                onOpenStateChanged(checkedId);
                break;
            case R.id.rg_discover:
                onDiscoverStateChanged(checkedId);
                break;
        }
    }

    private void onOpenStateChanged(int checkedId) {
        switch (checkedId) {
            case R.id.rb_open_y :
                if (ba.enable()) {
                    Toast.makeText(this, "蓝牙打开成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "蓝牙打开失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rb_open_n :
                if (ba.disable()) {
                    Toast.makeText(this, "蓝牙关闭成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "蓝牙关闭失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void onDiscoverStateChanged(int checkedId) {
        switch (checkedId) {
            case R.id.rb_discover_y :
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
                startActivityForResult(discoverableIntent, DISCOVERED_ENABLE);
                break;
            case R.id.rb_discover_n :
                //尝试关闭蓝牙可见性
                try {
                    Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
                    setDiscoverableTimeout.setAccessible(true);
                    Method setScanMode =BluetoothAdapter.class.getMethod("setScanMode", int.class,int.class);
                    setScanMode.setAccessible(true);
                    setDiscoverableTimeout.invoke(ba, 1);
                    setScanMode.invoke(ba, BluetoothAdapter.SCAN_MODE_CONNECTABLE,1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DISCOVERED_ENABLE) {
            switch (resultCode) {
                case 0 :
                    ((RadioButton)rg_discover.getChildAt(1)).setChecked(true);
                    break;
                case 1 :
                    ((RadioButton)rg_discover.getChildAt(0)).setChecked(true);
                    break;
            }
        }
    }

    @Event(R.id.btn_start_discovery)
    private void onStartDiscoveryClicked(View view) {
        if (ba.startDiscovery()) {
            Toast.makeText(this, "搜索成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "搜索失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Event(R.id.btn_cancel_discovery)
    private void onCancelDiscoveryClicked(View view) {
        ba.cancelDiscovery();
    }

    @Override
    protected void onDestroy() {
        ((RadioButton)rg_open.getChildAt(1)).setChecked(true);

        super.onDestroy();
    }
}
