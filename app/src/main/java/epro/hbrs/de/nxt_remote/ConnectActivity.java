package epro.hbrs.de.nxt_remote;

import android.app.Activity;
import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.RotateAnimation;

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
import epro.hbrs.de.nxt_remote.adapter.DeviceRecyclerViewAdapter;


public class ConnectActivity extends Activity {

    private RecyclerView deviceRecyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;

    private List<String> devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            devices = Arrays.asList(extras.getStringArray("devices"));
        }
        Log.d("intent", "" + devices.size());

//        for (int i = 0; i <= 100; i++) {
//            devices.add("NXT " + ((Double) (Math.random() * 10)).intValue());
//        }
        initRecyclerView();
    }

    private void initRecyclerView() {
        deviceRecyclerView = (RecyclerView) findViewById(R.id.device_recycler_view);

        recyclerLayoutManager = new LinearLayoutManager(this);
        deviceRecyclerView.setLayoutManager(recyclerLayoutManager);

        recyclerAdapter = new DeviceRecyclerViewAdapter(devices, this);
        deviceRecyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        Crouton.cancelAllCroutons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
