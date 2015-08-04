package informatics.uk.ac.ed.track.esm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import informatics.uk.ac.ed.track.esm.Constants;
import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.Utils;
import informatics.uk.ac.ed.track.feedback.activities.FeedbackViewPager;


public class UserLogin extends AppCompatActivity {

    private EditText txtPassword;
    private TextInputLayout txtPassword_inpLyt;

    /**
     * Enum identifying which activity to launch upon user success.
     * Should be passed as an intent Extra (activityToLaunchOnLogin)
     */
    public enum LoginSuccessActivity {
        FeedbackViewPager
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        // initialise UI controls
        this.txtPassword = (EditText) findViewById(R.id.txtPassword);
        this.txtPassword_inpLyt = (TextInputLayout) findViewById(R.id.txtPassword_InpLyt);
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

    public void btnNext_onClick(View view) {
        boolean valid = this.validate();

        if (valid) {
            // mark user as logged in
            SharedPreferences settings =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(Constants.USER_IS_LOGGED_IN, true);
            editor.apply();

            // proceed to next activity
            LoginSuccessActivity nextActivity = (LoginSuccessActivity) getIntent()
                    .getSerializableExtra(Constants.ACTIVITY_TO_LAUNCH_ON_LOGIN_SUCCESS);

            Intent intent;

            switch (nextActivity) {
                case FeedbackViewPager:
                    intent = new Intent(this, FeedbackViewPager.class);
                    break;
                default:
                    intent = new Intent(this, HomeActivity.class);
            }

            startActivity(intent);
        }
    }

    public void txtVwForgotPassword_onClick(View view) {
        TextView txtVwForgotPassword = (TextView) findViewById(R.id.txtVwForgotPassword);
        txtVwForgotPassword.setTextColor(getResources().getColor(R.color.accent));

        Intent intent = new Intent(this, ResetPassword.class);
        startActivity(intent);
    }

    private boolean validate() {
        return Utils.validateUserPassword(this, this.txtPassword, this.txtPassword_inpLyt);
    }
}
