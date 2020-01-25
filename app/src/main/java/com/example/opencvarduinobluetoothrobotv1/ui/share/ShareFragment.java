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
import com.google.android.material.snackbar.Snackbar;

import static android.content.ContentValues.TAG;
import static android.net.wifi.p2p.WifiP2pDevice.CONNECTED;

import android.content.BroadcastReceiver;
import android.bluetooth.BluetoothDevice;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class ShareFragment extends Fragment implements View.OnClickListener
{
    BluetoothAdapter bluetoothAdapter;
    private String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    private BluetoothChatService mChatService = null;
    private ShareViewModel shareViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareViewModel = ViewModelProviders.of(this).get(ShareViewModel.class);
        View root = inflater.inflate(R.layout.fragment_share, container, false);
        final TextView textView = root.findViewById(R.id.blueOps);
        shareViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
            Button onOffButton = root.findViewById(R.id.BlueOn);
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            onOffButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enableDisableBT();
                    if (!bluetoothAdapter.isEnabled()) {

                        Snackbar snackBar = Snackbar.make(textView.findViewById(R.id.blueOps),
                                R.string.turned_on, Snackbar.LENGTH_LONG);
                        snackBar.show();


                    } else if (bluetoothAdapter.isEnabled()) {

                        Snackbar snackBar = Snackbar.make(textView.findViewById(R.id.blueOps),
                                R.string.turned_off, Snackbar.LENGTH_LONG);
                        snackBar.show();


                    }
                }
            });
            Button blueTest = root.findViewById(R.id.BlueTest);
            blueTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("1");
            }
        });
        //The following code is a test to see if the system can communicate via blutooth currently


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

    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            //mOutStringBuffer.setLength(0);

        }
    }
}