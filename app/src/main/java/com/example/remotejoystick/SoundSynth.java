package com.example.remotejoystick;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Process;
import android.util.Log;

import static android.content.ContentValues.TAG;
import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.abs;

public class SoundSynth implements Runnable {
    protected int SAMPLERATE;
    protected AudioTrack mAudio;
    protected int buffsize;
    protected short[] mSound;
    protected boolean playing;
    protected static XYView ref;
    protected Handler nester;
    protected short[] sync;
    protected int actualSize;

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
            play();
    }

    protected void init() {
        playing = false;
        SAMPLERATE = 44100;
        buffsize = AudioTrack.getMinBufferSize(
                SAMPLERATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );
        mSound = new short[buffsize * 2];

        mAudio = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                SAMPLERATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                buffsize,
                AudioTrack.MODE_STREAM
        );

        try {
            mAudio.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public SoundSynth(XYView xy) {
        init();
        nester = new Handler();
        ref = xy;
    }

    protected double omega(int freq) {
        return 2 * PI * abs(freq);
    }

    public double offset(int freq) {
        short end = sync[buffsize -1];
        double t = asin((double) end / Short.MAX_VALUE) / omega(freq);
        return t;
    }


    public void synth(int freq) {
        sync = mSound.clone();
        java.util.Arrays.fill(mSound, (short) 0);
        double L = 0;
        double t;
        //double slip = offset(freq);
        int i = 0;
        while (i < buffsize) {
            t = (double) i / SAMPLERATE;
            L = Short.MAX_VALUE * sin(omega(freq) * t);
            mSound[i] = (short) L;
            ++i;
        };
        actualSize = i;
    }

    public int buferMS() {
        return 1000 * actualSize / SAMPLERATE;
    }

    public void saturationDistortion(double v) {
        int sample;
        short point;
        for(int i=0; i < buffsize; ++i) {
            sample = (int) round(mSound[i] * v);
            point = sample > Short.MAX_VALUE ? Short.MAX_VALUE : (short) sample;
            point = sample < -Short.MAX_VALUE ? -Short.MAX_VALUE: point;
            mSound[i] = point;
        }
    }

    public int mix(short[] toneA, short[] toneB, short[] mWave) {
        int mix, lmix = 0, idx = 0;
        for(int i=0; i < actualSize; ++i) {
            mix = toneA[i] + toneB[i];
            mWave[i] = (short) round(mix / 2);
            if(mix > 0 && lmix <= 0) idx = i;
            lmix = mix;
        }
        return idx;
    }

    public int mux(short[] toneA, short[] toneB, short[] mWave) {
        long mix, lmix = 0, idx = 0;
        for(int i=0; i < actualSize; ++i) {
            mix = toneA[i] * toneB[i];
            mWave[i] = (short) round(mix / Short.MAX_VALUE);
            if(mix > 0 && lmix <= 0) idx = i;
            lmix = mix;
        }
        return (int) idx;
    }



    public void mix(int f0, int f1) {
        synth(f0);
        saturationDistortion(2);
        short toneA[] = mSound.clone();
        synth(f1);
        saturationDistortion(2);
        short toneB[] = mSound.clone();
        actualSize = mix(toneA, toneB, mSound);
    }


    public void mux(int f0, int f1) {
        synth(f0);
        //saturationDistortion(2);
        short toneA[] = mSound.clone();
        synth(f1);
       // saturationDistortion(2);
        short toneB[] = mSound.clone();
        /*long mix, lmix=0, idx = 0;
        for(int i=0; i < actualSize; ++i) {
            mix = toneA[i] * toneB[i];
            mSound[i] = (short) round(mix / Short.MAX_VALUE);
            if(mix > 0 && lmix <= 0) idx = i;
            lmix = mix;
        }*/
        actualSize = mux(toneA, toneB, mSound);
        //saturationDistortion(2);
        //actualSize = (int) idx;
    }

    public void mixmux(int f0, int f1) {
        mux(f0,f1);
        short toneB[] = mSound.clone();
        mix(f0,f1);
        short toneA[] = mSound.clone();
        actualSize = mix(toneA,toneB,mSound);
    }

    public void play(int freq) {
        synth(freq);
        saturationDistortion(2);
        play();
    }

    public void play(int f0, int f1) {
        mixmux(f0, f1);
        play();
    }

    public void play() {
        try {
            mAudio.play(); playing = true;
            mAudio.write(mSound, 0, actualSize);
        } catch (Exception e) {
            Log.d(TAG, "Exception");
            e.printStackTrace();
            init();
        }

    }

}
