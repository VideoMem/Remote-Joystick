package com.example.remotejoystick;
import android.util.Log;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Deque;

import static android.content.ContentValues.TAG;

public class SoundBuffer {
    protected int     buffsize;
    protected boolean mute;
    protected static short[] silence;
    protected static short[] last;
    protected int     readSize;
    protected int     SAMPLERATE;
    protected volatile Deque<Short> qSound;
    protected final int margin = 2;
    protected final int depth = 1;
    protected short[] read;
    protected int buffmargin;

    public int getSAMPLERATE() { return  SAMPLERATE; }
    public int getBuffsize() { return  buffsize; }
    public int getReadSize() { return  readSize; }

    public short[] getSilence() { return  silence; }

    public SoundBuffer() { }

    public void init(int size, int samplerate) {
        mute(false);
        SAMPLERATE = samplerate;
        buffsize = size;
        silence = new short[buffsize];
        qSound  = new LinkedList<>();
        last = silence.clone();
        buffmargin = buffsize * margin;
        read = new short[buffmargin];
    }

    public void mute(boolean f) { mute = f; }

    public synchronized void write(short[] s) {
        last = s.clone();
        int qSize = size();

        if(qSize < depth) {
            if(!mute) {
                for (int i = 0; i < s.length; ++i) {
                    qSound.add(s[i]);
                }
            } else {
                for (int i = 0; i < buffsize; ++i) {
                    qSound.add(silence[i]);
                }
            }
        } else {
            Log.d(TAG,  "SoundBuffer write overflow");
        }

    }

    public synchronized int size() {
        int end;
        try {
            end = qSound.size();
        } catch (Exception e) {
            e.printStackTrace();
            end = -1;
        }
        return end;
    }

    public synchronized short[] read() {
        int end = size();
        int p =0,i=0;

        Iterator iterator = qSound.iterator();
        while (iterator.hasNext() && i < buffmargin) {
            try {
                read[i] = qSound.remove();
                if(i > 0 && read[i] > 0 && read[i -1] <= 0) p = i;
                ++i;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("SNDB remove exception",
                        String.format(
                                "end: %d, i: %d, b: %b",
                                end, i, qSound.isEmpty())
                );
                break;
            }
        }

        readSize = p;

        if (!mute) {
            if(i > 0) return read;
            write(last);
            //return read();
        }
        return silence;
    }

}
