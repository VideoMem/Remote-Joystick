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
import android.util.Log;

import com.caverock.androidsvg.RenderOptions;
import com.caverock.androidsvg.SVG;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.log;
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
    Picture speaker;

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
            SVG spkr = SVG.getFromResource(getContext().getResources(), R.raw.speaker);
            RenderOptions renderOptions = RenderOptions.create();
            renderOptions.css(String.format("* { fill: %s; }", colors.foreground));
            cat.setRenderDPI(dp2px(8));
            caterpillar = cat.renderToPicture(dp2px(30),dp2px(10), renderOptions);
            tr.setRenderDPI(dp2px(5));
            tractor = tr.renderToPicture(dp2px(30),dp2px(10), renderOptions);
            spkr.setRenderDPI(dp2px(8));
            speaker = spkr.renderToPicture(dp2px(30), dp2px(10), renderOptions);
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

    public void gyroPitch() {
        Point center = new Point();
        middle(center);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor(colors.foreground));
        paint.setStrokeWidth(dp2px(3));
        int radius = radius() / 4;

        Point bottomLeft = new Point();
        bottomLeft.x = center.x - radius /2;
        bottomLeft.y = center.y;
        Point bottomRight = new Point();
        bottomRight.x = center.x + radius /2;
        bottomRight.y = center.y;

        //360 2p
        //a   x
        double slipL = (param.getGyroPitch() * PI /180) - PI /4;
        double slipR = (param.getGyroPitch() * PI /180) + PI * 3/4;
        Point L = rotate(bottomLeft, bottomLeft, slipL);
        Point R = rotate(bottomLeft, bottomLeft, slipR);
        Point M = rotate(bottomRight, bottomRight, slipL);
        Point S = rotate(bottomRight, bottomRight, slipR);

        Point topRight = traslate(R, x() - dp2px(60), dp2px(100));
        Point topLeft  = traslate(L, x() - dp2px(60), dp2px(100));
        Point topS     = traslate(S, x() - dp2px(60), dp2px(100));
        Point topM     = traslate(M, x() - dp2px(60), dp2px(100));

        mCanvas.drawLine(topLeft.x, topLeft.y, topM.x, topM.y, paint);
        //mCanvas.drawLine(topM.x, topM.y, topS.x, topS.y, paint);

    }

    public void gyroRoll() {
        Point center = new Point();
        middle(center);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor(colors.foreground));
        paint.setStrokeWidth(dp2px(3));
        int radius = radius() / 4;

        Point bottomLeft = new Point();
        bottomLeft.x = center.x - radius /2;
        bottomLeft.y = center.y;
        Point bottomRight = new Point();
        bottomRight.x = center.x + radius /2;
        bottomRight.y = center.y;

        //360 2p
        //a   x
        double slipL = (param.getGyroRoll() * PI /180) - PI /4;
        double slipR = (param.getGyroRoll() * PI /180) + PI * 3/4;
        Point L = rotate(bottomLeft, bottomLeft, slipL);
        Point R = rotate(bottomLeft, bottomLeft, slipR);
        Point M = rotate(bottomRight, bottomRight, slipL);
        Point S = rotate(bottomRight, bottomRight, slipR);

        Point topRight = traslate(R, dp2px(60), dp2px(100));
        Point topLeft  = traslate(L, dp2px(60), dp2px(100));
        Point topS     = traslate(S, dp2px(60), dp2px(100));
        Point topM     = traslate(M, dp2px(60), dp2px(100));

        mCanvas.drawLine(topLeft.x, topLeft.y, topM.x, topM.y, paint);
        //mCanvas.drawLine(topM.x, topM.y, topS.x, topS.y, paint);

    }

    public void shaft() {
        Point center = new Point();
        middle(center);

        double dx = (double) (screen.x - center.x) / 3;
        double dy = (double) (screen.y - center.y) / 3;

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(dp2px(1));
        paint.setColor(Color.parseColor(colors.dark));
        int radius = radius() / 6;
        mCanvas.drawCircle(center.x + round(dx), center.y + round(dy), radius, paint);
        paint.setColor(Color.parseColor(colors.foreground));
        paint.setAlpha(69);
        paint.setStyle(Paint.Style.STROKE);
        mCanvas.drawCircle(center.x + round(dx), center.y + round(dy), radius, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(0xFF);

        Point bottomLeft = new Point();
        bottomLeft.x = center.x - radius /2;
        bottomLeft.y = center.y;

        Point L = rotate(bottomLeft, screen, PI /4);
        Point R = rotate(bottomLeft, screen, PI * 5/4);

        mCanvas.drawCircle(center.x + round(dx), center.y + round(dy), radius(L.x, L.y), paint);


        paint.setColor(Color.parseColor(colors.foreground));
        Path stick = new Path();
        stick.moveTo(L.x + round(dx), L.y + round(dy));
        stick.lineTo(R.x + round(dx), R.y + round(dy));
        Point topRight = traslate(R, screen.x, screen.y);
        Point topLeft  = traslate(L, screen.x, screen.y);
        stick.lineTo(topRight.x, topRight.y);
        stick.lineTo(topLeft.x, topLeft.y);
        stick.lineTo(L.x + round(dx), L.y + round(dy));
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

    public void drawSpeaker() {
        int left = dp2px(240);
        int top  = dp2px(12);
        Rect dst = new Rect(
                left,
                top,
                left + speaker.getWidth(),
                top + speaker.getHeight()
        );

        mCanvas.drawPicture(speaker, dst);
    }

    public void icons() {
        if(param.getCaterpillar()) {
            drawCaterpillar();

        } else {
            drawTruck();
        }
        if(param.getSound())
            drawSpeaker();
    }

    protected double logValue(int x) {
        double out = 67.605 * log((double) abs(x)) - 106.732;
        return out;
    }

    protected int logCorrection(int x) {
        double log = logValue(x);
        double lin = abs(x);
        double a = param.getLogAmount() / 100;
        double b = (100 - param.getLogAmount()) / 100;
        double out = (a * log) + (b * lin);
        out = out > param.getPower()? param.getPower(): out;
        if(out < 0) out = 0;
        return x > 0 ? (int) round(out) : -(int) round(out);
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
            paint.setAlpha(128);
            mCanvas.drawText("8: 888", dp2px(20), dp2px(40), paint);
            mCanvas.drawText("8: 888", dp2px(20), dp2px(75), paint);
            mCanvas.drawText("I: III", dp2px(20), dp2px(40), paint);
            mCanvas.drawText("I: III", dp2px(20), dp2px(75), paint);
            mCanvas.drawText("X: XXX", dp2px(20), dp2px(40), paint);
            mCanvas.drawText("X: XXX", dp2px(20), dp2px(75), paint);
            paint.setAlpha(0xFF);
            paint.setColor(Color.parseColor(colors.foreground));
            String displayU = param.getCaterpillar() ? "U: %03d" : "X: %03d";
            String displayV = param.getCaterpillar() ? "V: %03d" : "Y: %03d";
            int valueU = param.getCaterpillar() ? abs(pu): abs(px);
            int valueV = param.getCaterpillar() ? abs(pv): abs(py);
            valueU = param.getLogMode() ? logCorrection(valueU): valueU;
            valueV = param.getLogMode() ? logCorrection(valueV): valueV;
            mCanvas.drawText(String.format(displayU, valueU), dp2px(20), dp2px(40), paint);
            mCanvas.drawText(String.format(displayV, valueV), dp2px(20), dp2px(75), paint);
        }

        paint.setTypeface(hd44780);
        paint.setTextSize(dp2px(15));

        mCanvas.drawText(
                String.format(ctx.getString(R.string.JW_MAXPOWER),
                        param.getPower()),
                dp2px(20),
                y() - dp2px(60),
                paint
        );

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
        gyroPitch();
        gyroRoll();
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

    public void drawRubber() {
        Point center = new Point();
        middle(center);
        Paint fore = new Paint();
        Paint back = new Paint();
        Paint bright = new Paint();

        bright.setColor(Color.parseColor(colors.foreground));
        bright.setStyle(Paint.Style.STROKE);
        bright.setStrokeWidth(dp2px(1));
        bright.setAlpha(69);

        fore.setColor(Color.parseColor(colors.background));
        fore.setStyle(Paint.Style.FILL);
        fore.setStrokeWidth(dp2px(1));
        fore.setAlpha(128);

        back.setColor(Color.parseColor(colors.dark));
        back.setStyle(Paint.Style.FILL);
        back.setStrokeWidth(dp2px(1));

        double pos; long pow; int idx, j;
        double dx, dy;
        for(int i = 1000; i >= 0; i-=100) {
            j = 1000 - i;
            idx = i / 100;
            dx = (double) (screen.x - center.x) * j / 3000;
            dy = (double) (screen.y - center.y) * j / 3000;
            //Log.d("idx:", String.format("%d\n", idx));
            Paint selected = new Paint(idx % 2 == 0? back: fore);
            pow = round(0xFF * (double) i / 1000);
            pos = (double) radius() * (double) logCorrection((int) pow) / 0xFF;
            mCanvas.drawCircle(center.x + round(dx), center.y + round(dy), (float) pos, selected);
            mCanvas.drawCircle(center.x + round(dx), center.y + round(dy), (float) pos, bright);
        }

    }

    protected void updateScreen(int x, int y) {
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

            if (cartesian.x < 0) {
                screen.x = x() - screen.x;
                screen.y = y() - screen.y;
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

        updateScreen(x,y);

        Paint paint = new Paint();

        //blank
        mCanvas.drawColor(Color.parseColor(colors.background));

        paint.setColor(Color.parseColor(colors.foreground));
        //circle
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp2px(3));
        mCanvas.drawCircle(center.x, center.y, radius(), paint);

        drawRubber();

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

        /*
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

            if (cartesian.x < 0) {
                screen.x = x() - screen.x;
                screen.y = y() - screen.y;
            }
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
 */
        powerUi();
    }

}
