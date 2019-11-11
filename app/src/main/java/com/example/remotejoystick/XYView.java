package com.example.remotejoystick;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.abs;


public class XYView extends JoystickWidgets {
    protected Pallete colors;
    AppCompatActivity activity;
    Timer timer;
    protected boolean ignoreUpdate;
    protected boolean ignoreMove;

    public XYView(Context context, AppCompatActivity ref) {
        super(context);
        activity = ref;
        timer = new Timer();
        ignoreUpdate = false; retractTimer(500);
        ignoreMove = false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap,0 ,0, mBitmapPaint);
        canvas.restore();
    }

    protected void retractTimer(int delay) {
        final Runnable runnableUpdate = new Runnable() {
            public void run() {
                if(!ignoreUpdate) {
                    xyScreen();
                    invalidate();
                }
            }
        };

        TimerTask task = new TimerTask(){
            public void run() {
                activity.runOnUiThread(runnableUpdate);
            }
        };


        timer.schedule(task, delay);
    }

    protected void animTimer(int delay) {
        final Runnable runnableUpdate = new Runnable() {
            public void run() {
                if(!ignoreUpdate) {

                    //if(radius(movex,movey) > radius()) movex = screen.x; movey = screen.y;

                    Point crt  = new Point(); toCartesian(movex, movey, crt);

                    if(abs(crt.x) > animSpeed || abs(crt.y) > animSpeed) {
                        if(abs(crt.x) > animSpeed) {
                            if (crt.x > 0)
                                movex -= animSpeed;
                            else
                                movex += animSpeed;
                        }
                        if(abs(crt.y) > animSpeed) {
                            if(crt.y > 0)
                                movey += animSpeed;
                            else
                                movey -= animSpeed;
                        }
                        animTimer(animSampler);
                    } else {
                        Point screen = new Point(); fromCartesian(0,0, screen);
                        movex = screen.x;
                        movey = screen.y;
                    }
                    crossHair(movex, movey);
                    invalidate();
                }
            }
        };

        TimerTask task = new TimerTask(){
            public void run() {
                activity.runOnUiThread(runnableUpdate);
            }
        };


        timer.schedule(task, delay);
    }

    public boolean options(int x, int y) {
        Point size = new Point();
        getSize(size);
        if(  x > (size.x - sizer(Sizes.small))
         && (y < sizer(Sizes.small)) && zero()) {
            return true;
        } else{
            return false;
        }
    }

    public void SettingsView() {
        Intent intent = new Intent(activity, SettingsView.class);
        activity.startActivity(intent);

        //activity.setContentView(R.layout.settings_activity);
        //activity.startActivity(R.layout.settings_activity);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int x = (int)ev.getX();
        int y = (int)ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ignoreUpdate = true;
                if(!options(x,y)) {
                    ox = x;
                    oy = y;
                    ignoreMove = false;
                } else {
                    ignoreMove = true;
                    drawOptionB();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                ignoreUpdate = true;
                if(!ignoreMove)
                    rubberCtrl(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                ignoreUpdate = false;
                if(options(x,y)) {
                    SettingsView();
                }
                animTimer(retractDelay);
                break;
        }
        return true;
    }

}
