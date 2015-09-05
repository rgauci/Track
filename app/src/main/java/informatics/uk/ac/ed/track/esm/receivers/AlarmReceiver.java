package informatics.uk.ac.ed.track.esm.receivers;

import android.app.AlarmManager;
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

import informatics.uk.ac.ed.track.esm.Constants;
import informatics.uk.ac.ed.track.esm.NotificationSchedule;
import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.SurveyNotificationManager;
import informatics.uk.ac.ed.track.esm.Utils;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // first of all, check whether this is just a request to cancel a notification

        boolean cancelNotification = intent.getBooleanExtra(Constants.IS_CANCEL_NOTIFICATION_ALARM, Constants.DEF_VALUE_BOOL);

        if (cancelNotification) {
            this.cancelNotification(context, intent);
            return;
        }

        // if not, then this is an alarm to display a survey notification

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

        // otherwise:
        // save notification time in preferences
        // and current day number
        Calendar cal = GregorianCalendar.getInstance();
        cal.getTimeInMillis();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long notificationTime = cal.getTimeInMillis();

        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(Constants.LAST_NOTIFICATION_TIME_MILLIS, notificationTime);
        editor.putInt(Constants.CURRENT_STUDY_DAY_NUMBER,
                Utils.getCurrentStudyDayNumber(context.getApplicationContext()));
        editor.apply();

        // display notification
        int notificationWindow =
                settings.getInt(Constants.NOTIFICATION_WINDOW_MINUTES, Constants.DEF_VALUE_INT);

        int requestCode = intent.getIntExtra(Constants.REQUEST_CODE, Constants.DEF_VALUE_INT);

        Resources res = context.getResources();
        String msg = res.getString(R.string.notificationMsg);
        String msgText = String.format(
                res.getString(R.string.notificationMsgText), notificationWindow);
        this.displayNotification(context, msg,
                msgText, msg, requestCode, notificationTime, notificationWindow);
    }

    private void cancelRepeatingAlarm(Context context, Intent intent) {
        int requestCode = intent.getIntExtra(Constants.REQUEST_CODE, -1);
        SurveyNotificationManager notificationManager = new SurveyNotificationManager(context);
        notificationManager.cancelAlarm(requestCode);
    }

    private void cancelNotification(Context context, Intent intent) {
        int notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID, Constants.DEF_VALUE_INT);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }

    private void displayNotification(Context context, String msg, String msgText, String msgAlert,
                                     final int alarmRequestCode,
                                     long notificationTime_Millis, int notificationWindow_Minutes) {

        PendingIntent notificationIntent = PendingIntent.getActivity(context, 0,
                Utils.getLaunchSurveyIntent(context), PendingIntent.FLAG_CANCEL_CURRENT);

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

        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // post notification on screen (use the alarm request code as the notification ID)
        // FIXED alarms request codes are DAY_OF_YEAR [1,366]
        // Random alarm request codes are numbered starting from 1 to the total number of alarms
        // in the study
        // Adding a relatively big number (10,000) should ensure we have a unique notification ID
        // (Notification will be cancelled once notification window is expired anyway.)
        int notificationId = alarmRequestCode + 10000; // use also for 'cancel' alarm request code
        notificationManager.notify(notificationId, mBuilder.build());

        // cancel notification after notification window expiry (in minutes)
        // set up an alarm which will fire once the notification window expires
        long cancelNotificationTime =
                notificationTime_Millis + (notificationWindow_Minutes * 60 * 1000);

        Intent alarmReceiverIntent = new Intent(context, AlarmReceiver.class);
        alarmReceiverIntent.putExtra(Constants.IS_CANCEL_NOTIFICATION_ALARM, true);
        alarmReceiverIntent.putExtra(Constants.NOTIFICATION_ID, notificationId);
        PendingIntent pendingAlarmReceiverIntent = PendingIntent.getBroadcast(
                context, notificationId, alarmReceiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cancelNotificationTime,
                pendingAlarmReceiverIntent);
    }
}
