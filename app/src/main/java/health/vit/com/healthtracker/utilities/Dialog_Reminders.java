package health.vit.com.healthtracker.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

import health.vit.com.healthtracker.R;
import health.vit.com.healthtracker.models.Reminder;
import rx.subjects.PublishSubject;

/**
 * Created by akshaymahajan on 22/04/17.
 */

public class Dialog_Reminders extends DialogFragment {

    PublishSubject subject;
    private EditText tv_title;
    private EditText tv_desc;

    public Dialog_Reminders() {
    }

    public Dialog_Reminders(PublishSubject subject) {
        this.subject = subject;
    }

    public static Dialog_Reminders newInstance(PublishSubject subject) {
        Dialog_Reminders dialog_reminders = new Dialog_Reminders(subject);
        return dialog_reminders;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_reminders, null);
        builder.setTitle("Create a Reminder");
        builder.setView(view);

        tv_title = (EditText) view.findViewById(R.id.tv_dialog_reminders_title);
        tv_desc = (EditText) view.findViewById(R.id.tv_dialog_reminders_desc);

        builder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Date today = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM,hh:mm a");
                RemindersDbHelper.getInstance(subject, getContext().getApplicationContext()).insertData(new Reminder(0, tv_title.getText().toString(), tv_desc.getText().toString(), simpleDateFormat.format(today)));
            }
        });

        builder.setNegativeButton("DISCARD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissAllowingStateLoss();
            }
        });

        return builder.create();
    }
}
