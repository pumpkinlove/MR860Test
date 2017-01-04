package com.miaxis.mr860test.activity;

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

@ContentView(R.layout.activity_old)
public class OldActivity extends BaseTestActivity {

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

    @Event(R.id.tv_pass)
    private void onPass(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_OLD, Constants.STATUS_PASS));
        finish();
    }

    @Event(R.id.tv_deny)
    private void onDeny(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_OLD, Constants.STAUTS_DENIED));
        finish();
    }
}
