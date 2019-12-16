package com.example.remotejoystick;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

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
    protected Switch caterpillarEnabled;
    protected Switch logMode;
    protected Switch tractionControl;
    protected SeekBar logComponent;
    protected EditText yawGain;
    protected TextView version;

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
            case R.id.logMixer:
                param.setLogAmount(logComponent.getProgress());
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
            case R.id.caterpillarMode:
                param.setCaterpillar(isChecked);
                break;
            case R.id.logScale:
                param.setLogMode(isChecked);
                break;
            case R.id.trackAuto:
                param.setAutoTraction(isChecked);
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
        logComponent = findViewById(R.id.logMixer);
        logComponent.setOnSeekBarChangeListener(this);
        showCoords = findViewById(R.id.showCoords);
        showCoords.setOnCheckedChangeListener(this);
        soundEnable = findViewById(R.id.soundFeed);
        soundEnable.setOnCheckedChangeListener(this);
        caterpillarEnabled = findViewById(R.id.caterpillarMode);
        caterpillarEnabled.setOnCheckedChangeListener(this);
        logMode = findViewById(R.id.logScale);
        logMode.setOnCheckedChangeListener(this);
        tractionControl = findViewById(R.id.trackAuto);
        tractionControl.setOnCheckedChangeListener(this);
        yawGain = findViewById(R.id.yawGain);
        version = findViewById(R.id.appVersion);

        read();
        TextView Device = findViewById(R.id.DevConfig);
        if(param.name != null)
            Device.setText(String.format(
                    this.getString(R.string.SET_device_fmt),
                    param.name, param.address));
        else
            Device.setText(this.getString(R.string.SET_no_device));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        submit();
    }


    protected void read() {
        double sns = param.getSensitivity() * 10;
        sens.setProgress((int) sns);
        power.setProgress(param.getPower());
        retractDelay.setProgress(param.getRetractDelay());
        retractSpeed.setProgress(param.getRetractSpeed());
        logComponent.setProgress((int) param.getLogAmount());
        showCoords.setChecked(param.showCoordinates());
        soundEnable.setChecked(param.getSound());
        caterpillarEnabled.setChecked(param.getCaterpillar());
        logMode.setChecked(param.getLogMode());
        tractionControl.setChecked(param.getAutoTraction());
        yawGain.setText(String.valueOf(param.getYawGain()));
        version.setText(String.format(getString(R.string.SET_BUILD), param.getBuildNumber()));
    }

    protected void submit() {
        param.setYawGain(Float.parseFloat(yawGain.getText().toString()));
    }

    public void resetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(this.getString(R.string.SET_param_reset_conf));
        builder.setMessage(this.getString(R.string.SET_param_question));

        builder.setPositiveButton(this.getText(R.string.SET_yes),
                new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                param.defaults();
                read();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(this.getText(R.string.SET_no),
                new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void exitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(this.getString(R.string.SET_exit_conf));
        builder.setMessage(this.getString(R.string.SET_exit_question));

        builder.setPositiveButton(
                this.getString(R.string.SET_yes), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", "EXIT");
                startActivity(intent);
                finish();
            }
        });

        builder.setNegativeButton(R.string.SET_no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
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
                Intent intent = new Intent(this, ListBTDevices.class);
                this.startActivity(intent);
                submit();
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