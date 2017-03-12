package com.miaxis.mr860test.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miaxis.mr860test.Constants.Constants;
import com.miaxis.mr860test.R;
import com.miaxis.mr860test.domain.DisableEvent;
import com.miaxis.mr860test.domain.ResultEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_touch)
public class TouchActivity extends BaseTestActivity {

    @ViewInject(R.id.ll_grid)   private LinearLayout ll_grid;
    @ViewInject(R.id.iv_touch)  private ImageView iv_touch;
    @ViewInject(R.id.btn_exit)  private Button btn_exit;

    @ViewInject(R.id.tv_pass)   private TextView tv_pass;
    @ViewInject(R.id.tv_deny)   private TextView tv_deny;
    @ViewInject(R.id.tv_test)   private TextView tv_test;

    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;
    private EventBus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);

        x.view().inject(this);
        initData();
        initView();
        bus.post(new DisableEvent(true, false, false));

    }

    @Override
    protected void initData() {
        bitmap = Bitmap.createBitmap(1280, 752, Bitmap.Config.ARGB_4444);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);
        bus = EventBus.getDefault();
        bus.register(this);
    }

    @Override
    protected void initView() {
        iv_touch.setOnTouchListener(new View.OnTouchListener() {
            float startx;
            float starty;
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int type = event.getAction();
                switch (type) {
                    case MotionEvent.ACTION_DOWN:
                        startx = event.getX();
                        starty = event.getY();
                        System.out.println(iv_touch.getWidth());
                        System.out.println(iv_touch.getHeight());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float endx = event.getX();
                        float endy = event.getY();
                        //画画
                        canvas.drawLine(startx, starty, endx, endy, paint);
                        startx = event.getX();
                        starty = event.getY();
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
        paintIntro();
        bus.post(new DisableEvent(true, true, true));
    }

    private void paintIntro() {
        DashPathEffect pathEffect = new DashPathEffect(new float[] { 4, 5 }, 3);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setPathEffect(pathEffect);
        Path path = new Path();
        path.moveTo(80, 80);
        path.lineTo(1200, 80);
        path.lineTo(1200, 240);
        path.lineTo(80, 240);
        path.lineTo(80, 380);
        path.lineTo(1200, 380);
        path.lineTo(1200, 530);
        path.lineTo(80, 530);
        path.lineTo(80, 680);
        path.lineTo(1200, 680);
        canvas.drawPath(path, paint);
        iv_touch.setImageBitmap(bitmap);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisableEvent(DisableEvent e) {
        enableButtons(e.isFlag(),  tv_test, R.color.dark);
        enableButtons(e.isFlag2(), tv_pass, R.color.green_dark);
        enableButtons(e.isFlag3(), tv_deny, R.color.red);
    }

    @Override
    protected void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }
}
