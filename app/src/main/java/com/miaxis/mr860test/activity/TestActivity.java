package com.miaxis.mr860test.activity;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.CommonEvent;
import com.miaxis.mr860test.domain.DisableEvent;
import com.miaxis.mr860test.utils.BmpLoader;

import org.xutils.view.annotation.Event;
import org.xutils.x;
import org.zz.jni.zzFingerAlg;
import org.zz.mxhidfingerdriver.MXFingerDriver;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

@Deprecated
public class TestActivity extends AppCompatActivity {

    private MXFingerDriver fingerDriver;
    private zzFingerAlg alg;

    private Button button;

    private static final int TIME_OUT           = 15 * 1000; // 等待按手指的超时时间，单位：毫
    private static final int IMAGE_X_BIG        = 256;
    private static final int IMAGE_Y_BIG        = 360;
    private static final int IMAGE_SIZE_BIG     = IMAGE_X_BIG * IMAGE_Y_BIG;

    private byte[] bImgBuf   = new byte[IMAGE_SIZE_BIG];
    private byte[] tzBuffer1 = new byte[1024];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        fingerDriver = new MXFingerDriver(this,true);
        alg = new zzFingerAlg();
        x.view().inject(this);
    }

    @Event(R.id.button)
    private void onButtonClicked(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int ret = fingerDriver.mxAutoGetImage(bImgBuf, IMAGE_X_BIG, IMAGE_Y_BIG, TIME_OUT, 0);

                int re = -1;
                byte[] version = new byte[100];
                re = alg.mxGetVersion(version);

            }
        }).start();
    }

    @Event(R.id.button2)
    private void onButton2Clicked(View view) {
//        int re = alg.mxGetTzBase64(bImgBuf, tzBuffer1);
        byte[] bFingerTz1 = new byte[TZ_SIZE];
        int re = GetTz(R.raw.f20000400, bFingerTz1);


    }

    private static final int IMAGE_DPI     = 500;
    private static final int IMAGE_WIDTH   = 256;
    private static final int IMAGEG_HEIGTH = 360;
    private static final int TZ_SIZE       = 1024;

    private int GetTz(int rawId, byte[] bTz) {
        Resources res      = this.getResources();
        InputStream in     = null;
        byte[] pBmpbuf     = new byte[IMAGE_WIDTH * IMAGEG_HEIGTH + 1078+10];
        byte[] pW= new byte[4];
        byte[] pH= new byte[4];
        byte[] imagebuf    = new byte[IMAGE_WIDTH * IMAGEG_HEIGTH];
        byte[] isoImagebuf = new byte[IMAGE_WIDTH * IMAGEG_HEIGTH + 54];
        int ret = 0;
        int byteread = 0;
        int iLength = 0;
        try {
            in = res.openRawResource(rawId);
            while ((byteread = in.read(pBmpbuf)) != -1) {
                System.out.write(pBmpbuf, 0, byteread);
                iLength = iLength+byteread;
            }
            Log.e("openRawResource","iLength="+iLength);
            BmpLoader.Bmp2Raw(pBmpbuf, imagebuf, pW, pH);
//				BMP.SaveBMP("/mnt/sdcard/Img1.bmp",imagebuf, IMAGE_WIDTH,IMAGEG_HEIGTH);
            //ImgFormatTrans.ImgToIso(imagebuf, IMAGE_DPI, IMAGE_WIDTH, IMAGEG_HEIGTH, isoImagebuf);
            //fingeralg.mxImgToIso(imagebuf,  IMAGE_DPI, IMAGE_WIDTH, IMAGEG_HEIGTH, isoImagebuf);
            //ret = fingeralg.mxGetTzBase64FromISO(isoImagebuf, bTz);
            //ret = fingeralg.mxGetTz512(imagebuf, bTz);
//            ret = alg.mxGetTzBase64(imagebuf, bTz);
        } catch (Resources.NotFoundException e) {
            Toast.makeText(this, "文本文件不存在", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(this, "文本编码出现异常", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this, "文件读取错误", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return ret;
    }
}
