package informatics.uk.ac.ed.track.esm.activities;

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

import java.util.Calendar;
import java.util.GregorianCalendar;

import informatics.uk.ac.ed.track.esm.Constants;
import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.Utils;


public class UserAccountSetup extends AppCompatActivity {

    private EditText txtPassword, txtConfirmPassword;
    private TextInputLayout txtPassword_inpLyt, txtConfirmPassword_inpLyt;

    private String password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_setup);

        /* initialise UI controls */
        this.txtPassword = (EditText) findViewById(R.id.txtPassword);
        this.txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);
        this.txtPassword_inpLyt = (TextInputLayout) findViewById(R.id.txtPassword_InpLyt);
        this.txtConfirmPassword_inpLyt = (TextInputLayout) findViewById(R.id.txtConfirmPassword_InpLyt);
    }

    public void btnNext_onClick(View view) {
        boolean valid = this.setAndValidate();

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
        this.password = Utils.getTrimmedText(this.txtPassword);
        this.confirmPassword = Utils.getTrimmedText(this.txtConfirmPassword);

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
        Utils.saveNewUserPasswordToPreferences(this, this.password, true);
    }
}
