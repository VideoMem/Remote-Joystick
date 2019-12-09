package com.example.remotejoystick;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RCProtocol {

    protected static double getVal(String raw, String opCode, double def) {
        Pattern pattern = Pattern.compile(opCode + "-?\\d*\\.{0,1}\\d+");
        Matcher matcher = pattern.matcher(raw);

        if(matcher.find()) {
            return Double.valueOf(matcher.group(0).replace(opCode, ""));
        } else
            return def;

    }


    public static double getBatteryVoltage(String raw, double def) {
        return getVal(raw, "B", def);
    }

    public static double getGyroPitch(String raw, double def) {
        return getVal(raw, "N", def);
    }

    public static double getGyroRoll(String raw, double def) {
        return getVal(raw, "O", def);
    }

    public static double getAccPitch(String raw, double def) {
        return getVal(raw, "Q", def);
    }

    public static double getAccRoll(String raw, double def) {
        return getVal(raw, "R", def);
    }

    public static String nullCmd() { return "U0V0\n"; }

}
