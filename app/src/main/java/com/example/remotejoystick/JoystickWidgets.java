package com.example.remotejoystick;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Vibrator;

import com.caverock.androidsvg.RenderOptions;
import com.caverock.androidsvg.SVG;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;

public class JoystickWidgets extends ViewPort {
    protected Pallete colors;
    protected static Canvas  mCanvas;
    protected Bitmap  mBitmap;
    protected Paint   mBitmapPaint;
    protected int ox, oy;
    protected int movex, movey;
    protected double sensx, sensy;
    protected int retractDelay;
    protected int animSampler;
    protected int animSpeed;
    protected Point screen;
    protected int maxPower;
    protected Context ctx;
    AssetManager am;
    Typeface segments;
    Typeface hd44780;
    Picture tractor;
    Picture caterpillar;

    public JoystickWidgets(Context context, AppParameters p)  {
        super(context, p);
        setParams(p);
        ctx = context;
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mCanvas = new Canvas();
        screen = new Point();
        am = ctx.getApplicationContext().getAssets();
        segments = Typeface.createFromAsset(am, String.format("fonts/%s", "DSEG14Classic-Bold.ttf"));
        hd44780 = Typeface.createFromAsset(am, String.format("fonts/%s", "hd44780.ttf"));
        reset();
        try {
            SVG tr = SVG.getFromResource(getContext().getResources(), R.raw.tractor);
            SVG cat = SVG.getFromResource(getContext().getResources(), R.raw.caterpillar);
            RenderOptions renderOptions = RenderOptions.create();
            renderOptions.css(String.format("* { fill: %s; }", colors.foreground));
            cat.setRenderDPI(dp2px(8));
            caterpillar = cat.renderToPicture(dp2px(30),dp2px(10), renderOptions);
            tr.setRenderDPI(dp2px(5));
            tractor = tr.renderToPicture(dp2px(30),dp2px(10), renderOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        Point size = new Point();
        getSize(size);
        movex = size.x /2;
        movey = size.y /2;
        ox = movex;
        oy = movey;
        animSampler = 10;
        setMaxPower(param.getPower());
        setSens(param.getSensitivity());
        setRetractDelay(param.getRetractDelay());
        setRetractSpeed(dp2px(param.getRetractSpeed()));

    }

    public int getMaxPower() { return maxPower; }
    public void setMaxPower(int v) { maxPower = v; }
    public void setSens(double x, double y) { sensx = x; sensy = y; }
    public void setSens(double v) { setSens(v,v); }
    public double getSensx() { return sensx; }
    public double getSensy() { return sensy; }
    public double getSens() { return getSensx(); }
    public void setRetractDelay(int del) { retractDelay = del; }
    public int getRetractDelay() { return retractDelay; }
    public void setRetractSpeed(int spd) { animSpeed = spd; }
    public int getRetractSpeed() { return animSpeed; }

    public boolean zero() {
        Point center = new Point(); middle(center);
        return movex == center.x && movey == center.y;
    }

    public void drawOption(String color) {
        Paint paint = new Paint();
        Point size = new Point(); getSize(size);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor(color));
        int len = sizer(Sizes.small) /2;
        int sep = sizer(Sizes.small) /8;
        paint.setStrokeWidth(dp2px(3));
        for(int i = 1; i < 8; i++){
            if(i % 2 == 0 )
            mCanvas.drawLine(
                    size.x - sizer(Sizes.small) + sep,
                    i * sep,
                    size.x - sep,
                    i * sep,
                    paint
            );
        }
    }

    public void drawOptionB() {
        Paint paint = new Paint();
        Point size = new Point(); getSize(size);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor(colors.foreground));
        mCanvas.drawRect(new RectF(
                size.x - sizer(Sizes.small),
                0,
                size.x,
                sizer(Sizes.small)),
                paint);
        drawOption(colors.background);
        invalidate();
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

    public int xPow() {
        Point crt = new Point(); crt.x = movex; crt.y = movey;
        return xPos(crt);
    }

    public int yPow() {
        Point crt = new Point(); crt.x = movex; crt.y = movey;
        return yPos(crt);
    }

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

    public int px2dp(int px) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return round(px / density);
    }

    public int dp2px(int dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return round(dp * density);
    }

    public void sphere() {
        shaft();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(dp2px(3));
        paint.setColor(Color.parseColor(colors.brick));
        mCanvas.drawCircle(screen.x, screen.y, radius() /3, paint);
        paint.setColor(Color.parseColor(colors.foreground));
        mCanvas.drawCircle(screen.x - dp2px(10), screen.y - dp2px(10), radius() / 9, paint);

    }

    public void shaft() {
        Point center = new Point();

        middle(center);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(dp2px(3));
        paint.setColor(Color.parseColor(colors.foreground));
        int radius = radius() / 6;
        mCanvas.drawCircle(center.x, center.y, radius, paint);

        Point bottomLeft = new Point();
        bottomLeft.x = center.x - radius /2;
        bottomLeft.y = center.y;

        Point L = rotate(bottomLeft, screen, PI /4);
        Point R = rotate(bottomLeft, screen, PI * 5/4);

        paint.setColor(Color.parseColor(colors.foreground));
        Path stick = new Path();
        stick.moveTo(L.x, L.y);
        stick.lineTo(R.x, R.y);
        Point topRight = traslate(L, screen.x, screen.y);
        stick.lineTo(topRight.x, topRight.y);
        stick.lineTo(L.x, L.y);
        mCanvas.drawPath(stick, paint);
    }

