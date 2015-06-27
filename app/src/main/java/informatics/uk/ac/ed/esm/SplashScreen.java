package informatics.uk.ac.ed.esm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;

import informatics.uk.ac.ed.esm.util.DefaultActivity;

public class SplashScreen extends AppCompatActivity {

    public static final int SPLASH_DISPLAY_TIME_MILLIS = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                // get settings
                SharedPreferences settings =
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                boolean setupComplete =
                        settings.getBoolean(Constants.SETUP_COMPLETE, Constants.DEF_VALUE_BOOL);

                // if setup is complete
                if (setupComplete) {
                    boolean surveyAvailable = false;

                    long lastNotifTime =
                            settings.getLong(Constants.LAST_NOTIFICATION_TIME_MILLIS,
                                    Constants.DEF_VALUE_LNG);
                    long lastCompletedTime =
                            settings.getLong(Constants.LAST_SURVEY_COMPLETED_TIME_MILLIS,
                                    Constants.DEF_VALUE_LNG);

                    if (lastCompletedTime < lastNotifTime) {
                        long notifWindowMillis =
                                settings.getInt(Constants.NOTIFICATION_WINDOW_MINUTES,
                                        Constants.DEF_VALUE_INT) * 1000;

                        Calendar cal = GregorianCalendar.getInstance();
                        long currentTime = cal.getTimeInMillis();

                        boolean expired = ((lastNotifTime + notifWindowMillis) > currentTime);

                        if (!expired) {
                            surveyAvailable = true;
                        }
                    }

                    Intent intent;

                    // if survey is available
                    // shower user login screen to start survey
                    if (surveyAvailable) {
                        intent = new Intent(SplashScreen.this, UserLogin.class);
                    } else {
                        // otherwise show no survey currently available screen
                        intent = new Intent(SplashScreen.this, DefaultActivity.class);
                    }

                    startActivity(intent);
                } else {
                    // otherwise start setup
                    Intent intent = new Intent(SplashScreen.this, ResearcherSetup.class);
                    startActivity(intent);
                }
            }
        }, SPLASH_DISPLAY_TIME_MILLIS);
    }
}
