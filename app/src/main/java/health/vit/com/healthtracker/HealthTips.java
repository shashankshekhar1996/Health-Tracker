package health.vit.com.healthtracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import static health.vit.com.healthtracker.R.id.toolbar;

public class HealthTips extends AppCompatActivity {

    private String[] healthTipsHeading = {};//getResources().getStringArray(R.array.heading); //{"Drink more Water today.","Drink more Water today.","Drink more Water today.","Drink more Water today."};
    private String[] healthTipsPara = {}; //getResources().getStringArray(R.array.paras);//{"Drink more Water today.","Drink more Water today.","Drink more Water today.","Drink more Water today."};
    ListView lv;
  //  AlarmManager am;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_tips);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       // am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //setOneTimeAlarm();

        healthTipsHeading = getResources().getStringArray(R.array.heading);
        healthTipsPara = getResources().getStringArray(R.array.paras);


        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(new ArrayAdapter<>(HealthTips.this, android.R.layout.simple_list_item_1 , healthTipsHeading));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clicked = healthTipsHeading[position];
                Intent intent = new Intent(HealthTips.this, HealthTipsInfo.class);
                intent.putExtra("head", healthTipsHeading[position]);
                intent.putExtra("para", healthTipsPara[position]);
                startActivity(intent);
            }
        });


        //setListAdapter(new ArrayAdapter<String>(HealthTips.this, android.R.layout.i , healthTipsHeading));

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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

/*
    public void setOneTimeAlarm() {
        Intent intent = new Intent(HealthTips.this, AlarmTime.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (5), pendingIntent);
    }


    public void setRepeatingAlarm() {
        Intent intent = new Intent(HealthTips.this, AlarmTime.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (5 * 1000), pendingIntent);
    }
*/

    /*

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String clicked = healthTipsHeading[position];
        Intent intent = new Intent(this, HealthTipsInfo.class);
        intent.putExtra("head", healthTipsHeading[position]);
        intent.putExtra("para", healthTipsPara[position]);
        startActivity(intent);
    }*/
}
