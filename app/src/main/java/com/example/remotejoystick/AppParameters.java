package com.example.remotejoystick;

import android.app.Application;
import android.util.Log;

public class AppParameters extends Application {

    protected double sensitivity;
    protected int power;
    protected int retractDelay;
    protected int retractSpeed;
    protected boolean coordVisible;
    protected boolean sound;

    public void defaults() {
        setPower(255);
        setSensitivity(4);
        setRetractDelay(300);
        setRetractSpeed(50);
        setCoordVisible(true);
    }

    public AppParameters() {
        defaults();
    }

    public AppParameters self() { return this; }

    public int getPower() { return power; }
    public double getSensitivity() { return sensitivity; }
    public void setPower(int p) { power = p; }
    public void setSensitivity(double v) { sensitivity = v; }
    public void setRetractDelay(int del) { retractDelay = del; }
    public int getRetractDelay() { return retractDelay; }
    public void setRetractSpeed(int spd) { retractSpeed = spd; }
    public int getRetractSpeed() { return retractSpeed; }
    public boolean showCoordinates() { return coordVisible; }
    public void setCoordVisible(boolean v) { coordVisible = v; }
    public boolean getSound() { return sound; }
    public void setSound(boolean v) { sound = v; }

}
