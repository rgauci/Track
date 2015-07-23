package informatics.uk.ac.ed.track.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.GregorianCalendar;

import informatics.uk.ac.ed.track.Constants;
import informatics.uk.ac.ed.track.NotificationSchedule;
import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.SurveyNotificationManager;
import informatics.uk.ac.ed.track.Utils;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // get shared preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        // get notification schedule type: fixed / random
        NotificationSchedule notificationSchedule = NotificationSchedule.fromInt(
                settings.getInt(Constants.NOTIFICATION_SCHEDULE_TYPE, Constants.DEF_VALUE_INT));

        if (notificationSchedule == NotificationSchedule.FIXED) {
            // if notification schedule is fixed
            // this alarm might be after daily end time
            // if yes, cancel the day's repeating alarm
            long currentTime = GregorianCalendar.getInstance().getTimeInMillis();
            long dayEndTime = intent.getLongExtra(Constants.DAY_END_TME_MILLIS, -1);

            if (currentTime > dayEndTime) {
                // if we have passed the end time for today
                // cancel repeating alarm
                // and return
                this.cancelRepeatingAlarm(context, intent);
                return;
            }
        }

        // otherwise save notification time in preferences
        // and display notification
        Calendar cal = GregorianCalendar.getInstance();
        cal.getTimeInMillis();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(Constants.LAST_NOTIFICATION_TIME_MILLIS, cal.getTimeInMillis());

        int notificationWindow =
                settings.getInt(Constants.NOTIFICATION_WINDOW_MINUTES, Constants.DEF_VALUE_INT);

        int requestCode = intent.getIntExtra(Constants.REQUEST_CODE, Constants.DEF_VALUE_INT);

        Resources res = context.getResources();
        String msg = res.getString(R.string.notificationMsg);
        String msgText = String.format(
                res.getString(R.string.notificationMsgText), notificationWindow);
        this.displayNotification(context, msg,
                msgText, msg, requestCode, notificationWindow);
    }

    private void cancelRepeatingAlarm(Context context, Intent intent) {
        int requestCode = intent.getIntExtra(Constants.REQUEST_CODE, -1);
        SurveyNotificationManager notificationManager = new SurveyNotificationManager(context);
        notificationManager.cancelAlarm(requestCode);
    }

    private void displayNotification(Context context, String msg, String msgText, String msgAlert,
                                     final int requestCode, int notificationWindow_Minutes) {

        PendingIntent notificationIntent = PendingIntent.getActivity(context, 0,
                Utils.getLaunchSurveyIntent(context), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.track_icon_white_36x36)
                .setContentTitle(msg)
                .setTicker(msgAlert)
                .setContentText(msgText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msgText)); // needed to display more than one line of text

        mBuilder.setContentIntent(notificationIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        mBuilder.setAutoCancel(true); // automatically stopped when clicked on

        final NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // post notification on screen (use the alarm request code as the notification ID)
        mNotificationManager.notify(requestCode, mBuilder.build());

        // cancel notification after notification window expiry (in minutes)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mNotificationManager.cancel(requestCode);
            }
        }, notificationWindow_Minutes * 60 * 1000);
    }
}
