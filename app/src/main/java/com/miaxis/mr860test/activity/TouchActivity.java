package com.miaxis.mr860test.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.ResultEvent;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_touch)
public class TouchActivity extends BaseTestActivity {

    @ViewInject(R.id.ll_grid)
    private LinearLayout ll_grid;

    @ViewInject(R.id.iv_touch)
    private ImageView iv_touch;

    @ViewInject(R.id.btn_exit)
    private Button btn_exit;

    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;


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
        bitmap = Bitmap.createBitmap(getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_4444);
        canvas = new Canvas(bitmap);

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);

    }

    @Override
    protected void initView() {
        iv_touch.setOnTouchListener(new View.OnTouchListener() {
            int startx;
            int starty;
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int type = event.getAction();
                switch (type) {
                    case MotionEvent.ACTION_DOWN:
                        startx = (int) event.getX();
                        starty = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endx = (int) event.getX();
                        int endy = (int) event.getY();
                        //画画
                        canvas.drawLine(startx, starty, endx, endy, paint);
                        startx = (int) event.getX();
                        starty = (int) event.getY();
                        iv_touch.setImageBitmap(bitmap);
                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                }
                return true;
            }
        });
    }

    @Event(R.id.tv_test)
    private void onTest(View view) {
        ll_grid.setVisibility(View.VISIBLE);
        iv_touch.setVisibility(View.VISIBLE);
        btn_exit.setVisibility(View.VISIBLE);
    }

    @Event(R.id.tv_pass)
    private void onPass(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_TOUCH, Constants.STATUS_PASS));
        finish();
    }

    @Event(R.id.tv_deny)
    private void onDeny(View view) {
        EventBus.getDefault().post(new ResultEvent(Constants.ID_TOUCH, Constants.STAUTS_DENIED));
        finish();
    }

    @Event(R.id.btn_exit)
    private void onExitTest(View viw) {
        ll_grid.setVisibility(View.GONE);
        btn_exit.setVisibility(View.GONE);
        iv_touch.setVisibility(View.GONE);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }
}
