package com.miaxis.mr860test.activity;

import android.os.Bundle;
import android.view.WindowManager;

import com.miaxis.mr860test.R;

import org.xutils.view.annotation.ContentView;
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

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }
}
