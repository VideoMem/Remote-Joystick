package com.example.remotejoystick;

import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class SettingsView extends AppCompatActivity
    implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener,
        Switch.OnCheckedChangeListener {

    protected Button mButton;
    protected Button mReset;
    protected Button mExit;
    protected SeekBar power;
    protected SeekBar sens;
    protected SeekBar retractDelay;
    protected SeekBar retractSpeed;
    protected AppParameters param;
    protected Switch showCoords;
    protected Switch soundEnable;

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch(seekBar.getId()) {
            case R.id.power:
                param.setPower(power.getProgress());
                break;
            case R.id.sensitivity:
                param.setSensitivity((double) sens.getProgress() / 10);
                break;
            case R.id.retractDelay:
                param.setRetractDelay(retractDelay.getProgress());
                break;
            case R.id.retractSpeed:
                param.setRetractSpeed(retractSpeed.getProgress());
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.showCoords:
                param.setCoordVisible(isChecked);
                break;
            case R.id.soundFeed:
                param.setSound(isChecked);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        param = ((AppParameters) this.getApplication()).self();

        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(this);
        mReset = findViewById(R.id.defSettings);
        mReset.setOnClickListener(this);
        mExit = findViewById(R.id.exitApp);
        mExit.setOnClickListener(this);
        power = findViewById(R.id.power);
        power.setOnSeekBarChangeListener(this);
        sens = findViewById(R.id.sensitivity);
        sens.setOnSeekBarChangeListener(this);
        retractDelay = findViewById(R.id.retractDelay);
        retractDelay.setOnSeekBarChangeListener(this);
        retractSpeed = findViewById(R.id.retractSpeed);
        retractSpeed.setOnSeekBarChangeListener(this);
        showCoords = findViewById(R.id.showCoords);
        showCoords.setOnCheckedChangeListener(this);
        soundEnable = findViewById(R.id.soundFeed);
        soundEnable.setOnCheckedChangeListener(this);
        read();
    }

    protected void read() {
        double sns = param.getSensitivity() * 10;
        sens.setProgress((int) sns);
        power.setProgress(param.getPower());
        retractDelay.setProgress(param.getRetractDelay());
        retractSpeed.setProgress(param.getRetractSpeed());
        showCoords.setChecked(param.showCoordinates());
        soundEnable.setChecked(param.getSound());
    }

    public void resetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm parameter reset");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                param.defaults();
                read();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void exitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm application exit");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
                break;
            case R.id.defSettings:
                resetDialog();
                break;
            case R.id.exitApp:
                exitDialog();
                break;
        }

    }

}