package health.vit.com.healthtracker;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

public class PulseDataView extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    TextView tv;
    private Button btn;
    int flag1 = 0;
    int flag2 = 0;

    private String[] arr = {"Select Data Display Format", "Tabular Data","Bar Graph/Chart Data","Linear Graphical Data"};
    private TextView fromDate;
    private TextView toDate;
    private String toDateUpdated;
    private ImageView fromDateBtn;
    private ImageView toDateBtn;
    private Button tablebtn;
    private Button linebtn;
    private Button barbtn;
    private Date to;
    private Date from;
    private int fromYear;
    private int fromMonth;
    private int fromDay;

    private int toYear;
    private int toMonth;
    private int toDay;

    private Spinner spinner;
    static final int DATE_DIALOG_ID1 = 0;
    private Button proceed;
    private final int DATE_DIALOG_ID2 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulse_data_view);
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

       // tv = (TextView) findViewById(R.id.textView4);
        //spinner = (Spinner) findViewById(R.id.spinner);
        fromDate = (TextView) findViewById(R.id.fromDate);
        toDate = (TextView) findViewById(R.id.toDate);
        fromDateBtn = (ImageView) findViewById(R.id.fromDateBtn);
        toDateBtn = (ImageView) findViewById(R.id.toDateBtn);
        tablebtn = (Button) findViewById(R.id.btn_table);
        linebtn = (Button) findViewById(R.id.btn_linechart);
        barbtn = (Button) findViewById(R.id.btn_barchart);

        tablebtn.setOnClickListener(this);
        linebtn.setOnClickListener(this);
        barbtn.setOnClickListener(this);

        /*tablebtn.setClickable(false);
        linebtn.setClickable(false);
        barbtn.setClickable(false);*/


        /** Listener for click event of the button */
        fromDateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID1);
            }
        });

        toDateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID2);
            }
        });

        /** Get the current date */
        final Calendar cal1 = Calendar.getInstance();
        fromYear = cal1.get(Calendar.YEAR);
        fromMonth = cal1.get(Calendar.MONTH);
        fromDay = cal1.get(Calendar.DAY_OF_MONTH);

        final Calendar cal2 = Calendar.getInstance();
        toYear = cal2.get(Calendar.YEAR);
        toMonth = cal2.get(Calendar.MONTH);
        toDay = cal2.get(Calendar.DAY_OF_MONTH);

        /** Display the current date in the TextView */
        fromUpdateDisplay();
        toUpdateDisplay();
        validate();
       //validate();

    }


    private String validateDate(int param){
        String res="0";
        String temp = null;
        if(param<10){
            temp = String.valueOf(param);
            res= res + temp;

        }else{
            res = String.valueOf(param);
        }
        return res;
    }

    private void validate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            to = sdf.parse(toDate.getText().toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            from = sdf.parse(fromDate.getText().toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        if (to.getTime()<from.getTime()) {
            Toast.makeText(this, "To Date cannot be less than from Date",  Toast.LENGTH_LONG).show();
            linebtn.setClickable(false);
            barbtn.setClickable(false);
            tablebtn.setClickable(false);
            // catalog_outdated = 1;
        }else{
            linebtn.setClickable(true);
            barbtn.setClickable(true);
            tablebtn.setClickable(true);
        }
    }

    private DatePickerDialog.OnDateSetListener fromDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    fromYear = year;
                    fromMonth = monthOfYear;
                    fromDay = dayOfMonth;
                    fromUpdateDisplay();
                   // displayToast();
                }
            };

    private DatePickerDialog.OnDateSetListener toDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    toYear = year;
                    toMonth = monthOfYear;
                    toDay = dayOfMonth;
                    toUpdateDisplay();
                   // displayToast();
                }
            };

    private void fromUpdateDisplay() {
        fromDate.setText(
                    new StringBuilder()
                            // Month is 0 based so add 1
                            .append(validateDate(fromDay)).append("/")
                            .append(validateDate(fromMonth + 1)).append("/")
                            .append(String.valueOf(fromYear)));
        if(flag1 == 0){
            flag1 = 1;
        }else{
            validate();
        }
      // validate();
    }

    private void toUpdateDisplay() {
        toDate.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(validateDate(toDay)).append("/")
                        .append(validateDate(toMonth + 1)).append("/")
                        .append(String.valueOf(toYear)));

        StringBuilder sb = new StringBuilder()
                .append(validateDate(toDay + 1)).append("/")
                .append(validateDate(toMonth + 1)).append("/")
                .append(String.valueOf(toYear));
        toDateUpdated = sb.toString();

        if(flag2 == 0){
            flag2 = 1;
        }else{
            validate();
        }
        //validate();
    }

   /* private void displayToast() {
        Toast.makeText(this, new StringBuilder().append("Date choosen is ").append(fromDate.getText()),  Toast.LENGTH_SHORT).show();

    }*/

   /* private void fetchData() {
        PulseData pd = new PulseData(PulseDataView.this);
        pd.open();
        String res = pd.getData();
        pd.close();
        tv.setText(res);
        //if(pd )
        //pd.getData();
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        PulseData pd = new PulseData(PulseDataView.this);
        switch(v.getId()){
            case R.id.btn_linechart:
                pd.open();
                LinkedHashMap<String, Integer> mapline = pd.getAllData(fromDate.getText().toString(),toDateUpdated,"");
                pd.close();
                if(mapline.size() == 0){
                    startActivity(new Intent(PulseDataView.this,DataError.class));
                }else {
                    intent = new Intent(PulseDataView.this, PulseRateGraph.class);
                    intent.putExtra("from", fromDate.getText().toString());
                    intent.putExtra("to", toDateUpdated);//toDate.getText().toString());
                    startActivity(intent);
                }

                break;
            case R.id.btn_barchart:

                pd.open();
                LinkedHashMap<String, Integer> mapbar = pd.getAllData(fromDate.getText().toString(),toDateUpdated,"");//toDate.getText().toString());
                pd.close();
                if(mapbar.size() == 0){
                    startActivity(new Intent(PulseDataView.this,DataError.class));
                }else {
                    intent = new Intent(PulseDataView.this, PulseGraph.class);
                    intent.putExtra("from", fromDate.getText().toString());
                    intent.putExtra("to", toDateUpdated);//toDate.getText().toString());
                    startActivity(intent);
                }
               // startActivity(new Intent(PulseDataView.this, PulseGraph.class));
                break;
            case R.id.btn_table:
                pd.open();
                LinkedHashMap<String, Integer> maptable = pd.getAllData(fromDate.getText().toString(),toDateUpdated,"");//toDate.getText().toString());
                pd.close();
                if(maptable.size() == 0){
                    startActivity(new Intent(PulseDataView.this,DataError.class));
                }else {
                    intent = new Intent(PulseDataView.this, PulseTable.class);
                    intent.putExtra("from", fromDate.getText().toString());
                    intent.putExtra("to",toDateUpdated );//toDate.getText().toString());
                    startActivity(intent);
                }
               // startActivity(new Intent(PulseDataView.this,PulseGraph.class));
        }

    }

    /** Create a new dialog for date picker */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID1:
                return new DatePickerDialog(this,
                        fromDateSetListener,
                        fromYear, fromMonth, fromDay);
            case DATE_DIALOG_ID2:
                return new DatePickerDialog(this,
                        toDateSetListener,
                        toYear, toMonth, toDay);
        }
        return null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
