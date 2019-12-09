package com.example.remotejoystick;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import static android.content.ContentValues.TAG;
import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;

public class ViewPort extends View {
    public ViewPort(Context context, AppParameters p)  { super(context); param = p; }
    protected AppParameters param;

    public void setParams(AppParameters p) { param = p;}
    public AppParameters getParam() { return param; }

    protected Point zeroXY() { Point z = new Point(); z.x = 0; z.y = 0; return z; }

    protected int x() {
        Point size = new Point();
        getSize(size);
        return size.x;
    }

    protected int y() {
        Point size = new Point();
        getSize(size);
        return size.y;
    }

    protected void middle(Point p) {
        p.x = x() / 2;
        p.y = y() / 2;
    }

    protected int radius(int x, int y) {
        Point center = new Point();
        middle(center);
        double xv = x - center.x;
        double yv = y - center.y;
        return (int) sqrt(pow(xv, 2) + pow(yv, 2));
    }

    protected void toCenter(int x, int y, Point res) {
        int inv_y = -y;
        Point center = new Point();
        middle(center);
        res.x = x - center.x;
        res.y = inv_y - center.y;
    }

    protected void toCartesian(int x, int y, Point res) {
        Point center = new Point();
        middle(center);
        res.x = x - center.x;
        res.y = center.y - y;
    }

    protected void fromCartesian(int x, int y, Point res) {
        Point center = new Point();
        middle(center);
        res.x = x + center.x;
        res.y = center.y - y;
    }

    protected void rotate(int x, int y, double angle, Point res) {
        Point crt = new Point(); toCartesian(x, y, crt);

        int u = (int)((double) crt.x * Math.cos(angle) - (double) crt.y * Math.sin(angle));
        int v = (int)((double) crt.x * Math.sin(angle) + (double) crt.y * Math.cos(angle));

        Point screen = new Point();
        fromCartesian(u, v, screen);

        res.x = screen.x; res.y = screen.y;
    }

    protected Point rotate(Point src, Point screen, double slip) {
        Point result = new Point();
        Point cartesian = new Point();
        toCartesian(screen.x, screen.y, cartesian);
        rotate(src, angle(screen.x, screen.y) + slip , result);

        if(cartesian.x < 0) {
            result.x = x() - result.x;
            result.y = y() - result.y;
        }

        return result;
    }

    protected Point traslate(Point src, int x, int y) {
        Point cartesian = new Point(); toCartesian(src.x, src.y, cartesian);
        Point offset = new Point(); toCartesian(x, y, offset);
        Point traslated = new Point();
        traslated.x = cartesian.x + offset.x;
        traslated.y = cartesian.y + offset.y;
        Point screen = new Point();
        fromCartesian(traslated.x, traslated.y, screen);
        return screen;
    }

    protected void rotate(Point src, double angle, Point res) {
        Point crt = new Point();
        Point screen = new Point();
        toCartesian(src.x, src.y, crt);
        float norm = radius(src.x, src.y);
        int u = (int)((double) norm * Math.cos(angle) - (double) norm * Math.sin(angle));
        int v = (int)((double) norm * Math.sin(angle) + (double) norm * Math.cos(angle));
        fromCartesian(u, v, screen);
        res.x = screen.x; res.y = screen.y;

    }


    public void getSize(final Point p) {
        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            p.x = getWidth();
            p.y = getHeight();
            p.z = 0;
        }
    }

    protected int sizer(double percent) {
        Point p = new Point();
        getSize(p);
        int max = isVertical()? p.y: p.x;
        double aux = max * percent / 100;
        return (int) round(aux);
    }

    public int smaller() {
        Point p = new Point();
        getSize(p);
        return p.x > p.y? p.y:p.x;
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

    public double angleCenter(int x, int y) {
        Point cartesian = new Point();
        Point screen = new Point();
        toCenter(x, y, cartesian);
        fromCartesian(cartesian.x, cartesian.y, screen);

        double angle = 0;
        if (cartesian.x == 0) cartesian.x = 1;
        angle = atan((double) cartesian.y / cartesian.x);

        return angle;
    }

    public boolean isVertical() {
        Point p = new Point();
        getSize(p);
        return p.x < p.y;
    }

    boolean isHorizontal() {
        Point p = new Point();
        getSize(p);
        return p.x > p.y;
    }

}
