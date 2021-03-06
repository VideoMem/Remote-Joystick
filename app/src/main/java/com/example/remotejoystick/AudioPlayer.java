package com.example.remotejoystick;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Process;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class AudioPlayer extends SoundSynth {
    protected static AudioTrack mAudio;
    protected boolean kill;
    protected int frame;

    protected XYView ref;

    public void end() { kill = true; }

    public AudioPlayer(XYView ref) {
        super(ref);
        setup();
    }

    public synchronized void setup() {
        mBuffer = new SoundBuffer();
        mute(false);
        frame = 0;
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
        mBuffer.init(buffsize, SAMPLERATE);
        init();

        try {
            mAudio.stop();
            mAudio.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        kill = false;

        Log.d("AudioPlayer", "Audio Thread started");
    }

    protected void play() {
        try {
            send();
            short[] read = mBuffer.read();
            if(mBuffer.getReadSize() > 0) {
                mAudio.play();
                mAudio.write(read, 0, mBuffer.getReadSize());
                ++frame;
            }
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
