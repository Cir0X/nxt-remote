package epro.hbrs.de.nxt_remote;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.zerokol.views.JoystickView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import epro.hbrs.de.nxt_remote.base.Preferences;

public class MainActivity extends Activity {

    private Context mContext;
    private JoystickView joystick;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> pairedDevices;
//    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private OutputStream bluetoothOutputStream;
    private InputStream bluetoothInputStream;

    private boolean bluetoothConnected = false;

    private List<String> devices;
    private int selectedDevicePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        joystick = (JoystickView) findViewById(R.id.joystick_view);
        devices = new ArrayList<String>();
//        getPairedBluetoothDevices();

        joystick.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {
            @Override
            public void onValueChanged(int angle, int power, int direction) {
                if (!bluetoothConnected) {
                    Crouton.makeText((Activity) mContext,
                            getString(R.string.bluetooth_not_connected_alert), Style.ALERT).show();
                } else {

                    float amplification = 3.5f;
                    int leftDirection = ((int) (calcLeftDirection(angle, power) * amplification));
                    int rightDirection = ((int) (calcRightDirection(angle, power) * amplification));

                    if (leftDirection > 100) {
                        leftDirection = 100;
                    } else if (leftDirection < -100) {
                        leftDirection = -100;
                    }

                    if (rightDirection > 100) {
                        rightDirection = 100;
                    } else if (rightDirection < -100) {
                        rightDirection = -100;
                    }

                    Log.d("LEFT_DIRECTION ", " leftDirection:  " + leftDirection);
                    Log.d("RIGHT_DIRECTION", "rightDirection: " + rightDirection);

                    send((byte) 1, (byte) leftDirection);
                    send((byte) 2, (byte) rightDirection);
                }
            }
        }, JoystickView.DEFAULT_LOOP_INTERVAL);
    }

    private void checkForSelectedDevicePosition() {
        String position = Preferences.get(mContext, "SelectedDevicePosition");
        if (position != "EMPTY") {
            selectedDevicePosition = Integer.parseInt(position);
            connectToDevice(pairedDevices.get(selectedDevicePosition));
        }
    }

    private byte calcRightDirection(int angle, int power) {
        double x = (power * Math.sin(Math.toRadians(angle)));
        double y = (power * Math.cos(Math.toRadians(angle)));
//        Log.d("XY", "x: " + x + " y: " + y);
        return (byte) ((x + y) / 2);
    }

    private byte calcLeftDirection(int angle, int power) {
        double x = (power * Math.sin(Math.toRadians(angle)));
        double y = (power * Math.cos(Math.toRadians(angle)));
//        Log.d("XY", "x: " + x + " y: " + y);
        return (byte) ((y - x) / 2);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForSelectedDevicePosition();
    }

    @Override
    public void onPause() {
        super.onPause();
        Crouton.cancelAllCroutons();
    }

    private void getPairedBluetoothDevices() {
        pairedDevices = new ArrayList<BluetoothDevice>();
        devices = new ArrayList<String>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Crouton.makeText(this, getString(R.string.bluetooth_not_supported), Style.ALERT).show();
        } else {
            pairedDevices.addAll(bluetoothAdapter.getBondedDevices());

            // Add device names to devies list
            for (BluetoothDevice device : pairedDevices) {
                devices.add(device.getName());
//                bluetoothDevice = device;
//                break;
            }
        }
    }

    private void connectToDevice(BluetoothDevice bluetoothDevice) {
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();

            bluetoothOutputStream = bluetoothSocket.getOutputStream();
            bluetoothInputStream = bluetoothSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Crouton.makeText((Activity) mContext, "Connected to Device", Style.CONFIRM).show();
        bluetoothConnected = true;
    }

    public void send(byte motor, byte speed) {
        byte[] buffer = new byte[15];
        buffer[0] = 13;// message length
        buffer[1] = 0;  //

        buffer[2] = 0x00; // byte 0
        buffer[3] = 0x04;
        buffer[4] = motor; // engine b = 1   engine c = 2
        buffer[5] = speed; // speed range: -100 - 100

        buffer[6] = 1; //
        buffer[7] = 0;
        buffer[8] = 0;
        buffer[9] = 0x20;
        try {
            bluetoothOutputStream.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_connect) {
            getPairedBluetoothDevices(); // put it into the async task
            Log.d("intent", "" + devices.size());
            Bundle bundle = new Bundle();
            bundle.putStringArray("devices", devices.toArray(new String[devices.size()]));
            Intent intent = new Intent(this, ConnectActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}