package com.example.remotejoystick;

import android.app.Application;
import android.media.AudioTrack;
import android.util.Log;

import java.util.Deque;
import java.util.LinkedList;
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
    protected String address = null;
    protected String name = null;
    public volatile LinkedList<String> sendStream;
    public volatile boolean btStatus;
    protected int txBuffSize;
    public volatile double voltage;

    public void clearSend() { while (sendStream.size() > 0) sendStream.remove(); }
    public void setBtStatus(boolean state) { btStatus = state; }
    public boolean getBtStatus() { return  btStatus; }
    public void setTxBuffSize(int s) { txBuffSize = s; }
    public int  getTxBuffSize() { return txBuffSize; }

    public void defaults() {
        setPower(255);
        setSensitivity(2);
        setRetractDelay(300);
        setRetractSpeed(50);
        setCoordVisible(true);
        setSound(false);
        setTxBuffSize(32);
    }

    public AppParameters() {
        soundBuffer = new SoundBuffer();
        sendStream  = new LinkedList<>();
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
    public void setAddress(String addr) { address = addr; }
    public String getAddress() { return address; }
    public void setName(String n) { name = n; }
    public String getName() { return name; }

}
