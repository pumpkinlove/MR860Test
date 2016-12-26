package com.miaxis.mr860test.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.ResultEvent;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.x;

@ContentView(R.layout.activity_voice)
public class VoiceActivity extends BaseTestActivity {

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

    }

    @Override
    protected void initView() {

    }

    @Event(R.id.tv_test)
    private void onTest(View view) {
        NotificationManager manger = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification();
        notification.defaults=Notification.DEFAULT_SOUND;
        manger.notify(1, notification);
    }

    @Event(R.id.tv_pass)
    private void onPass(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_VOICE, Constants.STATUS_PASS));
        finish();
    }

    @Event(R.id.tv_deny)
    private void onDeny(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_VOICE, Constants.STAUTS_DENIED));
        finish();
    }
}