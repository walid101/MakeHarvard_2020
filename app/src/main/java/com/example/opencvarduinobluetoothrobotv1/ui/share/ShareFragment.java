package com.example.opencvarduinobluetoothrobotv1.ui.share;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.opencvarduinobluetoothrobotv1.R;

import static android.content.ContentValues.TAG;
import android.content.BroadcastReceiver;
import android.bluetooth.BluetoothDevice;
public class ShareFragment extends Fragment implements View.OnClickListener {

    private ShareViewModel shareViewModel;
    private final BroadcastReceiver BroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "BroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "BroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "BroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };
    public BluetoothAdapter bluetoothAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareViewModel =
                ViewModelProviders.of(this).get(ShareViewModel.class);
        View root = inflater.inflate(R.layout.fragment_share, container, false);
        final TextView textView = root.findViewById(R.id.BlueTooth);
        shareViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        Button onOffButton = textView.findViewById(R.id.BlueOn);
        Button offButton = textView.findViewById(R.id.BlueOff);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        onOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableDisableBT();
            }
        });

        return root;
    }
    public void enableDisableBT()
    {
        if(bluetoothAdapter == null)
        {
            Log.d(TAG, "bluetoothAdapter does not have bluetooth capabilties");
        }
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);

            IntentFilter STfilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            Log.d(TAG,"blutooth is enabled");
        }
        if(bluetoothAdapter.isEnabled())
        {
            Log.d(TAG,"blutooth is disabled");
            bluetoothAdapter.disable();

        }
    }
    @Override
    public void onClick(View v) {

    }
}