package com.miaxis.mr860test.activity;

import android.app.smdt.SmdtManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.DisableEvent;
import com.miaxis.mr860test.domain.ResultEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_led)
public class LEDActivity extends BaseTestActivity {

    @ViewInject(R.id.tv_pass)   private TextView tv_pass;
    @ViewInject(R.id.tv_deny)   private TextView tv_deny;

    private SmdtManager smdt;
    private EventBus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        initData();
        initView();
        onTurnOn(null);
    }

    @Override
    protected void initData() {
        smdt = SmdtManager.create(this);
        bus = EventBus.getDefault();
        bus.register(this);
        bus.post(new DisableEvent(false));
    }

    @Override
    protected void initView() {

    }

    @Event(R.id.tv_led_on)
    private void onTurnOn(View view) {
        try {
            int re = smdt.smdtSetExtrnalGpioValue(3, true);
            if (re == 0) {
                bus.post(new DisableEvent(true, true));
            } else {
                bus.post(new DisableEvent(false, true));
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            onDeny(null);
        }
    }

    @Event(R.id.tv_led_off)
    private void onTurnOff(View view) {
        try {
            int re = smdt.smdtSetExtrnalGpioValue(3, false);
            if (re == 0) {
                bus.post(new DisableEvent(true, true));
            } else {
                bus.post(new DisableEvent(false, true));
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            onDeny(null);
        }

    }

    @Event(R.id.tv_pass)
    private void onPass(View view) {
        onTurnOff(null);
        EventBus.getDefault().post(new ResultEvent(Constants.ID_LED, Constants.STATUS_PASS));
        finish();
    }

    @Event(R.id.tv_deny)
    private void onDeny(View view) {
        onTurnOff(null);
        EventBus.getDefault().post(new ResultEvent(Constants.ID_LED, Constants.STAUTS_DENIED));
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisableEvent(DisableEvent e) {
        if (e.isFlag()) {
            tv_pass.setEnabled(true);
            tv_pass.setClickable(true);
            tv_pass.setTextColor(getResources().getColor(R.color.green_dark));
            tv_deny.setEnabled(true);
            tv_deny.setClickable(true);
            tv_deny.setTextColor(getResources().getColor(R.color.red));
        } else {
            tv_pass.setEnabled(false);
            tv_pass.setClickable(false);
            tv_pass.setTextColor(getResources().getColor(R.color.gray_dark));
            tv_deny.setEnabled(false);
            tv_deny.setClickable(false);
            tv_deny.setTextColor(getResources().getColor(R.color.gray_dark));
        }
    }

    @Override
    public void finish() {
        smdt.smdtSetExtrnalGpioValue(3, false);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }
}
