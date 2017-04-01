package com.miaxis.mr860test.activity;

import android.app.smdt.SmdtManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.DisableEvent;
import com.miaxis.mr860test.domain.ResultEvent;
import com.miaxis.mr860test.fragment.CameraFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@Deprecated
@ContentView(R.layout.activity_camera)
public class CameraActivity extends BaseTestActivity {

    @ViewInject(R.id.tv_test)       private TextView tv_test;
    @ViewInject(R.id.tv_stop_test)  private TextView tv_stop_test;

    @ViewInject(R.id.tv_pass)       private TextView tv_pass;
    @ViewInject(R.id.tv_deny)       private TextView tv_deny;

    @ViewInject(R.id.fl_preview)    private FrameLayout fl_preview;

    private CameraFragment fragment;
    private EventBus bus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        initData();
        initView();
        bus.post(new DisableEvent(false, false));
    }

    @Override
    protected void initData() {
        bus = EventBus.getDefault();
        bus.register(this);


    }

    @Override
    protected void initView() {
        tv_test.setClickable(true);
        tv_stop_test.setClickable(false);
        tv_stop_test.setTextColor(Color.GRAY);
    }

    @Event(R.id.tv_test)
    private void onTest(View view) {
        fl_preview.setVisibility(View.VISIBLE);
        tv_test.setClickable(false);
        tv_test.setTextColor(Color.GRAY);
        tv_stop_test.setClickable(true);
        tv_stop_test.setTextColor(getResources().getColor(R.color.blue_band_dark));
        fragment = CameraFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_preview, fragment)
                .commit();

//        /**调用系统摄像头*/
//        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(i, 1);
    }

    @Event(R.id.tv_stop_test)
    private void onStopTest(View view) {
        tv_test.setClickable(true);
        tv_test.setTextColor(getResources().getColor(R.color.blue_band_dark));
        tv_stop_test.setClickable(false);
        tv_stop_test.setTextColor(Color.GRAY);
        fl_preview.setVisibility(View.GONE);

        getSupportFragmentManager().beginTransaction().detach(fragment);
        fragment = null;
    }

    @Event(R.id.tv_pass)
    private void onPass(View view) {
        onStopTest(null);
        EventBus.getDefault().post(new ResultEvent(Constants.ID_CAMERA, Constants.STATUS_PASS));
        finish();
    }

    @Event(R.id.tv_deny)
    private void onDeny(View view) {
        onStopTest(null);
        EventBus.getDefault().post(new ResultEvent(Constants.ID_CAMERA, Constants.STAUTS_DENIED));
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
