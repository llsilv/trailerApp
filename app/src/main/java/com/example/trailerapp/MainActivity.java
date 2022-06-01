package com.example.trailerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;



public class MainActivity extends AppCompatActivity {

    private BluetoothDevice deviceBT;
    private final String DEVICE_NAME = "HC-06";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private AcceptThread mAcceptThread;
    TextView rr;
    TextView rl;
    TextView b1;
    TextView b2;
    TextView f1;
    TextView f2;
    TextView il;
    TextView ir;
    TextView test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rr = findViewById(R.id.rr);
        rl = findViewById(R.id.rl);
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        f1 = findViewById(R.id.f1);
        f2 = findViewById(R.id.f2);
        il = findViewById(R.id.il);
        ir = findViewById(R.id.ir);
        test = findViewById(R.id.test);
        illuminateTrailer("000000");
        //111111 000000


        //bt set up on device
        BluetoothManager ManageBT = getSystemService(BluetoothManager.class);
        BluetoothAdapter AdapterBT = ManageBT.getAdapter();
        //test if bt is supported on device

        if (AdapterBT == null) {
            //bt not supported
            Toast.makeText(getApplicationContext(), "Bluetooth not supported", Toast.LENGTH_SHORT).show();
        }
        if (!AdapterBT.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Please enable Bluetooth", Toast.LENGTH_SHORT).show();
        }
        while (!AdapterBT.isEnabled()) {
        }

        Set<BluetoothDevice> pairedDevices = AdapterBT.getBondedDevices();
        if (pairedDevices.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please pair the device first", Toast.LENGTH_SHORT).show();
        } else {
            for (BluetoothDevice iterator : pairedDevices) {

                /*if(iterator.getAddress().equals(DEVICE_ADDRESS))
                {
                deviceBT=iterator;
                break;
                }
                Log.d("tester", "iterator");
                if (iterator.getName().equals(DEVICE_NAME)) {
                    deviceBT = iterator;
                    break;
                }*/
            }

        }
        //AcceptThread myConnection = new AcceptThread(deviceBT);
        //myConnection.start();
    }

    public void illuminateTrailer(String input_string ) {

        //test.setText(input_string);

        if (input_string.charAt(0) == '1') rr.setBackgroundColor(Color.parseColor("#FF0000"));
        else rr.setBackgroundColor(Color.parseColor("#990000"));
        if (input_string.charAt(1) == '1') rl.setBackgroundColor(Color.parseColor("#FF0000"));
        else rl.setBackgroundColor(Color.parseColor("#990000"));
        if (input_string.charAt(2) == '1')  {
            b1.setBackgroundColor(Color.parseColor("#FF0000"));
            b2.setBackgroundColor(Color.parseColor("#FF0000"));
        }
        else {
            b1.setBackgroundColor(Color.parseColor("#990000"));
            b2.setBackgroundColor(Color.parseColor("#990000"));
        }
        if (input_string.charAt(3) == '1')  {
            f1.setBackgroundColor(Color.parseColor("#FF0000"));
            f2.setBackgroundColor(Color.parseColor("#FF0000"));
        }
        else {
            f1.setBackgroundColor(Color.parseColor("#990000"));
            f2.setBackgroundColor(Color.parseColor("#990000"));
        }
        if (input_string.charAt(4) == '1') il.setBackgroundColor(Color.parseColor("#FFFF00"));
        else il.setBackgroundColor(Color.parseColor("#FF9900"));
        if (input_string.charAt(5) == '1') ir.setBackgroundColor(Color.parseColor("#FFFF00"));
        else ir.setBackgroundColor(Color.parseColor("#FF9900"));
    }


    // connect to server
    class AcceptThread extends Thread {
        private final BluetoothSocket mmSocket;

        AcceptThread(BluetoothDevice mmDevice) {
            BluetoothSocket temp = null;

            try {
                temp = mmDevice.createRfcommSocketToServiceRecord(PORT_UUID);

            } catch (IOException e) {
                Log.d("error", "temp bluetooth adapter error");

            }
            mmSocket = temp;
            Log.d("tester", "1234");
            run();

        }

        public void run() {
            BluetoothSocket socket = null;
            Log.d("tester", "before myDataTransfer.start");
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                Log.d("error", "mmSocket connect");
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.d("error", "close socket");
                }
                return;
            }

            TransferData myDataTransfer = new TransferData(mmSocket);
            myDataTransfer.start();
            Log.d("tester", "after myDataTransfer.start");
        }

        public void cancel() {
            try {

                mmSocket.close();
            } catch (IOException e) {
                Log.d("error", "socket cancel error");
            }
        }

    }

    private class TransferData extends Thread {
        //
        //
        final BluetoothSocket mmSocket;
        final InputStream mmIncData;
        //private MainActivity main;

        TransferData(BluetoothSocket socket) {
            //this.main = main;
            mmSocket = socket;
            InputStream tmpIn = null;

            try {
                tmpIn = socket.getInputStream();
                Log.d("tester1", "after getInputStream");
            } catch (IOException e) {
                Log.d("hahahahhaa", "getInput error");
                Log.d("tester1", "catch of getInputStream");
            }
            mmIncData = tmpIn;
            Log.d("tmpIn", "entered before run");
        }


        public void run() {

            Log.d("tester", "entered BUFFER");
            byte[] buffer = new byte[1024];
            int begin = 0;
            int ByteCount = 0;
            String string = "";
            while (true) {
                try {

                    begin = 0;
                    ByteCount += mmIncData.read(buffer, ByteCount, buffer.length - ByteCount);

                    for (int i = begin; i < ByteCount; i++) {
                        if (buffer[i] == "#".getBytes()[0]) {
                            byte[] inc = (byte[]) buffer;
                            string = new String(inc, 0, i);
                            Log.d("insider", "run: " + string);

                            //test.setText(string);
                            illuminateTrailer(string);


                            ByteCount = 0;
                        }
                    }
                } catch (IOException e) {
                    Log.d("tester", "run: loop crash");
                    //break;
                }
            }

        }
    }
}
