package com.miaxis.mr860test.app;

import android.app.Application;
import android.os.Environment;
import android.widget.Toast;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.domain.CommonEvent;
import com.miaxis.mr860test.domain.DisableEvent;
import com.miaxis.mr860test.utils.FileUtil;

import org.xutils.x;
import org.zz.idcard_hid_driver.IdCardDriver;
import org.zz.mxhidfingerdriver.MXFingerDriver;

import java.io.File;

/**
 * Created by xu.nan on 2016/12/19.
 */

public class MyApplication extends Application {

    private IdCardDriver idCardDriver;
    private MXFingerDriver mxFingerDriver;

    @Override
    public void onCreate() {
        x.Ext.init(this);
        super.onCreate();
        idCardDriver = new IdCardDriver(this);
        mxFingerDriver = new MXFingerDriver(this);

        initVersionConfig();
        preReadIdDevVersion();
    }

    private void initVersionConfig() {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), FileUtil.VERSION_CONFIG_PATH);
            if (!file.exists()) {
                if (file.createNewFile()) {
                    FileUtil.writeFile(file, Constants.ID_VERSION + "\r\n" + Constants.FINGER_VERSION, false);
                } else {
                    Toast.makeText(this, "生成配置文件失败，请手动添加", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "初始化配置文件失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 预读二代证模块版本， 解决 断电后 调用二代证模块 失败几次返回-1000 后 才能正常使用的问题
     */
    private void preReadIdDevVersion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] bDevVersion;
                    int re1;
                    int re2;
                    for (int i=0; i<10; i++) {
                        bDevVersion = new byte[64];
                        re1 = idCardDriver.mxGetIdCardModuleVersion(bDevVersion);
                        byte[] bVersion = new byte[120];
                        re2 = mxFingerDriver.mxGetDevVersion(bVersion);
                        if (re1 != -1000 && re2 != -1000) {
                            break;
                        }
                    }
                } catch (Exception e) {

                }
            }
        }).start();
    }
}
