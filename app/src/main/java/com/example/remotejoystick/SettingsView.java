package com.example.remotejoystick;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsView extends AppCompatActivity
    implements View.OnClickListener {

    protected Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        //getSupportFragmentManager()
          //      .beginTransaction()
          //      .replace(R.id.settings, new SettingsFragment())
           //    .commit();
        //ActionBar actionBar = getSupportActionBar();
       // if (actionBar != null) {
         //   actionBar.setDisplayHomeAsUpEnabled(true);
        //}

        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(this);
        Log.d("ID", "hello!");
    }

    @Override
    public void onClick(View view) {
        Log.d("ID", String.valueOf(view.getId()));

        switch (view.getId()) {
            case R.id.button:
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
                break;
        }
    }

    //public static class SettingsFragment extends PreferenceFragmentCompat {
    //    @Override
    //    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
          //  setPreferencesFromResource(R.layout.settings_activity, rootKey);
    //    }
    //}
}