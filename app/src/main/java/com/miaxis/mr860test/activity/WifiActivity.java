package com.miaxis.mr860test.activity;

import android.app.smdt.SmdtManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.app.MyApplication;
import com.miaxis.mr860test.domain.CommonEvent;
import com.miaxis.mr860test.domain.DisableEvent;
import com.miaxis.mr860test.domain.NetStatusEvent;
import com.miaxis.mr860test.domain.ResultEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.net.Socket;

@ContentView(R.layout.activity_wifi)
public class WifiActivity extends BaseTestActivity {

    @ViewInject(R.id.tv_net_type)       private TextView tv_net_type;
    @ViewInject(R.id.tv_eth_mac)        private TextView tv_eth_mac;
    @ViewInject(R.id.tv_eth_ip)         private TextView tv_eth_ip;
    @ViewInject(R.id.tv_eth_status)     private TextView tv_eth_state;

    @ViewInject(R.id.et_test_ip)        private EditText et_test_ip;
    @ViewInject(R.id.et_test_port)      private EditText et_test_port;
    @ViewInject(R.id.tv_test_result)    private TextView tv_test_result;

    @ViewInject(R.id.tv_pass)           private TextView tv_pass;
    @ViewInject(R.id.tv_deny)           private TextView tv_deny;

    private static final int SPEED = 1000;

    private WifiManager wifiManager;
    private ConnectivityManager connManager;

    private SmdtManager smdtManager;
    private EventBus bus;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        initData();
        initView();
        test(null);
    }

    @Override
    protected void initData() {
        bus = EventBus.getDefault();
        bus.register(this);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        smdtManager = SmdtManager.create(this);
    }

    @Override
    protected void initView() {
        et_test_ip.setText(MyApplication.test_ip);
        et_test_port.setText(MyApplication.test_port);
    }

    @Event(R.id.tv_test)
    private void test(View view) {
        try {
            if (!wifiManager.isWifiEnabled()) {
                Toast.makeText(this, "正在打开WIFI", Toast.LENGTH_LONG).show();
                wifiManager.setWifiEnabled(true);
            }
            String type = smdtManager.getCurrentNetType();
            tv_net_type.setTextColor(getResources().getColor(R.color.red));
            flag = false;
            if ("null".equals(type)) {
                tv_net_type.setText("无网络连接");
            } else if ("2G".equals(type)) {
                tv_net_type.setText("GSM网络 (2G)");
            } else if ("3G".equals(type)) {
                tv_net_type.setText("WCDM/EVDO网络 (3G)");
            } else if ("4G".equals(type)) {
                tv_net_type.setText("FDD网络 (4G)");
            } else if ("WIFI".equals(type)) {
                flag = true;
                tv_net_type.setTextColor(getResources().getColor(R.color.green_dark));
                tv_net_type.setText("WIFI无线网络");
            } else if ("ETH".equals(type)) {
                Toast.makeText(this, "请先拔掉网线再进行测试", Toast.LENGTH_LONG).show();
                tv_net_type.setText("以太网有线网络");
            }
            if (smdtManager.smdtGetEthernetState()) {
                tv_eth_state.setText("可用");
                tv_eth_state.setTextColor(getResources().getColor(R.color.green_dark));
            } else {
                tv_eth_state.setText("不可用");
                tv_eth_state.setTextColor(getResources().getColor(R.color.red));
            }
            tv_eth_ip.setText(smdtManager.smdtGetEthIPAddress());
            tv_eth_mac.setText(smdtManager.smdtGetEthMacAddress());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    bus.post(new CommonEvent(SPEED, 1, ""));      //刷新一下界面
                    connectTomcat();
                }
            }).start();

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            bus.post(new DisableEvent(false, true));
        }

    }

    @Event(R.id.tv_pass)
    private void onPass(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_WIFI, Constants.STATUS_PASS));
        finish();
    }

    @Event(R.id.tv_deny)
    private void onDeny(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_WIFI, Constants.STAUTS_DENIED));
        finish();
    }

    public boolean isWifiConnected() {

        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi != null) {
            return mWifi.isConnected();
        }
        return false;
    }

    public boolean isMobileConnected() {

        NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (mMobile != null) {
            return mMobile.isConnected();
        }
        return false;
    }

    /**
     * @return 网络是否连接可用
     */
    public boolean isNetworkConnected() {

        NetworkInfo networkinfo = connManager.getActiveNetworkInfo();

        if (networkinfo != null) {
            return networkinfo.isConnected();
        }

        return false;
    }

    private void append(StringBuffer stringBuffer, String text) {
        if (stringBuffer != null) {
            stringBuffer.append(text + "\n");
        }

    }

    private void connectTomcat() {
        if (!flag) {
            bus.post(new DisableEvent(false, true));
            return;
        }
        String url = "http://" + et_test_ip.getText().toString() + ":" + et_test_port.getText().toString();
        try {
            Document doc = Jsoup.connect(url).timeout(15000).get();
            String result = doc.body().html();
            if (result != null && result.length() > 0) {
                bus.post(new CommonEvent(SPEED, 0, ""));
                bus.post(new DisableEvent(true, true));
            } else {
                bus.post(new CommonEvent(SPEED, -1, ""));
                bus.post(new DisableEvent(false, true));
            }
        } catch (IOException e) {
            bus.post(new CommonEvent(SPEED, -1, ""));
            bus.post(new DisableEvent(false, true));
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetChangeEvent(NetStatusEvent event) {
        test(null);
    }

    @Override
    protected void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommonEvent(CommonEvent e) {
        switch (e.getCode()) {
            case SPEED:
                if (0 == e.getResult()) {
                    tv_test_result.setText("连接成功");
                    tv_test_result.setTextColor(getResources().getColor(R.color.green_dark));
                } else if (1 == e.getResult()) {
                    tv_test_result.setText("正在连接....");
                    tv_test_result.setTextColor(getResources().getColor(R.color.gold_dark));
                }else {
                    tv_test_result.setText("连接失败");
                    tv_test_result.setTextColor(getResources().getColor(R.color.red));
                }
            break;
            default:
                tv_test_result.setText("");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisableEvent(DisableEvent e) {
        if (e.isFlag()) {
            tv_pass.setEnabled(true);
            tv_pass.setClickable(true);
            tv_pass.setTextColor(getResources().getColor(R.color.green_dark));
        } else {
            tv_pass.setEnabled(false);
            tv_pass.setClickable(false);
            tv_pass.setTextColor(getResources().getColor(R.color.gray_dark));
        }
        if (e.isFlag2()) {
            tv_deny.setEnabled(true);
            tv_deny.setClickable(true);
            tv_deny.setTextColor(getResources().getColor(R.color.red));
        } else {
            tv_deny.setEnabled(false);
            tv_deny.setClickable(false);
            tv_deny.setTextColor(getResources().getColor(R.color.gray_dark));
        }
    }

}
