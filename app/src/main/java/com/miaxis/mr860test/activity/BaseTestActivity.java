package com.miaxis.mr860test.activity;

import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.miaxis.mr860test.R;

/**
 * Created by xu.nan on 2016/12/19.
 */

public abstract class BaseTestActivity extends AppCompatActivity {

    protected abstract void initData();
    protected abstract void initView();

    protected void enableButtons(boolean flag, TextView textView, int colorId) {
        textView.setEnabled(flag);
        textView.setClickable(flag);
        if (flag) {
            textView.setTextColor(getResources().getColor(colorId));
        } else {
            textView.setTextColor(getResources().getColor(R.color.gray_dark));
        }
    }


}
