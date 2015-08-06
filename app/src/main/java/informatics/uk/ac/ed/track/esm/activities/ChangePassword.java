package informatics.uk.ac.ed.track.esm.activities;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.Utils;

public class ChangePassword extends AppCompatActivity {

    private EditText txtOldPassword ,txtPassword, txtConfirmPassword;
    private TextInputLayout txtOldPassword_inpLyt, txtPassword_inpLyt, txtConfirmPassword_inpLyt;

    String password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // initialise UI controls
        this.txtOldPassword = (EditText) findViewById(R.id.txtOldPassword);
        this.txtPassword = (EditText) findViewById(R.id.txtPassword);
        this.txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);

        this.txtOldPassword_inpLyt =
                (TextInputLayout) findViewById(R.id.txtOldPassword_InpLyt);
        this.txtPassword_inpLyt =
                (TextInputLayout) findViewById(R.id.txtPassword_InpLyt);
        this.txtConfirmPassword_inpLyt =
                (TextInputLayout) findViewById(R.id.txtConfirmPassword_InpLyt);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_password, menu);
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

    public void txtVwForgotPassword_onClick(View view) {
        TextView txtVwForgotPassword = (TextView) findViewById(R.id.txtVwForgotPassword);
        txtVwForgotPassword.setTextColor(getResources().getColor(R.color.accent));

        Intent intent = new Intent(this, ResetPassword.class);
        startActivity(intent);
    }

    public void btnNext_onClick(View view) {
        boolean valid = this.setAndValidate();

        if (valid) {
            // update password in Shared Preferences
            Utils.saveNewUserPasswordToPreferences(this, this.password, false);

            // display Toast
            Toast toast = Toast.makeText(this,
                    getResources().getString(R.string.passwordHasBeenChangedMsg), Toast.LENGTH_LONG);
            toast.show();

            // go back to home activity
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }

    private boolean setAndValidate() {
        boolean hasErrors = false;

        if (!Utils.validateUserPassword(this, this.txtOldPassword, this.txtOldPassword_inpLyt)) {
            hasErrors = true;
        }

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
}
