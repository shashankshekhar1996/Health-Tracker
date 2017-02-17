package health.vit.com.healthtracker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

import static health.vit.com.healthtracker.R.id.avg;

public class PulseTable extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private String from;
    private String to;
    private TableLayout tableLayout;
    private TextView textView;
    private TextView msg;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulse_table);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        to = intent.getStringExtra("to");
        textView = (TextView) findViewById(avg);
        msg = (TextView) findViewById(R.id.msg);
        tableLayout = (TableLayout) findViewById(R.id.tabLayout);
        spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spin_data, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
       // spinner.setBackgroundColor(Color.LTGRAY);
       // spinner.setBackgroundColor(Color.rgb(255,0,0));
        spinner.setOnItemSelectedListener(this);
        getAvg();
        buildTable("DATETIMEASC");
    }

    private void getAvg(){
        PulseData pd = new PulseData(PulseTable.this);
        pd.open();
        Double avg = pd.getAvg();
        pd.close();
        String avgText = String.valueOf(avg);
        String average = "Your Average Heart rate for last few readings is " + avgText + ".";
        String message = "";

       // textView.setText("Your Average Heart rate for last few readings is " + result + ".");
        if(avg>=60 && avg<=100){
            message += "Your heart rate is in normal range. You have healthy heart rate";
        }else{
            message += "Your heart rate is not in normal range. You may be prone to health risks";
        }

        textView.setText(average);
        msg.setText(message);
    }

    private void buildTable(String str) {

        tableLayout.removeAllViews();
        PulseData pd = new PulseData(PulseTable.this);
        pd.open();
        LinkedHashMap<String, Integer> map = pd.getAllData(from,to,str);
        pd.close();

        ArrayList<Integer> pulse = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();

        for(String params : map.keySet()) {
            pulse.add(map.get(params));
        }

        for(String params : map.keySet()) {
            time.add(params);
        }
        Integer i = 0;
        Integer j = 0;
        //  int flag=0;


        TableRow row0= new TableRow(this);
        TableRow.LayoutParams lp0 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        row0.setLayoutParams(lp0);
        TextView sno = new TextView(this);
        TextView rate = new TextView(this);
        TextView d = new TextView(this);
        TextView t = new TextView(this);

        sno.setText("S.No.");
        sno.setTypeface(null, Typeface.BOLD);
        sno.setTextColor(Color.WHITE);
        row0.addView(sno,0);
        rate.setText("PulseRate");
        rate.setTypeface(null, Typeface.BOLD);
        rate.setTextColor(Color.WHITE);
        row0.addView(rate,1);
        d.setText("Date");
        d.setTypeface(null, Typeface.BOLD);
        d.setTextColor(Color.WHITE);
        row0.addView(d,2);
        t.setText("Time");
        t.setTypeface(null, Typeface.BOLD);
        t.setTextColor(Color.WHITE);
        row0.addView(t,3);
        row0.setBackgroundColor(Color.BLACK);
        tableLayout.addView(row0,j++);

        for(String params :map.keySet()) {

            TableRow row= new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            row.setLayoutParams(lp);
            TextView tvid = new TextView(this);
            TextView tvpulse = new TextView(this);
            TextView tvdate = new TextView(this);
            TextView tvtime = new TextView(this);

            tvid.setText(String.valueOf(i++));
            tvid.setTextColor(Color.BLACK);
            row.addView(tvid,0);

            tvpulse.setText(String.valueOf(map.get(params)));
            tvpulse.setTextColor(Color.BLACK);
            row.addView(tvpulse,1);

            Timestamp ts = Timestamp.valueOf(params);
            Date date = new Date(ts.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = sdf.format(date);

            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
            String formattedTime = sdf1.format(date);
           /* Date date = null;
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            Long n = ts.getTime();
            try {
                 date = format.parse(ts.toString());
            }catch (Exception e){
                e.printStackTrace();
            }*/

            tvdate.setText(formattedDate);
            tvdate.setTextColor(Color.BLACK);
            row.addView(tvdate,2);

            tvtime.setText(formattedTime);
            tvtime.setTextColor(Color.BLACK);
            row.addView(tvtime,3);



            if(i%2==0)
                row.setBackgroundColor(Color.GRAY);//(Color.rgb(102,178,255));
            else
                row.setBackgroundColor(Color.LTGRAY);//(Color.rgb(204,229,255));
            tableLayout.addView(row,j++);
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(position == 0){
            buildTable("DATETIMEASC");
        }else if(position == 1){
            buildTable("PULSEASC");
        }else if(position == 2){
            buildTable("DATETIMEDESC");
        }else{
            buildTable("PULSEDESC");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}



