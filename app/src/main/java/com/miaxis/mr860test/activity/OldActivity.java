package com.miaxis.mr860test.activity;

import android.app.smdt.SmdtManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.CommonEvent;
import com.miaxis.mr860test.domain.OldResult;
import com.miaxis.mr860test.domain.ResultEvent;
import com.miaxis.mr860test.utils.DateUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import org.zz.idcard_hid_driver.IdCardDriver;
import org.zz.mxhidfingerdriver.MXFingerDriver;

import java.util.Date;

@ContentView(R.layout.activity_old)
public class OldActivity extends BaseTestActivity implements SurfaceHolder.Callback{

    private static final int INIT_VIEW      = 2000;
    private static final int OPEN_LED       = 2001;
    private static final int OPEN_CAMERA    = 2002;
    private static final int READ_ID        = 2003;
    private static final int READ_FINGER    = 2004;
    private static final int CLOSE_CAMERA   = 2005;
    private static final int CLOSE_LED      = 2006;

    private int count = 0;

    private SmdtManager smdtManager;
    private IdCardDriver idCardDriver;
    private MXFingerDriver fingerDriver;
    private Camera camera;
    private SurfaceHolder holder;

    private static final int INTERVAL_TIME = 1000;
    private boolean continueFlag = true;

    private OldThread oldThread;

    private EventBus bus;

    @ViewInject(R.id.tv_old_start)              private TextView tv_old_start;
    @ViewInject(R.id.tv_old_stop)               private TextView tv_old_stop;
    @ViewInject(R.id.tv_old_count)              private TextView tv_old_count;
    @ViewInject(R.id.tv_old_begin_time)         private TextView tv_old_begin_time;
    @ViewInject(R.id.tv_old_end_time)           private TextView tv_old_end_time;
    @ViewInject(R.id.tv_old_id_version)         private TextView tv_old_id_version;
    @ViewInject(R.id.tv_old_finger_version)     private TextView tv_old_finger_version;



    @ViewInject(R.id.sv_old_camera)             private SurfaceView sv_old_camera;

