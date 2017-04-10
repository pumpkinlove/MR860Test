package com.miaxis.mr860test.activity;

import java.io.File;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import org.zz.mxhidfingerdriver.MXFingerDriver;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.CommonEvent;
import com.miaxis.mr860test.domain.DisableEvent;
import com.miaxis.mr860test.domain.FingerEvent;
import com.miaxis.mr860test.domain.ResultEvent;
import com.miaxis.mr860test.domain.ScrollMessageEvent;
import com.miaxis.mr860test.utils.FileUtil;
import org.zz.jni.zzFingerAlg;

@ContentView(R.layout.activity_finger)
public class FingerActivity extends BaseTestActivity {

    private static final int GET_DEVICE_VERSION = 10002;
    private static final int GET_DRIVER_VERSION = 10003;

    private static final int LEVEL              = 2;
    // 图像
    private static final int TIME_OUT           = 15 * 1000; // 等待按手指的超时时间，单位：毫
    private static final int IMAGE_X_BIG        = 256;
    private static final int IMAGE_Y_BIG        = 360;
    private static final int IMAGE_SIZE_BIG     = IMAGE_X_BIG * IMAGE_Y_BIG;
    private static final int TZ_SIZE            = 512;

    private byte[] colImgBuf                    = new byte[IMAGE_SIZE_BIG];
    private byte[] printImgBuf                  = new byte[IMAGE_SIZE_BIG];

    // 线程
    private Thread mGetFingerThread       = null;
    private Thread mGetDevVersionThread  = null;

    // 中正指纹仪驱动
    private MXFingerDriver fingerDriver;
    private zzFingerAlg alg;
    private boolean hasTest = false;
    private boolean fingerPass = false;

    private byte[] colTzBuffer   = new byte[TZ_SIZE];
    private byte[] printTzBuffer = new byte[TZ_SIZE];
    private EventBus bus;

    @ViewInject(R.id.btn_getFinger)             private Button btn_getFinger;
    @ViewInject(R.id.btn_cancel)                private Button btn_cancel;
    @ViewInject(R.id.btn_verify)                private Button btn_verify;

    @ViewInject(R.id.tv_pass)                   private TextView tv_pass;
    @ViewInject(R.id.tv_test)                   private TextView tv_test;
    @ViewInject(R.id.sv_show_msg)               private ScrollView sv_show_msg;

    @ViewInject(R.id.tv_message)                private TextView tv_message;

