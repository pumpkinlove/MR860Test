package com.miaxis.mr860test.activity;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.ResultEvent;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;

@ContentView(R.layout.activity_camera)
public class CameraActivity extends BaseTestActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    @ViewInject(R.id.tv_test)
    private TextView tv_test;

    @ViewInject(R.id.tv_stop_test)
    private TextView tv_stop_test;

    @ViewInject(R.id.tv_pass)
    private TextView tv_pass;

    @ViewInject(R.id.tv_deny)
    private TextView tv_deny;

    @ViewInject(R.id.sv_camera)
    private SurfaceView sv_camera;

    private SurfaceHolder surfaceHolder;

    private Camera camera;

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

        tv_test.setClickable(true);
        tv_stop_test.setClickable(false);
        tv_stop_test.setTextColor(Color.GRAY);
    }

    @Event(R.id.tv_test)
    private void onTest(View view) {

        try {
            surfaceHolder = sv_camera.getHolder();
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            sv_camera.setVisibility(View.VISIBLE);
            camera = Camera.open(0);
            if (camera == null) {
                Toast.makeText(this, "打开摄像头失败", Toast.LENGTH_SHORT).show();
                tv_deny.setClickable(true);
                tv_pass.setClickable(false);
                tv_pass.setTextColor(Color.GRAY);
                return;
            }
            camera.setPreviewCallback(this);
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            tv_stop_test.setClickable(true);
            tv_stop_test.setTextColor(getResources().getColor(R.color.blue_band_dark));
            tv_test.setClickable(false);
            tv_test.setTextColor(Color.GRAY);
            tv_pass.setClickable(true);
            tv_pass.setTextColor(getResources().getColor(R.color.green_dark));
            tv_deny.setClickable(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Event(R.id.tv_stop_test)
    private void onStopTest(View view) {
        tv_test.setClickable(true);
        tv_test.setTextColor(getResources().getColor(R.color.blue_band_dark));
        tv_stop_test.setClickable(false);
        tv_stop_test.setTextColor(Color.GRAY);
        if (camera != null) {
            camera.stopPreview();
            //当surfaceview关闭时，关闭预览并释放资源
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
            surfaceHolder = null;
        }

        sv_camera.setVisibility(View.GONE);
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

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}