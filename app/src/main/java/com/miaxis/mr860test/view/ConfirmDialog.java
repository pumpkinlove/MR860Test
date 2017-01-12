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

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by xu.nan on 2016/11/8.
 */

public class ConfirmDialog extends DialogFragment {

    @ViewInject(R.id.cd_content)
    private TextView tv_content;

    @ViewInject(R.id.tv_confirm)
    private TextView tv_confrim;

    @ViewInject(R.id.tv_cancel)
    private TextView tv_cancel;

    private String content;
    private View.OnClickListener confirmListener;
    private View.OnClickListener cancelListener;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.57), (int) (dm.heightPixels * 0.25));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_confirm, container);
        x.view().inject(this, view);
        tv_content.setText(content);
        return view;
    }

    public void setContent(String str) {
        content = str;
    }

    @Event(R.id.tv_confirm)
    private void onConfirmClick(View view) {
        if (confirmListener != null)
            confirmListener.onClick(view);
    }

    @Event(R.id.tv_cancel)
    private void onCancelClick(View view) {
        if (cancelListener != null)
            cancelListener.onClick(view);
    }

    public void setConfirmListener(View.OnClickListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public void setCancelListener(View.OnClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }
}
