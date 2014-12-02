package epro.hbrs.de.nxt_remote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.zerokol.views.JoystickView;
import com.zerokol.views.JoystickView;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MainActivity extends Activity {

    private Context mContext;
    private JoystickView joystick;

    private boolean bluetoothConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        joystick = (JoystickView) findViewById(R.id.joystick_view);

        joystick.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {
            @Override
            public void onValueChanged(int i, int i2, int i3) {
                if (!bluetoothConnected) {
                    Crouton.makeText((Activity) mContext,
                            getString(R.string.bluetooth_not_connected_alert), Style.ALERT).show();
                }
            }
        }, JoystickView.DEFAULT_LOOP_INTERVAL);
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
            startActivity(new Intent(this, ConnectActivity.class));
        } else if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
