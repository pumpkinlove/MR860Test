package com.miaxis.mr860test.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.miaxis.mr860test.R;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by xu.nan on 2017/1/16.
 */

public class OldDetailDialog extends DialogFragment {

    private String content;

    public void setContent(String content) {
        this.content = content;
    }

    @ViewInject(R.id.tv_old_begin_time)         private TextView tv_old_begin_time;
    @ViewInject(R.id.tv_old_end_time)           private TextView tv_old_end_time;

    @ViewInject(R.id.tv_old_count)              private TextView tv_old_count;

    @ViewInject(R.id.tv_open_led_success)       private TextView tv_open_led_success;
    @ViewInject(R.id.tv_open_led_fail)          private TextView tv_open_led_fail;

    @ViewInject(R.id.tv_open_camera_success)    private TextView tv_open_camera_success;
    @ViewInject(R.id.tv_open_camera_fail)       private TextView tv_open_camera_fail;

    @ViewInject(R.id.tv_read_id_success)        private TextView tv_read_id_success;
    @ViewInject(R.id.tv_read_id_fail)           private TextView tv_read_id_fail;

    @ViewInject(R.id.tv_read_finger_success)    private TextView tv_read_finger_success;
    @ViewInject(R.id.tv_read_finger_fail)       private TextView tv_read_finger_fail;

    @ViewInject(R.id.tv_close_camera_success)   private TextView tv_close_camera_success;
    @ViewInject(R.id.tv_close_camera_fail)      private TextView tv_close_camera_fail;

    @ViewInject(R.id.tv_close_led_success)      private TextView tv_close_led_success;
    @ViewInject(R.id.tv_close_led_fail)         private TextView tv_close_led_fail;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.66), (int) (dm.heightPixels * 0.75));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_old_detail, container);
        x.view().inject(this, view);

        initData();

        return view;
    }

    private void initData() {
        String[] contents = content.split("\\$");

        tv_old_begin_time.      setText(contents[0]);
        tv_old_end_time.        setText(contents[1]);
        tv_old_count.           setText(contents[2]);
        tv_open_led_success.    setText(contents[3]);
        tv_open_led_fail.       setText(contents[4]);

        tv_open_camera_success. setText(contents[5]);
        tv_open_camera_fail.    setText(contents[6]);

        tv_read_id_success.     setText(contents[7]);
        tv_read_id_fail.        setText(contents[8]);

        tv_read_finger_success. setText(contents[9]);
        tv_read_finger_fail.    setText(contents[10]);

        tv_close_camera_success.setText(contents[11]);
        tv_close_camera_fail.   setText(contents[12]);

        tv_close_led_success.   setText(contents[13]);
        tv_close_led_fail.      setText(contents[14]);
    }

}
