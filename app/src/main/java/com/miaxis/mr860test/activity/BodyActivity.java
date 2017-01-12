package com.miaxis.mr860test.activity;

import android.app.smdt.SmdtManager;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.CommonEvent;
import com.miaxis.mr860test.domain.ResultEvent;

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
    private EventBus bus;

    DetectThread detectThread;

    @ViewInject(R.id.ll_body_yes)   private LinearLayout ll_body_yes;
    @ViewInject(R.id.ll_body_no)    private LinearLayout ll_body_no;

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
        manager = SmdtManager.create(this);
        bus = EventBus.getDefault();
        bus.register(this);
        detectThread = new DetectThread();
    }

    @Override
    protected void initView() {

    }

    @Event(R.id.tv_led_on)
    private void onTurnOn(View view) {

//        Toast.makeText(this, "" + manager.smdtReadGpioValue(1), Toast.LENGTH_SHORT).show();

        if (detectThread.isAlive()) {
            detectThread.interrupt();
            detectThread = null;
        }
        detectThread = new DetectThread();
        detectThread.start();

    }

    @Event(R.id.tv_led_off)
    private void onTurnOff(View view) {
    }

    @Event(R.id.tv_pass)
    private void onPass(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_BODY, Constants.STATUS_PASS));
        finish();
    }

    @Event(R.id.tv_deny)
    private void onDeny(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_BODY, Constants.STAUTS_DENIED));
        finish();
    }

    class DetectThread extends Thread implements Runnable{

        @Override
        public void run() {
            try {
                int re;
                while (true) {
                    re = manager.smdtReadGpioValue(1);
                    bus.post(new CommonEvent(0, re, ""));
                    Thread.sleep(2000);
                }
            } catch (Exception e) {
                bus.post(new CommonEvent(0, -1, e.getMessage()));
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommonEvent(CommonEvent e) {
        switch (e.getResult()) {
            case 0:
                ll_body_no.setVisibility(View.VISIBLE);
                ll_body_yes.setVisibility(View.GONE);
                break;
            case 1:
                ll_body_no.setVisibility(View.GONE);
                ll_body_yes.setVisibility(View.VISIBLE);
                break;
            case -1:
                Toast.makeText(this, e.getContent(), Toast.LENGTH_LONG).show();
                onDeny(null);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }
}
