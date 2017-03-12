package com.miaxis.mr860test.activity;

import android.app.smdt.SmdtManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.DisableEvent;
import com.miaxis.mr860test.domain.ResultEvent;
import com.miaxis.mr860test.utils.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;

@ContentView(R.layout.activity_tf)
public class TFActivity extends BaseTestActivity {

    private SmdtManager manager;

    @ViewInject(R.id.et_write)      private EditText et_write;
    @ViewInject(R.id.tv_sd_path)    private TextView tv_sd_path;
    @ViewInject(R.id.tv_read)       private TextView tv_read;
    @ViewInject(R.id.tv_pass)       private TextView tv_pass;
    @ViewInject(R.id.tv_deny)       private TextView tv_deny;

    private EventBus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        initData();
        initView();
        onTest(null);
    }

    @Override
    protected void initData() {
        manager = SmdtManager.create(this);
        bus = EventBus.getDefault();
        bus.register(this);
    }

    @Override
    protected void initView() {

    }

    @Event(R.id.tv_test)
    private void onTest(View view) {
        try {
            String path = manager.smdtGetSDcardPath(this);
            if (path != null) {
                tv_sd_path.setText(path);
            }
            FileUtil.writeFilePath(path , "tfTest.txt", et_write.getText().toString(), false);

            String read = FileUtil.readFile(new File(path + "tfTest.txt"));
            if (et_write.getText().toString().equals(read)) {
                bus.post(new DisableEvent(true, true));
            } else {
                bus.post(new DisableEvent(false, true));
            }
            tv_read.setText(read);

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            onDeny(null);
        }

    }

    @Event(R.id.tv_pass)
    private void onPass(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_TF, Constants.STATUS_PASS));
        finish();
    }

    @Event(R.id.tv_deny)
    private void onDeny(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_TF, Constants.STAUTS_DENIED));
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisableEvent(DisableEvent e) {
        if (e.isFlag()) {
            tv_pass.setTextColor(getResources().getColor(R.color.green_dark));
        } else {
            tv_pass.setTextColor(Color.GRAY);
        }
        tv_pass.setClickable(e.isFlag());
    }

    @Override
    protected void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }
}
