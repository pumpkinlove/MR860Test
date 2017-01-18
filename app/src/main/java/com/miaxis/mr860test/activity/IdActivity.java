package com.miaxis.mr860test.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.DisableEvent;
import com.miaxis.mr860test.domain.IdCardPhotoEvent;
import com.miaxis.mr860test.domain.CommonEvent;
import com.miaxis.mr860test.domain.IdFingerEvent;
import com.miaxis.mr860test.domain.ResultEvent;
import com.miaxis.mr860test.utils.FileUtil;
import com.miaxis.mr860test.utils.JNIUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import org.zz.idcard_hid_driver.IdCardDriver;

import java.io.File;
import java.util.List;

@ContentView(R.layout.activity_id)
public class IdActivity extends BaseTestActivity {

    private static final int mPhotoSize         = 38862; //解码后身份证图片长度
    private static final int mFingerDataSize    = 512;   //指纹数据长度
    private static final int mFingerDataB64Size = 684;   //指纹数据Base64编码后的长度

    private static final int READ_VERSION       = 1001;
    private static final int READ_CARD_ID       = 1002;
    private static final int READ_INFO          = 1003;

    @ViewInject(R.id.tv_id_version_need)    private TextView tv_id_version_need;

    @ViewInject(R.id.tv_id_version)         private TextView tv_id_version;
    @ViewInject(R.id.tv_id_card_id)         private TextView tv_id_card_id;
    @ViewInject(R.id.tv_id_info)            private TextView tv_id_info;
    @ViewInject(R.id.tv_id_finger)          private TextView tv_id_finger;

    @ViewInject(R.id.ll_finger_info)        private LinearLayout ll_finger_info;

    @ViewInject(R.id.iv_id_photo)           private ImageView iv_id_photo;

    @ViewInject(R.id.btn_read_version)      private Button btn_read_version;
    @ViewInject(R.id.btn_read_id)           private Button btn_read_id;
    @ViewInject(R.id.btn_read_info)         private Button btn_read_info;
    @ViewInject(R.id.btn_read_full_info)    private Button btn_read_full_info;

    @ViewInject(R.id.tv_pass)               private TextView tv_pass;
    @ViewInject(R.id.tv_deny)               private TextView tv_deny;

    private EventBus bus;

    private IdCardDriver idCardDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏

        x.view().inject(this);

