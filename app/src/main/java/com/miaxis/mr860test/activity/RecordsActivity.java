package com.miaxis.mr860test.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

import com.miaxis.mr860test.R;
import com.miaxis.mr860test.adapter.MyFragmentAdapter;
import com.miaxis.mr860test.fragment.AfterFragment;
import com.miaxis.mr860test.fragment.BeforeFragment;
import com.miaxis.mr860test.fragment.HistoryFragment;
import com.miaxis.mr860test.fragment.InspectionFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_records)
public class RecordsActivity extends BaseTestActivity {

    @ViewInject(R.id.tl_records)
    private TabLayout tl_records;
    @ViewInject(R.id.vp_records)
    private ViewPager vp_records;

    private MyFragmentAdapter adapter;
    private List<Fragment> fragmentList;
    private BeforeFragment beforeFragment;
    private AfterFragment afterFragment;
    private InspectionFragment inspectionFragment;
    private HistoryFragment historyFragment;

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
        beforeFragment = new BeforeFragment();
        afterFragment = new AfterFragment();
        inspectionFragment = new InspectionFragment();
        historyFragment = new HistoryFragment();

        fragmentList = new ArrayList<>();
        fragmentList.add(historyFragment);
        fragmentList.add(beforeFragment);
        fragmentList.add(afterFragment);
        fragmentList.add(inspectionFragment);


        adapter = new MyFragmentAdapter(getSupportFragmentManager(), fragmentList);

    }

    @Override
    protected void initView() {
        String[] titles = {"测试历史", "老化前测试", "老化后测试", "成品抽检"};
        vp_records.setAdapter(adapter);
        ViewPager.OnPageChangeListener listener = new TabLayout.TabLayoutOnPageChangeListener(tl_records);
        vp_records.addOnPageChangeListener(listener);
        tl_records.setupWithViewPager(vp_records);
        vp_records.setOffscreenPageLimit(20);
        for (int i=0; i< tl_records.getTabCount(); i++) {
            final TabLayout.Tab tab = tl_records.getTabAt(i);
            tab.setText(titles[i]);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
