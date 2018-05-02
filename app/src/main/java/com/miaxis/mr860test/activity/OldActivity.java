package com.miaxis.mr860test.activity;

import android.app.smdt.SmdtManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.CommonEvent;
import com.miaxis.mr860test.domain.DisableEvent;
import com.miaxis.mr860test.domain.HideCpEvent;
import com.miaxis.mr860test.domain.OldResult;
import com.miaxis.mr860test.domain.ResultEvent;
import com.miaxis.mr860test.domain.ShowCpEvent;
import com.miaxis.mr860test.domain.ToastEvent;
import com.miaxis.mr860test.utils.DateUtil;
import com.miaxis.mr860test.utils.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import org.zz.idcard_hid_driver.IdCardDriver;
import org.zz.mxhidfingerdriver.MXFingerDriver;

import java.io.File;
import java.util.Date;
import java.util.List;

@ContentView(R.layout.activity_old)
public class OldActivity extends BaseTestActivity implements SurfaceHolder.Callback {

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

    private static final int INTERVAL_TIME = 1500;
    private boolean continueFlag = false;

    private OldThread oldThread;

    private EventBus bus;
    private boolean backFlag;
    private SurfaceHolder oldHolder;
    private Camera mCamera;
    private boolean hasFinger;
    private boolean hasIdCard;

    @ViewInject(R.id.tv_pass)                   private TextView tv_pass;
    @ViewInject(R.id.tv_deny)                   private TextView tv_deny;
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
    private String startTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        initData();
        initView();

