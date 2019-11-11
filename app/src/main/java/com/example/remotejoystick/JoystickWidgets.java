package com.example.remotejoystick;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;



public class JoystickWidgets extends ViewPort {
    protected Pallete colors;
    protected Canvas  mCanvas;
    protected Bitmap  mBitmap;
    protected Paint   mBitmapPaint;
    protected int ox, oy;
    protected int movex, movey;
    protected double sensx, sensy;
    protected int retractDelay;
    protected int animSampler;
    protected int animSpeed;
    protected Point screen;
    protected int debugLevel;
    protected int maxPower;

    public JoystickWidgets(Context context)  {
        super(context);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        screen = new Point();
        debugLevel = 1;
        maxPower = 255;
        reset();
    }

    public void reset() {
        Point size = new Point();
        getSize(size);
        movex = size.x /2;
        movey = size.y /2;
        sensx = 4;
        sensy = 4;
        retractDelay = 300;
        animSampler = 10;
        animSpeed = 60;
        ox = movex;
        oy = movey;

    }

    public int getMaxPower() { return maxPower; }
    public void setMaxPower(int v) { maxPower = v; }

    public boolean zero() {
        Point center = new Point(); middle(center);
        return movex == center.x && movey == center.y;
    }

    public void example() {
        int radius;
        radius = 50;
        setBackgroundColor(Color.parseColor(colors.background));
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor(colors.foreground));
        mCanvas.drawCircle(150,200, radius, paint);
        mCanvas.drawRoundRect(new RectF(20,20,100,100), 20, 20, paint);
        mCanvas.rotate(-45);
        Point p = new Point();
        getSize(p);
        mCanvas.drawText("Hello world", 40, 180, paint);
    }

    public int xPos(int max, Point crt) {
        double val =
            radius(crt.x,crt.y) > radius() ?
                    radius() * cos(angle(crt.x, crt.y)) :
                    radius(crt.x, crt.y) * cos(angle(crt.x, crt.y));
        double scale = val * (double) max / radius();

        Point nor = new Point(); toCartesian(crt.x, crt.y, nor);
        if(nor.x > 0)
            return (int) abs(scale);
        else
            return (int) -abs(scale);
    }

    public int yPos(int max, Point crt) {
        double val =
                radius(crt.x,crt.y) > radius() ?
                        radius() * sin(angle(crt.x, crt.y)) :
                        radius(crt.x, crt.y) * sin(angle(crt.x, crt.y));
        double scale = val * (double) max / radius();

        Point nor = new Point(); toCartesian(crt.x, crt.y, nor);
        if(nor.y > 0)
            return (int) abs(scale);
        else
            return (int) -abs(scale);
    }

    public int xPos(Point crt) { return xPos(maxPower, crt); }
    public int yPos(Point crt) { return yPos(maxPower, crt); }

    public int uPow() {
        Point crt = new Point(); crt.x = movex; crt.y = movey;
        Point rotated = new Point();
        rotate(movex, movey, -PI / 4, rotated);
        return xPos(rotated);
    }

    public int vPow() {
        Point crt = new Point(); crt.x = movex; crt.y = movey;
        Point rotated = new Point();
        rotate(movex, movey, -PI / 4, rotated);
        return yPos(rotated);
    }

    public void powerUi() {
        Point crt = new Point(); crt.x = movex; crt.y = movey;

        int px = xPos(crt);
        int py = yPos(crt);

        int pu = uPow();
        int pv = vPow();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor(colors.foreground));
        paint.setTextSize(60);
        if(debugLevel > 0) {
            //mCanvas.drawText(String.format("X: %03d", px), 40, 80, paint);
           // mCanvas.drawText(String.format("Y: %03d", py), 40, 140, paint);
            mCanvas.drawText(String.format("U: %03d", pu), 40, 80, paint);
            mCanvas.drawText(String.format("V: %03d", pv), 40, 140, paint);
        }
    }


    public void rubberCtrl(int x, int y) {
        Point size = new Point();
        getSize(size);
        ox = ox <= 0? x: ox;
        oy = oy <= 0? y: oy;
        movex+=(x-ox) * sensx;
        movey+=(y-oy) * sensy;
        crossHair(movex,movey);
        ox = x;
        oy = y;
    }

    protected int radius() {
        int radius = (smaller() - sizer(Sizes.small)) /2;
        return radius;
    }

    public double angle(int x, int y) {
        Point cartesian = new Point();
        Point screen = new Point();
        toCartesian(x, y, cartesian);
        fromCartesian(cartesian.x, cartesian.y, screen);

        double angle = 0;
        if (cartesian.x == 0) cartesian.x = 1;
        angle = atan((double) cartesian.y / cartesian.x);

        return angle;
    }

    public void xyScreen() {
        reset();
        crossHair(movex,movey);
    }

    public void crossHair(int x, int y) {
        Point center = new Point();
        middle(center);
        int size = sizer(Sizes.small);
        int half = size/2;

        screen.x = x;
        screen.y = y;

        Paint paint = new Paint();

        //blank
        mCanvas.drawColor(Color.parseColor(colors.background));

        paint.setColor(Color.parseColor(colors.foreground));
        //circle
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        mCanvas.drawCircle(center.x, center.y, radius(), paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(60);
        paint.setTextAlign(Paint.Align.CENTER);
        mCanvas.drawText("F", center.x, center.y - radius() - 15, paint);
        mCanvas.drawText("B", center.x, center.y + radius() + 60, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        mCanvas.drawText("L", center.x - radius() -15, center.y + 15, paint);
        paint.setTextAlign(Paint.Align.LEFT);
        mCanvas.drawText("R", center.x + radius() +15, center.y + 15, paint);


        if (radius(x, y) > radius()) {
            Point cartesian = new Point();
            toCartesian(x, y, cartesian);
            fromCartesian(
                    (int) round(radius() * cos(angle(x, y))),
                    (int) round(radius() * sin(angle(x, y))),
                    screen
            );

            if(cartesian.x < 0) {
                screen.x = x() - screen.x;
                screen.y = y() - screen.y;
            }
            int sxs = screen.x - half;
            int sxe = screen.x + half;
            int sys = screen.y - half;
            int sye = screen.y + half;
            paint.setStrokeWidth(3);
            mCanvas.drawLine(sxs, screen.y, sxe, screen.y, paint);
            mCanvas.drawLine(screen.x, sys, screen.x, sye, paint);
        } else {
            int xs = x - half;
            int xe = x + half;
            int ys = y - half;
            int ye = y + half;

            //crosshair
            paint.setStrokeWidth(1);
            mCanvas.drawLine(xs, y, xe, y, paint);
            mCanvas.drawLine(x, ys, x, ye, paint);
        }

        mCanvas.drawLine(center.x, center.y, screen.x, screen.y, paint);
        powerUi();
    }

    protected int sizer(double percent) {
        Point p = new Point();
        getSize(p);
        int max = isVertical()? p.y: p.x;
        double aux = max * percent / 100;
        return (int) round(aux);
    }

}
