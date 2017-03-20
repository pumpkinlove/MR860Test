package com.miaxis.mr860test.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.OpenBlueToothEvent;
import com.miaxis.mr860test.domain.ResultEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@ContentView(R.layout.activity_blue_tooth)
public class BlueToothActivity extends BaseTestActivity implements RadioGroup.OnCheckedChangeListener {

    @ViewInject(R.id.rg_open)       private RadioGroup rg_open;
    @ViewInject(R.id.tv_pass)       private TextView tv_pass;

    private ProgressDialog pd_open_bt;
    private EventBus bus;
    private BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
    private static final int DISCOVERED_ENABLE = 3;

    private SoundPool soundPool;
    private Map<Integer, Integer> soundMap;

    public static final float LEFT_VOLUME =1.0f, RIGHT_VOLUME =1.0f;
    public static final int PRIORITY =1, LOOP = 0;
    public static final float SOUND_RATE =1.0f;//正常速率

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
        pd_open_bt = new ProgressDialog(this);
        bus = EventBus.getDefault();
        bus.register(this);
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundMap = new HashMap<>();
        soundMap.put(1, soundPool.load(this, R.raw.success, 1));
    }

    @Override
    protected void initView() {
        pd_open_bt.setCanceledOnTouchOutside(false);
        rg_open.setOnCheckedChangeListener(this);
        ((RadioButton)rg_open.getChildAt(0)).setChecked(true);
    }

    @Event(R.id.tv_test)
    private void onTest(View view) {
        playSound(1);
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
        }
    }

    private void onOpenStateChanged(int checkedId) {
        switch (checkedId) {
            case R.id.rb_open_y :
                if (ba.enable()) {
                    Toast.makeText(this, "正在打开蓝牙...", Toast.LENGTH_SHORT).show();
                    pd_open_bt.setMessage("正在打开蓝牙...");
                    pd_open_bt.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException ie) {
                            }
                            bus.post(new OpenBlueToothEvent(true));
                        }
                    }).start();
                } else {
                    Toast.makeText(this, "蓝牙打开失败", Toast.LENGTH_SHORT).show();
                    ((RadioButton)rg_open.getChildAt(1)).setChecked(true);
                    enableButtons(false, (TextView)findViewById(R.id.tv_pass), R.color.green_dark);
                }
                break;
            case R.id.rb_open_n :
                if (ba.disable()) {
                    Toast.makeText(this, "正在关闭蓝牙...", Toast.LENGTH_SHORT).show();
                    pd_open_bt.setMessage("正在关闭蓝牙...");
                    pd_open_bt.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException ie) {
                            }
                            bus.post(new OpenBlueToothEvent(false));
                        }
                    }).start();

                } else {
                    Toast.makeText(this, "蓝牙关闭失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        ((RadioButton)rg_open.getChildAt(1)).setChecked(true);
        bus.unregister(this);
        super.onDestroy();
    }

    private void playSound(int soundID) {
        soundPool.play(soundMap.get(soundID), LEFT_VOLUME, RIGHT_VOLUME, PRIORITY, LOOP, SOUND_RATE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenBlueTooth(OpenBlueToothEvent e) {
        if (e.isFlag() && ba.isEnabled()) {
            Toast.makeText(this, "蓝牙打开成功", Toast.LENGTH_SHORT).show();
            enableButtons(true, tv_pass, R.color.green_dark);
        } else if (e.isFlag() && !ba.isEnabled()) {
            Toast.makeText(this, "蓝牙打开失败", Toast.LENGTH_SHORT).show();
            ((RadioButton)rg_open.getChildAt(1)).setChecked(true);
            enableButtons(true, tv_pass, R.color.green_dark);
        } else if (!e.isFlag() && !ba.isEnabled()) {
            Toast.makeText(this, "蓝牙关闭成功", Toast.LENGTH_SHORT).show();
            enableButtons(true, tv_pass, R.color.green_dark);
        } else if (!e.isFlag() && ba.isEnabled()) {
            Toast.makeText(this, "蓝牙关闭失败", Toast.LENGTH_SHORT).show();
            enableButtons(false, tv_pass, R.color.green_dark);
        }

        pd_open_bt.dismiss();
    }
}
