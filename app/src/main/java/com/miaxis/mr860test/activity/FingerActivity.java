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
import android.os.Bundle;
import android.os.Environment;
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

    private static final int GET_IMAGE          = 10001;
    private static final int GET_DEVICE_VERSION = 10002;
    private static final int GET_DRIVER_VERSION = 10003;
    private static final int GET_MB             = 10004;
    private static final int VERIFY             = 10005;
    private static final int CANCEL             = 10006;
    private static final int GET_MB_1           = 10007;
    private static final int GET_MB_2           = 10008;
    private static final int GET_MB_3           = 10009;

    private static final int LEVEL              = 2;
    // 图像
    private static final int TIME_OUT           = 15 * 1000; // 等待按手指的超时时间，单位：毫
    private static final int IMAGE_X_BIG        = 256;
    private static final int IMAGE_Y_BIG        = 360;
    private static final int IMAGE_SIZE_BIG     = IMAGE_X_BIG * IMAGE_Y_BIG;
    private static final int TZ_SIZE            = 512;

    private byte[] bImgBuf                      = new byte[IMAGE_SIZE_BIG];
    private byte[] bImgBuf1                     = new byte[IMAGE_SIZE_BIG];
    private byte[] bImgBuf2                     = new byte[IMAGE_SIZE_BIG];
    private byte[] bImgBuf3                     = new byte[IMAGE_SIZE_BIG];

    private Bitmap m_bitmap                     = null;

    // 线程
    private GetImageThread        m_GetImageThread       = null;
    private GetDevVersionThread   m_GetDevVersionThread  = null;

    // 中正指纹仪驱动
    private MXFingerDriver fingerDriver;
    private zzFingerAlg alg;
    private int mbFlag = GET_MB_1;
    private boolean hasMb = false;
    private boolean hasTest = false;

    private byte[] tzBuffer1 = new byte[TZ_SIZE];
    private byte[] tzBuffer2 = new byte[TZ_SIZE];
    private byte[] tzBuffer3 = new byte[TZ_SIZE];
    private byte[] mbBuffer  = new byte[TZ_SIZE];
    private byte[] vBuffer   = new byte[TZ_SIZE];



    private EventBus bus;

    @ViewInject(R.id.btn_getDevVersion)         private Button btn_getDevVersion;
    @ViewInject(R.id.btn_getDriverVersion)      private Button btn_getDriverVersion;
    @ViewInject(R.id.btn_getImage)              private Button btn_getImage;
    @ViewInject(R.id.btn_cancel)                private Button btn_cancel;
    @ViewInject(R.id.btn_getMB)                 private Button btn_getMB;
    @ViewInject(R.id.btn_verify)                private Button btn_verify;

    @ViewInject(R.id.tv_pass)                   private TextView tv_pass;
    @ViewInject(R.id.tv_test)                   private TextView tv_test;
    @ViewInject(R.id.sv_show_msg)               private ScrollView sv_show_msg;

    @ViewInject(R.id.iv_finger)                 private ImageView iv_finger;
    @ViewInject(R.id.iv_mb1)                    private ImageView iv_mb1;
    @ViewInject(R.id.iv_mb2)                    private ImageView iv_mb2;
    @ViewInject(R.id.iv_mb3)                    private ImageView iv_mb3;

    @ViewInject(R.id.tv_message)                private TextView tv_message;
    @ViewInject(R.id.tv_score)                  private TextView tv_score;

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
        fingerDriver = new MXFingerDriver(this,true);
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

    private class GetImageThread extends Thread {
        public void run() {
            try {
                GetImage();
            } catch (Exception e) {
                appendMessage("采集图像异常" + e.getMessage());
            }
        }
    }

    public void GetImage() {
        bImgBuf = new byte[IMAGE_SIZE_BIG];
        int ret = fingerDriver.mxAutoGetImage(bImgBuf, IMAGE_X_BIG, IMAGE_Y_BIG, TIME_OUT, 0);
        if (ret == 0) {
            bus.post(new FingerEvent(R.id.iv_finger, bImgBuf));
            appendMessage("采集图像成功");
        } else {
            appendMessage("采集图像失败 " + ret);
        }
        bus.post(new DisableEvent(true));
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

    @Event(R.id.btn_getImage)
    private void onGetImageClicked(View view) {
        hasTest = true;
        appendMessage("请按手指...");
        onDisableEvent(new DisableEvent(false));
        if (m_GetImageThread != null) {
            m_GetImageThread.interrupt();
            m_GetImageThread = null;
        }
        m_GetImageThread = new GetImageThread();
        m_GetImageThread.start();
    }

    @Event(R.id.btn_getDevVersion)
    private void onGetDevVersionClicked(View view) {
        onDisableEvent(new DisableEvent(false));
        if (m_GetDevVersionThread != null) {
            m_GetDevVersionThread.interrupt();
            m_GetDevVersionThread = null;
        }
        m_GetDevVersionThread = new GetDevVersionThread();
        m_GetDevVersionThread.start();
    }

    @Event(R.id.btn_getDriverVersion)
    private void onGetDriverVersionClicked(View view) {
        onDisableEvent(new DisableEvent(false));
        bus.post(new CommonEvent(GET_DRIVER_VERSION, 0, ""));
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ver = fingerDriver.mxGetDriverVersion();
                bus.post(new CommonEvent(GET_DRIVER_VERSION, 0, ver));
            }
        }).start();
    }

    @Event(R.id.btn_cancel)
    private void onCancelClicked(View view) {
        continueFlag = false;
        fingerDriver.mxCancelGetImage();
        bus.post(new DisableEvent(true));
    }

    boolean continueFlag = true;

    @Event(R.id.btn_getMB)
    private void onGetMBClicked(final View view) {
        hasMb = false;
        mbBuffer = new byte[TZ_SIZE];
        bImgBuf1 = new byte[IMAGE_SIZE_BIG];
        bImgBuf2 = new byte[IMAGE_SIZE_BIG];
        bImgBuf3 = new byte[IMAGE_SIZE_BIG];
        iv_mb1.setImageBitmap(null);
        iv_mb2.setImageBitmap(null);
        iv_mb3.setImageBitmap(null);
        hasTest = true;
        mbFlag = GET_MB_1;
        onDisableEvent(new DisableEvent(false));
        continueFlag = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                int re = -10;
                while (continueFlag) {
                    appendMessage("请按手指...");
                    bImgBuf = new byte[IMAGE_SIZE_BIG];
                    re = fingerDriver.mxAutoGetImage(bImgBuf, IMAGE_X_BIG, IMAGE_Y_BIG, TIME_OUT, 0);
                    if (0 != re) {
                        appendMessage("图像采集失败 " + re);
                        if (-2 == re) {
                            hasMb = false;
                            return;
                        }
                    } else {
                        getTzByMbFlag();
                    }
                }
                re = alg.mxGetMB512(tzBuffer1, tzBuffer2, tzBuffer3, mbBuffer);
                if (re > 0) {
                    appendMessage("合成模板成功,得分 " + re);
                    hasMb = true;
                } else {
                    appendMessage("合成模板失败 " + re);
                    hasMb = false;
                }
                if (view == null && hasMb) {     // 点击开始测试 会传进来null
                    onVerifyClicked(null);
                }
            }
        }).start();
    }

    private void getTzByMbFlag() {
        switch (mbFlag) {
            case GET_MB_1:
                bImgBuf1 = bImgBuf;
                bus.post(new FingerEvent(R.id.iv_finger, bImgBuf1));
                appendMessage("图像采集成功");
                int re1 = alg.mxGetTz512(bImgBuf1, tzBuffer1);
                if (re1 == 1) {
                    appendMessage("特征 1 提取成功");
                    bus.post(new FingerEvent(R.id.iv_mb1, bImgBuf1));
                    mbFlag = GET_MB_2;
                } else {
                    appendMessage("特征 1 提取失败" + re1);
                }
                break;
            case GET_MB_2:
                bImgBuf2 = bImgBuf;
                bus.post(new FingerEvent(R.id.iv_finger, bImgBuf2));
                appendMessage("图像采集成功");
                int re2 = alg.mxGetTz512(bImgBuf2, tzBuffer2);
                if (re2 == 1) {
                    appendMessage("特征 2 提取成功");
                    bus.post(new FingerEvent(R.id.iv_mb2, bImgBuf2));
                    mbFlag = GET_MB_3;
                } else {
                    appendMessage("特征 2 提取失败" + re2);
                }
                break;
            case GET_MB_3:
                bImgBuf3 = bImgBuf;
                bus.post(new FingerEvent(R.id.iv_finger, bImgBuf3));
                appendMessage("图像采集成功");
                int re3 = alg.mxGetTz512(bImgBuf3, tzBuffer3);
                if (re3 == 1) {
                    appendMessage("特征 3 提取成功");
                    bus.post(new FingerEvent(R.id.iv_mb3, bImgBuf3));
                    mbFlag = GET_MB_1;
                    continueFlag = false;
                    bus.post(new DisableEvent(true));
                } else {
                    appendMessage("特征 3 提取失败" + re3);
                }
                break;

        }
    }

    private void appendMessage(String message) {
        bus.post(new ScrollMessageEvent(message));
    }

    @Event(R.id.btn_verify)
    private void onVerifyClicked(View view) {
        onDisableEvent(new DisableEvent(false));
        new Thread(new Runnable() {
            @Override
            public void run() {
                appendMessage("请按手指...");
                bImgBuf = new byte[IMAGE_SIZE_BIG];
                int ret = fingerDriver.mxAutoGetImage(bImgBuf, IMAGE_X_BIG, IMAGE_Y_BIG, TIME_OUT, 0);
                if (0 != ret) {
                    appendMessage("指纹图像采集失败");
                    return;
                } else {
                    appendMessage("指纹图像采集成功");
                    bus.post(new FingerEvent(R.id.iv_finger, bImgBuf));
                    ret = alg.mxGetTz512(bImgBuf, vBuffer);
                    if (ret == 1) {
                        appendMessage("提取特征成功");
                        ret = alg.mxFingerMatch512(mbBuffer, vBuffer, LEVEL);
                        if (ret == 0) {
                            appendMessage("比对通过！");
                        } else {
                            appendMessage("比对失败 " + ret);
                        }
                    } else {
                        appendMessage("提取特征失败 " + ret);
                    }
                }
                bus.post(new DisableEvent(true));
            }
        }).start();


    }

    @Event(R.id.tv_pass)
    private void onPass(View view) {
        bus.post(new ResultEvent(Constants.ID_FINGER, Constants.STATUS_PASS));
        finish();
    }

    @Event(R.id.tv_deny)
    private void onDeny(View view) {
        bus.post(new ResultEvent(Constants.ID_FINGER, Constants.STAUTS_DENIED));
        finish();
    }

    @Event(R.id.tv_test)
    private void onTest(View view) {
        onGetMBClicked(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisableEvent(DisableEvent e) {
        if (e.isFlag()) {
            tv_pass             .setTextColor(getResources().getColor(R.color.green_dark));
        } else {
            tv_pass             .setTextColor(getResources().getColor(R.color.gray_dark));
        }
        enableButtons(e.isFlag(), tv_test, R.color.dark);
        tv_pass                 .setClickable(e.isFlag());
        tv_pass                 .setEnabled(e.isFlag());
        btn_getImage            .setEnabled(e.isFlag());
        btn_getDevVersion       .setEnabled(e.isFlag());
        btn_getDriverVersion    .setEnabled(e.isFlag());
        btn_getMB               .setEnabled(e.isFlag());
        btn_cancel              .setEnabled(!e.isFlag());
        if (!hasMb) {
            btn_verify.setEnabled(false);
        } else {
            btn_verify.setEnabled(e.isFlag());
        }
        if (!hasTest) {
            tv_pass.setTextColor(getResources().getColor(R.color.gray_dark));
            tv_pass.setClickable(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommonEvent(CommonEvent e) {
        switch (e.getCode()) {
            case GET_IMAGE :
                break;
            case GET_DEVICE_VERSION :
                tv_f_device_version.setText(e.getContent());
                String v_need = tv_f_device_version_need.getText().toString().trim();
                String v = e.getContent().trim();
                if (v.equals(v_need)) {
                    tv_f_device_version.setTextColor(getResources().getColor(R.color.green_dark));
                    bus.post(new DisableEvent(true));
                } else {
                    tv_f_device_version.setTextColor(getResources().getColor(R.color.red));
                    bus.post(new DisableEvent(false));
                    btn_cancel.setEnabled(false);
                }
                break;
            case GET_DRIVER_VERSION :
                tv_f_driver_version.setText(e.getContent());
                if (null == e.getContent() || "".equals(e.getContent())) {
                    bus.post(new DisableEvent(false));
                } else {
                    bus.post(new DisableEvent(true));
                }
                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFingerEvent(FingerEvent e) {
        if (m_bitmap != null) {
            if (!m_bitmap.isRecycled()) {
                m_bitmap.recycle();
            }
        }
        m_bitmap = fingerDriver.Raw2Bimap(e.getImgBuff(), IMAGE_X_BIG, IMAGE_Y_BIG);
        if (m_bitmap != null) {
            ImageView ivv = (ImageView)findViewById(e.getIvId());
            if (ivv != null) {
                ivv.setImageBitmap(m_bitmap);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScrollMessageevent(ScrollMessageEvent e){
        tv_message.append(e.getMessage() + "\r\n");
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
