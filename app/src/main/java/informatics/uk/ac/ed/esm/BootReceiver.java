package informatics.uk.ac.ed.esm;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    // TODO disable boot receiver when study finishes
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        long studyEndMillis =
                settings.getLong(Constants.STUDY_END_DATE_TIME_MILLIS, Constants.DEF_VALUE_INT);

        if (Calendar.getInstance().getTimeInMillis() > studyEndMillis) {
            // if study is over, disable boot receiver
            // so that it is not called unnecessarily
            this.disableBootReceiver(context);
        } else {
            // otherwise re-schedule alarms for the entire duration of the study
            SurveyNotificationManager notificationManager =
                    new SurveyNotificationManager(context.getApplicationContext());
            // no need to cancel any alarms since reboot would have cancelled them
            notificationManager.SetupNotifications(false);
        }
    }

    private void disableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
