package health.vit.com.healthtracker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import static android.R.attr.data;
import static health.vit.com.healthtracker.R.id.avg;

public class PulseRateGraph extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private LineChart lineChart;
   // String[] quarters;
    ArrayList<String> quarters;
    private String from;
    private String to;
    private Spinner spinner;
    private TextView textView;
    private TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulse_rate_graph);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lineChart = (LineChart) findViewById(R.id.lineGraph);
        spinner = (Spinner) findViewById(R.id.spinner);
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenHeight = size.y;
        LinearLayout layout1 = (LinearLayout)findViewById(R.id.layout1);
        textView = (TextView) findViewById(avg);
        msg = (TextView) findViewById(R.id.msg);
        ViewGroup.LayoutParams params1 = layout1.getLayoutParams();
        params1.height = screenHeight;// - getStatusBarHeight();
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
        PulseData pd = new PulseData(PulseRateGraph.this);
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

    private void plotGraph(String str) {
        lineChart.removeAllViews();
        PulseData pd = new PulseData(PulseRateGraph.this);
        pd.open();
        LinkedHashMap<String, Integer> map = pd.getAllData(from,to,str);
        pd.close();
        if(map.size()==0){
            startActivity(new Intent(PulseRateGraph.this,DataError.class));
        }
        ArrayList<Entry> pulse = new ArrayList<>();
        ArrayList<String> time = new ArrayList<>();
        quarters = new ArrayList<>();
        Integer i;
        i=0;
        if(map.size() == 1){
            pulse.add(new Entry(i++,0f));
            quarters.add("0 / 0");
        }
        for(String params : map.keySet()){
            pulse.add(new Entry(i++,map.get(params)));
        }

        //quarters = new String[50];
        i=0;


        for(String params : map.keySet()){
            Timestamp ts = Timestamp.valueOf(params);
            Date date = new Date(ts.getTime());

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = sdf.format(date);

            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
            String formattedTime = sdf1.format(date);
            quarters.add(formattedDate + "/" + formattedTime);
            // quarters[i++] = formattedDate + " / " + formattedTime;
        }

        /*for(String params : map.keySet()){
            quarters[i++] = params;
            //time.add(params);
        }*/

        LineDataSet lineDataSet = new LineDataSet(pulse, "Pulse");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setColor(Color.DKGRAY);
      //  lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setCircleColor(Color.BLUE);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setDrawCircleHole(false);

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);

        //final String[] quarters = new String[] { "Q1", "Q2", "Q3", "Q4" };

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters.get((int)value);//[(int) value];
            }
        };

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(90);

        //YAxis leftAxis = lineChart.getAxisLeft();

       /* LimitLine ll = new LimitLine(100f, "Upper Normal Rate Limit");
        ll.setLineColor(Color.RED);
        ll.setLineWidth(4f);
        ll.setTextColor(Color.BLACK);
        ll.setTextSize(12f);
// .. and more styling options

        leftAxis.addLimitLine(ll);*/

        /*LimitLine ll1 = new LimitLine(60f, "Lower Normal Rate Limit");
        ll1.setLineColor(Color.RED);
        ll1.setLineWidth(4f);
        ll1.setTextColor(Color.BLACK);
        ll1.setTextSize(12f);
        leftAxis.addLimitLine(ll1);*/

        //lineChart.setFitBars(true);

        lineChart.setMinimumHeight(200);
         // make the x-axis fit exactly all bars
        lineChart.invalidate();
        try {
            lineChart.setData(data);
        }catch (Exception e){
            e.printStackTrace();
        }
        //lineChart.set
        lineChart.animateX(1500); // animate horizontal 3000 milliseconds
        lineChart.animateY(1500);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
