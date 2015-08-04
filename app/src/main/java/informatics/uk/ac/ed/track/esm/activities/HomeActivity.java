package informatics.uk.ac.ed.track.esm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import informatics.uk.ac.ed.track.esm.Constants;
import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.DatabaseHelper;
import informatics.uk.ac.ed.track.esm.Utils;
import informatics.uk.ac.ed.track.feedback.activities.FeedbackViewPager;

public class HomeActivity extends AppCompatActivity {

    private Button btnLaunchDemo, btnViewFeedback, btnChangePassword;
    private TextView txtVwTitle, txtVwSubTitle, txtVwMsg;

    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activity);

        // initialise UI controls
        btnLaunchDemo = (Button) findViewById(R.id.btnLaunchDemo);
        btnViewFeedback = (Button) findViewById(R.id.btnViewFeedback);
        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        txtVwTitle = (TextView) findViewById(R.id.txtVwTitle);
        txtVwSubTitle = (TextView) findViewById(R.id.txtVwSubTitle);
        txtVwMsg = (TextView) findViewById(R.id.txtVwMsg);

        // initialise resoureces
        this.res = getResources();

        // set text, title & msg (if available)
        Intent intent = getIntent();
        String title = intent.getStringExtra(Constants.HOME_SCREEN_TITLE);
        String subTitle = intent.getStringExtra(Constants.HOME_SCREEN_SUBTITLE);
        String msg = intent.getStringExtra(Constants.HOME_SCREEN_MSG);

        // if title, subtitle & msg are all empty
        // show defaults
        if (Utils.isNullOrEmpty(title) && Utils.isNullOrEmpty(subTitle) && Utils.isNullOrEmpty(msg)) {
            title = res.getString(R.string.noSurveyAvailableTitle);
            subTitle = res.getString(R.string.noSurveyAvailableSubTitle);
        }

        this.showOrHideText(this.txtVwTitle, title);
        this.showOrHideText(this.txtVwSubTitle, subTitle);
        this.showOrHideText(this.txtVwMsg, msg);

        // set menu buttons onClick listeners
        btnLaunchDemo.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, DemoIntro.class);
                startActivity(intent);
            }
        });

        // show / hide feedback & change password buttons
        // depending on whether feedback module is enabled
        boolean useFeedbackModule = this.res.getBoolean(R.bool.useFeedbackModule);
        if (useFeedbackModule) {
            btnViewFeedback.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchFeedbackModule();
                }
            });

            btnChangePassword.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, ChangePassword.class);
                    startActivity(intent);
                }
            });
        } else {
            btnViewFeedback.setVisibility(View.GONE);
            btnChangePassword.setVisibility(View.GONE);
        }

        // TODO remove this and corresponding button
        Button relaunchSetup = (Button) findViewById(R.id.btnRelaunchSetup);
        relaunchSetup.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ResearcherSetup.class);
                startActivity(intent);
            }
        });

        // TODO remove this and corresponding button
        Button launchSurvey = (Button) findViewById(R.id.btnLaunchSurvey);
        launchSurvey.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Utils.getLaunchSurveyIntent(getApplicationContext());
                startActivity(intent);
            }
        });
    }

    private void launchFeedbackModule(){
        // check if enough surveys have been completed for survey activation
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int minimumSurveys = settings.getInt(
                Constants.MINIMUM_SURVEYS_FOR_FEEDBACK_ACTIVATION,
                Constants.DEF_VALUE_INT);

        long passwordResetTime = settings.getLong(
                Constants.PARTICIPANT_PASSWORD_RESET_TIME_MILLIS,
                Constants.DEF_VALUE_LNG);

        // get number of surveys completed from DB (since password was last set/reset)
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT COUNT(*) FROM `" + DatabaseHelper.TABLE_NAME_SURVEY_RESPONSES + "` " +
                "WHERE `" + DatabaseHelper.COLUMN_NAME_SURVEY_COMPLETED_TIME + "` " +
                "> DATETIME('" + dbHelper.getDateInIsoFormat(passwordResetTime) + "')";

        SQLiteStatement statement = db.compileStatement(sql);
        long surveysCompleted = statement.simpleQueryForLong();
        db.close();

        if (surveysCompleted >= minimumSurveys) {
            // if feedback feature is activated (min # surveys completed)
            if (Utils.getIsUserLoggedIn(getApplicationContext())) {
                // if user is already logged in, show FeedbackViewPager activity
                Intent intent = new Intent(HomeActivity.this, FeedbackViewPager.class);
                startActivity(intent);
            } else {
                // otherwise, launch Log In Screen
                // passing an extra to tell Log In screen to launch FeedbackViewPager
                // on successful login
                Intent intent = new Intent(HomeActivity.this, UserLogin.class);
                intent.putExtra(Constants.ACTIVITY_TO_LAUNCH_ON_LOGIN_SUCCESS,
                        UserLogin.LoginSuccessActivity.FeedbackViewPager);
                startActivity(intent);
            }
        } else {
            // show dialog informing user feature is still locked
            long remaining = minimumSurveys - surveysCompleted;
            String msg =
                    (remaining > 1) ?
                            res.getString(R.string.feedbackLockedDialogMessage_ManyLeft)
                            :
                            res.getString(R.string.feedbackLockedDialogMessage_OneLeft);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    HomeActivity.this, R.style.AlertDialogTheme);
            alertDialog.setTitle(res.getString(R.string.feedbackLockedDialogTitle));
            alertDialog.setMessage(String.format(msg, remaining));
            alertDialog.show();
        }
    }

    private void showOrHideText(TextView txtVw, String text) {
        if (Utils.isNullOrEmpty(text)) {
            txtVw.setVisibility(View.GONE);
        } else {
            txtVw.setText(text);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_default, menu);
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
