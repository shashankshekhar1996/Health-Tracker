package health.vit.com.healthtracker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

public class PulseGraph extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

   // String[] quarters;
    ArrayList<String> quarters;
    private BarChart barChart;
    private TextView textView;
    private TextView msg;
    private String from;
    private String to;
    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulse_graph);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenHeight = size.y;

        LinearLayout layout1 = (LinearLayout)findViewById(R.id.layout1);
        spinner = (Spinner) findViewById(R.id.spinner);
        textView = (TextView) findViewById(R.id.avg);
        msg = (TextView) findViewById(R.id.msg);
       // LinearLayout layout2 = (LinearLayout findViewById(R.id.layout2);

        ViewGroup.LayoutParams params1 = layout1.getLayoutParams();
        params1.height = screenHeight;// - getStatusBarHeight();

        //ViewGroup.LayoutParams params2 = layout2.getLayoutParams();
       // params2.height = screenHeight;


        barChart = (BarChart) findViewById(R.id.barGraph);
        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        to = intent.getStringExtra("to");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spin_data, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        // spinner.setBackgroundColor(Color.LTGRAY);
        // spinner.setBackgroundColor(Color.rgb(255,0,0));
        spinner.setOnItemSelectedListener(this);
        getAvg();
        plotGraph("DATETIMEASC");


    }
    private void getAvg(){
        PulseData pd = new PulseData(PulseGraph.this);
        pd.open();
        Double avg = pd.getAvg();
        //List<Map<String, Integer>> map = pd.getMinMaxData();
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

    private void plotGraph(String str) {
        barChart.removeAllViews();
        PulseData pd = new PulseData(PulseGraph.this);
        pd.open();
        LinkedHashMap<String, Integer> map = pd.getAllData(from,to,str);
        pd.close();
        if(map.size()==0){
            startActivity(new Intent(PulseGraph.this,DataError.class));
        }
        ArrayList<BarEntry> pulse = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();
        Integer i;
        i=0;
        for(String params : map.keySet()){
            pulse.add(new BarEntry(i++,map.get(params)));
        }
        BarDataSet dataset = new BarDataSet(pulse,"data");
        dataset.setColor(Color.BLUE);



        BarData finalData = new BarData(dataset);
        //finalData.setBarWidth(0.9f); // set custom bar width
       // barChart.setData(data);

     // = new String[] { "Q1", "Q2", "Q3", "Q4" };
        //quarters = new String[50];
        quarters = new ArrayList<>();
        i=0;

        for(String params : map.keySet()){
            Timestamp ts = Timestamp.valueOf(params);
            Date date = new Date(ts.getTime());

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = sdf.format(date);

            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
            String formattedTime = sdf1.format(date);
            quarters.add(formattedDate + " / " + formattedTime);
        }


        /*for(String params : map.keySet()){
            quarters[i++] = params;
            //time.add(params);
        }*/

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters.get((int) value);
            }

        };

        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(90);

        YAxis leftAxis = barChart.getAxisLeft();

      /*  LimitLine ll = new LimitLine(140f, "Critical Blood Pressure");
        ll.setLineColor(Color.RED);
        ll.setLineWidth(4f);
        ll.setTextColor(Color.BLACK);
        ll.setTextSize(12f);
// .. and more styling options

        leftAxis.addLimitLine(ll);*/
        barChart.setFitBars(true);
        barChart.setMinimumHeight(200); // make the x-axis fit exactly all bars
        barChart.invalidate();
        barChart.setData(finalData);
        barChart.animateX(1500); // animate horizontal 3000 milliseconds
        barChart.animateY(1500);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(position == 0){
            plotGraph("DATETIMEASC");
        }else if(position == 1){
            plotGraph("PULSEASC");
        }else if(position == 2){
            plotGraph("DATETIMEDESC");
        }else{
            plotGraph("PULSEDESC");
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
