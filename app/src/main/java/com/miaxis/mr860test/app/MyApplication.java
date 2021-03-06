package com.miaxis.mr860test.app;

import android.app.Application;
import android.app.smdt.SmdtManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.domain.CommonEvent;
import com.miaxis.mr860test.domain.DisableEvent;
import com.miaxis.mr860test.utils.FileUtil;

import org.xutils.x;
import org.zz.idcard_hid_driver.IdCardDriver;
import org.zz.mxhidfingerdriver.MXFingerDriver;

import java.io.File;
import java.util.List;

/**
 * Created by xu.nan on 2016/12/19.
 */

public class MyApplication extends Application {

    private IdCardDriver idCardDriver;
    private MXFingerDriver mxFingerDriver;
    public static String test_ip;
    public static String test_port;
    private SmdtManager smdtManager;

    @Override
    public void onCreate() {
        x.Ext.init(this);
        super.onCreate();
        idCardDriver = new IdCardDriver(this);
        mxFingerDriver = new MXFingerDriver(this);
        smdtManager = SmdtManager.create(this);
        initConfig();
        int re = initHsIdPhotoDecodeLib();
        if (re != 0) {
            Toast.makeText(this, "初始化二代证图像解码库失败！", Toast.LENGTH_LONG).show();
        }
        preReadId();
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                smdtManager.smdtSetGpioDirection(1, 0);
                Thread.sleep(100);
                smdtManager.smdtSetGpioDirection(2, 1);
                Thread.sleep(100);
//                smdtManager.smdtSetGpioDirection(3, 1);
//                Thread.sleep(100);
//                smdtManager.smdtSetGpioValue(3, false);
            }
        } catch (Exception e) {

        }
    }

    private void initConfig() {
        test_ip = Constants.TEST_IP.split("=")[1];
        test_port = Constants.TEST_PORT.split("=")[1];
        try {
            File file = new File(Environment.getExternalStorageDirectory(), FileUtil.VERSION_CONFIG_PATH);
            if (!file.exists()) {
                if (file.createNewFile()) {
                    FileUtil.writeFile(file, Constants.ID_VERSION + "\r\n"
                            + Constants.FINGER_VERSION + "\r\n"
                            + Constants.HAS_4G + "\r\n"
                            + Constants.TEST_IP + "\r\n"
                            + Constants.TEST_PORT + "\r\n"
                            + Constants.DEVICE_CODE + "\r\n"
                            + Constants.HAS_FINGER + "\r\n"
                            + Constants.HAS_ID + "\r\n"
                            , false);
                } else {
                    Toast.makeText(this, "生成配置文件失败，请手动添加", Toast.LENGTH_LONG).show();
                }
            } else if (FileUtil.readFileToList(file).size() != 8) {
                if (file.delete()) {
                    if (file.createNewFile()) {
                        FileUtil.writeFile(file, Constants.ID_VERSION + "\r\n"
                                        + Constants.FINGER_VERSION + "\r\n"
                                        + Constants.HAS_4G + "\r\n"
                                        + Constants.TEST_IP + "\r\n"
                                        + Constants.TEST_PORT + "\r\n"
                                        + Constants.DEVICE_CODE + "\r\n"
                                        + Constants.HAS_FINGER + "\r\n"
                                        + Constants.HAS_ID + "\r\n"
                                , false);
                    } else {
                        Toast.makeText(this, "生成配置文件失败，请手动添加", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "删除旧配置文件失败，请手动删除", Toast.LENGTH_LONG).show();
                }
            } else {
                List<String> stringList = FileUtil.readFileToList(file);
                if (stringList != null) {
                    test_ip = stringList.get(3).split("=")[1];
                    test_port = stringList.get(4).split("=")[1];
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "初始化配置文件失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
            test_ip = "";
            test_port = "";
        }
    }

    private void preReadId() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int nRet = -1;
                    byte[] bDevVersion = new byte[64];
                    for (int i=0; i<20; i++) {
                        nRet = idCardDriver.mxGetIdCardModuleVersion(bDevVersion);
                        if (nRet == 0) {
                            break;
                        }
                    }
                } catch (Exception e) {
                }
            }
        }).start();
    }

    @Override
    public void onTrimMemory(int level) {
        Log.e("onTrimMemory","level" + level);
        smdtManager.smdtSetExtrnalGpioValue(2, false);
        super.onTrimMemory(level);
    }

    /**
     * 复制宇松二代证解码库的授权文件到指定目录
     * @return
     */
    private int initHsIdPhotoDecodeLib() {

        String hsLibDirName = "wltlib";
        String hsFile1 = "base.dat";
        String hsFile2 = "license.lic";
        String hsFile3 = "test.dat";
        String hsFile4 = "zp.wlt";
        File wltlibDir = new File(FileUtil.getAvailableImgPath(this));
        if (!wltlibDir.exists()) {
            if (!wltlibDir.mkdirs()) {
                return -1;
            }
        }
        FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + hsFile1,wltlibDir + File.separator + hsFile1);
        FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + hsFile2,wltlibDir + File.separator + hsFile2);
        FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + hsFile3,wltlibDir + File.separator + hsFile3);
        FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + hsFile4,wltlibDir + File.separator + hsFile4);
        FileUtil.copyAssetsFile(this, "testPhoto.jpg", FileUtil.getAvailableImgPath(this) + File.separator + "testPhoto.jpg");
        return 0;
    }

}
