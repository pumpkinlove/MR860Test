package com.miaxis.mr860test.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.IdCardPhotoEvent;
import com.miaxis.mr860test.domain.IdEvent;
import com.miaxis.mr860test.domain.ResultEvent;
import com.miaxis.mr860test.utils.JNIUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import org.zz.idcard_hid_driver.IdCardDriver;

@ContentView(R.layout.activity_id)
public class IdActivity extends BaseTestActivity {

    private static final int mPhotoSize         = 38862; //解码后身份证图片长度
    private static final int mFingerDataSize    = 512;   //指纹数据长度
    private static final int mFingerDataB64Size = 684;   //指纹数据Base64编码后的长度

    private static final int READ_VERSION = 1001;
    private static final int READ_CARD_ID = 1002;
    private static final int READ_INFO = 1003;

    @ViewInject(R.id.tv_id_version)
    private TextView tv_id_version;

    @ViewInject(R.id.tv_id_card_id)
    private TextView tv_id_card_id;

    @ViewInject(R.id.tv_id_info)
    private TextView tv_id_info;

    @ViewInject(R.id.iv_id_photo)
    private ImageView iv_id_photo;

    private EventBus bus;
    // 中正驱动
    IdCardDriver idCardDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏

        x.view().inject(this);

        initData();
        initView();

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


    @Event(R.id.tv_read_version)
    private void OnClickDevVersion(View view) {
        byte[] bDevVersion = new byte[64];
        int nRet = idCardDriver.mxGetIdCardModuleVersion(bDevVersion);
        if (nRet != 0) {
            if (nRet==-100) {
                bus.post(new IdEvent(READ_VERSION, nRet, "无设备"));
            }
            else {
                bus.post(new IdEvent(READ_VERSION, nRet, "失败"));
            }
        } else {
            bus.post(new IdEvent(READ_VERSION, nRet, new String(bDevVersion)));
        }
    }

    @Event(R.id.tv_read_id)
    private void OnClickCardId(View view) {
        byte[] bCardId = new byte[64];
        int nRet = idCardDriver.mxReadCardId(bCardId);
        if (nRet != 0) {
            if (nRet==-100) {
                bus.post(new IdEvent(READ_CARD_ID, nRet, "无设备"));
            } else {
                bus.post(new IdEvent(READ_CARD_ID, nRet, "失败"));
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
            bus.post(new IdEvent(READ_CARD_ID, nRet, new String(strTmp)));
        }
    }

    public void GetCardInfo() {
        byte[] bCardInfo = new byte[256 + 1024];
        int nRet = idCardDriver.mxReadCardInfo(bCardInfo);
        if (nRet != 0) {
            if(nRet==-100){
                bus.post(new IdEvent(READ_INFO, nRet, "无设备"));
            }
            else {
                bus.post(new IdEvent(READ_INFO, nRet, "失败"));
            }
        } else {
            bus.post(new IdEvent(READ_INFO, nRet, "成功" + bCardInfo.length));
            showIdCardInfo(bCardInfo, true);
        }
    }

    @Event(R.id.tv_read_info)
    private void OnClickCardFullInfo(View view) {
        try {
            byte[] bCardFullInfo = new byte[256 + 1024+1024];
            int nRet = idCardDriver.mxReadCardFullInfo(bCardFullInfo);
            if (nRet != 0) {
                if (nRet == -14) {
                    GetCardInfo();
                } else if (nRet == -100) {
                    bus.post(new IdEvent(READ_INFO, nRet, "无设备"));
                } else {
                    bus.post(new IdEvent(READ_INFO, nRet, "失败"));
                }
            } else {
//                showIdCardInfo(bCardFullInfo, true);
//                byte[] bFingerData1 = new byte[mFingerDataSize];
//                byte[] bFingerData2 = new byte[mFingerDataSize];
//                byte[] bFingerData1_B64 = new byte[mFingerDataB64Size];
//                byte[] bFingerData2_B64 = new byte[mFingerDataB64Size];
//                for (int i = 0; i < bFingerData1.length; i++) {
//                    bFingerData1[i]=bCardFullInfo[256+1024+i];
//                }
//                for (int i = 0; i < bFingerData1.length; i++) {
//                    bFingerData2[i]=bCardFullInfo[256+1024+512+i];
//                }
//                idCardDriver.Base64Encode(bFingerData1,mFingerDataSize,bFingerData1_B64,mFingerDataB64Size);
//                idCardDriver.Base64Encode(bFingerData2,mFingerDataSize,bFingerData2_B64,mFingerDataB64Size);
//                ShowMessage("指纹数据1(Base64编码显示)：" + new String(bFingerData1_B64), true);
//                ShowMessage("指纹数据2(Base64编码显示)：" + new String(bFingerData1_B64), true);
            }
        } catch (Exception e) {

        }
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
            sb.append("用户名:" + unicode2String(id_Name));

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
            bus.post(new IdEvent(READ_INFO, 0, sb.toString()));
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
    public void onIdEvent(IdEvent event) {
        TextView tv = null;
        switch (event.getCode()) {
            case READ_VERSION:
                tv = tv_id_version;
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