package health.vit.com.healthtracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;
import java.util.Random;

import health.vit.com.healthtracker.models.Reminder;
import health.vit.com.healthtracker.utilities.RemindersDbHelper;

public class AlarmTime extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("Receiver" + intent.getAction());
        List<Reminder> reminderList = RemindersDbHelper.getInstance(context.getApplicationContext()).getAllReminders();

        if (intent.getAction().equals("android.intent.action.REMINDERACTIVITY")) {
            if (!reminderList.isEmpty()) {
                Random rand = new Random();
                int number = 0;
                if (reminderList.size() != 1) {
                    number = rand.nextInt(reminderList.size() - 1);
                }
                Reminder reminder = reminderList.get(number);
                String notifTitle = reminder.getTitle();
                String notifDesc = reminder.getDesc();
                Log.i("NOTIFY", "NOTIFIED");
                Intent notificationIntent = new Intent(context, ReminderActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(notificationIntent);

                PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                Notification notification = builder.setContentTitle("You have Reminders").setContentText(notifTitle).setContentInfo(notifDesc).setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pendingIntent).setSound(alarmSound).setAutoCancel(true).build();

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(2, notification);
            }
        } else {
            /*Intent service1 = new Intent(context, MyAlarmService.class);
            service1.setAction(intent.getAction());
            context.startService(service1);*/

            String[] healthTipsHeading = {};
            String[] healthTipsPara = {};

            healthTipsHeading = context.getResources().getStringArray(R.array.heading);
            healthTipsPara = context.getResources().getStringArray(R.array.paras);

            Random rand = new Random();
            int number = rand.nextInt(15);

            Intent notificationIntent = new Intent(context, HealthTipsInfo.class);
            notificationIntent.putExtra("head", healthTipsHeading[number]);
            notificationIntent.putExtra("para", healthTipsPara[number]);

            Log.i("NOTIFY", "NOTIFIED");
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(HealthTips.class);
            stackBuilder.addNextIntent(notificationIntent);

            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


            Notification notification = builder.setContentTitle(healthTipsHeading[number]).setContentText(healthTipsPara[number]).setTicker("New Message Alert!").setSmallIcon(R.mipmap.ic_launcher).setSound(alarmSound).setAutoCancel(true).setContentIntent(pendingIntent).build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);

            Intent notificationIntent1 = new Intent(context, FindDoctorActivity.class);
            Double avg = getAvg(context);
            Log.i("NOTIFY", String.valueOf(avg));
            if (avg < 50 || avg > 120) {
                Log.i("NOTIFYing", String.valueOf(avg));
                TaskStackBuilder stackBuilder1 = TaskStackBuilder.create(context);
                stackBuilder1.addParentStack(MainActivity.class);
                stackBuilder1.addNextIntent(notificationIntent1);

                PendingIntent pendingIntent1 = stackBuilder1.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification1 = builder.setContentTitle("Health Risk Detected! Consult Doctor!").setContentText("Heart Rate : " + String.valueOf(avg) + " (Not in Normal Range)").setTicker("New Message Alert!").setSmallIcon(R.mipmap.ic_launcher).setSound(alarmSound).setAutoCancel(true).setContentIntent(pendingIntent1).build();
                NotificationManager notificationManager1 = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager1.notify(1, notification1);
            } else {

            }
        }
    }


    private Double getAvg(Context context) {
        PulseData pd = new PulseData(context);
        pd.open();
        Double avg = pd.getAvg();
        pd.close();
        return avg;
    }


}