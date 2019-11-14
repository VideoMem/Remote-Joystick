package com.example.remotejoystick;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import static java.lang.Math.PI;
import static java.lang.Math.sin;

public class Sound {
    protected final int SAMPLERATE = 44100;
    protected AudioTrack mAudio;
    protected int buffsize;
    short[] mSound;

    public Sound() {
        buffsize = AudioTrack.getMinBufferSize(
                SAMPLERATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );
        mSound = new short[buffsize];

        mAudio = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                SAMPLERATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                buffsize,
                AudioTrack.MODE_STREAM
        );
        Log.d("mAudio session", String.valueOf(mAudio.getAudioSessionId()));
    }

    public void play(int freq) {
        double L = 0;
        double omegaL = 2 * PI * freq;

        for(int i =0; i < buffsize; i++) {
            int t = i / SAMPLERATE;
            L = Short.MAX_VALUE * sin(omegaL * t);
            mSound[i] = (short) L;
        }
        try {
            mAudio.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
            mAudio.play();
            mAudio.write(mSound, 0, buffsize);
            mAudio.stop();
            mAudio.release();
            Log.d("Message", "Sucess");
        } catch (Exception e) {
            Log.d("Message", "Exception");
        }
    }

}
