package com.miaxis.mr860test.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.ResultEvent;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_lcd)
public class LCDActivity extends BaseTestActivity {

    @ViewInject(R.id.lcd)
    private View lcd;

    private int step = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        initData();
        initView();

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Event(R.id.tv_test)
    private void onTest(View view) {
        lcd.setVisibility(View.VISIBLE);
    }

    @Event(R.id.tv_pass)
    private void onPass(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_LCD, Constants.STATUS_PASS));
        finish();
    }

    @Event(R.id.tv_deny)
    private void onDeny(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_LCD, Constants.STAUTS_DENIED));
        finish();
    }

    @Event(R.id.lcd)
    private void onChangeColor(View view) {
        step ++;
        switch (step) {
            case 0:
                view.setBackgroundColor(Color.RED);
                break;
            case 1:
                view.setBackgroundColor(Color.GREEN);
                break;
            case 2:
                view.setBackgroundColor(Color.BLUE);
                break;
            case 3:
                view.setBackgroundColor(Color.WHITE);
                break;
            case 4:
                view.setBackgroundColor(Color.BLACK);
                break;
            case 5:
                view.setVisibility(View.GONE);
                view.setBackgroundColor(Color.RED);
                step = 0;
                break;
        }
    }

}
