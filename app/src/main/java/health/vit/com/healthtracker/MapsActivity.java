package health.vit.com.healthtracker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import health.vit.com.healthtracker.utilities.Constants;

import static health.vit.com.healthtracker.R.id.map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    public static final String TAG = "MAPS_ACTIVITY";
    private final static int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 0;
    private static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 007;
    private static int REQUEST_CHECK_SETTINGS = 1;
    private GoogleMap googleMap;
    private LocationRequest mLocationRequest;
    private Location location;
    private boolean circleSet = false;
    private LinearLayout ll_seekbar;
    private SeekBar radius_selector;
    private String currentLocationString = null;
    private String radius = "1000";
    private LatLng currentLocation;

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ll_seekbar = (LinearLayout) findViewById(R.id.ll_seekbar);
        radius_selector = (SeekBar) findViewById(R.id.radius_selector);
        radius_selector.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Toast.makeText(MapsActivity.this, "Radius set to " + progress + "kms", Toast.LENGTH_SHORT).show();
                radius = String.valueOf(progress) + "000"; // in meters
                findHospitals(currentLocationString, String.valueOf(radius));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ll_seekbar.setVisibility(View.GONE);
            }
        });
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** show the seekbar */
                showSeekBar();
            }
        });

        /** Map Init */
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);
    }

    private void showSeekBar() {
        final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(App.getGoogleSignInHelper().getApiClient(), builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        /** All location settings are satisfied. The client can initialize location requests here */
                        Log.i(TAG, "GPS is turned on.");
                        if (ll_seekbar.getVisibility() == View.GONE) {
                            ll_seekbar.setVisibility(View.VISIBLE);
                            ll_seekbar.animate().alpha(1.0f).translationX(0).setDuration(500);
                        } else {
                            ll_seekbar.animate().alpha(0.0f).translationX(-ll_seekbar.getWidth()).setDuration(500);
                            ll_seekbar.setVisibility(View.GONE);
                        }
                        currentLocation = new LatLng(getLocation().getLatitude(), getLocation().getLongitude());
                        currentLocationString = String.valueOf(getLocation().getLatitude()) + "," + String.valueOf(getLocation().getLongitude());
                        /** Add my location to Maps */
                        googleMap.addMarker(new MarkerOptions().title("My Location").position(currentLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        /** Location settings are not satisfied. But could be fixed by showing the user a dialog.*/
                        try {
                            /** Show the dialog by calling startResolutionForResult(),
                             and check the result in onActivityResult()*/
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        /** Location settings are not satisfied. However, we have no way to fix the
                         settings so we won't show the dialog.*/
                        break;
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        /** Do everything in else */
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            /** asking for permissions */
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
            return;
        } else {
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));

                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }

            map.setMyLocationEnabled(true);
            map.setBuildingsEnabled(true);
            map.setIndoorEnabled(true);
            turnOnGPS(radius);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                googleMap.addMarker(new MarkerOptions().title(place.getName().toString() + "  -  " + place.getRating() + "/5").position(place.getLatLng()).snippet(place.getAddress().toString()));
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(place.getLatLng()).tilt(30).zoom(18).build()));
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made
                    System.out.println("All good.");
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    /** Permission Granted */
                    googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
                    turnOnGPS(radius);
                    return;

                } else {
                    /** Permission Denied */
                    Toast.makeText(this, "Grant Access_Location permission to enable this feature.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_maps_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_autocomplete) {
            try {
                /** Right now,focusing on INDIA */
                AutocompleteFilter filter = new AutocompleteFilter.Builder().setCountry("IN").setTypeFilter(AutocompleteFilter.TYPE_FILTER_GEOCODE).build();

                Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(filter).build(MapsActivity.this);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * blue circle to place all markers within the required radius
     */
    private void addBoundary(LatLng currentLocation, double radius) {
        googleMap.clear();
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(currentLocation);
        circleOptions.strokeColor(Color.argb(200, 255, 100, 0));
        circleOptions.strokeWidth(4.0f);
        circleOptions.fillColor(0x550000ff);
        circleOptions.radius(radius);
        googleMap.addCircle(circleOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13));

    }

    private void turnOnGPS(final String radius) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(App.getGoogleSignInHelper().getApiClient(), builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        /** All location settings are satisfied. The client can initialize location requests here */
                        Log.i(TAG, "GPS is turned on.");
                        if (getLocation() != null) {
                            //Toast.makeText(MapsActivity.this, "Location = " + getLocation().getLatitude() + "," + getLocation().getLongitude(), Toast.LENGTH_SHORT).show();
                            currentLocation = new LatLng(getLocation().getLatitude(), getLocation().getLongitude());
                            currentLocationString = String.valueOf(getLocation().getLatitude()) + "," + String.valueOf(getLocation().getLongitude());
                            //System.out.println(currentLocationString);
                            /** Add my location to Maps */
                            googleMap.addMarker(new MarkerOptions().title("My Location").position(currentLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                            findHospitals(currentLocationString, radius);
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        /** Location settings are not satisfied. But could be fixed by showing the user a dialog.*/
                        try {
                            /** Show the dialog by calling startResolutionForResult(),
                             and check the result in onActivityResult()*/
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        /** Location settings are not satisfied. However, we have no way to fix the
                         settings so we won't show the dialog.*/
                        break;
                }
            }
        });
    }

    private void findHospitals(String currentLocationString, final String radius) {
        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + currentLocationString + "&radius=" + radius + "&sensor=true&key=" + Constants.MAPS_API_KEY + "&types=hospital";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String statusCode = response.getString("status");
                    if (statusCode.equals("OK")) {
                        /** add a radius boundary*/
                        addBoundary(currentLocation, Double.valueOf(radius));
                        JSONArray results = response.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            if (i == 12)
                                break;
                            JSONObject place = results.getJSONObject(i);
                            String place_name = place.getString("name");
                            String place_rating = "N/A";
                            try {
                                place_rating = place.getString("rating");
                            } catch (JSONException e) {
                                //System.out.println(e.getLocalizedMessage());
                            }
                            JSONObject place_geometry = place.getJSONObject("geometry");
                            JSONObject place_location = place_geometry.getJSONObject("location");
                            String latitude = place_location.getString("lat");
                            String longitude = place_location.getString("lng");
                            LatLng position = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                            googleMap.addMarker(new MarkerOptions().title(place_name).snippet(place_rating + "\u2605").position(position).icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(MapsActivity.this, R.drawable.ic_hospital_map))));
                        }
                    } else {
                        Log.i(TAG, statusCode);
                    }
                    googleMap.addMarker(new MarkerOptions().title("My Location").position(currentLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(request);
    }

    private Location getLocation() {
        GoogleApiClient mGoogleApiClient = App.getGoogleSignInHelper().getApiClient();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            /** asking for permissions */
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
            return null;
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            return location;
        }
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public void onLocationChanged(Location location) {
        setLocation(location);
    }
}
