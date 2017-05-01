package health.vit.com.healthtracker;

import android.animation.Animator;
import android.animation.ValueAnimator;
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
import android.widget.FrameLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CircleImageView iv_profile_pic;
    private TextView tv_profile_name, tv_profile_email;
    private TextView tv_heart;

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

        /** Show Stats button */
        FrameLayout btn_stats = (FrameLayout) findViewById(R.id.frame_stats);
        btn_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PulseDataView.class));
            }
        });

        //notifications

        App.getInstance().startNotifications();

        /** set User Profile details */

        View hView = navigationView.getHeaderView(0);
        iv_profile_pic = (CircleImageView) hView.findViewById(R.id.iv_profile_pic);
        tv_profile_name = (TextView) hView.findViewById(R.id.tv_profile_username);
        tv_profile_email = (TextView) hView.findViewById(R.id.tv_profile_email);

        tv_profile_email.setText(App.getInstance().getEMAIL_ADDRESS());
        tv_profile_name.setText(App.getInstance().getUSERNAME());
        downloadProfilePic(App.getInstance().getPROFILE_PIC_URL());

        tv_heart = (TextView) findViewById(R.id.tv_heart_rate_avg);

        /*final LottieAnimationView heart = (LottieAnimationView) findViewById(R.id.animation_view);
        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (heart.isAnimating()) {
                    heart.pauseAnimation();
                } else {
                    heart.playAnimation();
                }
            }
        });*/

        startCountAnimation();
    }

    private void startCountAnimation() {
        PulseData pd = new PulseData(MainActivity.this);
        pd.open();
        final int avg = (int) Math.floor(pd.getAvg());
        Log.i("anim", avg + "d");
        Map<String, String> mapList = pd.getMinMaxData();
        final int min = Integer.valueOf((mapList.get("minRate")));
        final int max = Integer.valueOf((mapList.get("maxRate")));
        String timeMin = mapList.get("minTime");
        String timeMax = mapList.get("maxTime");
        Timestamp ts_min = Timestamp.valueOf(timeMin);
        Timestamp ts_max = Timestamp.valueOf(timeMax);
        Date dateMin = new Date(ts_min.getTime());
        Date dateMax = new Date(ts_max.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
        final String formattedDateMin = sdf.format(dateMin);
        final String formattedDateMax = sdf.format(dateMax);
        final ValueAnimator animator = ValueAnimator.ofInt(0, avg);
        animator.setDuration(1000);
        animator.setStartDelay(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                tv_heart.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                tv_heart.animate().alpha(1.0f).setDuration(500);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ValueAnimator minValueAnimator = ValueAnimator.ofInt(0, min);
                minValueAnimator.setDuration(1000);
                minValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ((TextView) findViewById(R.id.tv_min)).setText(animation.getAnimatedValue().toString());
                        TextView tv_min = (TextView) findViewById(R.id.tv_time_min);
                        tv_min.setText(formattedDateMin);
                        tv_min.animate().alpha(1.0f).setDuration(500);
                    }
                });
                ValueAnimator maxValueAnimator = ValueAnimator.ofInt(min, max);
                maxValueAnimator.setDuration(1000);
                maxValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ((TextView) findViewById(R.id.tv_max)).setText(animation.getAnimatedValue().toString());
                        TextView tv_max = (TextView) findViewById(R.id.tv_time_max);
                        tv_max.setText(formattedDateMax);
                        tv_max.animate().alpha(1.0f).setDuration(500);
                    }
                });
                minValueAnimator.setStartDelay(200);
                maxValueAnimator.setStartDelay(200);
                minValueAnimator.start();
                maxValueAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
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
        if (id == R.id.action_manual) {
            startActivity(new Intent(MainActivity.this, PulseDataFetch.class));
            return true;
        } else if (id == R.id.action_record_data) {
            startActivity(new Intent(MainActivity.this, DeviceList.class));
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

        } else if (id == R.id.nav_reminders) {
            /** Go to Find Doctors Activity */
            startActivity(new Intent(MainActivity.this, ReminderActivity.class));

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

        //

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
