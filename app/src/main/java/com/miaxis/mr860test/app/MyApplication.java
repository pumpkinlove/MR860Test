package com.miaxis.mr860test.app;

import android.app.Application;
import android.os.Environment;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.utils.FileUtil;

import org.xutils.x;

import java.io.File;

/**
 * Created by xu.nan on 2016/12/19.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        x.Ext.init(this);
        super.onCreate();

        initVersionConfig();

    }

    private void initVersionConfig() {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), FileUtil.VERSION_CONFIG_PATH);
            if (!file.exists()) {
                file.createNewFile();
                FileUtil.writeFile(file, Constants.ID_VERSION + "\r\n" + Constants.FINGER_VERSION, false);
            }
        } catch (Exception e) {

        }

    }
}
