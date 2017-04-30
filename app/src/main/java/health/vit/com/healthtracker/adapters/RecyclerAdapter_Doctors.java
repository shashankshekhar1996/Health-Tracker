package health.vit.com.healthtracker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import health.vit.com.healthtracker.R;
import health.vit.com.healthtracker.models.Doctors;
import health.vit.com.healthtracker.utilities.RecyclerViewClickListener;

/**
 * Created by akshaymahajan on 26/01/17.
 */

public class RecyclerAdapter_Doctors extends RecyclerView.Adapter<RecyclerAdapter_Doctors.ViewHolder> {

    Context context;
    private ArrayList<Doctors> doctors_arraylist;
    private RecyclerViewClickListener listener;

    public RecyclerAdapter_Doctors(Context context, ArrayList<Doctors> doctors_arraylist, RecyclerViewClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.doctors_arraylist = doctors_arraylist;
    }

    @Override
    public RecyclerAdapter_Doctors.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /** find View and pass them */
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout_doctors, parent, false);
        final ViewHolder viewHolder = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter_Doctors.ViewHolder holder, int position) {
        /** set Text in textviews */
        holder.tv_name.setText(doctors_arraylist.get(position).getName());
        holder.tv_desc.setText(doctors_arraylist.get(position).getDesc());
        holder.tv_phone.setText(doctors_arraylist.get(position).getPhone());
        holder.tv_city.setText(doctors_arraylist.get(position).getCity());
        holder.tv_address.setText(doctors_arraylist.get(position).getAddress());
        holder.tv_timings.setText(doctors_arraylist.get(position).getTimings());
    }

    @Override
    public int getItemCount() {
        return doctors_arraylist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, tv_phone, tv_city, tv_address, tv_desc, tv_timings;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_desc = (TextView) itemView.findViewById(R.id.tv_desc);
            tv_phone = (TextView) itemView.findViewById(R.id.tv_phone);
            tv_city = (TextView) itemView.findViewById(R.id.tv_city);
            tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            tv_timings = (TextView) itemView.findViewById(R.id.tv_timings);
        }
    }

}
