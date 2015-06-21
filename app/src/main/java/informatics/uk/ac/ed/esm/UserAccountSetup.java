package informatics.uk.ac.ed.esm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;


public class UserAccountSetup extends AppCompatActivity {

    private EditText txtPassword, txtConfirmPassword;
    private TextView txtVwParticipantId;
    private TextInputLayout txtPassword_inpLyt, txtConfirmPassword_inpLyt;

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_setup);

        /* initialise UI controls */
        txtVwParticipantId = (TextView) findViewById(R.id.txtVwParticipantId);

        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);

        txtPassword_inpLyt = (TextInputLayout) findViewById(R.id.txtPassword_InpLyt);
        txtConfirmPassword_inpLyt = (TextInputLayout) findViewById(R.id.txtConfirmPassword_InpLyt);

        /* get shared preferences */
        this.settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        /* display participant ID */
        int participantId = settings.getInt(Constants.PARTICIPANT_ID, -1);
        txtVwParticipantId.setText(String.valueOf(participantId));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_account_setup, menu);
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

        // get values
        String password = this.txtPassword.getText().toString(); // do not trim so we can check for whitespace
        String confirmPassword = this.txtConfirmPassword.getText().toString();

        // TODO re-enable validation
        /*
        // validate password
        if (!Utils.isValidPasswordLength(password)) {
            txtPassword_inpLyt.setError(getString(R.string.error_invalidPasswordLength));
            hasErrors = true;
        } else if (!Utils.isValidPassword(password)) {
            txtPassword_inpLyt.setError(getString(R.string.error_invalidPassword));
            hasErrors = true;
        } else {
            txtPassword_inpLyt.setError(null);
        }

        // validate confirm password
        if (!confirmPassword.equals(password)) {
            txtConfirmPassword_inpLyt.setError(getString(R.string.error_confirmPassword));
            hasErrors = true;
        } else {
            txtConfirmPassword_inpLyt.setError(null);
        }
        */

        if (!hasErrors) {
            Intent intent = new Intent(this, SetupComplete.class);
            startActivity(intent);
        }
    }
}
