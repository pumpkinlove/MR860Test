package com.miaxis.mr860test.activity;

import android.app.smdt.SmdtManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.DisableEvent;
import com.miaxis.mr860test.domain.ResultEvent;
import com.miaxis.mr860test.domain.ScrollMessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_body)
public class BodyActivity extends BaseTestActivity {

    private SmdtManager manager;
    @ViewInject(R.id.sv_body)       private ScrollView sv_body;
    @ViewInject(R.id.tv_message)    private TextView tv_message;

    @ViewInject(R.id.tv_pass)       private TextView tv_pass;
    @ViewInject(R.id.tv_deny)       private TextView tv_deny;
    @ViewInject(R.id.tv_body_on)    private TextView tv_body_on;
    @ViewInject(R.id.tv_body_off)   private TextView tv_body_off;

    private EventBus bus = EventBus.getDefault();
    private boolean flag = false;
    private Thread detectThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        bus.register(this);
        initData();
        initView();

    }

    @Override
    protected void initData() {
        manager = SmdtManager.create(this);
    }

    @Override
    protected void initView() {
        onDisableEvent(new DisableEvent(true));
    }

    private class DetectThread extends Thread {
        @Override
        public void run() {
            while (flag) {
                try {
                    Thread.sleep(1000);
                    int re = manager.smdtReadGpioValue(1);
                    if (re == 1) {
                        bus.post(new ScrollMessageEvent(re + "___有人"));
                    } else {
                        bus.post(new ScrollMessageEvent(re + "___没人"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Event(R.id.tv_body_on)
    private void onTurnOn(View view) {
        onDisableEvent(new DisableEvent(false));
        flag = true;
        if (detectThread == null) {
            detectThread = new DetectThread();
            detectThread.start();
        }
    }

    @Event(R.id.tv_body_off)
    private void onTurnOff(View view) {
        onDisableEvent(new DisableEvent(true));
        flag = false;
        if (detectThread != null && detectThread.isAlive()) {
            detectThread.interrupt();
            detectThread = null;
        }
    }

    @Event(R.id.tv_pass)
    private void onPass(View view) {
        if (flag) {
            Toast.makeText(this, "请先关闭检测", Toast.LENGTH_SHORT).show();
            return;
        }
        EventBus.getDefault().post(new ResultEvent(Constants.ID_BODY, Constants.STATUS_PASS));
        finish();
    }

    @Event(R.id.tv_deny)
    private void onDeny(View view) {
        if (flag) {
            Toast.makeText(this, "请先关闭检测", Toast.LENGTH_SHORT).show();
            return;
        }
        EventBus.getDefault().post(new ResultEvent(Constants.ID_BODY, Constants.STAUTS_DENIED));
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScrollMessageevent(ScrollMessageEvent e) {
        tv_message.append(e.getMessage() + "\r\n");
        sv_body.fullScroll(ScrollView.FOCUS_DOWN);
    }

    @Override
    protected void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisableEvent(DisableEvent e) {
        if (e.isFlag()) {
            tv_body_off.setClickable(false);
            tv_body_off.setTextColor(Color.GRAY);
            tv_body_on.setTextColor(getResources().getColor(R.color.blue_band_dark));
            tv_body_on.setClickable(true);
        } else {
            tv_body_off.setClickable(true);
            tv_body_off.setTextColor(getResources().getColor(R.color.blue_band_dark));
            tv_body_on.setTextColor(Color.GRAY);
            tv_body_on.setClickable(false);
        }
    }
}
