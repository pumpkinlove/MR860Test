package com.miaxis.mr860test.fragment;


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

        return v;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            camera = Camera.open(0);
            if (camera != null) {

                camera.setDisplayOrientation(180);

//                List<Camera.Size> sizeList = camera.getParameters().getSupportedPreviewSizes();
//                for (int i=0; i<sizeList.size(); i++) {
//
//                }
                camera.setPreviewDisplay(surfaceHolder);
            } else {
                Toast.makeText(getActivity(), "打开摄像头失败", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            if (null != camera) {
                camera.release();
                camera = null;
            }
            Toast.makeText(getActivity(), "打开摄像头失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (camera != null) camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}
