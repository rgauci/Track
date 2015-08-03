package informatics.uk.ac.ed.track.esm.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.Constants;
import informatics.uk.ac.ed.track.esm.Utils;

public class ResetPassword extends AppCompatActivity {

    private EditText txtPassword, txtConfirmPassword;
    private TextInputLayout txtPassword_inpLyt, txtConfirmPassword_inpLyt;

    private String password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // initialise UI controls
        this.txtPassword = (EditText) findViewById(R.id.txtPassword);
        this.txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);
        this.txtPassword_inpLyt = (TextInputLayout) findViewById(R.id.txtPassword_InpLyt);
        this.txtConfirmPassword_inpLyt = (TextInputLayout) findViewById(R.id.txtConfirmPassword_InpLyt);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reset_password, menu);
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
        // validate
        boolean valid = this.setAndValidate();

        if (valid) {
            // show confirmation dialog
            Resources res = getResources();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    ResetPassword.this, R.style.AlertDialogTheme);
            alertDialog.setTitle(res.getString(R.string.resetPasswordDialogTitle));
            alertDialog.setMessage(res.getString(R.string.resetPasswordDialogMessage));
            alertDialog.setPositiveButton(
                    res.getString(R.string.resetPasswordDialogPositiveBtn),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // reset password (updated SharedPreferences)
                            savePreferences();
                            // and proceed
                            proceed();
                        }
                    });
            // Setting Negative "NO" Button
            alertDialog.setNegativeButton(
                    res.getString(R.string.resetPasswordDialogNegativeBtn),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            // Showing Alert Message
            alertDialog.show();
        }
    }

    private void proceed() {
        // display Toast
        Toast toast = Toast.makeText(this,
                getResources().getString(R.string.passwordHasBeenResetMsg), Toast.LENGTH_LONG);
        toast.show();
        // go back to home screen
        Intent intent = new Intent(ResetPassword.this, HomeActivity.class);
        startActivity(intent);
    }

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
        Calendar calendar = GregorianCalendar.getInstance();
        long passwordResetTime = calendar.getTimeInMillis();

        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(Constants.PARTICIPANT_PASSWORD_HASHED, Utils.computeHash(this.password));
        editor.putLong(Constants.PARTICIPANT_PASSWORD_RESET_TIME_MILLIS, passwordResetTime);

        editor.apply();
    }
}
