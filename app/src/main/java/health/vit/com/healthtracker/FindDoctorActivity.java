package health.vit.com.healthtracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FindDoctorActivity extends AppCompatActivity {

    private ListView doctors_listview;
    private ListAdapter_Doctors adapter;
    private ArrayList<Doctors> doctors_list;
    private Doctors doctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_doctor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        doctors_listview = (ListView) findViewById(R.id.lv_doctors);
        doctors_list = new ArrayList<>();
        adapter = new ListAdapter_Doctors(FindDoctorActivity.this, R.layout.list_layout_doctors, doctors_list);
        doctors_listview.setAdapter(adapter);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        RequestQueue requestQueue = Volley.newRequestQueue(FindDoctorActivity.this);
        String filter_doctors_url = getString(R.string.filter_doctor_url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, filter_doctors_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //Log.i("SUCCESS",response.get("details").toString());
                    JSONArray details = (JSONArray) response.get("details");
                    for (int i = 0; i < details.length(); i++) {
                        JSONObject obj = details.getJSONObject(i);
                        String doctor_name = obj.get("name").toString();
                        Log.i("MESSAGE", doctor_name);
                        doctor = new Doctors(Integer.valueOf(obj.get("id").toString()), obj.get("name").toString(), obj.get("phone").toString(), obj.get("city").toString());
                        doctors_list.add(doctor);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERROR", error.getMessage());
            }
        });
        requestQueue.add(request);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}