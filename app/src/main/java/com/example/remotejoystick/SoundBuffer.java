package com.example.remotejoystick;

import android.media.AudioFormat;
import android.media.AudioTrack;
import java.util.Queue;

public class SoundBuffer {
    protected int     buffsize;
    protected boolean playing;
    protected short[] silence;
    protected int     actualSize;
    protected int     SAMPLERATE;
    Queue<Short>      qSound;
    protected static final int margin = 2;
    protected static final int depth = 3;

    SoundBuffer() {
        init();
    }

    public void init() {
        playing = false;
        SAMPLERATE = 44100;
        buffsize = AudioTrack.getMinBufferSize(
                SAMPLERATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );
        actualSize = buffsize * margin;
        silence = new short[buffsize];
    }

    public void setSize(int size) { actualSize = size; }
    public void playEnable(boolean f) { playing = f; }

    public void write(short[] s) {
        int qSize = qSound.size() / buffsize;
        if(qSize < depth) {
            if(playing) {
                for (int i = 0; i < buffsize; ++i) qSound.add(s[i]);
            } else for (int i = 0; i < buffsize; ++i) {
                qSound.add(silence[i]);
            }
        }
    }

    public short[] read() {
        short[] read = new short[buffsize];
        if(qSound.size() > buffsize) {
            for (int i = 0; i < buffsize; ++i) read[i] = qSound.poll();
        } else
            return silence;
        return read;
    }

}
