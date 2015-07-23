package informatics.uk.ac.ed.track.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import informatics.uk.ac.ed.track.services.ExternalDatabaseService;


public class ConnectivityChangeReceiver extends BroadcastReceiver {

    // TODO disable receiver on study complete (and all responses have been synced)

    @Override
    public void onReceive(Context context, Intent intent) {

        // query the network and check if we have an Internet connection
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            return;
        }

        Intent externalDbService = new Intent(context,
                ExternalDatabaseService.class);
        context.startService(externalDbService);
    }

}
