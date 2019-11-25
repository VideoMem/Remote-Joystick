package com.example.remotejoystick;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RCProtocol {

    public static double getBatteryVoltage(String raw) {
        Pattern pattern = Pattern.compile("B-?\\d*\\.{0,1}\\d+");
        Matcher matcher = pattern.matcher(raw);

        if(matcher.find()) {
            return Double.valueOf(matcher.group(0).replace("B", ""));
        } else
            return 0.0;
    }

    public static String nullCmd() { return "U0V0\n"; }

}
