package com.example.remotejoystick;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    protected static XYView xy = null;
    protected volatile AppParameters params = null;
    protected static AudioPlayer player = null;
    protected static BTConnManager btman = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        params = ((AppParameters) this.getApplication()).self();

        Intent newint = getIntent();
        if("EXIT".equals(newint.getStringExtra("EXIT"))) {
            finish();
            System.exit(0);
            return;
        } else {
            Log.d("ST", "No exit flag");
        }


        if(newint.getStringExtra("NAME") != null)
            if(params.getAddress() == null || !params.getAddress().equals(
                    newint.getStringExtra(ListBTDevices.EXTRA_ADDRESS))
            ) {
            params.setAddress(newint.getStringExtra(ListBTDevices.EXTRA_ADDRESS));
            params.setName(newint.getStringExtra("NAME"));
            if(btman != null) btman.kill();
            btman = new BTConnManager(params);
            btman.connect();
            btman.start();
        }
        if(xy == null) {
            xy = new XYView(this, this, params);
        } else {
            if(xy.getParent() != null) {
                ((ViewGroup)xy.getParent()).removeView(xy); // <- fix
            }
        }
        xy.setParams(params);
        setContentView(xy);
        xy.refreshTimer(100);

        if(player != null) player.end();
        player = new AudioPlayer();
        player.setup(params.soundBuffer);
        player.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if(player != null) player.end();
        //if(btman  != null) btman.kill();
    }

}
