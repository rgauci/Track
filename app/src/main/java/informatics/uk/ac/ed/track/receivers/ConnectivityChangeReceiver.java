package informatics.uk.ac.ed.track.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class ConnectivityChangeReceiver extends BroadcastReceiver {

    // TODO disable receiver on study complete

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
    }

}
