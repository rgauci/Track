package informatics.uk.ac.ed.track.esm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.lang.StringBuilder;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.GregorianCalendar;

import informatics.uk.ac.ed.track.esm.receivers.AlarmReceiver;

public class SurveyNotificationManager {

    private static final String LOG_TAG = "TRACK.SurveyNotifMngr";

    private Context appContext;
    private Calendar studyStart, studyEnd;
    private boolean sampleDayGoesPastMidnight;
    private int duration;
    private int samplesPerDay;
    private long intervalMillis;
    private NotificationSchedule notificationSchedule;

    public SurveyNotificationManager(Context appContext) {
        this.appContext = appContext;

        // get shared preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.appContext);

        // get interval between notifications in milliseconds
        this.intervalMillis =
                settings.getLong(Constants.NOTIFICATION_INTERVAL_MILLIS, Constants.DEF_VALUE_LNG);

        // get duration and number of samples per day
        this.duration = settings.getInt(Constants.DURATION_DAYS, Constants.DEF_VALUE_INT);
        this.samplesPerDay = settings.getInt(Constants.SAMPLES_PER_DAY, Constants.DEF_VALUE_INT);
        this.sampleDayGoesPastMidnight =
                settings.getBoolean(Constants.SAMPLE_DAY_GOES_PAST_MIDNIGHT, Constants.DEF_VALUE_BOOL);

        // get start & end date calendars
        long studyStartMillis =
                settings.getLong(Constants.STUDY_START_DATE_TIME_MILLIS, Constants.DEF_VALUE_LNG);
        long studyEndMillis =
                settings.getLong(Constants.STUDY_END_DATE_TIME_MILLIS, Constants.DEF_VALUE_LNG);

        // get notification schedule type: fixed / random
        this.notificationSchedule = NotificationSchedule.fromInt(
                settings.getInt(Constants.NOTIFICATION_SCHEDULE_TYPE, Constants.DEF_VALUE_INT));

        this.studyStart = GregorianCalendar.getInstance();
        this.studyStart.setTimeInMillis(studyStartMillis);
        this.studyEnd = GregorianCalendar.getInstance();
        this.studyEnd.setTimeInMillis(studyEndMillis);
    }

    /**
     * Creates new alarms for the duration of the study,
     * depending on the current date and the study start and end dates.
     * @param cancelCurrentAlarms If true, any currently-set alarms will be cancelled.
     */
    public void SetupNotifications(boolean cancelCurrentAlarms) {
        if (cancelCurrentAlarms) {
            this.cancelAllAlarms();
        }

        // request codes still need to be reset
        this.resetRequestCodes();

        // get current date & time to determine what alarms needs to be set
        Calendar currentDateTime = GregorianCalendar.getInstance();

        // if study is over
        // we are done, no alarms to set
        if (currentDateTime.after(this.studyEnd)) {
            return;
        }

        // if study hasn't even started yet
        // set up notifications for the entire duration of the study
        if (currentDateTime.before(this.studyStart)) {
            switch (this.notificationSchedule) {
                case RANDOM:
                    this.setupAllNotifications_Random();
                    break;
                case FIXED:
                    this.setupDailyNotifications_Fixed(this.studyStart);
                    break;
            }
            return;
        }

        // if we are in the middle of the study
        if (currentDateTime.after(this.studyStart) && currentDateTime.before(this.studyEnd)) {
            switch (this.notificationSchedule) {
                case RANDOM:
                    // re-schedule remaining alarms
                    this.setupRemainingAlarms_Random();
                    break;
                case FIXED:
                    // set up repeating alarms for the remainder of the study (starting from tomorrow)
                    if (currentDateTime.get(Calendar.DAY_OF_YEAR)
                            != this.studyEnd.get(Calendar.DAY_OF_YEAR)) {
                        Calendar tomorrow = GregorianCalendar.getInstance();
                        tomorrow.add(Calendar.DATE, 1);
                        this.setupDailyNotifications_Fixed(tomorrow);
                    }
                    // set up remaining alarms for today
                    this.setupTodaysNotifications_Fixed(currentDateTime);
                    break;
            }
        }
    }

