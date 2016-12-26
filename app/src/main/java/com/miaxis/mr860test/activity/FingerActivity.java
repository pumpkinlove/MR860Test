package com.miaxis.mr860test.activity;

import java.util.Calendar;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.Event;
import org.xutils.x;
import org.zz.mxhidfingerdriver.MXFingerDriver;
import org.zz.tool.BMP;
import org.zz.tool.ToolUnit;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.ResultEvent;

public class FingerActivity extends Activity {

    private static final int PROMTP_MSG  = 0;      // 提示信息
    private static final int SUCCESS_MSG  = 1;      // 成功
    private static final int FAILED_MSG     = 2;       // 失败
    private static final int IMG_SUCCESS_MSG  = 3; // 获取图像成功

    // 图像
    private static final  int  TIME_OUT = 15 * 1000; // 等待按手指的超时时间，单位：毫
    private static final  int IMAGE_X_BIG   = 256;
    private static final  int IMAGE_Y_BIG   = 360;
    private static final  int IMAGE_SIZE_BIG  = IMAGE_X_BIG*IMAGE_Y_BIG;
    byte[] bImgBuf = new byte[IMAGE_SIZE_BIG];
    Bitmap m_bitmap = null;
    // 线程
    private GetImageThread        m_GetImageThread       = null;
    private GetDevVersionThread   m_GetDevVersionThread  = null;
    // 中正指纹仪驱动
    MXFingerDriver fingerDriver;

    // 定义一个负责更新的进度的Handler
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题
        setContentView(R.layout.activity_finger);
        fingerDriver = new MXFingerDriver(this,true);
        x.view().inject(this);
    }

    /* Toast控件显示提示信息 */
    public void DisplayToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    private void EnableButton(Boolean bFlag) {
        Button btn_getImage = (Button) findViewById(R.id.btn_getImage);
        btn_getImage.setEnabled(bFlag);
        Button btn_getDriverVersion = (Button) findViewById(R.id.btn_getDriverVersion);
        btn_getDriverVersion.setEnabled(bFlag);
        Button btn_getDevVersion = (Button) findViewById(R.id.btn_getDevVersion);
        btn_getDevVersion.setEnabled(bFlag);
    }

    /**
     * 提示信息
     * */
    private void ShowMessage(String strMsg,Boolean bAdd){
        EditText edit_show_msg = (EditText) findViewById(R.id.edit_show_msg);
        if (bAdd) {
            String strShowMsg  = edit_show_msg.getText().toString();
            strMsg = strShowMsg+strMsg;
        }
        edit_show_msg.setText(strMsg+"\r\n");
        ScrollView scrollView_show_msg = (ScrollView) findViewById(R.id.scrollView_show_msg);
        scrollToBottom(scrollView_show_msg,edit_show_msg);
    }

    public static void scrollToBottom(final View scroll, final View inner) {
        Handler mHandler = new Handler();
        mHandler.post(new Runnable() {
            public void run() {
                if (scroll == null || inner == null) {
                    return;
                }
                int offset = inner.getMeasuredHeight() - scroll.getHeight();
                if (offset < 0) {
                    offset = 0;
                }
                scroll.scrollTo(0, offset);
            }
        });
    }

    private void SendMsg(int what, String obj) {
        Message message = new Message();
        message.what = what;
        message.obj  = obj;
        message.arg1 = 0;
        LinkDetectedHandler.sendMessage(message);
    }

    private Handler LinkDetectedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROMTP_MSG:
                    ShowMessage(""+msg.obj,true);
                    EnableButton(false);
                    break;
                case SUCCESS_MSG:
                case FAILED_MSG:
                    ShowMessage(""+msg.obj,true);
                    EnableButton(true);
                    break;
                case IMG_SUCCESS_MSG:
                    // 如果图片还没有回收，强制回收
                    if(m_bitmap!=null){
                        if (!m_bitmap.isRecycled()) {
                            m_bitmap.recycle();
                        }
                    }
                    m_bitmap = fingerDriver.Raw2Bimap(bImgBuf,IMAGE_X_BIG,IMAGE_Y_BIG);
                    if(m_bitmap!=null){
                        ImageView image_open = (ImageView) findViewById(R.id.image_open);
                        image_open.setImageBitmap(m_bitmap);
                    }
                    //DisplayToast((String) msg.obj);
                    ShowMessage(""+msg.obj,false);
                    EnableButton(true);
                    break;
                default:
                    ShowMessage(""+msg.obj,true);
                    break;
            }
        }
    };

    /**
     * 功能：获取图像
     * */
    public void OnClickGetImage(View view) {
        if (m_GetImageThread != null) {
            m_GetImageThread.interrupt();
            m_GetImageThread = null;
        }
        m_GetImageThread = new GetImageThread();
        m_GetImageThread.start();
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
        SendMsg(PROMTP_MSG,"获取指纹图像，请按手指...");
        Calendar time1 = Calendar.getInstance();
        ret = fingerDriver.mxAutoGetImage(bImgBuf, IMAGE_X_BIG,IMAGE_Y_BIG, TIME_OUT,0);
        if (ret == 0) {
            String strSDCardPath = ToolUnit.getSDCardPath();
            String strFileName = strSDCardPath +"/finger.bmp";
            BMP.SaveBMP(strFileName,bImgBuf,IMAGE_X_BIG,IMAGE_Y_BIG);
        }
        Calendar time2 = Calendar.getInstance();
        long bt_time = time2.getTimeInMillis() - time1.getTimeInMillis();
        if (ret == 0) {
            SendMsg(IMG_SUCCESS_MSG, "获取图像成功，耗时：" + bt_time + "ms");
        } else {
            SendMsg(FAILED_MSG, "获取图像失败,ret="+ret);
        }
    }

    /**
     * 功能：获取设备版本
     * */
    public void OnClickGetDevVersion(View view) {
        if (m_GetDevVersionThread != null) {
            m_GetDevVersionThread.interrupt();
            m_GetDevVersionThread = null;
        }
        m_GetDevVersionThread = new GetDevVersionThread();
        m_GetDevVersionThread.start();
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
            SendMsg(SUCCESS_MSG,"设备版本:"+new String(bVersion));
        } else {
            SendMsg(FAILED_MSG,"获取设备版本失败，ret: " + iRet);
        }
    }

    /**
     * 功能：获取驱动版本
     * */
    public void OnClickGetDriverVersion(View view) {
        SendMsg(SUCCESS_MSG,"驱动版本:"+fingerDriver.mxGetDriverVersion());
    }

    public void OnClickCancel(View view) {
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
}
