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

    public void btnNext_onClick(View view){
        Resources res = getResources();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
