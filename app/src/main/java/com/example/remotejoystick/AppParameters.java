package com.example.remotejoystick;

import android.app.Application;
import android.media.AudioTrack;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class AppParameters extends Application {

    protected double sensitivity;
    protected int power;
    protected int retractDelay;
    protected int retractSpeed;
    protected boolean coordVisible;
    protected boolean sound;
    public volatile SoundBuffer soundBuffer;
    //public volatile AudioTrack mTrack;
    protected Timer timer;




    public void defaults() {
        setPower(255);
        setSensitivity(2);
        setRetractDelay(300);
        setRetractSpeed(50);
        setCoordVisible(true);
        setSound(false);
    }

    public AppParameters() {
        soundBuffer = new SoundBuffer();
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
