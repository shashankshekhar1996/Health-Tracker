package health.vit.com.healthtracker;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import static health.vit.com.healthtracker.R.id.textView;

public class HealthTipsInfo extends AppCompatActivity {

    private TextView header;
    private TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_tips_info);
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

        header = (TextView) findViewById(R.id.textView2);
        content = (TextView) findViewById(R.id.textView3);

        header.setTypeface(null, Typeface.BOLD);
        content.setTypeface(null, Typeface.NORMAL);

        Intent intent = getIntent();
        String head = intent.getStringExtra("head");
        String para = intent.getStringExtra("para");


        header.setText(head);
        content.setText(para);

    }

}
