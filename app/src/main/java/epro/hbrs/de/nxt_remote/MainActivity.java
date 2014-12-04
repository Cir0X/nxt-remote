package epro.hbrs.de.nxt_remote;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.zerokol.views.JoystickView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MainActivity extends Activity {

    private Context mContext;
    private JoystickView joystick;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private OutputStream bluetoothOutputStream;
    private InputStream bluetoothInputStream;

    private boolean bluetoothConnected = false;

    private List<String> devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        joystick = (JoystickView) findViewById(R.id.joystick_view);
        devices = new ArrayList<String>();
        getPairedBluetoothDevices();

        joystick.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {
            @Override
            public void onValueChanged(int angle, int power, int direction) {
                Log.d("JoyStick", angle + " " + power + " " + direction);
                if (!bluetoothConnected) {
                    Crouton.makeText((Activity) mContext,
                            getString(R.string.bluetooth_not_connected_alert), Style.ALERT).show();
                } else {
                    send(0, 0);
                }
            }
        }, JoystickView.DEFAULT_LOOP_INTERVAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        Crouton.cancelAllCroutons();
    }

    private void getPairedBluetoothDevices() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Crouton.makeText(this, getString(R.string.bluetooth_not_supported), Style.ALERT).show();
        } else {
            pairedDevices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : pairedDevices) {
                devices.add(device.getName());
                bluetoothDevice = device;
                break;
            }

            try {
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothSocket.connect();

                bluetoothOutputStream = bluetoothSocket.getOutputStream();
                bluetoothInputStream = bluetoothSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bluetoothConnected = true;
        }
    }

    public void send(int motor, int speed) {
        byte[] buffer = new byte[15];
        buffer[0] = 13; // message length
        buffer[1] = 0;  //

        buffer[2] = 0; // byte 0
        buffer[3] = 0x04;
        buffer[4] = 2; // engine b = 1   engine c = 2
        buffer[5] = 100; // speed range: -100 - 100

        buffer[6] = 1; //
        buffer[9] = 1;
        try {
            Log.d("send", "" + buffer);
            bluetoothOutputStream.write(buffer);
        } catch (IOException e) {
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