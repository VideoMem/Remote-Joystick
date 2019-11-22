package com.example.remotejoystick;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    protected static XYView xy = null;
    protected Timer timer;
    protected volatile AppParameters params;
    protected static AudioPlayer player;

    protected void soundTimer() {
        final Runnable runnableUpdate = new Runnable() {
            public void run() {
                Log.d("Advice", "Param run");
                if (params.getSound()) {
                    try {
                        player.setup(params.soundBuffer);
                        player.run();
                    }  catch (Exception e) {
                        Log.d("MSG", "Thread already running");
                    }
                } else
                    player.end();
                soundTimer();
            }
        };
        TimerTask task = new TimerTask(){
            public void run() {
                runOnUiThread(runnableUpdate);
            }
        };
        timer.schedule(task, 1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //vola AppParameters params;
        params = ((AppParameters) this.getApplication()).self();
        xy = new XYView(this, this, params);
        setContentView(xy);
        player = new AudioPlayer();
        player.setup(params.soundBuffer);
        //timer = new Timer();
        //soundTimer();
        //player.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.end();
    }

}
