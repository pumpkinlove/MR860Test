package com.miaxis.mr860test.fragment;


import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.miaxis.mr860test.R;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.List;

public class CameraFragment extends Fragment implements SurfaceHolder.Callback {

    @ViewInject(R.id.sv_camera)     private SurfaceView sv_camera;

    private SurfaceHolder surfaceHolder;

    private Camera camera;

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

        surfaceHolder = sv_camera.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        return v;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            camera = Camera.open(0);
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            if (null != camera) {
                camera.release();
                camera = null;
            }
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }
}
