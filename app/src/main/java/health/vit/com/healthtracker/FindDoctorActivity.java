package health.vit.com.healthtracker;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import health.vit.com.healthtracker.adapters.RecyclerAdapter_Doctors;
import health.vit.com.healthtracker.models.Doctors;
import health.vit.com.healthtracker.utilities.Dialog_Doctor;
import health.vit.com.healthtracker.utilities.RecyclerViewClickListener;

public class FindDoctorActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    SearchView searchView;
    MenuItem searchMenuItem;
    int filter_choice = -1;
    private ProgressBar progressBar;
    private ArrayList<Doctors> doctors_list;
    private Doctors doctor;
    private Dialog_Doctor dialog;
    private RecyclerAdapter_Doctors adapter_doctors;
    private RecyclerView doctors_recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_doctor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        doctors_recyclerview = (RecyclerView) findViewById(R.id.rv_doctors);
        doctors_recyclerview.setHasFixedSize(true);

        //layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FindDoctorActivity.this);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        doctors_recyclerview.setLayoutManager(linearLayoutManager);

        doctors_list = new ArrayList<>();
        adapter_doctors = new RecyclerAdapter_Doctors(FindDoctorActivity.this, doctors_list, new RecyclerViewClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Doctors doctor = doctors_list.get(position);
                Log.i("Doctor", doctor.getName());

                dialog = Dialog_Doctor.newInstance(doctor);
                dialog.show(getFragmentManager(), "Dialog_Doctor");
            }
        });

        doctors_recyclerview.setAdapter(adapter_doctors);
        callApi();

    }

    public void dismissDialog1() {
        dialog.dismiss();
    }

    private void callApi() {
        doctors_list.clear();
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
                        doctor = new Doctors(Integer.valueOf(obj.get("id").toString()),
                                obj.get("name").toString(), obj.get("description").toString(), obj.get("phone").toString(),
                                obj.get("city").toString(), obj.get("address").toString());
                        doctors_list.add(doctor);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter_doctors.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                doctors_recyclerview.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERROR", error.getMessage());
            }
        });
        requestQueue.add(request);
    }

    private void filterBy(String category, String query) {
        doctors_list.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(FindDoctorActivity.this);
        String filter_doctors_url = getString(R.string.filter_doctor_url) + category + "=" + query;

        doctors_recyclerview.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
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
                        doctor = new Doctors(Integer.valueOf(obj.get("id").toString()),
                                obj.get("name").toString(), obj.get("description").toString(), obj.get("phone").toString(),
                                obj.get("city").toString(), obj.get("address").toString());
                        doctors_list.add(doctor);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter_doctors.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                doctors_recyclerview.setVisibility(View.VISIBLE);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.menu_item_search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Find by name or city..");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        if (item.getItemId() == R.id.menu_item_filter) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(FindDoctorActivity.this);
            builder.setTitle("Filter by").setSingleChoiceItems(R.array.filter_find_doctor, filter_choice, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    filter_choice = which;
                    /** set filter category here: 0 = city, 1 = name */
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (searchView.isShown()) {
            searchMenuItem.collapseActionView();
            searchView.setQuery("", false);
        }
        if (filter_choice == 0) {
            filterBy("city", query);
        } else if (filter_choice == 1) {
            filterBy("name", query);
        } else {
            Toast.makeText(this, "Please choose a category", Toast.LENGTH_LONG).show();
        }

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}