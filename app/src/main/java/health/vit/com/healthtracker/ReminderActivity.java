package health.vit.com.healthtracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import health.vit.com.healthtracker.adapters.RecyclerAdapter_Reminders;
import health.vit.com.healthtracker.models.Reminder;
import health.vit.com.healthtracker.utilities.Dialog_Reminders;
import health.vit.com.healthtracker.utilities.RemindersDbHelper;
import rx.Subscriber;
import rx.subjects.PublishSubject;

public class ReminderActivity extends AppCompatActivity {

    private RecyclerView rv_reminders;
    private FloatingActionButton fab;
    private RemindersDbHelper dbHelper;
    private RecyclerAdapter_Reminders reminderAdapter;
    private List<Reminder> reminderList;
    private Dialog_Reminders dialog;
    private PublishSubject<Object> subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        subject = PublishSubject.create();
        dbHelper = RemindersDbHelper.getInstance(subject, getApplicationContext());
        reminderList = dbHelper.getAllReminders();

        rv_reminders = (RecyclerView) findViewById(R.id.rv_reminders);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rv_reminders.setLayoutManager(layoutManager);

        reminderAdapter = new RecyclerAdapter_Reminders(ReminderActivity.this, reminderList);
        rv_reminders.setAdapter(reminderAdapter);

        reminderAdapter.notifyDataSetChanged();

        initFab();

        subject.subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {
                Reminder reminder = (Reminder) o;
                reminderList.add(reminder);
                reminderAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initFab() {
        fab = (FloatingActionButton) findViewById(R.id.fab_reminder);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = Dialog_Reminders.newInstance(subject);
                dialog.show(getSupportFragmentManager(), "Dialog_Reminders");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return true;
    }
}
