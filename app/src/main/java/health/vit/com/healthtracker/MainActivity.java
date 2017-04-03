package health.vit.com.healthtracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CircleImageView iv_profile_pic;
    private TextView tv_profile_name, tv_profile_email;
    private BroadcastReceiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupDrawer();
    }

    private void setupDrawer() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //notifications

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");

        PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),60000, broadcast);
        Log.i("Here","mainactivity");

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
       // client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        View hView = navigationView.getHeaderView(0);
        iv_profile_pic = (CircleImageView) hView.findViewById(R.id.iv_profile_pic);
        tv_profile_name = (TextView) hView.findViewById(R.id.tv_profile_username);
        tv_profile_email = (TextView) hView.findViewById(R.id.tv_profile_email);

        tv_profile_email.setText(App.getInstance().getEMAIL_ADDRESS());
        tv_profile_name.setText(App.getInstance().getUSERNAME());
        downloadProfilePic(App.getInstance().getPROFILE_PIC_URL());

        final LottieAnimationView heart = (LottieAnimationView) findViewById(R.id.animation_view);
        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (heart.isAnimating()) {
                    heart.pauseAnimation();
                } else {
                    heart.playAnimation();
                }
            }
        });
    }


    private void downloadProfilePic(String url) {

        System.out.println(App.getInstance().getPROFILE_PIC_URL());

        Picasso.with(MainActivity.this).load(url).fit().placeholder(R.drawable.ic_person).error(R.drawable.ic_person)
                .into(iv_profile_pic, new Callback() {
            @Override
            public void onSuccess() {
                System.out.println("Loaded from internet");
                iv_profile_pic.setImageTintList(null);
                iv_profile_pic.setAlpha(1.0f);
            }

            @Override
            public void onError() {
                System.out.println("Could not fetch");
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finishAffinity();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            //Intent i = new Intent("")
            startActivity(new Intent(MainActivity.this, HealthTips.class));

        } else if (id == R.id.nav_doctors) {
            /** Go to Find Doctors Activity */
            startActivity(new Intent(MainActivity.this, FindDoctorActivity.class));

        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(MainActivity.this, DeviceList.class));


        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(MainActivity.this, PulseDataView.class));


        } else if (id == R.id.nav_maps) {
            /** Maps Activity*/
            startActivity(new Intent(MainActivity.this, MapsActivity.class));

        } else if (id == R.id.nav_settings) {
            /** Preferences Screen */
            startActivity(new Intent(MainActivity.this, PreferencesScreen.class));
        } //else if (id == R.id.nav_share) {

        //}
            else if (id == R.id.nav_send) {

            startActivity(new Intent(MainActivity.this, PulseDataFetch.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
