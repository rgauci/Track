package informatics.uk.ac.ed.track.esm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import informatics.uk.ac.ed.track.esm.Constants;
import informatics.uk.ac.ed.track.R;


public class BriefingComplete extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_briefing_complete);

        // initialise UI controls
        TextView txtViewInfo = (TextView) findViewById(R.id.txtVwInfo);

        // get settings
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // mark setup is complete
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(Constants.SETUP_COMPLETE, true);
        editor.apply();

        // set info text
        Calendar startDate = GregorianCalendar.getInstance();
        startDate.setTimeInMillis(
                settings.getLong(Constants.STUDY_START_DATE_TIME_MILLIS, Constants.DEF_VALUE_LNG));

        Calendar endDate = GregorianCalendar.getInstance();
        endDate.setTimeInMillis(
                settings.getLong(Constants.STUDY_END_DATE_TIME_MILLIS, Constants.DEF_VALUE_LNG));

        int duration =
                settings.getInt(Constants.DURATION_DAYS, Constants.DEF_VALUE_INT);

        int notificationWindow =
                settings.getInt(Constants.NOTIFICATION_WINDOW_MINUTES, Constants.DEF_VALUE_INT);

        Resources res = getResources();
        SimpleDateFormat sdf = new SimpleDateFormat(
                res.getString(R.string.dateFormatFullMonthNoYear, Constants.DEF_VALUE_STR));

        txtViewInfo.setText(Html.fromHtml(res.getString(
                R.string.briefCompleteExplanation,
                sdf.format(startDate.getTime()), duration, sdf.format(endDate.getTime()),
                notificationWindow)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_briefing_complete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnNext_onClick(View view){
        Resources res = getResources();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(Constants.HOME_SCREEN_TITLE,
                res.getString(R.string.noSurveyAvailableTitle));
        intent.putExtra(Constants.HOME_SCREEN_SUBTITLE,
                res.getString(R.string.noSurveyAvailableSubTitle));
        startActivity(intent);
    }
}
