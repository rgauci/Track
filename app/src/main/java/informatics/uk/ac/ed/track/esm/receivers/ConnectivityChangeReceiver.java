package informatics.uk.ac.ed.track.esm.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import informatics.uk.ac.ed.track.esm.Utils;
import informatics.uk.ac.ed.track.esm.services.ExternalDatabaseService;


public class ConnectivityChangeReceiver extends BroadcastReceiver {

    // TODO disable receiver on study complete (and all responses have been synced)

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnected = Utils.isConnectedToInternet(context);

        if (!isConnected) {
            return;
        }

        Intent externalDbService = new Intent(context,
                ExternalDatabaseService.class);
        context.startService(externalDbService);
    }

}
