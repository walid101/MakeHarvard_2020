package com.example.opencvarduinobluetoothrobotv1.ui.share;

import android.bluetooth.BluetoothAdapter;
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

public class ShareFragment extends Fragment implements View.OnClickListener {

    private ShareViewModel shareViewModel;
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
        Button onButton = textView.findViewById(R.id.BlueOn);
        Button offButton = textView.findViewById(R.id.BlueOff);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        onButton.setOnClickListener(new View.OnClickListener() {
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
            //registerReciever
        }
    }
    @Override
    public void onClick(View v) {

    }
}