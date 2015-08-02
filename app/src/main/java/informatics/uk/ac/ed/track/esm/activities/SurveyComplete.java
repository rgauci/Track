package informatics.uk.ac.ed.track.esm.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.Constants;

public class SurveyComplete extends AppCompatActivity {

    private TextView txtSurveyComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_complete);

        /* initialise UI controls */
        this.txtSurveyComplete = (TextView) findViewById(R.id.txtSurveyCompleteText);
        this.txtSurveyComplete.setText(
                String.format(getResources().getString(R.string.surveyCompleteMsg),
                        getResources().getString(R.string.app_name)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_survey_complete, menu);
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
