package com.example.remotejoystick;
import android.content.Intent;
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
    protected static BTConnManager btman;

    protected void soundTimer() {
        final Runnable runnableUpdate = new Runnable() {
            public void run() {
                Log.d("Advice", "Param run");
                if (params.getSound()) {
                    try {
                        //player.setup(params.soundBuffer);
                        player.start();
                    }  catch (Exception e) {
                        Log.d("MSG", "Thread already running");
                    }
                    soundTimer();
                } else
                    player.end();

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
        params = ((AppParameters) this.getApplication()).self();
        Intent newint = getIntent();
        params.setAddress(newint.getStringExtra(ListBTDevices.EXTRA_ADDRESS));
        params.setName(newint.getStringExtra("NAME"));

        xy = new XYView(this, this, params);
        setContentView(xy);
        xy.refreshTimer(100);

        if(btman != null) btman.kill();
        btman = new BTConnManager(params);
        if(params.name != null) {
            btman.connect();
            btman.start();
        }

        if(player != null) player.end();
        player = new AudioPlayer();
        player.setup(params.soundBuffer);
        player.start();
        //timer = new Timer();
        //soundTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.end();
    }

}
