package com.example.remotejoystick;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class ListBTDevices extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {
    RecyclerView devicelist;

    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";
    protected MyRecyclerViewAdapter adapter;
    ArrayList<String> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_btdevices);
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if ( myBluetooth==null ) {
            Toast.makeText(getApplicationContext(),
                    this.getString(R.string.BT_no_bt_device),
                    Toast.LENGTH_LONG).show();
            finish();
        } else if ( !myBluetooth.isEnabled() ) {
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1);
            finish();
        }
        devicelist = findViewById(R.id.devices);
        devicelist.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MyRecyclerViewAdapter(this, list);
        devicelist.setAdapter(adapter);

        pairedDevicesList();
        adapter.setClickListener(this);
    }

    private void pairedDevicesList () {
        try {
            pairedDevices = myBluetooth.getBondedDevices();

            if ( pairedDevices.size() > 0 ) {
                for ( BluetoothDevice bt : pairedDevices ) {
                    list.add(bt.getName().trim() + "\n" + bt.getAddress().trim());
                    Log.d("List",bt.getName().trim() + "\n" + bt.getAddress().trim());
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        this.getString(R.string.BT_no_paired), Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    this.getString(R.string.BT_not_available), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onItemClick(View view, int position) {
        String info = adapter.getItem(position);
        String address = info.substring(info.length()-17);
        String name = info.substring(0, info.length()-17).trim();

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(EXTRA_ADDRESS, address);
        i.putExtra("NAME", name);

        Log.d("Address", address);
        Toast.makeText(this,
                String.format(this.getString(R.string.BT_connecting_to), name ,address),
                Toast.LENGTH_SHORT).show();

        startActivity(i);
        finish();

    }
}
