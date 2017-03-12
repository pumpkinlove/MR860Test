package com.miaxis.mr860test.activity;

import android.app.smdt.SmdtManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.camera.simplewebcam.CameraPreview;
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

@ContentView(R.layout.activity_camera2)
public class Camera2Activity extends BaseTestActivity {

    @ViewInject(R.id.tv_test)       private TextView tv_test;
    @ViewInject(R.id.tv_stop_test)  private TextView tv_stop_test;

    @ViewInject(R.id.tv_pass)       private TextView tv_pass;
    @ViewInject(R.id.tv_deny)       private TextView tv_deny;

    @ViewInject(R.id.cp_preview)    private CameraPreview cp_preview;

    private EventBus bus;
    private SmdtManager smdtManager;

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
        smdtManager = SmdtManager.create(this);

    }

    @Override
    protected void initView() {

    }

    @Event(R.id.tv_test)
    private void onTest(View view) {
        if (smdtManager != null) {
            smdtManager.smdtSetExtrnalGpioValue(2, true);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        cp_preview.setVisibility(View.VISIBLE);
        bus.post(new DisableEvent(true, true));
    }

    @Event(R.id.tv_stop_test)
    private void onStopTest(View view) {
        cp_preview.setVisibility(View.GONE);
        bus.post(new DisableEvent(true, false));
        if (smdtManager != null) {
            smdtManager.smdtSetExtrnalGpioValue(2, false);
        }
    }

    @Event(R.id.tv_pass)
    private void onPass(View view) {
        onStopTest(null);
        bus.post(new ResultEvent(Constants.ID_CAMERA, Constants.STATUS_PASS));
        finish();
    }

    @Event(R.id.tv_deny)
    private void onDeny(View view) {
        onStopTest(null);
        bus.post(new ResultEvent(Constants.ID_CAMERA, Constants.STAUTS_DENIED));
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
            tv_stop_test.setEnabled(true);
            tv_stop_test.setClickable(true);
            tv_stop_test.setTextColor(getResources().getColor(R.color.blue_band_dark));
            tv_test.setEnabled(false);
            tv_test.setClickable(false);
            tv_test.setTextColor(getResources().getColor(R.color.gray_dark));
        } else {
            tv_stop_test.setEnabled(false);
            tv_stop_test.setClickable(false);
            tv_stop_test.setTextColor(getResources().getColor(R.color.gray_dark));
            tv_test.setEnabled(true);
            tv_test.setClickable(true);
            tv_test.setTextColor(getResources().getColor(R.color.blue_band_dark));

        }
    }

    @Override
    public void finish() {
        smdtManager.smdtSetExtrnalGpioValue(2, false);
        onStopTest(null);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        smdtManager.smdtSetExtrnalGpioValue(2, false);
        bus.unregister(this);
        super.onDestroy();
    }

}
