package com.miaxis.mr860test.activity;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import org.zz.mxhidfingerdriver.MXFingerDriver;
import org.zz.tool.BMP;
import org.zz.tool.ToolUnit;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.CommonEvent;
import com.miaxis.mr860test.domain.DisableEvent;
import com.miaxis.mr860test.domain.FingerEvent;
import com.miaxis.mr860test.domain.ResultEvent;
import com.miaxis.mr860test.utils.FileUtil;

@ContentView(R.layout.activity_finger)
public class FingerActivity extends BaseTestActivity {

    private static final int GET_IMAGE          = 10001;
    private static final int GET_DEVICE_VERSION = 10002;
    private static final int GET_DRIVER_VERSION = 10003;
    private static final int CANCEL             = 10004;

    // 图像
    private static final int TIME_OUT           = 15 * 1000; // 等待按手指的超时时间，单位：毫
    private static final int IMAGE_X_BIG        = 256;
    private static final int IMAGE_Y_BIG        = 360;
    private static final int IMAGE_SIZE_BIG     = IMAGE_X_BIG * IMAGE_Y_BIG;
    byte[] bImgBuf = new byte[IMAGE_SIZE_BIG];
    Bitmap m_bitmap = null;
    // 线程
    private GetImageThread        m_GetImageThread       = null;
    private GetDevVersionThread   m_GetDevVersionThread  = null;
    // 中正指纹仪驱动
    MXFingerDriver fingerDriver;

    private EventBus bus;

    @ViewInject(R.id.btn_getDevVersion)         private Button btn_getDevVersion;
    @ViewInject(R.id.btn_getDriverVersion)      private Button btn_getDriverVersion;
    @ViewInject(R.id.btn_getImage)              private Button btn_getImage;
    @ViewInject(R.id.btn_cancel)                private Button btn_cancel;
    @ViewInject(R.id.tv_pass)                   private TextView tv_pass;
    @ViewInject(R.id.iv_finger)                 private ImageView iv_finger;
    @ViewInject(R.id.tv_message)                private TextView tv_message;
    @ViewInject(R.id.tv_f_device_version_need)  private TextView tv_f_device_version_need;
    @ViewInject(R.id.tv_f_device_version)       private TextView tv_f_device_version;
    @ViewInject(R.id.tv_f_driver_version)       private TextView tv_f_driver_version;


    // 定义一个负责更新的进度的Handler
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);

        x.view().inject(this);

        initData();
        initView();
        initVersion();
        onGetDevVersionClicked(null);
    }

    @Override
    protected void initData() {
        fingerDriver = new MXFingerDriver(this,true);
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void GetImage() {
        int ret = 0;
        Calendar time1 = Calendar.getInstance();
        ret = fingerDriver.mxAutoGetImage(bImgBuf, IMAGE_X_BIG, IMAGE_Y_BIG, TIME_OUT, 0);
        if (ret == 0) {
            String strSDCardPath = ToolUnit.getSDCardPath();
            String strFileName = strSDCardPath +"/finger.bmp";
            BMP.SaveBMP(strFileName, bImgBuf, IMAGE_X_BIG, IMAGE_Y_BIG);
        }
        Calendar time2 = Calendar.getInstance();
        long bt_time = time2.getTimeInMillis() - time1.getTimeInMillis();
        if (ret == 0) {
            bus.post(new CommonEvent(GET_IMAGE, ret, "获取图像成功，耗时：" + bt_time + "ms"));
        } else {
            bus.post(new CommonEvent(GET_IMAGE, ret,  "获取图像失败"));
        }

    }

    private class GetDevVersionThread extends Thread {
        public void run() {
            try {
                GetDevVersion();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void GetDevVersion() {
        int iRet = -1;
        byte[] bVersion = new byte[120];
        iRet = fingerDriver.mxGetDevVersion(bVersion);
        if (iRet == 0) {
            bus.post(new CommonEvent(GET_DEVICE_VERSION, iRet, new String(bVersion)));
        } else {
            bus.post(new CommonEvent(GET_DEVICE_VERSION, iRet, new String("获取设备版本失败")));
        }
    }

    @Event(R.id.btn_getImage)
    private void onGetImageClicked(View view) {
        tv_message.append("\r\n请按手指....");
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
        fingerDriver.mxCancelGetImage();
    }

    @Event(R.id.tv_pass)
    private void onPass(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_FINGER, Constants.STATUS_PASS));
        finish();
    }

    @Event(R.id.tv_deny)
    private void onDeny(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_FINGER, Constants.STAUTS_DENIED));
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisableEvent(DisableEvent e) {
        if (e.isFlag()) {
            tv_pass.setTextColor(getResources().getColor(R.color.green_dark));
            btn_getImage.setTextColor(getResources().getColor(R.color.dark));
            btn_getDriverVersion.setTextColor(getResources().getColor(R.color.dark));
        } else {
            tv_pass.setTextColor(getResources().getColor(R.color.gray_dark));
            btn_getImage.setTextColor(getResources().getColor(R.color.gray_dark));
            btn_getDriverVersion.setTextColor(getResources().getColor(R.color.gray_dark));
        }
        tv_pass.setClickable(e.isFlag());
        tv_pass.setEnabled(e.isFlag());
        btn_getImage.setEnabled(e.isFlag());
        btn_getDevVersion.setEnabled(e.isFlag());
        btn_getDriverVersion.setEnabled(e.isFlag());
        btn_cancel.setEnabled(!e.isFlag());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFingerEvent(CommonEvent e) {
        switch (e.getCode()) {
            case GET_IMAGE :
                if (e.getResult() == 0) {
                    if (m_bitmap != null) {
                        if (!m_bitmap.isRecycled()) {
                            m_bitmap.recycle();
                        }
                    }
                    m_bitmap = fingerDriver.Raw2Bimap(bImgBuf, IMAGE_X_BIG, IMAGE_Y_BIG);
                    if (m_bitmap != null) {
                        iv_finger.setImageBitmap(m_bitmap);
                    }
                }
                tv_message.append( "\r\n" + e.getContent());
                break;
            case GET_DEVICE_VERSION :
                tv_f_device_version.setText(e.getContent());
                String v_need = tv_f_device_version_need.getText().toString().trim();
                String v = e.getContent().trim();
                if (v.equals(v_need)) {
                    tv_f_device_version.setTextColor(getResources().getColor(R.color.green_dark));
                } else {
                    tv_f_device_version.setTextColor(getResources().getColor(R.color.red));
                    bus.post(new DisableEvent(false));
                }
                break;
            case GET_DRIVER_VERSION :
                tv_f_driver_version.setText(e.getContent());
                break;
        }
        bus.post(new DisableEvent(true));
    }
}
