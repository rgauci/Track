package informatics.uk.ac.ed.track.esm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.Constants;

public class ResearchParticipation extends AppCompatActivity {

    private RadioButton rdBtnYes, rdBtnNo;
    private TextView txtVwQuestion, txtVwMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_research_participation);

        // initialise UI controls
        this.rdBtnYes = (RadioButton) findViewById(R.id.rdBtnYes);
        this.rdBtnNo = (RadioButton) findViewById(R.id.rdBtnNo);

        this.txtVwQuestion = (TextView) findViewById(R.id.txtVwQuestion);
        this.txtVwMsg = (TextView) findViewById(R.id.txtVwMsg);

        String researcherFirstName = getString(R.string.researcherFirstName);
        String researcherLastName = getString(R.string.researcherLastName);
        this.txtVwQuestion.setText(String.format(getString(R.string.participantQuestion),
                researcherFirstName, researcherLastName));
        this.txtVwMsg.setText(String.format(getString(R.string.participantYesMsg),
                researcherFirstName));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_research_participation, menu);
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

    public void btnNext_onClick(View view) {
        boolean hasErrors = false;
        boolean isParticipant = false;

        if (this.rdBtnYes.isChecked()) {
            isParticipant = true;
        } else if (this.rdBtnNo.isChecked()) {
            isParticipant = false;
        } else {
            hasErrors = true;
            Toast toast = Toast.makeText(this,
                    getString(R.string.error_answerToProceed), Toast.LENGTH_SHORT);
            toast.show();
        }

        if (hasErrors) {
            return;
        }

        // if option has been selected, save to SharedPreferences
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean(Constants.IS_RESEARCH_PARTICIPANT, isParticipant);
        editor.apply();

        // proceed to next activity
        Intent intent;
        if (isParticipant) {
            intent = new Intent(this, ResearcherSetup.class);
        } else {
            intent = new Intent(this, StudyConfiguration.class);
        }

        startActivity(intent);
    }
}
