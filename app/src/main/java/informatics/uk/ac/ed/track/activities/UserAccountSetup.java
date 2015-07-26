package informatics.uk.ac.ed.track.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import informatics.uk.ac.ed.track.Constants;
import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.Utils;


public class UserAccountSetup extends AppCompatActivity {

    private EditText txtPassword, txtConfirmPassword;
    private TextView txtVwParticipantId;
    private TextInputLayout txtPassword_inpLyt, txtConfirmPassword_inpLyt;

    private String password, confirmPassword;

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
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        /* display participant ID */
        int participantId = settings.getInt(Constants.PARTICIPANT_ID, Constants.DEF_VALUE_INT);
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
        // TODO re-enable validation
        //boolean valid = this.setAndValidate();
        boolean valid = true;

        savePreferences();

        if (valid) {
            // save settings
            this.savePreferences();
            // proceed to next activity
            Intent intent = new Intent(this, SetupComplete.class);
            startActivity(intent);
        }
    }

    /**
     * Validate form input.
     * @return true if validation succeeds
     */
    private boolean setAndValidate() {
        boolean hasErrors = false;

        // get values
        this.password = this.txtPassword.getText().toString(); // do not trim so we can check for whitespace
        this.confirmPassword = this.txtConfirmPassword.getText().toString();

        // password
        if (!Utils.isValidPasswordLength(password)) {
            txtPassword_inpLyt.setError(getString(R.string.error_invalidPasswordLength));
            hasErrors = true;
        } else if (!Utils.isValidPassword(password)) {
            txtPassword_inpLyt.setError(getString(R.string.error_invalidPassword));
            hasErrors = true;
        } else {
            txtPassword_inpLyt.setError(null);
        }

        // confirm password
        if (!confirmPassword.equals(password)) {
            txtConfirmPassword_inpLyt.setError(getString(R.string.error_confirmPassword));
            hasErrors = true;
        } else {
            txtConfirmPassword_inpLyt.setError(null);
        }

        return !hasErrors;
    }

    public void savePreferences() {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(Constants.PARTICIPANT_PASSWORD_HASHED, Utils.computeHash(this.password));

        editor.apply();
    }
}
