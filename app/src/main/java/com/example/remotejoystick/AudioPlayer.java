package com.example.remotejoystick;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Process;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class AudioPlayer extends Thread {
    protected volatile SoundBuffer mBuffer;
    protected static AudioTrack mAudio;
    protected boolean kill;
    protected int frame;
    protected int SAMPLERATE = 44100;

    public void end() { kill = true; }

    public void setup(SoundBuffer mb) {
        frame = 0;
        mBuffer = mb;
        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
        int buffsize = AudioTrack.getMinBufferSize(
                SAMPLERATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );
        mAudio = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                SAMPLERATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                buffsize,
                AudioTrack.MODE_STREAM
        );
        mAudio.stop();
        mBuffer.init(buffsize, SAMPLERATE);


        try {
            mAudio.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        kill = false;
    }

    protected void play() {
        try {
            mAudio.play();
            mAudio.write(mBuffer.read(), 0, mBuffer.getReadSize());
            ++frame;
        } catch (Exception e) {
            //setup(mBuffer);
            Log.d(TAG, "Audio Thread Exception");
            Log.d(TAG, String.format("Frame ID: %d", frame));
            Log.d(TAG, String.valueOf(mBuffer.size()));
            e.printStackTrace();
            //kill= true;
        }
    }

    @Override
    public void run() {
        while(!kill)
            play();
        Log.d(TAG, "Audio thread ended");
    }

}
