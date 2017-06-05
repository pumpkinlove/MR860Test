package com.miaxis.mr860test.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.ToastEvent;
import com.miaxis.mr860test.utils.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import org.zz.idcard_hid_driver.IdCardDriver;

import java.io.File;
import java.util.List;

@ContentView(R.layout.activity_device_code)
public class DeviceCodeActivity extends BaseTestActivity {

    @ViewInject(R.id.et_device_code)    private EditText et_device_code;
    @ViewInject(R.id.tv_error_info)     private TextView tv_error_info;
    @ViewInject(R.id.iv_dev_code)       private ImageView iv_dev_code;
    @ViewInject(R.id.iv_id_dev_code)    private ImageView iv_id_dev_code;
    @ViewInject(R.id.tv_id_dev_code)    private TextView tv_id_dev_code;

    private IdCardDriver idCardDriver;
    private List<String> stringList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);

        x.view().inject(this);
        initData();
        initView();
        onRead(null);
        onReadidDevNo(null);
    }

    @Override
    protected void initData() {
        idCardDriver = new IdCardDriver(this);
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
                        Bitmap codeBmp = CreateOneDCode(strs[1].trim());
                        iv_dev_code.setImageBitmap(codeBmp);
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

    @Event(R.id.tv_read_idDevNo)
    private void onReadidDevNo(View view) {
        String SAMId = idCardDriver.mxReadSAMId();

        SAMId = SAMId.replace(".", "");
        SAMId = SAMId.replace("-", "");
        SAMId = SAMId.substring(0, 22);
        tv_id_dev_code.setText(SAMId);
        try {
            Bitmap bitmap = createTwoDCode(SAMId);
            iv_id_dev_code.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
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
        return !(no < 1 || no > 9999);
    }

    public Bitmap CreateOneDCode(String content) throws WriterException {
        // 生成一维条码,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.CODE_128, 500, 200);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public Bitmap createTwoDCode(String content) throws WriterException {
        BitMatrix matrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, 500, 300);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

}
