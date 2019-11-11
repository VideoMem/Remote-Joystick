package com.example.remotejoystick;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XYView xy = new XYView(this, this);
        setContentView(xy);
        xy.invalidate();

    }

}
