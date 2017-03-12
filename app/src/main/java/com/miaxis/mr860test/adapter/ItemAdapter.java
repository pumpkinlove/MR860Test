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

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder>{

    private List<TestItem> itemList;
    private Context context;
    private TestClickListenenr listener;

    public ItemAdapter(List<TestItem> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_item, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        TestItem item = itemList.get(position);
        if (item == null) {
            return;
        }
        holder.tv_name.setText(item.getName());
        if (item.getOpdate() != null)
            holder.tv_opdate.setText(item.getOpdate());

        switch (item.getStatus()) {
            case Constants.STATUS_PASS:
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.blue_band_dark));
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.green_dark));
                holder.tv_status.setText("通过");
                holder.v_aero.setBackgroundColor(context.getResources().getColor(R.color.green_light_aero));
                holder.ll_item.setClickable(true);
                break;
            case Constants.STAUTS_DENIED:
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.blue_band_dark));
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.red));
                holder.tv_status.setText("不通过");
                holder.v_aero.setBackgroundColor(context.getResources().getColor(R.color.red_light_aero));
                holder.ll_item.setClickable(true);
                break;
            case Constants.STAUTS_UNABLE:
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.gray_dark));
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.gray_dark));
                holder.tv_status.setText("不可用");
                holder.ll_item.setClickable(false);
                break;
            case Constants.STAUTS_RECORD:
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.blue_band_dark));
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.green_dark));
                holder.tv_status.setText("已记录");
                holder.v_aero.setBackgroundColor(context.getResources().getColor(R.color.green_light_aero));
                holder.ll_item.setClickable(true);
                break;
            default:
                holder.tv_name.setTextColor(context.getResources().getColor(R.color.blue_band_dark));
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.blue_band_dark2));
                holder.tv_status.setText("未测试");
                holder.v_aero.setBackgroundColor(context.getResources().getColor(R.color.aero));
                holder.ll_item.setClickable(true);
                break;
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

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @ViewInject(R.id.tv_name)       private TextView tv_name;
        @ViewInject(R.id.tv_status)     private TextView tv_status;
        @ViewInject(R.id.tv_opdate)     private TextView tv_opdate;
        @ViewInject(R.id.v_aero)        private View v_aero;
        @ViewInject(R.id.ll_item)       private LinearLayout ll_item;

        private TestClickListenenr listener;

        public ItemViewHolder(View itemView, TestClickListenenr listener) {
            super(itemView);
            this.listener = listener;
            x.view().inject(this, itemView);
        }

        @Event(R.id.ll_item)
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
}
