package com.miaxis.mr860test.activity;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by xu.nan on 2016/12/19.
 */

public abstract class BaseTestActivity extends AppCompatActivity {

    protected abstract void initData();
    protected abstract void initView();

}
