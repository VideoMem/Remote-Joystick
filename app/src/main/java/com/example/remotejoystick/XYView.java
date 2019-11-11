package com.example.remotejoystick;

import android.content.Context;
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

    public XYView(Context context, AppCompatActivity ref) {
        super(context);
        activity = ref;
        timer = new Timer();
        ignoreUpdate = false; retractTimer(500);

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


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int x = (int)ev.getX();
        int y = (int)ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ignoreUpdate = true;
                ox = x;
                oy = y;
                break;
            case MotionEvent.ACTION_MOVE:
                ignoreUpdate = true;
                rubberCtrl(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                ignoreUpdate = false;
                animTimer(retractDelay);
                break;
        }
        return true;
    }

}
