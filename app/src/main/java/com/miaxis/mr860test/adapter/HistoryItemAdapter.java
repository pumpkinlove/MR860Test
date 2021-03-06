package com.miaxis.mr860test.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.TestItem;
import com.miaxis.mr860test.utils.DateUtil;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by xu.nan on 2016/12/19.
 */

public class HistoryItemAdapter extends RecyclerView.Adapter<HistoryItemAdapter.HistoryItemViewHolder> {

    private List<TestItem> itemList;
    private Context context;
    private TestClickListenenr listener;

    public HistoryItemAdapter(List<TestItem> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public HistoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HistoryItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_history, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(HistoryItemViewHolder holder, int position) {

        TestItem item = itemList.get(itemList.size() - position - 1);
        if (item == null) {
            return;
        }

        holder.tv_history_name.setText(item.getName());

        if (item.getOpdate() != null)
            holder.tv_history_opdate.setText(item.getOpdate());

        switch (item.getStatus()) {
            case Constants.STATUS_PASS:
                holder.tv_history_status.setTextColor(context.getResources().getColor(R.color.green_dark));
                holder.tv_history_status.setText("通过");
                break;
            case Constants.STAUTS_DENIED:
                holder.tv_history_status.setTextColor(context.getResources().getColor(R.color.red));
                holder.tv_history_status.setText("不通过");
                break;
            case Constants.STAUTS_RECORD:
                holder.tv_history_status.setTextColor(context.getResources().getColor(R.color.green_dark));
                holder.tv_history_status.setText("已记录");
                break;
            default:
                holder.tv_history_status.setTextColor(context.getResources().getColor(R.color.dark));
                break;
        }
        if (item.getId() == Constants.ID_OLD && null != item.getRemark() && !"null".equals(item.getRemark())) {
            holder.tv_history_remark.setText("点击查看详细");
        } else {
            holder.tv_history_remark.setText("备注");
        }

    }

    @Override
    public int getItemCount() {
        if (itemList != null) {
            return itemList.size();
        } else {
            return 0;
        }
    }

    class HistoryItemViewHolder extends RecyclerView.ViewHolder {

        @ViewInject(R.id.tv_history_opdate)     private TextView tv_history_opdate;
        @ViewInject(R.id.tv_history_name)       private TextView tv_history_name;
        @ViewInject(R.id.tv_history_status)     private TextView tv_history_status;
        @ViewInject(R.id.tv_history_remark)     private TextView tv_history_remark;

        private TestClickListenenr listener;

        HistoryItemViewHolder(View itemView, TestClickListenenr listener) {
            super(itemView);
            this.listener = listener;
            x.view().inject(this, itemView);
        }

        @Event(R.id.ll_item_history)
        private void onClick(View view) {
            if(listener != null) {
                listener.onItemClick(view, getPosition());
            }
        }

    }

    public interface TestClickListenenr {
        void onItemClick(View view, int position);
    }

    public TestClickListenenr getListener() {
        return listener;
    }

    public void setListener(TestClickListenenr listener) {
        this.listener = listener;
    }

    public List<TestItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TestItem> itemList) {
        this.itemList = itemList;
    }
}
