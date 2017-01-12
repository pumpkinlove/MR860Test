package com.miaxis.mr860test.activity;

import android.app.smdt.SmdtManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.ResultEvent;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.x;

@ContentView(R.layout.activity_led)
public class LEDActivity extends BaseTestActivity {

    private SmdtManager smdt;

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
        smdt = SmdtManager.create(this);
    }

    @Override
    protected void initView() {

    }

    @Event(R.id.tv_led_on)
    private void onTurnOn(View view) {
        try {
            Toast.makeText(getApplicationContext(), smdt.smdtSetExtrnalGpioValue(3, true) + "", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            onDeny(null);
        }
    }

    @Event(R.id.tv_led_off)
    private void onTurnOff(View view) {
        try {
            Toast.makeText(getApplicationContext(), smdt.smdtSetExtrnalGpioValue(3, false) + "", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            onDeny(null);
        }

    }

    @Event(R.id.tv_pass)
    private void onPass(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_LED, Constants.STATUS_PASS));
        finish();
    }

    @Event(R.id.tv_deny)
    private void onDeny(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_LED, Constants.STAUTS_DENIED));
        finish();
    }
}