    private void setupAllNotifications_Random() {
        SecureRandom randomGenerator = new SecureRandom();

        Calendar intervalStartTime = GregorianCalendar.getInstance();
        StringBuilder sb = new StringBuilder();

        // generate random times for entire duration of the study
        for (int i = 0; i < duration; i++) {
            intervalStartTime.setTimeInMillis(this.studyStart.getTimeInMillis());
            intervalStartTime.add(Calendar.DATE, i);
            for (int j = 0; j < samplesPerDay; j++) {
                if (j > 0) {
                    intervalStartTime.add(Calendar.MILLISECOND, (int)this.intervalMillis);
                }
                long min = intervalStartTime.getTimeInMillis();
                long max = intervalStartTime.getTimeInMillis() + this.intervalMillis;
                long alarmTime = min + (Math.abs(randomGenerator.nextLong()) % (max - min));

                // add to string builder
                // add request code to string builder so it will be saved to preferences
                if (sb.length() > 0) {
                    sb.append(Constants.STRING_ARRAY_DELIMITER);
                }

                sb.append(alarmTime);
            }
        }

        // save times in preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.appContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.RANDOM_ALARM_TIMES, sb.toString());
        editor.apply();

        // actually set up alarms using saved times
        this.setupRemainingAlarms_Random();
    }

    private void setupRemainingAlarms_Random() {
        // load times from preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.appContext);
        String alarmTimesStr =
                settings.getString(Constants.RANDOM_ALARM_TIMES, Constants.DEF_VALUE_STR);

        if (alarmTimesStr.equals(Constants.DEF_VALUE_STR)) {
            return;
        }

        String[] alarmTimes = alarmTimesStr.split(Constants.STRING_ARRAY_DELIMITER);
        Calendar currentCal = GregorianCalendar.getInstance();
        long currentTime = currentCal.getTimeInMillis();

        AlarmManager alarmManager =
                (AlarmManager) this.appContext.getSystemService(Context.ALARM_SERVICE);
        StringBuilder sb = new StringBuilder();

        for (int alarmNum = 0; alarmNum < alarmTimes.length; alarmNum++) {
            long alarmTime = Long.parseLong(alarmTimes[alarmNum]);
            if (alarmTime > currentTime) {
                // use alarmNum as the request code (this will be different for every alarm)
                // create a pending intent that fires when the alarm is triggered.
                Intent alarmReceiverIntent = new Intent(this.appContext, AlarmReceiver.class);
                // add request code as an extra
                alarmReceiverIntent.putExtra(Constants.REQUEST_CODE, alarmNum);
                PendingIntent pendingAlarmReceiverIntent = PendingIntent.getBroadcast(
                        this.appContext, alarmNum, alarmReceiverIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                // set up alarm using RTC_WAKEUP
                // wakes up the device to fire the pending intent at the specified time.
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingAlarmReceiverIntent);

                // add request code to string builder so it will be saved to preferences
                if (sb.length() > 0) {
                    sb.append(Constants.STRING_ARRAY_DELIMITER);
                }

                sb.append(alarmNum);
            }
        }

        this.updateRequestCodes(sb.toString());
    }

    /**
     * Setup alarms for the remainder of the study.
     * @param startDate The date from which to start broadcasting alarms.
     */
    private void setupDailyNotifications_Fixed(Calendar startDate) {
        // dayStartTime : daily start time
        Calendar dayStartTime = GregorianCalendar.getInstance();
        dayStartTime.setTimeInMillis(startDate.getTimeInMillis());
        dayStartTime.set(Calendar.HOUR_OF_DAY, this.studyStart.get(Calendar.HOUR_OF_DAY));
        dayStartTime.set(Calendar.MINUTE, this.studyStart.get(Calendar.MINUTE));
        dayStartTime.set(Calendar.SECOND, this.studyStart.get(Calendar.SECOND));
        dayStartTime.set(Calendar.MILLISECOND, this.studyStart.get(Calendar.MILLISECOND));

        // add interval to start time to space out alarms
        dayStartTime.add(Calendar.MILLISECOND, (int)this.intervalMillis);

        // dayEndTime : daily end time
        Calendar dayEndTime = GregorianCalendar.getInstance();
        dayEndTime.setTimeInMillis(startDate.getTimeInMillis());
        if (this.sampleDayGoesPastMidnight) {
            dayEndTime.add(Calendar.DATE, 1);
        }
        dayEndTime.set(Calendar.HOUR_OF_DAY, this.studyEnd.get(Calendar.HOUR_OF_DAY));
        dayEndTime.set(Calendar.MINUTE, this.studyEnd.get(Calendar.MINUTE));
        dayEndTime.set(Calendar.SECOND, this.studyEnd.get(Calendar.SECOND));
        dayEndTime.set(Calendar.MILLISECOND, this.studyEnd.get(Calendar.MILLISECOND));

        // set up repeating alarms for every day starting from startDate to study end date
        AlarmManager alarmManager =
                (AlarmManager) this.appContext.getSystemService(Context.ALARM_SERVICE);

        // set up string builder to hold request codes for each alarm in SharedPreferences
        // eg: 1,2,3,4,5
        StringBuilder sb = new StringBuilder();

        while (dayStartTime.getTimeInMillis() < this.studyEnd.getTimeInMillis()) {
            // use DAY_OF_YEAR as the request code (this will be different for every alarm)
            int requestCode = dayStartTime.get(Calendar.DAY_OF_YEAR);

            // create a pending intent that fires when the alarm is triggered.
            Intent alarmReceiverIntent = new Intent(this.appContext, AlarmReceiver.class);

            // add day's final time to Intent as an extra, so that repeating alarm can be cancelled
            alarmReceiverIntent.putExtra(Constants.REQUEST_CODE, requestCode);
            alarmReceiverIntent.putExtra(Constants.DAY_END_TME_MILLIS,
                    dayEndTime.getTimeInMillis());

            PendingIntent pendingAlarmReceiverIntent = PendingIntent.getBroadcast(this.appContext,
                    requestCode, alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // set repeating alarm using RTC_WAKEUP
            // wakes up the device to fire the pending intent at the specified time.
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, dayStartTime.getTimeInMillis(),
                    this.intervalMillis, pendingAlarmReceiverIntent);

            dayStartTime.add(Calendar.DATE, 1);
            dayEndTime.add(Calendar.DATE, 1);

            // add request code to string builder so it will be saved to preferences
            if (sb.length() > 0) {
                sb.append(Constants.STRING_ARRAY_DELIMITER);
            }

            sb.append(requestCode);
        }

        this.updateRequestCodes(sb.toString());
    }

