package informatics.uk.ac.ed.esm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;


public class ResearcherSetup extends AppCompatActivity {

    private EditText txtEmail, txtConfirmEmail, txtPassword, txtConfirmPassword, txtParticipantId;
    private TextInputLayout txtEmail_inpLyt, txtConfirmEmail_inpLyt, txtPassword_inpLyt,
            txtConfirmPassword_inpLyt, txtParticipantId_inpLyt;

    private String emailAddress, confirmEmail, password, confirmPassword, participantId_str;
    private int participantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_researcher_setup);

        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtConfirmEmail = (EditText) findViewById(R.id.txtConfirmEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);
        txtParticipantId = (EditText) findViewById(R.id.txtParticipantId);

        txtEmail_inpLyt = (TextInputLayout) findViewById(R.id.txtEmail_InpLyt);
        txtConfirmEmail_inpLyt = (TextInputLayout) findViewById(R.id.txtConfirmEmail_InpLyt);
        txtPassword_inpLyt = (TextInputLayout) findViewById(R.id.txtPassword_InpLyt);
        txtConfirmPassword_inpLyt = (TextInputLayout) findViewById(R.id.txtConfirmPassword_InpLyt);
        txtParticipantId_inpLyt = (TextInputLayout) findViewById(R.id.txtParticipantId_inpLyt);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_researcher_setup, menu);
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
        // get values
        this.emailAddress = Utils.getTrimmedText(this.txtEmail);
        this.confirmEmail = Utils.getTrimmedText(this.txtConfirmEmail);
        this.password = this.txtPassword.getText().toString(); // do not trim so we can check for whitespace
        this.confirmPassword = this.txtConfirmPassword.getText().toString();
        this.participantId_str = Utils.getTrimmedText(this.txtParticipantId);

        if (this.validate()) {
            // save settings
            this.savePreferences();
            // proceed to next activity
            Intent intent = new Intent(this, StudyConfiguration.class);
            startActivity(intent);
        }
    }

    /**
     * Validate form input.
     * @return true if validation succeeds
     */
    public boolean validate() {
        boolean hasErrors = false;

        // validate email address
        if (emailAddress.isEmpty()) {
            txtEmail_inpLyt.setError(getString(R.string.error_missingEmail));
            hasErrors = true;
        } else if (!Utils.isValidEmail(emailAddress)){
            txtEmail_inpLyt.setError(getString(R.string.error_enterValidEmail));
            hasErrors = true;
        } else {
            txtEmail_inpLyt.setError(null);
        }

        // validate confirm email
        if (!confirmEmail.equals(emailAddress)) {
            txtConfirmEmail_inpLyt.setError(getString(R.string.error_confirmEmail));
            hasErrors = true;
        } else {
            txtConfirmEmail_inpLyt.setError(null);
        }

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

        // validate participant id
        if (participantId_str.isEmpty()) {
            txtParticipantId_inpLyt.setError(getString(R.string.error_enterParticipantId));
            hasErrors = true;
        } else if (!TextUtils.isDigitsOnly(participantId_str)) {
            txtParticipantId_inpLyt.setError(getString(R.string.error_enterValidNumber));
            hasErrors = true;
        } else {
            participantId = Integer.parseInt(participantId_str);
            txtParticipantId_inpLyt.setError(null);
        }

        return !hasErrors;
    }

    public void savePreferences() {
        // getDefaultSharedPreferences() uses a default preference-file name.
        // This default is set per application, so all activities in the same app context
        // can access it easily as in the following example:
        // http://stackoverflow.com/questions/5946135/difference-between-getdefaultsharedpreferences-and-getsharedpreferences
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(Constants.RESEARCHER_EMAIL, this.emailAddress);
        editor.putString(Constants.RESEARCHER_PASSWORD_HASHED, Utils.computeHash(this.password));
        editor.putInt(Constants.PARTICIPANT_ID, this.participantId);

        editor.commit();
    }
}
