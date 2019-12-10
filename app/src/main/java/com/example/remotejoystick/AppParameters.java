package com.example.remotejoystick;
import android.app.Application;
import java.util.LinkedList;


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
    protected boolean caterpillar;
    protected boolean logMode;
    protected double logAmount;
    protected boolean exitAll;
    protected boolean mute;

    public volatile double gyroPitch;
    public volatile double gyroRoll;
    public volatile double gyroYaw;

    public volatile double accPitch;
    public volatile double accRoll;
    public volatile double accYaw;


    public double getGyroPitch() { return gyroPitch; }
    public double getGyroRoll()  { return gyroRoll; }
    public double getGyroYaw()   { return gyroYaw; }
    public double getAccPitch()  { return accPitch; }
    public double getAccRoll()   { return  accRoll; }
    public double getAccYaw()    { return accYaw; }
    public synchronized void setGyroPitch(double v) { gyroPitch = v; }
    public synchronized void setAccPitch(double v)  { accPitch = v; }
    public synchronized void setGyroRoll(double v)  { gyroRoll = v; }
    public synchronized void setAccRoll(double v)   { accRoll = v; }
    public synchronized void setGyroYaw(double v)   { gyroYaw = v; }
    public synchronized void setAccYaw(double v)    { accYaw = v; }

    public void setMute(boolean m) { mute = m; }
    public boolean getMute() { return mute; }
    public void setExitAll(boolean flag) { exitAll = flag; }
    public boolean getExitAll() { return exitAll; }
    public void setLogAmount(double v) { logAmount = v; }
    public double getLogAmount() { return logAmount; }
    public void setLogMode(boolean log) { logMode = log; }
    public boolean getLogMode() { return logMode; }
    public void clearSend() { while (sendStream.size() > 0) sendStream.remove(); }
    public void setBtStatus(boolean state) { btStatus = state; }
    public boolean getBtStatus() { return  btStatus; }
    public void setTxBuffSize(int s) { txBuffSize = s; }
    public int  getTxBuffSize() { return txBuffSize; }

    public void defaults() {
        setPower(255);
        setSensitivity(1);
        setRetractDelay(300);
        setRetractSpeed(10);
        setCoordVisible(true);
        setSound(false);
        setTxBuffSize(32);
        setCaterpillar(true);
        setLogMode(true);
        setLogAmount(50);
        setExitAll(false);
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
    public void setSensitivity(double v) { sensitivity = v > 0? v : 0.1; }
    public void setRetractDelay(int del) { retractDelay = del; }
    public int getRetractDelay() { return retractDelay; }
    public void setRetractSpeed(int spd) { retractSpeed = spd > 0? spd: 1; }
    public int getRetractSpeed() { return retractSpeed; }
    public boolean showCoordinates() { return coordVisible; }
    public void setCoordVisible(boolean v) { coordVisible = v; }
    public boolean getSound() { return sound; }
    public void setSound(boolean v) { sound = v; }
    public void setAddress(String addr) { address = addr; }
    public String getAddress() { return address; }
    public void setName(String n) { name = n; }
    public String getName() { return name; }
    public void setCaterpillar(boolean c) { caterpillar = c; }
    public boolean getCaterpillar() { return caterpillar; }

}
