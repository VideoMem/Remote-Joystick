package com.example.remotejoystick;

import static java.lang.Math.PI;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.abs;

public class SoundSynth  {

    protected short[] mSound;
    protected static XYView ref;
    protected static SoundBuffer mBuffer;

    public void mute(boolean m) { mBuffer.mute(m); }

    protected void init() {
        mSound = new short[mBuffer.getBuffsize() * 2];
    }

    public SoundSynth(XYView xy, AppParameters param) {
        ref = xy;
        mBuffer = new SoundBuffer();
        mBuffer.init(param.soundBuffer.getBuffsize(), param.soundBuffer.getSAMPLERATE());
        init();
    }

    protected double omega(int freq) {
        final double v = 2 * PI * abs(freq);
        return v;
    }

    public void synth(int freq) {
        java.util.Arrays.fill(mSound, (short) 0);
        double L = 0;
        double t;
        int i = 0;
        while (i < mBuffer.getBuffsize()) {
            t = (double) i / mBuffer.getSAMPLERATE();
            L = Short.MAX_VALUE * sin(omega(freq) * t);
            mSound[i] = (short) L;
            ++i;
        }
    }

    public int buferMS() {
        return 1000 * mBuffer.size() / mBuffer.getSAMPLERATE();
    }

    public void saturationDistortion(double v) {
        int sample;
        short point;
        for(int i=0; i < mBuffer.getBuffsize(); ++i) {
            sample = (int) round(mSound[i] * v);
            point = sample > Short.MAX_VALUE ? Short.MAX_VALUE : (short) sample;
            point = sample < -Short.MAX_VALUE ? -Short.MAX_VALUE: point;
            mSound[i] = point;
        }
    }

    public void mix(short[] toneA, short[] toneB, short[] mWave) {
        int mix;
        for(int i=0; i < mBuffer.getBuffsize(); ++i) {
            mix = toneA[i] + toneB[i];
            mWave[i] = (short) round(mix / 2);
        }
    }

    public void mux(short[] toneA, short[] toneB, short[] mWave) {
        long mux;
        for(int i=0; i < mBuffer.getBuffsize(); ++i) {
            mux = toneA[i] * toneB[i];
            mWave[i] = (short) round(mux / Short.MAX_VALUE);
        }
    }

    public void mix(int f0, int f1) {
        synth(f0);
        saturationDistortion(2);
        short toneA[] = mSound.clone();
        synth(f1);
        saturationDistortion(2);
        short toneB[] = mSound.clone();
        mix(toneA, toneB, mSound);
    }


    public void mux(int f0, int f1) {
        synth(f0);
        short toneA[] = mSound.clone();
        synth(f1);
        short toneB[] = mSound.clone();
        mux(toneA, toneB, mSound);
    }

    public void mixmux(int f0, int f1) {
        mux(f0,f1);
        short toneB[] = mSound.clone();
        mix(f0,f1);
        short toneA[] = mSound.clone();
        mix(toneA,toneB,mSound);
    }

    public void play(int freq) {
        synth(freq);
        saturationDistortion(2);
        mBuffer.write(mSound);
    }

    public void play(int f0, int f1) {
        mixmux(f0, f1);
        mBuffer.write(mSound);
    }

    public void push(SoundBuffer sb) {
        sb.write(mBuffer.read());
    }

}
