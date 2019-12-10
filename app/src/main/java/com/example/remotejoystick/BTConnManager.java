package com.example.remotejoystick;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Process;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static android.os.Process.setThreadPriority;
import static java.lang.System.arraycopy;

public class BTConnManager extends Thread {
    private final int readTimeout = 3000;
    private static AppParameters param;
    private static BluetoothSocket btSocket = null;
    private static BluetoothAdapter myBluetooth = null;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean kill;
    private RCProtocol parser;
    private int failCount;
    private String lastCmd;
    private static String lastSucessfulAddr = null;
    private long lastRead;

    public void kill() { kill=true; }

    public void setup(AppParameters par) {
        param = par;
        kill = false;
        setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        parser = new RCProtocol();
        lastCmd = parser.nullCmd();
        failCount = 0;
        lastRead = 0;
    }

    BTConnManager(AppParameters p) { setup(p); }

    private void disconnect () {
        if ( btSocket!=null ) {
            try {
                failCount = 0;
                btSocket.close();
                param.setBtStatus(false);
            } catch(IOException e) {
                msg("Error closing socket");
            }
        }
    }


    public void connect() {
        param.sendStream.clear();
        lastCmd = parser.nullCmd();
        long lastTS;
        long elapsed;
        do {
            disconnect();
            new ConnectBT().execute();
            elapsed = 0; lastTS = System.currentTimeMillis();
            while(elapsed < readTimeout) {
                elapsed = System.currentTimeMillis() - lastTS;
            }
        } while(lastSucessfulAddr == param.getAddress() && !param.getBtStatus());
        lastRead = System.currentTimeMillis();
    }

    private void sendSignal(String msg) {
        if ( btSocket != null ) {
            try {
                int size = msg.length() + 1;
                byte[] sendArr = new byte[size];
                arraycopy(msg.getBytes(), 0, sendArr, 0, msg.length());
                sendArr[msg.length()] = 13; //adds enter
                if(btSocket.isConnected()) {
                    btSocket.getOutputStream().write(sendArr);
                } else
                    connect();
            } catch (IOException e) {
                msg("Error writing to socket");
            }
        }
    }

    private String receiveSignal() {
        String msg;
        byte[] recv = new byte[234354];
        Arrays.fill(recv, (byte) 0);
        msg = "";
        if ( btSocket != null ) {
            try {
                if(btSocket.getInputStream().available() > 0) {
                    int bytes = btSocket.getInputStream().read(recv);
                    Log.d("Read", String.valueOf(bytes));
                    msg = new String(recv, 0, bytes);
                    Log.d("Message", msg);
                    failCount = 0;
                    lastRead = System.currentTimeMillis();
                } else {
                    ++failCount;
                   // Log.d(TAG, "No input data this time");
                }
            } catch (IOException e) {
                msg("Error reading from socket");
            }
        }
        return msg;
    }

    private long millisLastRead() {
        return  System.currentTimeMillis() - lastRead;
    }

    static private void msg (String s) {
       // Toast.makeText(param.getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }


    private static class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected  void onPreExecute () {
            Log.d("Connecting", "please wait");
            msg("Connecting");
        }

        @Override
        protected Void doInBackground (Void... devices) {
            try {
                if ( btSocket==null || !param.getBtStatus() ) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(param.getAddress());
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed");
                Log.d(TAG, "Connection Failed. Is it a SPP Bluetooth? Try again.");
                param.setBtStatus(false);
            } else {
                msg("Connected");
                Log.d(TAG, "Connected!");
                param.setBtStatus(true);
                lastSucessfulAddr = param.getAddress();
            }

        }
    }

    public void sendControlModeDecimal() {
        sendSignal("M5");
    }

    public void parseResponse(String msg) {
        if(msg.length() > 0) {
            param.setGyroPitch(parser.getGyroPitch(msg, param.getGyroPitch()));
            param.setGyroRoll(parser.getGyroRoll(msg, param.getGyroRoll()));
            param.voltage = parser.getBatteryVoltage(msg, param.voltage);
        }
    }

    public void pollBattery() {
        sendSignal("B0");
        String msg = receiveSignal();
        parseResponse(msg);
    }

    public void pollGyro() {
        sendSignal("M10");
        String msg = receiveSignal();
        parseResponse(msg);
        sendSignal("M0");
        msg = receiveSignal();
        parseResponse(msg);
    }

    @Override
    public void run() {
        long sent = 0;
        long lastBpoll=0;
        long lastApoll=0;
        sendControlModeDecimal();

        while(!kill) {
            try {
                if(param.getBtStatus()) {

                    if (param.sendStream.size() > 0) {
                        sent+=param.sendStream.size();
                        lastCmd = param.sendStream.remove();
                        sendSignal(lastCmd);
                        lastRead = System.currentTimeMillis();
                    } else {
                        if (System.currentTimeMillis() - lastApoll > 100){
                            pollGyro();
                            lastApoll = System.currentTimeMillis();
                        }
                    }

                    if (System.currentTimeMillis() - lastBpoll > 1000) {
                        pollBattery();
                        sendSignal(lastCmd);
                        lastBpoll = System.currentTimeMillis();
                    }

                    if(millisLastRead() > readTimeout) {
                        Log.d(TAG, "TX timed out, reconnecting ...");
                        disconnect();
                        connect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        disconnect();
        Log.d(TAG,"Connection thread ended");
    }

}
