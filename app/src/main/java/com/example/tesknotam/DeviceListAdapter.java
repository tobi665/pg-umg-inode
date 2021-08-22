package com.example.tesknotam;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater mlayoutInflater;
    private ArrayList<BluetoothDevice> mDevices;
    private Map<BluetoothDevice, Integer> mbluetoothDevicesAndRSSI;
    private int  mviewResourceId;
    public static final String[] iNodeAddresses = { "D0:F0:18:44:0C:4B" };


    public DeviceListAdapter(Context context, int textViewResourceId, ArrayList<BluetoothDevice> devices, Map<BluetoothDevice, Integer> bluetoothDevicesAndRSSI){
        super(context, textViewResourceId, devices);
        this.mDevices = devices;
        this.mbluetoothDevicesAndRSSI = bluetoothDevicesAndRSSI;
        mlayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mviewResourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mlayoutInflater.inflate(mviewResourceId, null);

        BluetoothDevice device = mDevices.get(position);

        if (device != null) {
//            TextView textViewDeviceName = (TextView) convertView.findViewById(R.id.textView_deviceName);

            TextView textViewDeviceAddress = (TextView) convertView.findViewById(R.id.textView_deviceAddress);
            TextView textViewDeviceRSSI = (TextView) convertView.findViewById(R.id.textView_deviceRSSI);

//            if (textViewDeviceName != null) {
//                textViewDeviceName.setText(device.getName());
//            }

            if (textViewDeviceAddress != null) {
                String deviceAddress = device.getAddress().toUpperCase();
                if (Arrays.asList(iNodeAddresses).contains(deviceAddress)) {
                    // found a match to "software"
                    textViewDeviceAddress.setText(deviceAddress);
                    Integer deviceRSSI = mbluetoothDevicesAndRSSI.get(device);
                    if (deviceRSSI != null) {
                        textViewDeviceRSSI.setText(deviceRSSI.toString());
                    }
                }
            }
        }

        return convertView;
    }

//    public Map<BluetoothDevice, Integer> lookForInodes() {
//
//    }

}