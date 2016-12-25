package com.miaxis.mr860test.activity;

import android.app.ProgressDialog;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.adapter.HistoryItemAdapter;
import com.miaxis.mr860test.domain.DismissEvent;
import com.miaxis.mr860test.domain.TestItem;
import com.miaxis.mr860test.domain.ToastEvent;
import com.miaxis.mr860test.utils.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.List;

@ContentView(R.layout.activity_history)
public class HistoryActivity extends BaseTestActivity {

    @ViewInject(R.id.rv_history) private RecyclerView rv_history;

    @ViewInject(R.id.tv_before_name) private TextView tv_before_name;
    @ViewInject(R.id.tv_before_opdate) private TextView tv_before_opdate;
    @ViewInject(R.id.tv_before_status) private TextView tv_before_status;
    @ViewInject(R.id.tv_before_remark) private TextView tv_before_remark;

    @ViewInject(R.id.tv_after_name) private TextView tv_after_name;
    @ViewInject(R.id.tv_after_opdate) private TextView tv_after_opdate;
    @ViewInject(R.id.tv_after_status) private TextView tv_after_status;
    @ViewInject(R.id.tv_after_remark) private TextView tv_after_remark;

    @ViewInject(R.id.tv_inspection_name) private TextView tv_inspection_name;
    @ViewInject(R.id.tv_inspection_opdate) private TextView tv_inspection_opdate;
    @ViewInject(R.id.tv_inspection_status) private TextView tv_inspection_status;
    @ViewInject(R.id.tv_inspection_remark) private TextView tv_inspection_remark;

    private List<TestItem> itemList;
    private TestItem beforeItem;
    private TestItem afterItem;
    private TestItem inspectionItem;

    private ProgressDialog dialog;
    private HistoryItemAdapter adapter;
    private EventBus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);

        x.view().inject(this);
        initData();
        initView();

        dialog.show();
        readHistory();

    }

    @Override
    protected void initData() {
        bus = EventBus.getDefault();
        bus.register(this);
        dialog = new ProgressDialog(this);
        adapter = new HistoryItemAdapter(itemList, this);
        rv_history.setLayoutManager(new LinearLayoutManager(this));
        rv_history.setAdapter(adapter);
        
    }

    @Override
    protected void initView() {
        dialog.setMessage("正在加载测试记录...");
    }


    private void readHistory() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(Environment.getExternalStorageDirectory(), FileUtil.HISTORY_PATH);
                if (!file.exists()) {
                    bus.post(new ToastEvent("读取测试历史文件失败，请确认" + FileUtil.HISTORY_PATH + "存在"));
                    return;
                }
                String content = FileUtil.readFile(file);
                itemList = FileUtil.parseFromString(content);
                if (itemList == null) {
                    bus.post(new ToastEvent("测试历史文件为空"));
                    return;
                }
                bus.post(new DismissEvent());
            }
        }).start();

    }

    private void readBefore() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(Environment.getExternalStorageDirectory(), FileUtil.BEFORE_PATH);
                if (!file.exists()) {
                    bus.post(new ToastEvent("读取测试历史文件失败，请确认" + FileUtil.HISTORY_PATH + "存在"));
                    return;
                }
                String content = FileUtil.readFile(file);
                beforeItem = FileUtil.parseItemFromString(content);
                if (itemList == null) {
                    bus.post(new ToastEvent("测试历史文件为空"));
                    return;
                }
            }
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisMissEvent(DismissEvent e) {
        adapter.setItemList(itemList);
        adapter.notifyDataSetChanged();
        dialog.dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToastEvent(ToastEvent e) {
        dialog.dismiss();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }


}