        initData();
        initView();
        initVersion();
    }

    @Override
    protected void initData() {
        idCardDriver = new IdCardDriver(this);
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
                tv_id_version_need.setText("版本配置文件缺失");
            } else {
                List<String> stringList = FileUtil.readFileToList(file);
                if (stringList != null) {
                    tv_id_version_need.setText(stringList.get(0));
                }
            }
            OnClickDevVersion(null);

        } catch (Exception e) {

        }
    }

    @Event(R.id.btn_read_version)
    private void OnClickDevVersion(View view) {
        onDisableEvent(new DisableEvent(false));
        tv_id_version.setText("");
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] bDevVersion = new byte[64];
                int nRet = idCardDriver.mxGetIdCardModuleVersion(bDevVersion);
                if (nRet != 0) {
                    if (nRet == -100) {
                        bus.post(new CommonEvent(READ_VERSION, nRet, "无设备"));
                    }
                    else {
                        bus.post(new CommonEvent(READ_VERSION, nRet, "失败"));
                    }
                } else {
                    bus.post(new CommonEvent(READ_VERSION, nRet, new String(bDevVersion)));
                }
                bus.post(new DisableEvent(true));
            }
        }).start();

    }

    @Event(R.id.btn_read_id)
    private void OnClickCardId(View view) {
        onDisableEvent(new DisableEvent(false));
        tv_id_card_id.setText("");
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] bCardId = new byte[64];
                int nRet = idCardDriver.mxReadCardId(bCardId);
                if (nRet != 0) {
                    if (nRet == -100) {
                        bus.post(new CommonEvent(READ_CARD_ID, nRet, "无设备"));
                    } else {
                        bus.post(new CommonEvent(READ_CARD_ID, nRet, "失败"));
                    }
                } else {
                    String strTmp = null;
                    for (int i = 0; i < bCardId.length; i++) {
                        if(bCardId[i] == 0x00)
                            break;
                        if (i == 0) {
                            strTmp = String.format("%02x ", bCardId[i]);
                        } else {
                            strTmp += String.format("%02x ", bCardId[i]);
                        }
                    }
                    bus.post(new CommonEvent(READ_CARD_ID, nRet, new String(strTmp)));
                }
                bus.post(new DisableEvent(true));
            }
        }).start();
    }

    public void GetCardInfo() {
        byte[] bCardInfo = new byte[256 + 1024];
        int nRet = idCardDriver.mxReadCardInfo(bCardInfo);
        if (nRet != 0) {
            if (nRet == -100) {
                bus.post(new CommonEvent(READ_INFO, nRet, "无设备"));
            }
            else {
                bus.post(new CommonEvent(READ_INFO, nRet, "失败"));
            }
        } else {
            bus.post(new CommonEvent(READ_INFO, nRet, "成功" + bCardInfo.length));
            showIdCardInfo(bCardInfo, true);
        }
        bus.post(new DisableEvent(true));
    }

    @Event(R.id.btn_read_info)
    private void onReadInfoClicked(View view) {
        tv_id_info.setText("");
        tv_id_finger.setText("");
        ll_finger_info.setVisibility(View.GONE);
        onDisableEvent(new DisableEvent(false));
        new Thread(new Runnable() {
            @Override
            public void run() {
                GetCardInfo();
            }
        }).start();
    }

    @Event(R.id.btn_read_full_info)
    private void OnClickCardFullInfo(View view) {
        tv_id_info.setText("");
        tv_id_finger.setText("");
        ll_finger_info.setVisibility(View.GONE);
        onDisableEvent(new DisableEvent(false));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] bCardFullInfo = new byte[256 + 1024 + 1024];
                    int nRet = idCardDriver.mxReadCardFullInfo(bCardFullInfo);
                    if (nRet != 0) {
                        if (nRet == -14) {
                            GetCardInfo();
                        } else if (nRet == -100) {
                            bus.post(new CommonEvent(READ_INFO, nRet, "无设备"));
                        } else {
                            bus.post(new CommonEvent(READ_INFO, nRet, "失败"));
                        }
                    } else {
                        showIdCardInfo(bCardFullInfo, true);
                        byte[] bFingerData1 = new byte[mFingerDataSize];
                        byte[] bFingerData2 = new byte[mFingerDataSize];
                        byte[] bFingerData1_B64 = new byte[mFingerDataB64Size];
                        byte[] bFingerData2_B64 = new byte[mFingerDataB64Size];
                        for (int i = 0; i < bFingerData1.length; i++) {
                            bFingerData1[i]=bCardFullInfo[256+1024+i];
                        }
                        for (int i = 0; i < bFingerData1.length; i++) {
                            bFingerData2[i]=bCardFullInfo[256+1024+512+i];
                        }
                        idCardDriver.Base64Encode(bFingerData1,mFingerDataSize,bFingerData1_B64,mFingerDataB64Size);
                        idCardDriver.Base64Encode(bFingerData2,mFingerDataSize,bFingerData2_B64,mFingerDataB64Size);
                        bus.post(new IdFingerEvent(new String(bFingerData1_B64),  new String(bFingerData2_B64)));
                    }
                } catch (Exception e) {

                }
            bus.post(new DisableEvent(true));
            }
        }).start();
    }

    // ////////////////////////////////////////////////////////////////////////
    String[] FOLK = { "汉", "蒙古", "回", "藏", "维吾尔", "苗", "彝", "壮", "布依", "朝鲜",
            "满", "侗", "瑶", "白", "土家", "哈尼", "哈萨克", "傣", "黎", "傈僳", "佤", "畲",
            "高山", "拉祜", "水", "东乡", "纳西", "景颇", "柯尔克孜", "土", "达斡尔", "仫佬", "羌",
            "布朗", "撒拉", "毛南", "仡佬", "锡伯", "阿昌", "普米", "塔吉克", "怒", "乌孜别克",
            "俄罗斯", "鄂温克", "德昂", "保安", "裕固", "京", "塔塔尔", "独龙", "鄂伦春", "赫哲",
            "门巴", "珞巴", "基诺", "", "", "穿青人", "家人", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "其他", "外国血统", "",
            "" };

    public static String unicode2String(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length / 2; i++) {
            int a = bytes[2 * i + 1];
            if (a < 0) {
                a = a + 256;
            }
            int b = bytes[2 * i];
            if (b < 0) {
                b = b + 256;
            }
            int c = (a << 8) | b;
            sb.append((char) c);
        }
        return sb.toString();
    }

    public void showIdCardInfo(byte[] bCardInfo, Boolean bShowImage) {
        try {
            StringBuilder sb = new StringBuilder();
            byte[] id_Name = new byte[30]; // 姓名
            byte[] id_Sex = new byte[2]; // 性别 1为男 其他为女
            byte[] id_Rev = new byte[4]; // 民族
            byte[] id_Born = new byte[16]; // 出生日期
            byte[] id_Home = new byte[70]; // 住址
            byte[] id_Code = new byte[36]; // 身份证号
            byte[] _RegOrg = new byte[30]; // 签发机关
            byte[] id_ValidPeriodStart = new byte[16]; // 有效日期 起始日期16byte 截止日期16byte
            byte[] id_ValidPeriodEnd = new byte[16];
            byte[] id_NewAddr = new byte[36]; // 预留区域
            byte[] id_pImage = new byte[1024]; // 图片区域
            int iLen = 0;
            for (int i = 0; i < id_Name.length; i++) {
                id_Name[i] = bCardInfo[i + iLen];
            }
            iLen = iLen + id_Name.length;
            sb.append("姓名: " + unicode2String(id_Name));

            for (int i = 0; i < id_Sex.length; i++) {
                id_Sex[i] = bCardInfo[iLen + i];
            }
            iLen = iLen + id_Sex.length;

            if (id_Sex[0] == '1') {
                sb.append("\n性别：男");
            } else {
                sb.append("\n性别：女");
            }

            for (int i = 0; i < id_Rev.length; i++) {
                id_Rev[i] = bCardInfo[iLen + i];
            }
            iLen = iLen + id_Rev.length;
            int iRev = Integer.parseInt(unicode2String(id_Rev));
            sb.append("\n民族：" + FOLK[iRev - 1]);

            for (int i = 0; i < id_Born.length; i++) {
                id_Born[i] = bCardInfo[iLen + i];
            }
            iLen = iLen + id_Born.length;
            sb.append("\n出生日期：" + unicode2String(id_Born));

            for (int i = 0; i < id_Home.length; i++) {
                id_Home[i] = bCardInfo[iLen + i];
            }
            iLen = iLen + id_Home.length;
            sb.append("\n住址：" + unicode2String(id_Home));

            for (int i = 0; i < id_Code.length; i++) {
                id_Code[i] = bCardInfo[iLen + i];
            }
            iLen = iLen + id_Code.length;
            sb.append("\n身份证号：" + unicode2String(id_Code));

            for (int i = 0; i < _RegOrg.length; i++) {
                _RegOrg[i] = bCardInfo[iLen + i];
            }
            iLen = iLen + _RegOrg.length;
            sb.append("\n签发机关：" + unicode2String(_RegOrg));

            for (int i = 0; i < id_ValidPeriodStart.length; i++) {
                id_ValidPeriodStart[i] = bCardInfo[iLen + i];
            }
            iLen = iLen + id_ValidPeriodStart.length;
            for (int i = 0; i < id_ValidPeriodEnd.length; i++) {
                id_ValidPeriodEnd[i] = bCardInfo[iLen + i];
            }
            iLen = iLen + id_ValidPeriodEnd.length;
            sb.append("\n有效日期：" + unicode2String(id_ValidPeriodStart) + "-"
                    + unicode2String(id_ValidPeriodEnd));

            for (int i = 0; i < id_NewAddr.length; i++) {
                id_NewAddr[i] = bCardInfo[iLen + i];
            }

            iLen = iLen + id_NewAddr.length;
            bus.post(new CommonEvent(READ_INFO, 0, sb.toString()));
            if (bShowImage == true) {
                for (int i = 0; i < id_pImage.length; i++) {
                    id_pImage[i] = bCardInfo[i + iLen];
                }
                iLen = iLen + id_pImage.length;

                byte[] bmp = new byte[mPhotoSize];
                int re = idCardDriver.Wlt2Bmp(id_pImage, bmp);
                if (re == 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bmp, 0, bmp.length);
                    bus.post(new IdCardPhotoEvent(bitmap));
                } else {
                    bus.post(new IdCardPhotoEvent(null));
                }
            }
        } catch (Exception e) {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIdEvent(CommonEvent event) {
        TextView tv = null;
        switch (event.getCode()) {
            case READ_VERSION:
                tv = tv_id_version;
                if (event.getResult() == 0) {
                    String v_need = tv_id_version_need.getText().toString().trim();
                    String v = event.getContent().trim();
                    if (!v.equals(v_need)) {
                        tv_id_version.setTextColor(getResources().getColor(R.color.red));
                        bus.post(new DisableEvent(false));
                    } else {
                        tv_id_version.setTextColor(getResources().getColor(R.color.green_dark));
                        bus.post(new DisableEvent(true));
                    }
                }
                break;
            case READ_CARD_ID:
                tv = tv_id_card_id;
                break;
            case READ_INFO:
                tv = tv_id_info;
                break;
        }
        if (tv != null) {
            if (0 == event.getResult()) {
                tv.setText(event.getContent());
            } else {
                tv.setText(event.getResult() + " " +event.getContent());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIdCardPhotoEvent(IdCardPhotoEvent event) {
        if (event.getBitmap() == null) {
            tv_id_info.append("\n图片获取失败");
        } else {
            iv_id_photo.setImageBitmap(event.getBitmap());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisableEvent(DisableEvent e) {
        if (e.isFlag()) {
            tv_pass.            setTextColor(getResources().getColor(R.color.green_dark));
            btn_read_version.   setTextColor(getResources().getColor(R.color.dark));
            btn_read_id.        setTextColor(getResources().getColor(R.color.dark));
            btn_read_info.      setTextColor(getResources().getColor(R.color.dark));
            btn_read_full_info. setTextColor(getResources().getColor(R.color.dark));
        } else {
            tv_pass.            setTextColor(getResources().getColor(R.color.gray_dark));
            btn_read_version.   setTextColor(getResources().getColor(R.color.gray_dark));
            btn_read_id.        setTextColor(getResources().getColor(R.color.gray_dark));
            btn_read_info.      setTextColor(getResources().getColor(R.color.gray_dark));
            btn_read_full_info. setTextColor(getResources().getColor(R.color.gray_dark));
        }
        tv_pass.            setClickable(e.isFlag());
        tv_pass.            setEnabled(e.isFlag());
        btn_read_version.   setEnabled(e.isFlag());
        btn_read_id.        setEnabled(e.isFlag());
        btn_read_info.      setEnabled(e.isFlag());
        btn_read_full_info. setEnabled(e.isFlag());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIdFingerEvent(IdFingerEvent e) {
        ll_finger_info.setVisibility(View.VISIBLE);
        tv_id_finger.setText(e.getFinger0() + "\r\n" + e.getFinger1());

    }

    @Override
    protected void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }

    @Event(R.id.tv_pass)
    private void onPass(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_IDCARD, Constants.STATUS_PASS));
        finish();
    }

    @Event(R.id.tv_deny)
    private void onDeny(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_IDCARD, Constants.STAUTS_DENIED));
        finish();
    }



}
