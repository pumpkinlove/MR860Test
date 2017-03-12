package com.miaxis.mr860test.fragment;


import android.app.smdt.SmdtManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.DisableEvent;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.List;

public class CameraFragment extends Fragment implements SurfaceHolder.Callback {

    @ViewInject(R.id.sv_camera)     private SurfaceView sv_camera;

    private SurfaceHolder surfaceHolder;

    private Camera camera;

    private EventBus bus;
    private SmdtManager smdtManager;

    private int preWidth;
    private int preHeight;

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);
        x.view().inject(this, v);

        bus = EventBus.getDefault();
        smdtManager = SmdtManager.create(getActivity());

        surfaceHolder = sv_camera.getHolder();
        surfaceHolder.addCallback(this);

        return v;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            smdtManager.smdtSetExtrnalGpioValue(2, true);
            Thread.sleep(1000);
            camera = Camera.open(0);
            if (camera != null) {
                bus.post(new DisableEvent(true, true));
//                camera.setDisplayOrientation(180);
                camera.setPreviewDisplay(surfaceHolder);
            } else {
                Toast.makeText(getActivity(), "打开摄像头失败", Toast.LENGTH_SHORT).show();
                bus.post(new DisableEvent(false, true));
            }
        } catch (Exception e) {
            closeCamera();
            Toast.makeText(getActivity(), "打开摄像头失败", Toast.LENGTH_SHORT).show();
            bus.post(new DisableEvent(false, true));
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (camera != null) camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        closeCamera();
    }

    private void closeCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        smdtManager.smdtSetExtrnalGpioValue(2, false);
    }
}
