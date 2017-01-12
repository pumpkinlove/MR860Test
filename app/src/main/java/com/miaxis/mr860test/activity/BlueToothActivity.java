package com.miaxis.mr860test.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;

import com.miaxis.mr860test.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.x;

@ContentView(R.layout.activity_blue_tooth)
public class BlueToothActivity extends BaseTestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initData();
        initView();
    }


    @Event(R.id.tv_test)
    private void openBlueTooth() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try{
            startActivity(intent);
        } catch(ActivityNotFoundException ex){
            ex.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }
}
