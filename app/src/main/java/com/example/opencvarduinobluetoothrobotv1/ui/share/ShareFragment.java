package com.example.opencvarduinobluetoothrobotv1.ui.share;

import android.app.ActionBar;
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
import androidx.fragment.app.FragmentActivity;
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

public class ShareFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    BluetoothAdapter bluetoothAdapter;
    private String mConnectedDeviceName = null;
    BluetoothAdapter mBluetoothAdapter = null;
    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;
    private TextView textView = null; //used to be final
    private BluetoothChatService mChatService = null;
    private ShareViewModel shareViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareViewModel = ViewModelProviders.of(this).get(ShareViewModel.class);
        View root = inflater.inflate(R.layout.fragment_share, container, false);
        textView = root.findViewById(R.id.blueOps);
        shareViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        Button onOffButton = root.findViewById(R.id.BlueOn);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter = bluetoothAdapter;
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
                try {
                    sendMessage("1");
                } catch (Exception e) {
                    Snackbar snac = Snackbar.make(textView.findViewById(R.id.blueOps), "BlueTooth Chat Service is null (most likely), not paired",
                            Snackbar.LENGTH_LONG);
                    snac.show();
                }
            }
        });
        //The following code is a test to see if the system can communicate via blutooth currently


        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }


    }

    public void setupChat()
    {
        mChatService = new BluetoothChatService(getActivity(), mHandler);
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
            //Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            Snackbar snac = Snackbar.make(textView.findViewById(R.id.blueOps),"NOT CONNECTED TO BLUTOOTH",
                    Snackbar.LENGTH_LONG);
            snac.show();
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
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        /*
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                                */

                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        /*
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                                */

                    }
                    break;
            }
        }
    };
    private void setStatus(int resId) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }
    private void setStatus(CharSequence subTitle) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }
}