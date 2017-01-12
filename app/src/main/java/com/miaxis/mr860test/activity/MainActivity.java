package com.miaxis.mr860test.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.adapter.ItemAdapter;
import com.miaxis.mr860test.domain.ResultEvent;
import com.miaxis.mr860test.domain.SubmitEvent;
import com.miaxis.mr860test.domain.TestItem;
import com.miaxis.mr860test.utils.DateUtil;
import com.miaxis.mr860test.utils.FileUtil;
import com.miaxis.mr860test.view.ConfirmDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    private long mExitTime;
    private List<TestItem> itemList;
    private ItemAdapter adapter;

    @ViewInject(R.id.rv_items)  private RecyclerView rv_items;
    @ViewInject(R.id.tv_title)  private TextView tv_tiltle;


    private ConfirmDialog beforeDialog;
    private ConfirmDialog afterDialog;
    private ConfirmDialog inspectionDialog;

    private EventBus bus;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);

        x.view().inject(this);

        initData();
        initView();

    }

    private void initData() {
        bus = EventBus.getDefault();
        bus.register(this);
        beforeDialog = new ConfirmDialog();
        afterDialog = new ConfirmDialog();
        inspectionDialog = new ConfirmDialog();

        initList();

        adapter = new ItemAdapter(itemList, this);
        adapter.setListener(new ItemAdapter.TestClickListenenr() {
            @Override
            public void onItemClick(View view, int position) {
                TestItem item = itemList.get(position);
                if (item == null) {
                    return;
                }
                startTestById(item.getId());
            }
        });

    }

    private void initView() {
        rv_items.setLayoutManager(new GridLayoutManager(this, 4));
        rv_items.setAdapter(adapter);
        tv_tiltle.append("v " + getVersion());
    }

    private void initList() {
        itemList = new ArrayList<>();
        TestItem item = new TestItem();
        item.setOpdate(DateUtil.format(new Date()));
        item.setRemark("无");
        item.setId(Constants.ID_LCD);
        item.setName("液晶显示屏");
        itemList.add(item);

        item = new TestItem();
        item.setOpdate(DateUtil.format(new Date()));
        item.setRemark("无");
        item.setId(Constants.ID_TOUCH);
        item.setName("触摸屏");
        itemList.add(item);

        item = new TestItem();
        item.setOpdate(DateUtil.format(new Date()));
        item.setRemark("无");
        item.setId(Constants.ID_CAMERA);
        item.setName("摄像头");
        itemList.add(item);

        item = new TestItem();
        item.setOpdate(DateUtil.format(new Date()));
        item.setRemark("无");
        item.setId(Constants.ID_LED);
        item.setName("LED补光灯");
        itemList.add(item);

        item = new TestItem();
        item.setOpdate(DateUtil.format(new Date()));
        item.setRemark("无");
        item.setId(Constants.ID_BODY);
        item.setName("人体感应");
        itemList.add(item);

        item = new TestItem();
        item.setOpdate(DateUtil.format(new Date()));
        item.setRemark("无");
        item.setId(Constants.ID_FINGER);
        item.setName("指纹仪");
        itemList.add(item);

        item = new TestItem();
        item.setOpdate(DateUtil.format(new Date()));
        item.setRemark("无");
        item.setId(Constants.ID_IDCARD);
        item.setName("二代证模块");
        itemList.add(item);

        item = new TestItem();
        item.setOpdate(DateUtil.format(new Date()));
        item.setRemark("无");
        item.setId(Constants.ID_VOICE);
        item.setName("喇叭");
        itemList.add(item);

        item = new TestItem();
        item.setOpdate(DateUtil.format(new Date()));
        item.setRemark("无");
        item.setId(Constants.ID_HDMI);
        item.setName("HDMI");
        itemList.add(item);

        item = new TestItem();
        item.setOpdate(DateUtil.format(new Date()));
        item.setRemark("无");
        item.setId(Constants.ID_WIFI);
        item.setName("Wifi");
        itemList.add(item);

        item = new TestItem();
        item.setOpdate(DateUtil.format(new Date()));
        item.setRemark("无");
        item.setId(Constants.ID_NET);
        item.setName("网口");
        itemList.add(item);

        item = new TestItem();
        item.setOpdate(DateUtil.format(new Date()));
        item.setRemark("无");
        item.setId(Constants.ID_4G);
        item.setName("4G模块");
        itemList.add(item);

        item = new TestItem();
        item.setOpdate(DateUtil.format(new Date()));
        item.setRemark("无");
        item.setId(Constants.ID_USB);
        item.setName("USB测试");
        itemList.add(item);

        item = new TestItem();
        item.setOpdate(DateUtil.format(new Date()));
        item.setRemark("无");
        item.setId(Constants.ID_TF);
        item.setName("TF卡");
        itemList.add(item);

        item = new TestItem();
        item.setOpdate(DateUtil.format(new Date()));
        item.setRemark("无");
        item.setId(Constants.ID_BT);
        item.setName("蓝牙");
        itemList.add(item);

        item = new TestItem();
        item.setOpdate(DateUtil.format(new Date()));
        item.setRemark("无");
        item.setId(Constants.ID_OLD);
        item.setName("老化测试");
        itemList.add(item);

    }

    private void startTestById(int id) {
        switch (id) {
            case Constants.ID_LCD:
                startActivity(new Intent(MainActivity.this, LCDActivity.class));
                break;
            case Constants.ID_TOUCH:
                startActivity(new Intent(MainActivity.this, TouchActivity.class));
                break;
            case Constants.ID_CAMERA:
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
                break;
            case Constants.ID_LED:
                startActivity(new Intent(MainActivity.this, LEDActivity.class));
                break;
            case Constants.ID_BODY:
                startActivity(new Intent(MainActivity.this, BodyActivity.class));
                break;
            case Constants.ID_FINGER:
                startActivity(new Intent(MainActivity.this, FingerActivity.class));
                break;
            case Constants.ID_IDCARD:
                startActivity(new Intent(MainActivity.this, IdActivity.class));
                break;
            case Constants.ID_VOICE:
                startActivity(new Intent(MainActivity.this, VoiceActivity.class));
                break;
            case Constants.ID_HDMI:
                startActivity(new Intent(MainActivity.this, HDMIActivity.class));
                break;
            case Constants.ID_WIFI:
                startActivity(new Intent(MainActivity.this, WifiActivity.class));
                break;
            case Constants.ID_NET:
                startActivity(new Intent(MainActivity.this, NetActivity.class));
                break;
            case Constants.ID_4G:
                startActivity(new Intent(MainActivity.this, GPRSActivity.class));
                break;
            case Constants.ID_TF:
                startActivity(new Intent(MainActivity.this, TFActivity.class));
                break;
            case Constants.ID_USB:
                startActivity(new Intent(MainActivity.this, USBActivity.class));
                break;
            case Constants.ID_BT:
                startActivity(new Intent(MainActivity.this, BlueToothActivity.class));
                break;
            case Constants.ID_OLD:
                startActivity(new Intent(MainActivity.this, OldActivity.class));
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(ResultEvent event) {
        TestItem item = itemList.get(event.getId() - 1);
        if (item != null) {
            item.setStatus(event.getStatus());
            item.setOpdate(DateUtil.format(new Date()));
            adapter.notifyDataSetChanged();
            try {
                FileUtil.addRecord(FileUtil.HISTORY_PATH, item);
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubmitEvent(SubmitEvent e) {
        FileUtil.writeFile(e.getPath(), FileUtil.parseToString(e.getItemList()), false);
    }

    @Event(R.id.tv_history)
    private void onHistoryClick(View view) {
        startActivity(new Intent(this, RecordsActivity.class));
    }

    @Event(R.id.tv_before)
    private void onBeforeClick(View view) {

        beforeDialog.setContent("您确定将测试结果保存到 老化前测试 吗？");
        beforeDialog.setCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beforeDialog.dismiss();
            }
        });

        beforeDialog.setConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bus.post(new SubmitEvent(itemList, FileUtil.BEFORE_PATH));
                beforeDialog.dismiss();
            }
        });
        beforeDialog.show(getFragmentManager(), "onBeforeClick");
    }

    @Event(R.id.tv_after)
    private void onAfterClick(View view) {

        afterDialog.setContent("您确定将测试结果保存到 老化后测试 吗？");
        afterDialog.setCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                afterDialog.dismiss();
            }
        });

        afterDialog.setConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bus.post(new SubmitEvent(itemList, FileUtil.AFTER_PATH));
                afterDialog.dismiss();
            }
        });
        afterDialog.show(getFragmentManager(), "onAfterClick");
    }

    @Event(R.id.tv_inspection)
    private void onInspectionClick(View view) {

        inspectionDialog.setContent("您确定将测试结果保存到 成品抽检 吗？");
        inspectionDialog.setCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inspectionDialog.dismiss();
            }
        });

        inspectionDialog.setConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bus.post(new SubmitEvent(itemList, FileUtil.INSPECTION_PATH));
                inspectionDialog.dismiss();
            }
        });
        inspectionDialog.show(getFragmentManager(), "onInspectionClick");
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private String getVersion() {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

}
