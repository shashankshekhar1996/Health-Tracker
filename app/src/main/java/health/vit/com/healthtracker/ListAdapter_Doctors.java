package health.vit.com.healthtracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import health.vit.com.healthtracker.models.Doctors;


/**
 * Created by akshaymahajan on 22/01/17.
 */
public class ListAdapter_Doctors extends ArrayAdapter<Doctors> {

    TextView tv_name, tv_phone, tv_city;

    public ListAdapter_Doctors(Context context, int resource, List<Doctors> doctors) {
        super(context, resource, doctors);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item_view = convertView;
        if (item_view == null) {
            //LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LayoutInflater li = LayoutInflater.from(getContext());
            item_view = li.inflate(R.layout.list_layout_doctors, null);
        }

        Doctors doctors = getItem(position);

        if (doctors != null) {
            tv_name = (TextView) item_view.findViewById(R.id.tv_name);
            tv_phone = (TextView) item_view.findViewById(R.id.tv_phone);
            tv_city = (TextView) item_view.findViewById(R.id.tv_city);
        }

        if (tv_name != null) {
            tv_name.setText(doctors.getName());
        }
        if (tv_city != null) {
            tv_city.setText(doctors.getCity());
        }

        if (tv_phone != null) {
            tv_phone.setText(doctors.getPhone());
        }


        return item_view;
    }
}
