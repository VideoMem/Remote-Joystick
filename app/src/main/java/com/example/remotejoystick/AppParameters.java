package com.example.remotejoystick;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.LinkedList;


public class AppParameters extends Application {
    protected final int maxRecvSlots = 2048;
    protected double sensitivity;
    protected int power;
    protected int retractDelay;
    protected int retractSpeed;
    protected boolean coordVisible;
    protected boolean sound;
    public volatile SoundBuffer soundBuffer;
    protected String address = null;
    protected String name = null;
    protected volatile LinkedList<String> sendCopy;
    public volatile LinkedList<String> sendStream;
    protected volatile LinkedList<String> recvStream;
    public volatile boolean btStatus;
    protected int txBuffSize;
    public volatile double voltage;
    protected boolean caterpillar;
    protected boolean logMode;
    protected double logAmount;
    protected boolean exitAll;
    protected boolean mute;
    protected boolean autoTraction;
    protected float yawGain;

    public float getYawGain() { return yawGain; }
    public void setYawGain(float v) { yawGain = v; }

    synchronized void addRecvStream(String msg) {
        recvStream.add(msg);
        while(recvStream.size() > maxRecvSlots)
            recvStream.remove();
    }

    synchronized void addSendStream(String msg) {
        sendCopy.add(msg);
        while(sendCopy.size() > maxRecvSlots)
            sendCopy.remove();
    }

    synchronized LinkedList<String> getRecvStream() { return recvStream; }
    synchronized LinkedList<String> getSendStream() { return sendCopy; }

    public boolean getAutoTraction() { return autoTraction; }
    public void setAutoTraction(boolean v) { autoTraction = v; }

    protected volatile double gyroPitch;
    protected volatile double gyroRoll;
    protected volatile double gyroYaw;

    protected volatile double accPitch;
    protected volatile double accRoll;
    protected volatile double accYaw;

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
        setRetractDelay(200);
        setRetractSpeed(50);
        setCoordVisible(true);
        setSound(false);
        setTxBuffSize(32);
        setCaterpillar(true);
        setLogMode(true);
        setLogAmount(50);
        setExitAll(false);
        setAutoTraction(true);
        setYawGain(15);
    }

    public AppParameters() {
        soundBuffer = new SoundBuffer();
        sendStream  = new LinkedList<>();
        sendCopy = new LinkedList<>();
        recvStream = new LinkedList<>();
        defaults();
    }

    public String getBuildNumber() {
        String version = getString(R.string.SET_UNAVAILABLE_BUILD);
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
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