    private OldResult openLedResult;
    private OldResult openCameraResult;
    private OldResult readIdResult;
    private OldResult readFingerResult;
    private OldResult closeCameraResult;
    private OldResult closeLedResult;

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
        smdtManager  = SmdtManager.create(this);
        idCardDriver = new IdCardDriver(this);
        fingerDriver = new MXFingerDriver(this,true);
        holder       = sv_old_camera.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        openLedResult       = new OldResult(OPEN_LED,      0, 0, R.id.tv_open_led_success,     R.id.tv_open_led_fail,      R.id.tv_old_open_led);
        openCameraResult    = new OldResult(OPEN_CAMERA,   0, 0, R.id.tv_open_camera_success,  R.id.tv_open_camera_fail,   R.id.tv_old_open_camera);
        readIdResult        = new OldResult(READ_ID,       0, 0, R.id.tv_read_id_success,      R.id.tv_read_id_fail,       R.id.tv_old_read_id);
        readFingerResult    = new OldResult(READ_FINGER,   0, 0, R.id.tv_read_finger_success,  R.id.tv_read_finger_fail,   R.id.tv_old_read_finger);
        closeCameraResult   = new OldResult(CLOSE_CAMERA,  0, 0, R.id.tv_close_camera_success, R.id.tv_close_camera_fail,  R.id.tv_old_close_camera);
        closeLedResult      = new OldResult(CLOSE_LED,     0, 0, R.id.tv_close_led_success,    R.id.tv_close_led_fail,     R.id.tv_old_close_led);
    }

    @Override
    protected void initView() {
        findViewById(R.id.tv_old_open_led).     setVisibility(View.INVISIBLE);
        findViewById(R.id.tv_old_open_camera).  setVisibility(View.INVISIBLE);
        findViewById(R.id.tv_old_read_id).      setVisibility(View.INVISIBLE);
        findViewById(R.id.tv_old_read_finger).  setVisibility(View.INVISIBLE);
        findViewById(R.id.tv_old_close_camera). setVisibility(View.INVISIBLE);
        findViewById(R.id.tv_old_close_led).    setVisibility(View.INVISIBLE);
        tv_old_finger_version   .setText("");
        tv_old_id_version       .setText("");
    }

    @Event(R.id.tv_old_start)
    private void onStart(View view) {
        tv_old_begin_time.setText(DateUtil.format(new Date()));
        continueFlag = true;
        if (oldThread != null && oldThread.isAlive()) {
            return;
        }
        oldThread = new OldThread();
        oldThread.start();
        enableButton(false);
    }

    @Event(R.id.tv_old_stop)
    private void onStop(View view) {
        tv_old_end_time.setText(DateUtil.format(new Date()));
        if (oldThread != null && oldThread.isAlive()) {
            oldThread.interrupt();
            oldThread = null;
        }
        if (camera != null) {
            camera.release();
            camera = null;
        }
        continueFlag = false;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        enableButton(true);
    }

    @Event(R.id.tv_pass)
    private void onPass(View view) {
        if (oldThread != null && oldThread.isAlive()) {
            Toast.makeText(this, "请先停止老化测试", Toast.LENGTH_SHORT).show();
            return;
        }
        EventBus.getDefault().post(new ResultEvent(Constants.ID_OLD, Constants.STATUS_PASS, getContent()));
        finish();
    }

    @Event(R.id.tv_deny)
    private void onDeny(View view) {
        if (oldThread != null && oldThread.isAlive()) {
            Toast.makeText(this, "请先停止老化测试", Toast.LENGTH_SHORT).show();
            return;
        }
        EventBus.getDefault().post(new ResultEvent(Constants.ID_OLD, Constants.STAUTS_DENIED, getContent()));
        finish();
    }

    private String getContent() {
        StringBuilder content = new StringBuilder();

        content.append(tv_old_begin_time.   getText().toString() + "$");
        content.append(tv_old_end_time.     getText().toString() + "$");
        content.append(tv_old_count.        getText().toString() + "$");
        content.append(openLedResult.       getSuccessCount()    + "$");
        content.append(openLedResult.       getFailCount()       + "$");
        content.append(openCameraResult.    getSuccessCount()    + "$");
        content.append(openCameraResult.    getFailCount()       + "$");
        content.append(readIdResult.        getSuccessCount()    + "$");
        content.append(readIdResult.        getFailCount()       + "$");
        content.append(readFingerResult.    getSuccessCount()    + "$");
        content.append(readFingerResult.    getFailCount()       + "$");
        content.append(closeCameraResult.   getSuccessCount()    + "$");
        content.append(closeCameraResult.   getFailCount()       + "$");
        content.append(closeLedResult.      getSuccessCount()    + "$");
        content.append(closeLedResult.      getFailCount()            );

        return content.toString();
    }

    class OldThread extends Thread  implements Runnable {

        @Override
        public void run() {
            while (continueFlag) {
                try {
                    Thread.sleep(INTERVAL_TIME);
                    if (continueFlag) {
                        count++;
                        bus.post(new CommonEvent(INIT_VIEW, count, ""));
                    }

                    if (continueFlag)   openLed();

                    if (continueFlag)   openCamera();

                    if (continueFlag)   readId();

                    if (continueFlag)   readFinger();

                    if (continueFlag)   closeCamera();

                    if (continueFlag)   closeLed();

                } catch (Exception e) {

                }
            }
        }

    }

    private void openLed() {
        try {
            int re = smdtManager.smdtSetExtrnalGpioValue(3, true);
            if (re == 1) {
                bus.post(new CommonEvent(OPEN_LED, 0, ""));
            } else {
                bus.post(new CommonEvent(OPEN_LED, -1, ""));
            }
        } catch (Exception e) {
            bus.post(new CommonEvent(OPEN_LED, -1, e.getMessage()));
        }
        try {
            Thread.sleep(INTERVAL_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        int re = -1;
        try {
            camera = Camera.open();
            camera.setPreviewDisplay(holder);
            if (camera != null) {
                camera.startPreview();
                re = 0;
            }
            bus.post(new CommonEvent(OPEN_CAMERA, re, ""));
        } catch (Exception e) {
            bus.post(new CommonEvent(OPEN_CAMERA, -1, e.getMessage()));
        }
        try {
            Thread.sleep(INTERVAL_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void readId() {
        int re = -1;
        try {
            byte[] bDevVersion = new byte[64];
            re = idCardDriver.mxGetIdCardModuleVersion(bDevVersion);
            bus.post(new CommonEvent(READ_ID, re, new String(bDevVersion)));
        } catch (Exception e) {
            bus.post(new CommonEvent(READ_ID, -1, "" + re));
        }
        try {
            Thread.sleep(INTERVAL_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void readFinger() {
        int re = -1;
        try {
            String ver = fingerDriver.mxGetDriverVersion();
            if (ver != null) {
                re = 0;
            }
            bus.post(new CommonEvent(READ_FINGER, re, ver));
        } catch (Exception e) {
            bus.post(new CommonEvent(READ_FINGER, -1, e.getMessage()));
        }
        try {
            Thread.sleep(INTERVAL_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        int re = -1;
        try {
            if (camera != null) {
                camera.stopPreview();
                camera.release();
                camera = null;
                re = 0;
            }
            bus.post(new CommonEvent(CLOSE_CAMERA, re, ""));
        } catch (Exception e) {
            bus.post(new CommonEvent(CLOSE_CAMERA, re, ""));
        }
        try {
            Thread.sleep(INTERVAL_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void closeLed() {
        try {
            int re = smdtManager.smdtSetExtrnalGpioValue(3, false);
            bus.post(new CommonEvent(CLOSE_LED, re, ""));
        } catch (Exception e) {
            bus.post(new CommonEvent(CLOSE_LED, -1, ""));
        }
        try {
            Thread.sleep(INTERVAL_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (oldThread != null && oldThread.isAlive()) {
            Toast.makeText(this, "请先停止老化测试", Toast.LENGTH_SHORT).show();
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (oldThread != null) {
            oldThread.interrupt();
            oldThread = null;
        }
        smdtManager.smdtSetExtrnalGpioValue(3, false);
        bus.unregister(this);
        super.onDestroy();
    }

    private void enableButton(boolean flag) {
        tv_old_start.setClickable(flag);
        tv_old_stop.setClickable(!flag);
        if (flag) {
            tv_old_start.setTextColor(getResources().getColor(R.color.blue_band_dark));
            tv_old_stop.setTextColor(Color.GRAY);
        } else {
            tv_old_start.setTextColor(Color.GRAY);
            tv_old_stop.setTextColor(getResources().getColor(R.color.blue_band_dark));
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommonEvent(CommonEvent e) {
        switch (e.getCode()) {
            case INIT_VIEW:
                initView();
                tv_old_count.setText("" + e.getResult());
                break;
            case OPEN_LED :
                showTvById(e.getResult(), openLedResult);
                break;
            case OPEN_CAMERA :
                showTvById(e.getResult(), openCameraResult);
                break;
            case READ_ID :
                showTvById(e.getResult(), readIdResult);
                tv_old_id_version.setText(e.getContent());
                break;
            case READ_FINGER :
                showTvById(e.getResult(), readFingerResult);
                tv_old_finger_version.setText(e.getContent());
                break;
            case CLOSE_CAMERA :
                showTvById(e.getResult(), closeCameraResult);
                break;
            case CLOSE_LED :
                showTvById(e.getResult(), closeLedResult);
                break;
        }
    }

    private void showTvById(int re, OldResult oldResult) {
        TextView progressTv = (TextView) findViewById(oldResult.getProgressTvid());

        progressTv.setVisibility(View.VISIBLE);
        if (re == 0) {
            progressTv.setBackgroundColor(getResources().getColor(R.color.green_dark));

            TextView successTv = (TextView) findViewById(oldResult.getSuccessTvId());
            oldResult.setSuccessCount(oldResult.getSuccessCount() + 1);
            successTv.setText("" + oldResult.getSuccessCount());
        } else {
            progressTv.setBackgroundColor(Color.RED);

            TextView failTv = (TextView) findViewById(oldResult.getFailTvId());
            oldResult.setFailCount(oldResult.getFailCount() + 1);
            failTv.setText("" + oldResult.getFailCount());
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

}
