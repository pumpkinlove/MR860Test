package com.miaxis.mr860test.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.ToastEvent;
import com.miaxis.mr860test.utils.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.List;

@ContentView(R.layout.activity_device_code)
public class DeviceCodeActivity extends BaseTestActivity {

    @ViewInject(R.id.et_device_code)    private EditText et_device_code;
    @ViewInject(R.id.tv_error_info)    private TextView tv_error_info;

    private List<String> stringList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);

        x.view().inject(this);
        onRead(null);
        initView();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        et_device_code.setSelection(et_device_code.getText().toString().length());//将光标移至文字末尾
    }

    @Event(R.id.tv_read)
    private void onRead(View view) {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), FileUtil.VERSION_CONFIG_PATH);
            if (!file.exists()) {
                throw new Exception();
            } else {
                stringList = FileUtil.readFileToList(file);
                if (stringList != null) {
                    String[] strs = stringList.get(5).split("=");       //第五个是序列号， 顺序看MyApplication的默认写入顺序
                    if (strs.length == 2) {
                        et_device_code.setText(strs[1].trim());
                    } else if (strs.length == 1 && strs[0].equals("DEVICE_CODE")){
                        et_device_code.setText("");
                    } else {
                        throw new Exception();
                    }
                } else {
                    throw new Exception();
                }
            }
        } catch (Exception e) {
            tv_error_info.setText("配置文件错误, 请检查配置文件");
            tv_error_info.setTextColor(Color.RED);
        }
    }

    @Event(R.id.tv_save)
    private void onSave(View view) {
        try {
            String code = et_device_code.getText().toString();
            if (!checkCode(code)) {
                tv_error_info.setTextColor(Color.RED);
                return;
            } else {
                tv_error_info.setTextColor(getResources().getColor(R.color.dark));
            }
            File file = new File(Environment.getExternalStorageDirectory(), FileUtil.VERSION_CONFIG_PATH);
            FileUtil.writeFile(file, stringList.get(0) + "\r\n"
                            + stringList.get(1) + "\r\n"
                            + stringList.get(2) + "\r\n"
                            + stringList.get(3) + "\r\n"
                            + stringList.get(4) + "\r\n"
                            + "DEVICE_CODE=" + code + "\r\n"
                    , false);
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkCode(String code) {
        if (code == null ||code.length() != 8) {
            return false;
        }
        int year = Integer.valueOf(code.substring(0, 2));
        if (year > 99 || year < 17) {
            return false;
        }
        int week = Integer.valueOf(code.substring(2, 4));
        if (week < 1 || week > 53) {
            return false;
        }
        int no = Integer.valueOf(code.substring(4, 8));
        if (no < 1 || no > 9999) {
            return false;
        }
        return true;
    }


}
