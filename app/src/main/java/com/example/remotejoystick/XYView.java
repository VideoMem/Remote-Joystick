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
import static java.lang.Math.round;


public class XYView extends JoystickWidgets {
    protected Pallete colors;
    protected AppCompatActivity activity;
    protected Timer timer;
    protected boolean ignoreUpdate;
    protected boolean ignoreMove;
    protected static SoundSynth audio;
    protected boolean sound;
    protected boolean onSound() { return sound; }

    public XYView(Context context, AppCompatActivity ref, AppParameters p) {
        super(context, p);
        param = p;
        activity = ref;
        timer = new Timer();
        ignoreUpdate = false; retractTimer(500);
        ignoreMove = false;
       // handler = new Handler();
        audio = new SoundSynth(this);
        audio.mute(false);
        sound = false;
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

    protected  void audioSend() {
        int fx = 30 + abs(round(uPow() * 80 / param.getPower()));
        int fy = 30 + abs(round(vPow() * 80 / param.getPower()));
        if(param.getSound()) {
            audio.play(fx, fy);
            audio.push(param.soundBuffer);
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
                        animTimer(animSampler);
                    } else {
                        Point screen = new Point(); fromCartesian(0,0, screen);
                        movex = screen.x;
                        movey = screen.y;
                    }
                    crossHair(movex, movey);
                    if(movex == center.x && movey == center.y) {
                        param.soundBuffer.mute(true);
                    }

                    audioSend();
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
                    param.soundBuffer.mute(!param.getSound());
                    audioSend();
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
                    audioSend();
                }
                break;
            case MotionEvent.ACTION_UP:
                ignoreUpdate = false;
                if(options(x,y)) {
                    SettingsView();
                } else {
                    truncate();
                    animTimer(retractDelay);
                }
                break;
            default:
                break;
        }
        return true;
    }

}
