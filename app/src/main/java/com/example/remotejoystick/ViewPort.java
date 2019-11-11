package com.example.remotejoystick;

import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class ViewPort extends View {
    public ViewPort(Context context)  { super(context); }

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

        //normalize(vector); // No  need to normalize, vector is already ok...

        int u = (int)((double) crt.x * Math.cos(angle) - (double) crt.y * Math.sin(angle));

        int v = (int)((double) crt.x * Math.sin(angle) + (double) crt.y * Math.cos(angle));

        Point screen = new Point();
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

    public int smaller() {
        Point p = new Point();
        getSize(p);
        return p.x > p.y? p.y:p.x;
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
