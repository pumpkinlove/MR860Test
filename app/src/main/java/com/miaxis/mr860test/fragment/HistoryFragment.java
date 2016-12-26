package com.miaxis.mr860test.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.miaxis.mr860test.R;
import com.miaxis.mr860test.activity.RecordsActivity;
import com.miaxis.mr860test.adapter.HistoryItemAdapter;
import com.miaxis.mr860test.domain.DismissEvent;
import com.miaxis.mr860test.domain.TestItem;
import com.miaxis.mr860test.domain.ToastEvent;
import com.miaxis.mr860test.utils.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    @ViewInject(R.id.rv_history) private RecyclerView rv_history;

    private List<TestItem> itemList;

    private ProgressDialog dialog;

    private HistoryItemAdapter adapter;
    private EventBus bus;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_history, container, false);
        x.view().inject(this, v);

        initData();
        initView();

        dialog.show();
        readHistory();

        return v;
    }

    protected void initData() {
        bus = EventBus.getDefault();
        bus.register(this);
        dialog = new ProgressDialog(getActivity());
        adapter = new HistoryItemAdapter(itemList, getContext());
        rv_history.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_history.setAdapter(adapter);
    }

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDismissEvent(DismissEvent e) {
        adapter.setItemList(itemList);
        adapter.notifyDataSetChanged();
        dialog.dismiss();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToastEvent(ToastEvent event) {
        dialog.dismiss();
        Toast.makeText(getContext(), event.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }
}
