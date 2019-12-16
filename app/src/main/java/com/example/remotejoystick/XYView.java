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
    protected AppCompatActivity activity;
    protected Timer timer;
    protected boolean ignoreUpdate;
    protected boolean ignoreMove;
    protected boolean stopRetraction;
    protected int lastPointerCount;
    protected boolean firstDraw;

    public XYView(Context context, AppCompatActivity ref, AppParameters p) {
        super(context, p);
        param = p;
        activity = ref;
        timer = new Timer();
        ignoreUpdate = false;
        //retractTimer(500);
        ignoreMove = false;
        //audio = new SoundSynth(this, param);
        //audio.mute(false);
        stopRetraction = true;
        lastPointerCount = 0;
        firstDraw = true;
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
        if(firstDraw) { reset(); firstDraw = false; }
        //canvas.restore();
    }

    protected synchronized void command(String cmd) {
        param.sendStream.add(cmd);
    }

    protected synchronized void command() {
        read();
        if(param.sendStream.size() < param.getTxBuffSize())
            if (param.getCaterpillar()) {
                if (!param.getLogMode()) {
                    param.sendStream.add(String.format("U%dV%d",
                            uPow(),
                            vPow())
                    );
                } else {
                    param.sendStream.add(String.format("U%dV%d",
                            logCorrection(uPow()),
                            logCorrection(vPow()))
                    );
                }
            } else {
                if(!param.getLogMode()) {
                    param.sendStream.add(String.format("X%dY%d",
                            xPow(),
                            yPow())
                    );
                } else {
                    param.sendStream.add(String.format("X%dY%d",
                            logCorrection(xPow()),
                            logCorrection(yPow()))
                    );
                }
            }
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


    public void refreshTimer(int delay) {
        final Runnable runnableUpdate = new Runnable() {
            public void run() {
                if(param.showCoordinates()) {
                    crossHair(movex, movey);
                    invalidate();
                    refreshTimer(500);
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
                    Point center = new Point(); middle(center);
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
                        if(!stopRetraction) animTimer(animSampler);
                    } else {
                        Point screen = new Point(); fromCartesian(0,0, screen);
                        movex = screen.x;
                        movey = screen.y;
                    }
                    crossHair(movex, movey);
                    if(movex == center.x && movey == center.y) {
                        param.setMute(true);
                    } else {
                        param.setMute(false);
                    }

                    command();
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
        if(x > (size.x - sizer(Sizes.small))
         && (y < sizer(Sizes.small)) && zero()) {
            return true;
        } else{
            return false;
        }
    }

    public void SettingsView() {
        Intent intent = new Intent(activity, SettingsView.class);
        activity.startActivity(intent);
    }

    public void handleMultitouch(MotionEvent ev) {
        if(ev.getPointerCount() != lastPointerCount) {
            if(ev.getPointerCount() > 1) {
                command("M8");
            } else {
                command("M9");
            }
        }
        lastPointerCount = ev.getPointerCount();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        handleMultitouch(ev);
        int x = (int)ev.getX();
        int y = (int)ev.getY();

        if(ox != x || oy != y) command();

        stopRetraction = true;

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ignoreUpdate = true;
                if(!options(x,y)) {
                    ox = x;
                    oy = y;
                    ignoreMove = false;
                    param.setMute(false);
                } else {
                    ignoreMove = true;
                    drawOptionB();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                ignoreUpdate = true;
                if(!ignoreMove) {
                    rubberCtrl(x, y);
                    invalidate();
                    param.setMute(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                ignoreUpdate = false;
                if(options(x,y)) {
                    SettingsView();
                } else {
                    truncate();
                    stopRetraction = false;
                    animTimer(retractDelay);
                }
                break;
            default:
                break;
        }
        return true;
    }

}
