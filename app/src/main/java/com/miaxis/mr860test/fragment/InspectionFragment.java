package com.miaxis.mr860test.fragment;


import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.miaxis.mr860test.R;
import com.miaxis.mr860test.adapter.ItemAdapter;
import com.miaxis.mr860test.domain.TestItem;
import com.miaxis.mr860test.utils.FileUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class InspectionFragment extends Fragment {

    @ViewInject(R.id.rv_inspection)
    private RecyclerView rv_inspection;

    private List<TestItem> itemList;

    private ItemAdapter adapter;

    public InspectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_inspection, container, false);

        x.view().inject(this, v);

        initData();
        initView();

        return v;
    }

    private void initData() {
        File file = new File(Environment.getExternalStorageDirectory(), FileUtil.INSPECTION_PATH);
        if (!file.exists()) {
            Toast.makeText(getContext(), "成品抽检测试记录不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = FileUtil.readFile(file);
        itemList = FileUtil.parseFromString(content);
        adapter = new ItemAdapter(itemList, getContext());

    }

    private void initView() {
        rv_inspection.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rv_inspection.setAdapter(adapter);
    }

}