    @ViewInject(R.id.tv_f_device_version_need)  private TextView tv_f_device_version_need;
    @ViewInject(R.id.tv_f_device_version)       private TextView tv_f_device_version;
    @ViewInject(R.id.tv_f_driver_version)       private TextView tv_f_driver_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);

        x.view().inject(this);

        initData();
        initView();
        initVersion();

        onGetDriverVersionClicked(null);
        onGetDevVersionClicked(null);

    }

    @Override
    protected void initData() {
        int pid = 0x0202;
        int vid = 0x821B;
        fingerDriver = new MXFingerDriver(this, pid, vid);
        alg = new zzFingerAlg();
        bus = EventBus.getDefault();
        bus.register(this);
    }

    @Override
    protected void initView() {

    }

    private void initVersion() {
        try {

            File file = new File(Environment.getExternalStorageDirectory(), FileUtil.VERSION_CONFIG_PATH);
            if (!file.exists()) {
                tv_f_device_version_need.setText("版本配置文件缺失");
            } else {
                List<String> stringList = FileUtil.readFileToList(file);
                if (stringList != null) {
                    tv_f_device_version_need.setText(stringList.get(1));
                }
            }

        } catch (Exception e) {

        }
    }

    private class GetFingerThread extends Thread {
        public void run() {
            try {
                getFinger();
            } catch (Exception e) {
                appendMessage("采集图像", Constants.FAIL_HTML);
            }
        }
    }

    public void getFinger() {
        bus.post(new DisableEvent(false, true, false, false));
        colImgBuf = new byte[IMAGE_SIZE_BIG];
        int ret = fingerDriver.mxAutoGetImage(colImgBuf, IMAGE_X_BIG, IMAGE_Y_BIG, TIME_OUT, 0);
        if (ret == 0) {
            bus.post(new FingerEvent(R.id.iv_col_finger, colImgBuf));
            appendMessage("采集图像", Constants.SUCCESS_HTML);
            ret = alg.mxGetTz512(colImgBuf, colTzBuffer);
            if (ret == 1) {
                appendMessage("提取特征", Constants.SUCCESS_HTML);
            } else {
                appendMessage("提取特征", Constants.FAIL_HTML);
            }
        } else {
            appendMessage("采集图像", Constants.FAIL_HTML);
        }
        bus.post(new DisableEvent(true, false, true, true));
    }

    private class GetDevVersionThread extends Thread {
        public void run() {
            try {
                GetDevVersion();
            } catch (Exception e) {
            }
        }
    }

    public void GetDevVersion() {
        int iRet = -1;
        byte[] bVersion = new byte[120];
        bus.post(new CommonEvent(GET_DEVICE_VERSION, iRet, ""));
        for (int i=0; i<20; i++) {
            iRet = fingerDriver.mxGetDevVersion(bVersion);
            if (iRet == 0) {
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (iRet == 0) {
            bus.post(new CommonEvent(GET_DEVICE_VERSION, iRet, new String(bVersion)));
        } else {
            bus.post(new CommonEvent(GET_DEVICE_VERSION, iRet, new String("获取设备版本失败")));
        }
    }

    @Event(R.id.btn_getDevVersion)
    private void onGetDevVersionClicked(View view) {
        onDisableEvent(new DisableEvent(false, false, false, false));
        if (mGetDevVersionThread != null) {
            mGetDevVersionThread.interrupt();
            mGetDevVersionThread = null;
        }
        mGetDevVersionThread = new GetDevVersionThread();
        mGetDevVersionThread.start();
    }

    @Event(R.id.btn_getDriverVersion)
    private void onGetDriverVersionClicked(View view) {
        onDisableEvent(new DisableEvent(false, false, false, false));
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ver = fingerDriver.mxGetDriverVersion();
                bus.post(new CommonEvent(GET_DRIVER_VERSION, 0, ver));
            }
        }).start();
    }

    @Event(R.id.btn_getFinger)
    private void onGetFingerClicked(View view) {
        hasTest = true;
        appendMessage("请按手指...", "");
        if (mGetFingerThread != null) {
            mGetFingerThread.interrupt();
            mGetFingerThread = null;
        }
        mGetFingerThread = new GetFingerThread();
        mGetFingerThread.start();
    }

    @Event(R.id.btn_cancel)
    private void onCancelClicked(View view) {
        fingerDriver.mxCancelGetImage();
    }

    private void appendMessage(CharSequence message, CharSequence type) {
        bus.post(new ScrollMessageEvent(message, type));
    }

    @Event(R.id.btn_verify)
    private void onVerifyClicked(View view) {
        fingerPass = false;
        if (colTzBuffer == null || colTzBuffer.length != TZ_SIZE) {
            appendMessage("请先采集指纹", "");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                verifyFinger();
            }
        }).start();
    }

    private void verifyFinger() {
        bus.post(new DisableEvent(false, true, false, false));
        appendMessage("请按手指...", "");
        printImgBuf = new byte[IMAGE_SIZE_BIG];
        int ret = fingerDriver.mxAutoGetImage(printImgBuf, IMAGE_X_BIG, IMAGE_Y_BIG, TIME_OUT, 0);
        if (0 != ret) {
            appendMessage("指纹图像采集", Constants.FAIL_HTML);
        } else {
            appendMessage("指纹图像采集", Constants.SUCCESS_HTML);
            bus.post(new FingerEvent(R.id.iv_print_finger, printImgBuf));
            ret = alg.mxGetTz512(printImgBuf, printTzBuffer);
            if (ret == 1) {
                appendMessage("提取特征", Constants.SUCCESS_HTML);
                ret = alg.mxFingerMatch512(colTzBuffer, printTzBuffer, LEVEL);
                if (ret == 0) {
                    appendMessage("比对", Constants.SUCCESS_HTML);
                    fingerPass = true;
                } else {
                    appendMessage("比对 ", Constants.FAIL_HTML);
                }
            } else {
                appendMessage("提取特征", Constants.FAIL_HTML);
            }
        }
        bus.post(new DisableEvent(true, false, true, true));
    }

    @Event(R.id.tv_pass)
    private void onPass(View view) {
        bus.post(new ResultEvent(Constants.ID_FINGER, Constants.STATUS_PASS));
        finish();
    }

    @Event(R.id.tv_deny)
    private void onDeny(View view) {
        fingerDriver.mxCancelGetImage();
        bus.post(new ResultEvent(Constants.ID_FINGER, Constants.STAUTS_DENIED));
        finish();
    }

    @Event(R.id.tv_test)
    private void onTest(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getFinger();
                verifyFinger();
            }
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisableEvent(DisableEvent e) {
        enableButtons(e.isFlag(), btn_getFinger, R.color.dark);
        enableButtons(e.isFlag2(), btn_cancel, R.color.dark);
        enableButtons(e.isFlag3(), btn_verify, R.color.dark);
        enableButtons(e.isFlag4(), tv_test, R.color.dark);
        enableButtons(fingerPass, tv_pass, R.color.green_dark);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommonEvent(CommonEvent e) {
        switch (e.getCode()) {
            case GET_DEVICE_VERSION :
                tv_f_device_version.setText(e.getContent());
                String v_need = tv_f_device_version_need.getText().toString().trim();
                String v = e.getContent().trim();
                if (v.equals(v_need)) {
                    tv_f_device_version.setTextColor(getResources().getColor(R.color.green_dark));
                    onDisableEvent(new DisableEvent(true, false, true, true));
                } else {
                    tv_f_device_version.setTextColor(getResources().getColor(R.color.red));
                    onDisableEvent(new DisableEvent(false, false, false, false));
                }
                break;
            case GET_DRIVER_VERSION :
                tv_f_driver_version.setText(e.getContent());
                if (null == e.getContent() || "".equals(e.getContent())) {
                    onDisableEvent(new DisableEvent(false, false, false, false));
                } else {
                    onDisableEvent(new DisableEvent(true, true, true, true));
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFingerEvent(FingerEvent e) {
        Bitmap bitmap = fingerDriver.Raw2Bimap(e.getImgBuff(), IMAGE_X_BIG, IMAGE_Y_BIG);
        if (bitmap != null) {
            ImageView ivv = (ImageView)findViewById(e.getIvId());
            if (ivv != null) {
                ivv.setImageBitmap(bitmap);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScrollMessageevent(ScrollMessageEvent e){
        if (e.getType() != null && e.getType().equals(Constants.FAIL_HTML)) {
            fingerPass = false;
            enableButtons(false, tv_pass, Color.GREEN);
        }
        tv_message.append(e.getMessage());
        tv_message.append(Html.fromHtml(e.getType().toString()));
        tv_message.append("\r\n");
        sv_show_msg.post(new Runnable() {
            public void run() {
                sv_show_msg.fullScroll(ScrollView.FOCUS_DOWN);      //滚动到底部
            }
        });
    }

    @Override
    protected void onPause() {
        onCancelClicked(null);
        super.onPause();
    }
}
