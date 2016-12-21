package com.miaxis.mr860test.activity;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.NetStatusEvent;
import com.miaxis.mr860test.domain.PingEvent;
import com.miaxis.mr860test.domain.ResultEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@ContentView(R.layout.activity_net)
public class NetActivity extends BaseTestActivity {

    @ViewInject(R.id.tv_ping_result)
    private TextView tv_ping_result;

    @ViewInject(R.id.v_net_status)
    private View v_net_status;

    @ViewInject(R.id.v_wifi_status)
    private View v_wifi_status;

    @ViewInject(R.id.v_gprs_status)
    private View v_gprs_status;

    private WifiManager wifiManager;
    private ConnectivityManager connManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        EventBus.getDefault().register(this);

        initData();
        initView();
        checkWifi();
        checkGprs();
        checkNet();
    }

    @Override
    protected void initData() {
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void initView() {

    }

    @Event(R.id.tv_test)
    private void test(View view) {
//        if (wifiManager.isWifiEnabled()) {
//            wifiManager.setWifiEnabled(false);
//        }
        StringBuffer buffer = new StringBuffer();
        ping("192.168.6.39", 5, buffer);
        EventBus.getDefault().post(new PingEvent(buffer.toString()));
    }

    @Event(R.id.tv_pass)
    private void onPass(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_NET, Constants.STATUS_PASS));
        finish();
    }

    @Event(R.id.tv_deny)
    private void onDeny(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_NET, Constants.STAUTS_DENIED));
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

    public boolean ping(String host, int pingCount, StringBuffer stringBuffer) {
        String line = null;
        Process process = null;
        BufferedReader successReader = null;
//        String command = "ping -c " + pingCount + " -w 5 " + host;
        String command = "ping -c " + pingCount + " " + host;
        boolean isSuccess = false;
        try {
            process = Runtime.getRuntime().exec(command);
            if (process == null) {
                append(stringBuffer, "ping fail:process is null.");
                return false;
            }
            successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = successReader.readLine()) != null) {
                append(stringBuffer, line);
            }
            int status = process.waitFor();
            if (status == 0) {
                append(stringBuffer, "exec cmd success:" + command);
                isSuccess = true;
            } else {
                append(stringBuffer, "exec cmd fail.");
                isSuccess = false;
            }
            append(stringBuffer, "exec finished.");
        } catch (IOException e) {
        } catch (InterruptedException e) {
        } finally {
            if (process != null) {
                process.destroy();
            }
            if (successReader != null) {
                try {
                    successReader.close();
                } catch (IOException e) {
                }
            }
        }
        return isSuccess;
    }

    private void append(StringBuffer stringBuffer, String text) {
        if (stringBuffer != null) {
            stringBuffer.append(text + "\n");
        }

    }

    private void checkNet() {
        if (isNetworkConnected()) {
            v_net_status.setBackgroundColor(Color.GREEN);
        } else {
            v_net_status.setBackgroundColor(Color.RED);
        }
    }

    private void checkWifi() {
        if (isWifiConnected()) {
            v_wifi_status.setBackgroundColor(Color.GREEN);
        } else {
            v_wifi_status.setBackgroundColor(Color.RED);
        }
    }

    private void checkGprs() {
        if (isMobileConnected()) {
            v_gprs_status.setBackgroundColor(Color.GREEN);
        } else {
            v_gprs_status.setBackgroundColor(Color.RED);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetChangeEvent(NetStatusEvent event) {
        checkWifi();
        checkGprs();
        checkNet();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPingEvnet(PingEvent event) {
        tv_ping_result.append(event.getContent());
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
