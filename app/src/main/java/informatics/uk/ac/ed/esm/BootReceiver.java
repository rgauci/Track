package informatics.uk.ac.ed.esm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.GregorianCalendar;

public class BootReceiver extends BroadcastReceiver {

    // TODO disable boot receiver when study finishes
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = new NotificationManager(context);
        notificationManager.SetupNotifications();
    }
}
