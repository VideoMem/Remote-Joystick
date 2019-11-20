package com.example.remotejoystick;

import android.media.AudioTrack;
import android.os.Process;

public class AudioPlayer extends Thread {
    protected SoundBuffer mBuffer;
    protected AudioTrack mAudio;

    public void setup(SoundBuffer mb) {
        mBuffer = mb;
        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
    }

    public void run() {

    }
}
