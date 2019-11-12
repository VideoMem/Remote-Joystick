package com.example.remotejoystick;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    protected static XYView xy = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        xy = new XYView(this, this, ((AppParameters) this.getApplication()).self());
        setContentView(xy);
    }

}
