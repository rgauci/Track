package informatics.uk.ac.ed.track.esm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.Constants;

public class AppLocked extends AppCompatActivity {

    private TextView txtVwAppLockedTitle;
    private RelativeLayout lytAppLocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_locked);

        // initialise UI controls
        this.txtVwAppLockedTitle = (TextView) findViewById(R.id.txtVwAppLockedTitle);

        // set title text
        Resources res = getResources();
        this.txtVwAppLockedTitle.setText(String.format(res.getString(R.string.appLockedTitle),
                res.getInteger(R.integer.app_locked_hours)));

        // if first time, update related settings value
        Intent intent = getIntent();
        if (intent.getBooleanExtra(Constants.APP_LOCKED_FIRST_TIME, Constants.DEF_VALUE_BOOL)) {
            SharedPreferences settings =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = settings.edit();
            Calendar calendar = GregorianCalendar.getInstance();
            // reset counter to zero
            editor.putInt(Constants.INVALID_RESEARCHER_CREDENTIALS_COUNT, 0);
            // and set app as locked
            editor.putBoolean(Constants.IS_APP_LOCKED, true);
            editor.putLong(Constants.APP_LOCKED_TIME_MILLIS,
                    calendar.getTimeInMillis());
            editor.apply();
        }
    }
}
