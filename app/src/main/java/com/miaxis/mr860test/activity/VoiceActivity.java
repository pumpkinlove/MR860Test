package com.miaxis.mr860test.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

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

@ContentView(R.layout.activity_voice)
public class VoiceActivity extends BaseTestActivity {

    @ViewInject(R.id.tv_pass)       private TextView tv_pass;
    @ViewInject(R.id.tv_deny)       private TextView tv_deny;

    private EventBus bus;

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
        bus.register(this);
        bus.post(new DisableEvent(false, false));
    }

    @Override
    protected void initView() {

    }

    @Event(R.id.tv_test)
    private void onTest(View view) {
        try {
            NotificationManager manger = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification();
            notification.defaults=Notification.DEFAULT_SOUND;
            manger.notify(1, notification);
            bus.post(new DisableEvent(true, true));
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisableEvent(DisableEvent e) {
        if (e.isFlag()) {
            tv_pass.setEnabled(true);
            tv_pass.setClickable(true);
            tv_pass.setTextColor(getResources().getColor(R.color.green_dark));
        } else {
            tv_pass.setEnabled(false);
            tv_pass.setClickable(false);
            tv_pass.setTextColor(getResources().getColor(R.color.gray_dark));
        }
        if (e.isFlag2()) {
            tv_deny.setEnabled(true);
            tv_deny.setClickable(true);
            tv_deny.setTextColor(getResources().getColor(R.color.red));
        } else {
            tv_deny.setEnabled(false);
            tv_deny.setClickable(false);
            tv_deny.setTextColor(getResources().getColor(R.color.gray_dark));
        }
    }

    @Override
    protected void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }
}
