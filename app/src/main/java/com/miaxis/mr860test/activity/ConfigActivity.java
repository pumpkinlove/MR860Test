package com.miaxis.mr860test.activity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.utils.FileUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.List;

@ContentView(R.layout.activity_config)
public class ConfigActivity extends BaseTestActivity {

    @ViewInject(R.id.cb_4g)
    CheckBox cb_4g;
    @ViewInject(R.id.cb_finger)
    CheckBox cb_finger;
    @ViewInject(R.id.cb_id_card)
    CheckBox cb_id_card;

    @ViewInject(R.id.et_test_ip)
    EditText et_test_ip;
    @ViewInject(R.id.et_test_port)
    EditText et_test_port;
    @ViewInject(R.id.et_finger_ver)
    EditText et_finger_ver;
    @ViewInject(R.id.et_id_card_ver)
    EditText et_id_card_ver;

    @ViewInject(R.id.tv_device_code)
    TextView tv_device_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initData();
        initView();
        initConfig();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }

    void initConfig() {
        File file = new File(Environment.getExternalStorageDirectory(), FileUtil.VERSION_CONFIG_PATH);
        if (!file.exists()) {
            Toast.makeText(this, "版本配置文件缺失", Toast.LENGTH_LONG).show();
        } else {
            List<String> stringList = FileUtil.readFileToList(file);
            et_id_card_ver.setText(stringList.get(0).trim());
            et_finger_ver.setText(stringList.get(1).trim());
            cb_4g.setChecked(Boolean.valueOf(stringList.get(2).trim().split("=")[1]));
            et_test_ip.setText(stringList.get(3).trim().split("=")[1]);
            et_test_port.setText(stringList.get(4).trim().split("=")[1]);
            if (stringList.get(5).split("=").length < 2) {
                tv_device_code.setText("");
            } else {
                tv_device_code.setText(stringList.get(5).trim().split("=")[1]);
            }
            cb_finger.setChecked(Boolean.valueOf(stringList.get(6).trim().split("=")[1]));
            cb_id_card.setChecked(Boolean.valueOf(stringList.get(7).trim().split("=")[1]));
        }
    }

    @Event(R.id.tv_save)
    private void onSave(View view) {
        File file = new File(Environment.getExternalStorageDirectory(), FileUtil.VERSION_CONFIG_PATH);
        StringBuilder sbConfig = new StringBuilder();
        sbConfig.append(et_id_card_ver.getText().toString().trim() + "\r\n");
        sbConfig.append(et_finger_ver.getText().toString().trim() + "\r\n");
        if (cb_4g.isChecked()) {
            sbConfig.append("4g=true\r\n");
        } else {
            sbConfig.append("4g=false\r\n");
        }

        sbConfig.append("TEST_IP=" + et_test_ip.getText().toString().trim() + "\r\n");
        sbConfig.append("TEST_PORT=" + et_test_port.getText().toString().trim() + "\r\n");
        sbConfig.append("DEVICE_CODE=" + tv_device_code.getText().toString().trim() + "\r\n");

        if (cb_finger.isChecked()) {
            sbConfig.append("finger=true\r\n");
        } else {
            sbConfig.append("finger=false\r\n");
        }
        if (cb_id_card.isChecked()) {
            sbConfig.append("idCard=true\r\n");
        } else {
            sbConfig.append("idCard=false\r\n");
        }
        FileUtil.writeFile(file, sbConfig.toString(), false);
        finish();
    }

    @Event(R.id.tv_cancel)
    private void onCancel(View view) {
        finish();
    }

}