        bus.post(new DisableEvent(true, false, false));
        if (oldThread != null && oldThread.isAlive()) {
            oldThread.interrupt();
            oldThread = null;
        }
        oldThread = new OldThread();
        oldThread.start();
        backFlag = true;
    }

    @Override
    protected void initData() {
        bus = EventBus.getDefault();
        bus.register(this);
        smdtManager  = SmdtManager.create(this);
        idCardDriver = new IdCardDriver(this);
        int pid = 0x0202;
        int vid = 0x821B;
        fingerDriver = new MXFingerDriver(this, pid, vid);
        oldHolder = sv_old_camera.getHolder();
        oldHolder.addCallback(this);

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
        hasFinger = hasFinger();
        hasIdCard = hasIdCard();
        if (smdtManager != null) {
            smdtManager.smdtSetGpioValue(2, true);
        }

        preReadIdDevVersion();
        if (startTime == null) {
            startTime = DateUtil.format(new Date());
            tv_old_begin_time.setText(startTime);
        }
        continueFlag = true;
    }

    @Event(R.id.tv_old_stop)
    private void onStop(View view) {
        tv_old_end_time.setText(DateUtil.format(new Date()));
        continueFlag = false;
        bus.post(new DisableEvent(false, false, false));
    }

    @Event(R.id.tv_pass)
    private void onPass(View view) {
        if (oldThread != null && oldThread.isAlive()) {
            oldThread.interrupt();
            oldThread = null;
        }
        bus.post(new ResultEvent(Constants.ID_OLD, Constants.STAUTS_RECORD, getContent()));
        finish();
    }

    private String getContent() {
        StringBuilder content = new StringBuilder();

        content.append(tv_old_begin_time    .getText().toString()).append("$");
        content.append(tv_old_end_time      .getText().toString()).append("$");
        content.append(tv_old_count         .getText().toString()).append("$");
        content.append(openLedResult        .getSuccessCount())   .append("$");
        content.append(openLedResult        .getFailCount())      .append("$");
        content.append(openCameraResult     .getSuccessCount())   .append("$");
        content.append(openCameraResult     .getFailCount())      .append("$");
        content.append(readIdResult         .getSuccessCount())   .append("$");
        content.append(readIdResult         .getFailCount())      .append("$");
        content.append(readFingerResult     .getSuccessCount())   .append("$");
        content.append(readFingerResult     .getFailCount())      .append("$");
        content.append(closeCameraResult    .getSuccessCount())   .append("$");
        content.append(closeCameraResult    .getFailCount())      .append("$");
        content.append(closeLedResult       .getSuccessCount())   .append("$");
        content.append(closeLedResult       .getFailCount());

        return content.toString();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    class OldThread extends Thread  implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(INTERVAL_TIME);
                    if (continueFlag) {
                        bus.post(new DisableEvent(false, true, false));
                        count++;
                        bus.post(new CommonEvent(INIT_VIEW, count, ""));
                        openLed();
                        oldOpenCamera();
                        if (hasIdCard) {
                            readId();
                        }
                        if (hasFinger) {
                            readFinger();
                        }
                        oldCloseCamera();
                        closeLed();
                        if (!continueFlag) {
                            smdtManager.smdtSetExtrnalGpioValue(2, false);
                        }
                    } else {
                        bus.post(new DisableEvent(true, false, true));
                        Thread.sleep(1000);
                    }
                }
            } catch (Exception e) {
                bus.post(new ToastEvent("系统错误! 请重新打开老化测试"));
            }
        }
    }

    private void openLed() {
        try {
            Thread.sleep(INTERVAL_TIME);
            int re = smdtManager.smdtSetExtrnalGpioValue(3, true);
            Thread.sleep(500);
            bus.post(new CommonEvent(OPEN_LED, re, ""));
        } catch (InterruptedException ie) {
        } catch (Exception e) {
            bus.post(new CommonEvent(OPEN_LED, -1, e.getMessage()));
        }
    }

    private void oldOpenCamera() {
        try {
            Thread.sleep(INTERVAL_TIME);
            Thread.sleep(500);
            openCamera();
        } catch (InterruptedException ie) {
        } catch (Exception e) {
            bus.post(new CommonEvent(OPEN_CAMERA, -1, e.getMessage()));
        }
    }

    private void readId() {
        int re = -1;
        try {
            Thread.sleep(INTERVAL_TIME);
            byte[] bDevVersion = new byte[64];
            for (int i=0; i<20; i++) {
                Thread.sleep(100);
                re = idCardDriver.mxGetIdCardModuleVersion(bDevVersion);
                if (re == 0) {
                    break;
                }
            }
            bus.post(new CommonEvent(READ_ID, re, new String(bDevVersion)));
        } catch (InterruptedException ie) {
        } catch (Exception e) {
            bus.post(new CommonEvent(READ_ID, -1, "" + re));
        }
    }

    private void readFinger() {
        int re = -1;
        try {
            Thread.sleep(INTERVAL_TIME);
            byte[] bVersion = new byte[120];
            re = fingerDriver.mxGetDevVersion(bVersion);
            bus.post(new CommonEvent(READ_FINGER, re, new String(bVersion)));
        } catch (InterruptedException ie) {
        } catch (Exception e) {
            bus.post(new CommonEvent(READ_FINGER, -1, e.getMessage()));
        }
    }

    private void oldCloseCamera() {
        try {
            Thread.sleep(INTERVAL_TIME);
            closeCamera();
        } catch (InterruptedException ie) {
            bus.post(new CommonEvent(CLOSE_CAMERA, -1, ie.getMessage()));
        } catch (Exception e) {
            bus.post(new CommonEvent(CLOSE_CAMERA, -1, e.getMessage()));
        }
    }

    private void closeLed() {
        try {
            Thread.sleep(INTERVAL_TIME);
            int re = smdtManager.smdtSetExtrnalGpioValue(3, false);
            Thread.sleep(1000);
            bus.post(new CommonEvent(CLOSE_LED, re, ""));
        } catch (InterruptedException ie) {
        } catch (Exception e) {
            bus.post(new CommonEvent(CLOSE_LED, -1, ""));
        }
    }

    @Override
    public void onBackPressed() {
        if (continueFlag || !backFlag) {
            Toast.makeText(this, "请先停止老化测试", Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void finish() {
        if (oldThread != null && oldThread.isAlive()) {
            oldThread.interrupt();
            oldThread = null;
        }
//        smdtManager.smdtSetExtrnalGpioValue(2, false);
//        smdtManager.smdtSetExtrnalGpioValue(3, false);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        if (oldThread != null && oldThread.isAlive()) {
            oldThread.interrupt();
            oldThread = null;
        }
        Log.e("onDestroy","onDestroy");
        bus.unregister(this);
        super.onDestroy();
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
                if (e.getResult() != 0) {
                    Toast.makeText(this, e.getContent(), Toast.LENGTH_SHORT).show();
                }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowCpEvent(ShowCpEvent e) {
        sv_old_camera.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHideCpEvent(HideCpEvent e) {
        sv_old_camera.setVisibility(View.INVISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisableEvent(DisableEvent e) {
        enableButtons(e.isFlag(), tv_old_start, R.color.dark);
        enableButtons(e.isFlag2(), tv_old_stop, R.color.dark);
        enableButtons(e.isFlag3(), tv_pass, R.color.dark);
        backFlag = e.isFlag3();
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

    private void preReadIdDevVersion() {
        byte[] bDevVersion;
        int re1;
        for (int i=0; i<100; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bDevVersion = new byte[64];
            re1 = idCardDriver.mxGetIdCardModuleVersion(bDevVersion);
            if (re1 == 0) {
                break;
            }
        }
    }

    @Override
    protected void onPause() {
        Log.e("onPause","onPause");
        onStop(null);
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e("onStop","onStop");
        super.onStop();
    }

    private void openCamera() {
        try {
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(180);
            mCamera.setPreviewDisplay(oldHolder); // 通过SurfaceView显示取景画面
            mCamera.startPreview(); // 开始预览
            bus.post(new CommonEvent(OPEN_CAMERA, 0, ""));
        } catch (Exception e) {
            bus.post(new CommonEvent(OPEN_CAMERA, -1, e.getMessage()));
        }
    }

    private void closeCamera() {
        try {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null) ;
                mCamera.setPreviewDisplay(null);
                mCamera.release();
                mCamera = null;
                bus.post(new CommonEvent(CLOSE_CAMERA, 0, ""));
            } else {
                bus.post(new CommonEvent(CLOSE_CAMERA, -1, "mCamera==null"));
            }
        } catch (Exception e) {
            bus.post(new CommonEvent(CLOSE_CAMERA, -1, e.getMessage()));
        }
    }

    @Event(R.id.btn_exit)
    private void onExit(View view) {
        finish();
    }

    private boolean hasFinger() {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), FileUtil.VERSION_CONFIG_PATH);
            if (!file.exists()) {
                Toast.makeText(this, "版本配置文件缺失", Toast.LENGTH_LONG).show();
            } else {
                List<String> stringList = FileUtil.readFileToList(file);
                if (stringList != null) {
                    String hasFinger = stringList.get(6);
                    String re = hasFinger.split("=")[1];
                    return TextUtils.equals(re, "true");
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    private boolean hasIdCard() {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), FileUtil.VERSION_CONFIG_PATH);
            if (!file.exists()) {
                Toast.makeText(this, "版本配置文件缺失", Toast.LENGTH_LONG).show();
            } else {
                List<String> stringList = FileUtil.readFileToList(file);
                if (stringList != null) {
                    String hasIdCard = stringList.get(7);
                    String re = hasIdCard.split("=")[1];
                    return TextUtils.equals(re, "true");
                }
            }
        } catch (Exception e) {
        }
        return false;
    }
}
