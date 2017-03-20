package com.miaxis.mr860test.activity;

import android.app.smdt.SmdtManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.camera.simplewebcam.CameraPreview;
import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.CameraFailEvent;
import com.miaxis.mr860test.domain.CommonEvent;
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
public class Camera2Activity extends BaseTestActivity implements SurfaceHolder.Callback {

    @ViewInject(R.id.tv_test)       private TextView tv_test;
    @ViewInject(R.id.tv_stop_test)  private TextView tv_stop_test;

    @ViewInject(R.id.tv_pass)       private TextView tv_pass;
    @ViewInject(R.id.tv_deny)       private TextView tv_deny;

    @ViewInject(R.id.sv_preview)    private SurfaceView sv_preview;

    private EventBus bus;
    private SmdtManager smdtManager;
    private Camera mCamera;
    private SurfaceHolder sh_preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initData();
        initView();
        bus.post(new DisableEvent(true, false, false, false));
        onTest(null);
    }

    @Override
    protected void initData() {
        sh_preview = sv_preview.getHolder();
        sh_preview.addCallback(this);
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
        sv_preview.setVisibility(View.VISIBLE);
        bus.post(new DisableEvent(false, true, false, false));
    }

    @Event(R.id.tv_stop_test)
    private void onStopTest(View view) {
        if (smdtManager != null) {
            if (0 == smdtManager.smdtReadExtrnalGpioValue(2)) {
                return;
            }
            sv_preview.setVisibility(View.GONE);
            bus.post(new DisableEvent(true, false, true, true));
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
        enableButtons(e.isFlag(),   tv_test,        R.color.dark);
        enableButtons(e.isFlag2(),  tv_stop_test,   R.color.dark);
        enableButtons(e.isFlag3(),  tv_pass,        R.color.green_dark);
        enableButtons(e.isFlag4(),  tv_deny,        R.color.red);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCameraFailEvent(CameraFailEvent e) {
        enableButtons(false, tv_pass, R.color.green_dark);
    }

    @Override
    public void finish() {
        onStopTest(null);
        smdtManager.smdtSetExtrnalGpioValue(2, false);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        smdtManager.smdtSetExtrnalGpioValue(2, false);
        bus.unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        onStopTest(null);
        super.onPause();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        openCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        closeCamera();
    }

    private void openCamera() {
        try {
            mCamera = Camera.open();
            mCamera.setPreviewDisplay(sh_preview); // 通过SurfaceView显示取景画面
            mCamera.setDisplayOrientation(180);
            mCamera.startPreview(); // 开始预览
            bus.post(new DisableEvent(false, true, true, true));
        } catch (Exception e) {
            bus.post(new DisableEvent(true, false, false, true));
        }
    }

    private void closeCamera() {
        try {
            if (mCamera != null) {
                mCamera.setPreviewCallback(null) ;
                mCamera.setPreviewDisplay(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
        }
    }
}
