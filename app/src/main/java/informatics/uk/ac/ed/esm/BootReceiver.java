package informatics.uk.ac.ed.esm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    // TODO disable boot receiver when study finishes
    @Override
    public void onReceive(Context context, Intent intent) {
        // re-schedule alarms for the entire duration of the study
        SurveyNotificationManager notificationManager = new SurveyNotificationManager(context);
        // no need to cancel any alarms since reboot would have cancelled them
        notificationManager.SetupNotifications(false);
    }
}
