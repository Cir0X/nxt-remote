package epro.hbrs.de.nxt_remote.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import epro.hbrs.de.nxt_remote.R;


public class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.ViewHolder> {

    private Context mContext;

    private List<String> devices;

    public DeviceRecyclerViewAdapter(List<String> devices, Context context) {
        mContext = context;
        this.devices = devices;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.deviceTextView.setText(devices.get(position));

        viewHolder.deviceCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Crouton.makeText((Activity) mContext, "Device " + devices.get(position) + " selected", Style.CONFIRM).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CardView deviceCardView;
        public TextView deviceTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            deviceCardView = (CardView) itemView.findViewById(R.id.card_view_device);
            deviceTextView = (TextView) itemView.findViewById(R.id.device_text_view);
        }
    }

}