    public void drawCaterpillar() {
        try {
            int left = dp2px(130);
            int top  = dp2px(5);
            Rect dst = new Rect(
                    left,
                    top,
                    left + caterpillar.getWidth(),
                    top + caterpillar.getHeight()
            );

            mCanvas.drawPicture(caterpillar, dst);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawTruck() {
        try {
            int left = dp2px(135);
            int top  = dp2px(9);
            Rect dst = new Rect(
                    left,
                    top,
                    left + tractor.getWidth(),
                    top + tractor.getHeight()
            );

            mCanvas.drawPicture(tractor, dst);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void icons() {
        if(param.getCaterpillar()) {
            drawCaterpillar();

        } else {
            drawTruck();
        }
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
        paint.setTypeface(segments);
        paint.setTextSize(dp2px(30));

        if(true) {
            paint.setColor(Color.parseColor(colors.dark));
            mCanvas.drawText("8: 888", dp2px(20), dp2px(40), paint);
            mCanvas.drawText("8: 888", dp2px(20), dp2px(75), paint);
            mCanvas.drawText("I: III", dp2px(20), dp2px(40), paint);
            mCanvas.drawText("I: III", dp2px(20), dp2px(75), paint);
            mCanvas.drawText("X: XXX", dp2px(20), dp2px(40), paint);
            mCanvas.drawText("X: XXX", dp2px(20), dp2px(75), paint);

            paint.setColor(Color.parseColor(colors.foreground));
            String displayU = param.getCaterpillar() ? "U: %03d" : "X: %03d";
            String displayV = param.getCaterpillar() ? "V: %03d" : "Y: %03d";
            int valueU = param.getCaterpillar() ? abs(pu): abs(px);
            int valueV = param.getCaterpillar() ? abs(pv): abs(py);
            mCanvas.drawText(String.format(displayU, valueU), dp2px(20), dp2px(40), paint);
            mCanvas.drawText(String.format(displayV, valueV), dp2px(20), dp2px(75), paint);
        }

        paint.setTypeface(hd44780);
        paint.setTextSize(dp2px(15));
        if(param.getBtStatus()) {
            int signal = 100 - (100 * param.sendStream.size() / param.getTxBuffSize());
            mCanvas.drawText(
                    String.format(
                            ctx.getString(R.string.JW_connected),
                            param.getName(), signal),
                    dp2px(20),
                    y() - dp2px(40),
                    paint
            );
            mCanvas.drawText(
                    String.format(
                            ctx.getString(
                                    R.string.JW_BatteryVoltage
                            ), param.voltage),
                    dp2px(20),
                    y() - dp2px(20),
                    paint
            );

        } else
            mCanvas.drawText(ctx.getString(
                        R.string.JW_no_device
                    ),
                    dp2px(20),
                    y() - dp2px(40),
                    paint);
        sphere();
        icons();
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

    public void xyScreen() {
        reset();
        crossHair(movex, movey);
    }

    public void truncate() {
        Point crt = new Point();
        Point cartesian = new Point();
        toCartesian(movex, movey, cartesian);
        if (radius(movex, movey) > radius()) {
            fromCartesian(
                    (int) round(radius() * cos(angle(movex, movey))),
                    (int) round(radius() * sin(angle(movex, movey))),
                    crt
            );
            if(cartesian.x >= 0) {
                movex = crt.x;
                movey = crt.y;
            } else {
                movex = x() - crt.x;
                movey = y() - crt.y;
            }

        }
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
        paint.setStrokeWidth(dp2px(3));
        mCanvas.drawCircle(center.x, center.y, radius(), paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(dp2px(30));
        paint.setTextAlign(Paint.Align.CENTER);
        mCanvas.drawText(ctx.getString(R.string.JW_forward),
                center.x, center.y - radius() - dp2px(7), paint);
        mCanvas.drawText(ctx.getString(R.string.JW_backward),
                center.x, center.y + radius() + dp2px(28), paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        mCanvas.drawText(ctx.getString(R.string.JW_left),
                center.x - radius() - dp2px(3), center.y + dp2px(3), paint);
        paint.setTextAlign(Paint.Align.LEFT);
        mCanvas.drawText(ctx.getString(R.string.JW_right),
                center.x + radius() + dp2px(3), center.y + dp2px(3), paint);
        drawOption(colors.foreground);

        if (radius(x, y) > radius()) {

            Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
            if (System.currentTimeMillis() % 100 < 50 && param.showCoordinates())
                v.vibrate(10);

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
            paint.setStrokeWidth(dp2px(3));
            mCanvas.drawLine(sxs, screen.y, sxe, screen.y, paint);
            mCanvas.drawLine(screen.x, sys, screen.x, sye, paint);
        } else {
            int xs = x - half;
            int xe = x + half;
            int ys = y - half;
            int ye = y + half;

            //crosshair
            paint.setStrokeWidth(dp2px(1));
            mCanvas.drawLine(xs, y, xe, y, paint);
            mCanvas.drawLine(x, ys, x, ye, paint);
        }

        mCanvas.drawLine(center.x, center.y, screen.x, screen.y, paint);
        powerUi();
    }

}
