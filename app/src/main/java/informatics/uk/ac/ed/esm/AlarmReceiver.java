package informatics.uk.ac.ed.esm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long currentTime = GregorianCalendar.getInstance().getTimeInMillis();
        long dayEndTime = intent.getLongExtra(Constants.DAY_END_TME_MILLIS, -1);

        if (currentTime > dayEndTime) {
            // if we have passed the end time for today
            // cancel repeating alarm
            this.cancelRepeatingAlarm(context, intent);
        } else {
            // otherwise save notification time in preferences
            // and display notification
            Calendar cal = GregorianCalendar.getInstance();
            cal.getTimeInMillis();
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(Constants.LAST_NOTIFICATION_TIME_MILLIS, cal.getTimeInMillis());

            // TODO disable notification after 15 minutes
            this.displayNotification(context, "Time to check in!",
                    "This will only take a couple of minutes. Tap to start the survey. " +
                            "Notification will expire within 15 minutes.", "Survey Time");
        }
    }

    private void cancelRepeatingAlarm(Context context, Intent intent) {
        int requestCode = intent.getIntExtra(Constants.REQUEST_CODE, -1);
        SurveyNotificationManager notificationManager = new SurveyNotificationManager(context);
        notificationManager.cancelAlarm(requestCode);
    }
    
    private void displayNotification(Context context, String msg, String msgText, String msgAlert) {

        PendingIntent notificationIntent = PendingIntent.getActivity(context, 0, new Intent(context, UserLogin.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_mood_white_24dp)
                .setContentTitle(msg)
                .setTicker(msgAlert)
                .setContentText(msgText);

        mBuilder.setContentIntent(notificationIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        mBuilder.setAutoCancel(true); // automatically stopped when clicked on

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // post notification on screen
        mNotificationManager.notify(1, mBuilder.build());
    }
}
