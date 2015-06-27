package informatics.uk.ac.ed.esm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

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
                    // if survey is available
                    // shower user login screen

                    //
                } else {
                    // otherwise start setup
                    Intent intent = new Intent(SplashScreen.this, ResearcherSetup.class);
                    startActivity(intent);
                }
            }
        }, SPLASH_DISPLAY_TIME_MILLIS);
    }
}
