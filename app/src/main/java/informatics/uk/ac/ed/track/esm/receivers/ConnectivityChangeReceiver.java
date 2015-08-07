package informatics.uk.ac.ed.track.esm.receivers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import java.util.Calendar;

import informatics.uk.ac.ed.track.esm.Constants;
import informatics.uk.ac.ed.track.esm.DatabaseHelper;
import informatics.uk.ac.ed.track.esm.Utils;
import informatics.uk.ac.ed.track.esm.services.ExternalDatabaseService;


public class ConnectivityChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Utils.isConnectedToInternet(context)) {
            // start service to sync all unsynced responses to web server
            Intent externalDbService = new Intent(context,
                    ExternalDatabaseService.class);
            context.startService(externalDbService);
        }

        // disable the receiver if study is over and all responses have been synced
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        long studyEndMillis =
                settings.getLong(Constants.STUDY_END_DATE_TIME_MILLIS, Constants.DEF_VALUE_LNG);

        if ((studyEndMillis != Constants.DEF_VALUE_LNG)
                && (Calendar.getInstance().getTimeInMillis() > studyEndMillis)) {
            DatabaseHelper dbHelper = new DatabaseHelper(context.getApplicationContext());
            long numUnsynced = dbHelper.getUnsyncedResponsesCount();
            if (numUnsynced == 0) {
                disableReceiver(context);
            }
        }
    }

    public static void enableReceiver(Context context) {
        setEnabledSetting(context, PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
    }

    public static void disableReceiver(Context context) {
        setEnabledSetting(context, PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
    }

    private static void setEnabledSetting(Context context, int newState) {
        ComponentName receiver = new ComponentName(context, ConnectivityChangeReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                newState,
                PackageManager.DONT_KILL_APP);
    }
}