    /**
     *  Setup remaining alarms for today only.
     */
    private void setupTodaysNotifications_Fixed(Calendar currentTime) {
        Calendar todayStartTime = GregorianCalendar.getInstance();
        todayStartTime.set(Calendar.HOUR_OF_DAY, this.studyStart.get(Calendar.HOUR_OF_DAY));
        todayStartTime.set(Calendar.MINUTE, this.studyStart.get(Calendar.MINUTE));
        todayStartTime.set(Calendar.SECOND, this.studyStart.get(Calendar.SECOND));
        todayStartTime.set(Calendar.MILLISECOND, this.studyStart.get(Calendar.MILLISECOND));
        todayStartTime.add(Calendar.MILLISECOND, (int)this.intervalMillis);

        Calendar todayEndTime = GregorianCalendar.getInstance();
        if (this.sampleDayGoesPastMidnight) {
            todayEndTime.add(Calendar.DATE, 1);
        }
        todayEndTime.set(Calendar.HOUR_OF_DAY, this.studyEnd.get(Calendar.HOUR_OF_DAY));
        todayEndTime.set(Calendar.MINUTE, this.studyEnd.get(Calendar.MINUTE));
        todayEndTime.set(Calendar.SECOND, this.studyEnd.get(Calendar.SECOND));
        todayEndTime.set(Calendar.MILLISECOND, this.studyEnd.get(Calendar.MILLISECOND));

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

        this.updateRequestCodes(Integer.toString(requestCode));
    }

    /**
     * Saves new request codes to SharedPreferences.
     * If any request codes are already present, the new ones are appended to the list.
     * @param requestCodes The new intent request codes.
     */
    private void updateRequestCodes(String requestCodes) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.appContext);
        String currentRCs =
                settings.getString(Constants.ALARM_REQUEST_CODES, Constants.DEF_VALUE_STR);

        if (!currentRCs.equals(Constants.DEF_VALUE_STR)) {
            requestCodes =
                    currentRCs.concat(Constants.STRING_ARRAY_DELIMITER).concat(requestCodes);
        }

        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.ALARM_REQUEST_CODES, requestCodes);
        editor.apply();
    }

    private void cancelAllAlarms() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.appContext);
        String currentRCs =
                settings.getString(Constants.ALARM_REQUEST_CODES, Constants.DEF_VALUE_STR);

        if (currentRCs.equals(Constants.DEF_VALUE_STR)) {
            return;
        }

        String[] requestCodes = currentRCs.split(Constants.STRING_ARRAY_DELIMITER);

        for (String requestCode: requestCodes) {
            try {
                this.cancelAlarm(Integer.parseInt(requestCode));
            } catch (NumberFormatException nfe) {
                Log.e(LOG_TAG, "Unable to parse requestCode.", nfe);
            }
        }
    }

    private void resetRequestCodes(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.appContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(Constants.ALARM_REQUEST_CODES);
        editor.apply();
    }

    public void cancelAlarm(int requestCode) {
        Intent alarmReceiverIntent = new Intent(this.appContext, AlarmReceiver.class);
        PendingIntent pendingAlarmReceiverIntent = PendingIntent.getBroadcast(this.appContext,
                requestCode, alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager =
                (AlarmManager) this.appContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingAlarmReceiverIntent);
    }
}