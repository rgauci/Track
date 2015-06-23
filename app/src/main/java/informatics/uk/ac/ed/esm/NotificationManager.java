package informatics.uk.ac.ed.esm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class NotificationManager {

    private Context appContext;
    private Calendar studyStart;
    private Calendar studyEnd;
    private long intervalMillis;

    private final int DEF_VALUE = -1;

    public NotificationManager(Context appContext) {
        this.appContext = appContext;

        // get shared preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(appContext);

        // get interval between notifications in milliseconds
        this.intervalMillis = settings.getLong(Constants.NOTIFICATION_INTERVAL_MILLIS, DEF_VALUE);

        // get start & end date calendars
        long studyStartMillis = settings.getLong(Constants.START_DATE_MILLISECONDS, DEF_VALUE);
        long studyEndMillis = settings.getLong(Constants.STUDY_END_DATE_TIME_MILLIS, DEF_VALUE);
        this.studyStart = GregorianCalendar.getInstance();
        this.studyStart.setTimeInMillis(studyStartMillis);
        this.studyEnd = GregorianCalendar.getInstance();
        this.studyEnd.setTimeInMillis(studyEndMillis);
    }

    public void SetupNotifications() {
        Calendar currentDateTime = GregorianCalendar.getInstance();

        // if study is over
        // we are done, no alarms to set
        if (currentDateTime.after(this.studyEnd)) {
            return;
        }

        // if study hasn't even started yet
        // set up notifications for the entire duration of the study
        if (currentDateTime.before(this.studyStart)) {
            this.setupDailyNotifications(this.studyStart);
            return;
        }

        // if we are in the middle of the study
        if (currentDateTime.after(this.studyStart) && currentDateTime.before(studyEnd)) {
            // set up repeating alarms for the remainder of the study (staring from tomorrow)
            if (currentDateTime.get(Calendar.DAY_OF_YEAR) != studyEnd.get(Calendar.DAY_OF_YEAR)) {
                Calendar tomorrow = GregorianCalendar.getInstance();
                tomorrow.add(Calendar.DATE, 1);
                this.setupDailyNotifications(tomorrow);
            }
            // set up remaining alarms for today
            this.setupTodaysNotifications(currentDateTime);
        }
    }

    /**
     * Setup alarms for the remainder of the study.
     * @param startDate The date from which to start broadcasting alarms
     */
    private void setupDailyNotifications(Calendar startDate) {
        Calendar dayStartTime = GregorianCalendar.getInstance();
        dayStartTime.setTimeInMillis(startDate.getTimeInMillis());
        dayStartTime.set(Calendar.HOUR_OF_DAY, this.studyStart.get(Calendar.HOUR_OF_DAY));
        dayStartTime.set(Calendar.MINUTE, this.studyStart.get(Calendar.MINUTE));

        Calendar dayEndTime = GregorianCalendar.getInstance();
        dayEndTime.setTimeInMillis(dayStartTime.getTimeInMillis());
        dayEndTime.set(Calendar.HOUR_OF_DAY, this.studyEnd.get(Calendar.HOUR_OF_DAY));
        dayEndTime.set(Calendar.MINUTE, this.studyEnd.get(Calendar.MINUTE));

        // set up repeating alarms for every day starting from startDate to study end date
        AlarmManager alarmManager =
                (AlarmManager) this.appContext.getSystemService(Context.ALARM_SERVICE);

        while (dayStartTime.get(Calendar.DAY_OF_YEAR) != this.studyEnd.get(Calendar.DAY_OF_YEAR)) {
            // use DAY_OF_YEAR as the request code
            int requestCode = dayStartTime.get(Calendar.DAY_OF_YEAR);
            // create a pending intent that fires when the alarm is triggered.
            // add day's final time to Intent as an extra, so that repeating alarm can be cancelled
            Intent alarmReceiverIntent = new Intent(this.appContext, AlarmReceiver.class);
            alarmReceiverIntent.putExtra(Constants.REQUEST_CODE, requestCode);
            alarmReceiverIntent.putExtra(Constants.DAY_END_TME_MILLIS, dayEndTime.getTimeInMillis());

            PendingIntent pendingAlarmReceiverIntent = PendingIntent.getBroadcast(this.appContext,
                    requestCode, alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // set repeating alarm using RTC_WAKEUP
            // wakes up the device to fire the pending intent at the specified time.
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    dayStartTime.getTimeInMillis(), this.intervalMillis, pendingAlarmReceiverIntent);

            dayStartTime.add(Calendar.DATE, 1);
            dayEndTime.add(Calendar.DATE, 1);
        }
    }

    /**
     *  Setup remaining alarms for today only.
     */
    private void setupTodaysNotifications(Calendar currentTime) {
        Calendar todayStartTime = GregorianCalendar.getInstance();
        todayStartTime.set(Calendar.HOUR_OF_DAY, this.studyStart.get(Calendar.HOUR_OF_DAY));
        todayStartTime.set(Calendar.MINUTE, this.studyStart.get(Calendar.MINUTE));

        Calendar todayEndTime = GregorianCalendar.getInstance();
        todayEndTime.set(Calendar.HOUR_OF_DAY, this.studyEnd.get(Calendar.HOUR_OF_DAY));
        todayStartTime.set(Calendar.MINUTE, this.studyEnd.get(Calendar.MINUTE));

        while (todayStartTime.before(currentTime)) {
            todayStartTime.add(Calendar.MILLISECOND, (int)this.intervalMillis);
        }

        int requestCode = todayStartTime.get(Calendar.DAY_OF_YEAR);

        Intent alarmReceiverIntent = new Intent(this.appContext, AlarmReceiver.class);
        alarmReceiverIntent.putExtra(Constants.REQUEST_CODE, requestCode);
        alarmReceiverIntent.putExtra(Constants.DAY_END_TME_MILLIS, todayEndTime.getTimeInMillis());

        PendingIntent pendingAlarmReceiverIntent = PendingIntent.getBroadcast(this.appContext,
                requestCode, alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager =
                (AlarmManager) this.appContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                todayStartTime.getTimeInMillis(), this.intervalMillis, pendingAlarmReceiverIntent);
    }
}