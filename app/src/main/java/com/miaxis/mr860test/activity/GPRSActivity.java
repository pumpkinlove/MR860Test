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
import com.miaxis.mr860test.domain.NetStatusEvent;
import com.miaxis.mr860test.domain.ResultEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_gprs)
public class GPRSActivity extends BaseTestActivity {

    @ViewInject(R.id.tv_net_type)       private TextView tv_net_type;
    @ViewInject(R.id.tv_eth_mac)        private TextView tv_eth_mac;
    @ViewInject(R.id.tv_eth_ip)         private TextView tv_eth_ip;
    @ViewInject(R.id.tv_eth_status)     private TextView tv_eth_state;
    @ViewInject(R.id.wv_test)           private WebView  wv_test;
    @ViewInject(R.id.et_url)            private EditText et_url;

    private WifiManager wifiManager;
    private ConnectivityManager connManager;

    private SmdtManager smdtManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        EventBus.getDefault().register(this);

        initData();
        initView();
        test(null);
    }

    @Override
    protected void initData() {
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        smdtManager = SmdtManager.create(this);
    }

    @Override
    protected void initView() {
        WebSettings s = wv_test.getSettings();
        s.setBuiltInZoomControls(true);
        s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);
        s.setSavePassword(true);
        s.setSaveFormData(true);
        s.setJavaScriptEnabled(true);
        s.setGeolocationEnabled(true);
        s.setDomStorageEnabled(true);

    }

    @Event(R.id.tv_test)
    private void test(View view) {
        if (wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "禁用WIFI", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(false);
        }
        try {
            String type = smdtManager.getCurrentNetType();
            tv_net_type.setTextColor(getResources().getColor(R.color.red));
            if ("null".equals(type)) {
                tv_net_type.setText("无网络连接");
            } else if ("2G".equals(type)) {
                tv_net_type.setText("GSM网络 (2G)");
            } else if ("3G".equals(type)) {
                tv_net_type.setText("WCDM/EVDO网络 (3G)");
            } else if ("4G".equals(type)) {
                tv_net_type.setTextColor(getResources().getColor(R.color.green_dark));
                tv_net_type.setText("FDD网络 (4G)");
            } else if ("WIFI".equals(type)) {
                tv_net_type.setText("WIFI无线网络");
            } else if ("ETH".equals(type)) {
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

            wv_test.loadUrl(et_url.getText().toString());
            //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
            wv_test.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    // TODO Auto-generated method stub
                    //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                    view.loadUrl(url);
                    return true;
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            onDeny(null);
        }

    }

    @Event(R.id.tv_pass)
    private void onPass(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_4G, Constants.STATUS_PASS));
        finish();
    }

    @Event(R.id.tv_deny)
    private void onDeny(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_4G, Constants.STAUTS_DENIED));
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



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetChangeEvent(NetStatusEvent event) {
        test(null);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
