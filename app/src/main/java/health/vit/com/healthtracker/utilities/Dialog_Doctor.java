package health.vit.com.healthtracker.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import health.vit.com.healthtracker.Doctors;
import health.vit.com.healthtracker.R;

/**
 * Created by akshaymahajan on 22/01/17.
 */

public class Dialog_Doctor extends DialogFragment {


    public static Dialog_Doctor newInstance(Doctors doctor) {
        Dialog_Doctor dialog_doctor = new Dialog_Doctor();
        Bundle args = new Bundle();
        args.putParcelable("Doctor", doctor);
        dialog_doctor.setArguments(args);
        return dialog_doctor;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Doctors doctor = getArguments().getParcelable("Doctor");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Contact me :)").setMessage(doctor.getName()).setCancelable(true);

        LayoutInflater li = getActivity().getLayoutInflater();

        builder.setView(li.inflate(R.layout.dialog_custom_doctor,null));

        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /** getArguments() method retrieves the Bundle args */
                Toast.makeText(getActivity(), "Calling", Toast.LENGTH_SHORT).show();
                getActivity().startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + doctor.getPhone())));
            }
        });

        builder.setNegativeButton("Message", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "SMS", Toast.LENGTH_SHORT).show();
                Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + doctor.getPhone()));
                // TODO : Integrate USER DETAILS
                smsIntent.putExtra("sms_body", getResources().getString(R.string.send_sms_doctor));
                try {
                    getActivity().startActivity(smsIntent);
                } catch (ActivityNotFoundException e) {
                    Log.e("SMS", e.getMessage());
                    Toast.makeText(getActivity(), "No App to send SMS. You can use WhatsApp. ;)", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNeutralButton("Navigate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Toast.makeText(getActivity(), "Navigating", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + doctor.getAddress()));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    getActivity().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getActivity(), "Cannot open Navigation...Redirecting to Maps", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr=" + doctor.getAddress()));
                    getActivity().startActivity(intent);
                }
            }
        });

        //create dialog and return it
        return builder.create();
    }
}
