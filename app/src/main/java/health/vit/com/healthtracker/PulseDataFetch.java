package health.vit.com.healthtracker;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Timestamp;


public class PulseDataFetch extends AppCompatActivity implements View.OnClickListener{

    private EditText editText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulse_data_fetch);
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

        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button:
                boolean worked = false;
                try {
                    String s1 = editText.getText().toString();
                    Timestamp t = new Timestamp(System.currentTimeMillis());
                    String now = String.valueOf(t);
                    PulseData pd = new PulseData(PulseDataFetch.this);
                    pd.open();
                    pd.createEntry(s1, now);
                    pd.close();
                    worked=true;
                }catch (Exception e){
                    e.printStackTrace();
                    worked=false;
                }finally {
                    if(worked){
                        Dialog d = new Dialog(this);
                        d.setTitle("yes");
                    }
                }

        }
    }
}
