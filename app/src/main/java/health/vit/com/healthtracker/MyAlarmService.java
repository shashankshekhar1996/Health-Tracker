package health.vit.com.healthtracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Random;

/**
 * Created by shashankshekhar on 24/01/17.
 */

public class MyAlarmService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);

        //Random generate
        String[] healthTipsHeading = {};
        String[] healthTipsPara = {};

        healthTipsHeading = getResources().getStringArray(R.array.heading);
        healthTipsPara = getResources().getStringArray(R.array.paras);

        Random rand = new Random();
        int number = rand.nextInt(15);

        Intent notificationIntent = new Intent(MyAlarmService.this, HealthTipsInfo.class);
        notificationIntent.putExtra("head", healthTipsHeading[number]);
        notificationIntent.putExtra("para", healthTipsPara[number]);

       // notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);


/*
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("aa")
                .setContentText("bb")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis());

        Intent nIntent = getPackageManager().
                getLaunchIntentForPackage("health.vit.com.healthtracker");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        startForeground(0,notificationBuilder.build());*/



        //Intent notificationIntent = new Intent(this, HealthTips.class);
        Log.i("NOTIFY","NOTIFIED");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(HealthTips.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //builder.setSound(alarmSound);





        Notification notification = builder.setContentTitle(healthTipsHeading[number])
                .setContentText(healthTipsPara[number])
                .setTicker("New Message Alert!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(alarmSound)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent).build();

       // notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;


        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }



}
