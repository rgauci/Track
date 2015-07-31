package informatics.uk.ac.ed.track.esm.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import informatics.uk.ac.ed.track.esm.Constants;
import informatics.uk.ac.ed.track.R;


public class UserLogin extends AppCompatActivity {

    private TextView txtVwParticipantId;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        /* initialise UI controls */
        txtVwParticipantId = (TextView) findViewById(R.id.txtVwParticipantId);

        /* get shared preferences */
        this.settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        /* display participant ID */
        int participantId = settings.getInt(Constants.PARTICIPANT_ID, -1);
        txtVwParticipantId.setText(String.valueOf(participantId));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_login, menu);
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
}
