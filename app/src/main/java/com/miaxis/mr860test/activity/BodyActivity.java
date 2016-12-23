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

@ContentView(R.layout.activity_body)
public class BodyActivity extends BaseTestActivity {

    private SmdtManager manager;

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
    }

    @Override
    protected void initView() {

    }

    @Event(R.id.tv_led_on)
    private void onTurnOn(View view) {
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
}
