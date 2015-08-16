package informatics.uk.ac.ed.track.esm.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;

import informatics.uk.ac.ed.track.esm.Constants;
import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.Utils;
import informatics.uk.ac.ed.track.esm.WebServiceHelper;


public class ResearcherSetup extends AppCompatActivity {

    private final static String LOG_TAG = "ResearcherSetup";

    private EditText txtUsername, txtPassword, txtParticipantId;
    private TextInputLayout txtUsername_inpLyt, txtPassword_inpLyt, txtParticipantId_inpLyt;
    private LinearLayout lytNoInternet;

    // Progress Dialog
    private ProgressDialog pDialog;

    private String username, password;
    private int participantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_researcher_setup);

        // initialise UI controls

        this.lytNoInternet = (LinearLayout) findViewById(R.id.lytNoInternet);

        this.txtUsername = (EditText) findViewById(R.id.txtUsername);
        this.txtPassword = (EditText) findViewById(R.id.txtPassword);
        this.txtParticipantId = (EditText) findViewById(R.id.txtParticipantId);

        this.txtUsername_inpLyt = (TextInputLayout) findViewById(R.id.txtUsername_InpLyt);
        this.txtPassword_inpLyt = (TextInputLayout) findViewById(R.id.txtPassword_InpLyt);
        this.txtParticipantId_inpLyt = (TextInputLayout) findViewById(R.id.txtParticipantId_inpLyt);

        // confirm user is connected to Internet
        // (error message will be shown if not)
        this.confirmInternetConnection();
    }

    public void btnNext_onClick(View view){
        this.confirmInternetConnection(); // will display/clear error msg as neccessary

        boolean valid = this.setAndValidate();

        if (valid) {
            // save settings
            this.savePreferences();

            // re-confirm whether an Internet connection is available
            if (this.confirmInternetConnection()) {
                // if yes, attempt login and, if successful, proceed to next activity
                new ResearcherLoginTask(this.getApplicationContext())
                        .execute(this.username, this.password);
            }
        }
    }

    /**
     * Set instance variables and validate form input.
     * @return true if validation succeeds
     */
    private boolean setAndValidate() {
        // get values
        this.username = Utils.getTrimmedText(this.txtUsername);
        this.password = Utils.getTrimmedText(this.txtPassword);

        String participantId_str = Utils.getTrimmedText(this.txtParticipantId);

        boolean hasErrors = false;

        // setAndValidate username
        if (this.username.isEmpty()) {
            txtUsername_inpLyt.setError(getString(R.string.error_missingResearcherUsername));
            hasErrors = true;
        } else {
            txtUsername_inpLyt.setError(null);
        }

        // setAndValidate password
        if (this.password.isEmpty()) {
            txtPassword_inpLyt.setError(getString(R.string.error_missingResearcherPassword));
            hasErrors = true;
        } else {
            txtPassword_inpLyt.setError(null);
        }

        // setAndValidate participant id
        if (participantId_str.isEmpty()) {
            txtParticipantId_inpLyt.setError(getString(R.string.error_enterParticipantId));
            hasErrors = true;
        } else if (!TextUtils.isDigitsOnly(participantId_str)) {
            txtParticipantId_inpLyt.setError(getString(R.string.error_enterValidNumber));
            hasErrors = true;
        } else {
            this.participantId = Integer.parseInt(participantId_str);
            txtParticipantId_inpLyt.setError(null);
        }

        return !hasErrors;
    }

    private void savePreferences() {
        // getDefaultSharedPreferences() uses a default preference-file name.
        // This default is set per application, so all activities in the same app context
        // can access it easily as in the following example:
        // http://stackoverflow.com/questions/5946135/difference-between-getdefaultsharedpreferences-and-getsharedpreferences
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt(Constants.PARTICIPANT_ID, this.participantId);
        editor.apply();
    }

    private class ResearcherLoginTask extends AsyncTask<String, Void, JSONObject> {

        private SharedPreferences settings;

        private boolean success; // true if login was successful
        private boolean error; // true if an exception was thrown

        public ResearcherLoginTask(Context appContext) {
            this.settings = PreferenceManager.getDefaultSharedPreferences(appContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ResearcherSetup.this);
            pDialog.setMessage("Verifying credentials...");
            pDialog.setIndeterminate(true);
            pDialog.show();
            // set private variables to default values
            this.error = false;
            this.success = false;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            Resources res = getResources();

            String url = (res.getString(R.string.trackWebServiceUrl)
                    + res.getString(R.string.loginWebMethod));

            String username = strings[0];
            String password = strings[1];

            ContentValues params = new ContentValues();
            params.put(WebServiceHelper.PARAM_RESEARCHER_USERNAME, username);
            params.put(WebServiceHelper.PARAM_RESEARCHER_PASSWORD, password);

            // make request and get response
            JSONObject jsonObject =  WebServiceHelper.makeHttpRequest(url,
                    WebServiceHelper.RequestMethod.POST, params);

            if (jsonObject != null) {
                try {
                    int successCode = jsonObject.getInt(WebServiceHelper.OUT_PARAM_SUCCESS);
                    this.success = (successCode == WebServiceHelper.SUCCESS_CODE);
                } catch (JSONException je) {
                    Log.e(LOG_TAG, "Error parsing JSON object from server.", je);
                    this.error = true;
                }
            } else {
                // no object returned from server - something must have gone wrong!
                this.error = true;
            }

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            // dismiss the dialog
            pDialog.dismiss();
            // check if an error was thrown
            if (this.error) {
                // if yes, show error message
                Toast.makeText(ResearcherSetup.this,
                        getString(R.string.error_verifyingCredentials), Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences.Editor editor = this.settings.edit();

                // if not, check whether login was successful
                if (this.success) {
                    // if yes, reset invalid credential count to zero
                    // (count is only for *subsequent* invalid credentials)
                    editor.putInt(Constants.INVALID_RESEARCHER_CREDENTIALS_COUNT, 0);
                    editor.apply();

                    // and proceed to next activity
                    Intent intent = new Intent(ResearcherSetup.this, StudyConfiguration.class);
                    startActivity(intent);
                } else {
                    // otherwise, show error message
                    Toast.makeText(ResearcherSetup.this,
                            getString(R.string.error_invalidResearcherCredentials),
                            Toast.LENGTH_SHORT).show();

                    // and increment incorrect count
                    int invalidCount = this.settings.getInt(
                            Constants.INVALID_RESEARCHER_CREDENTIALS_COUNT, Constants.DEF_VALUE_INT);

                    if (invalidCount == Constants.DEF_VALUE_INT) {
                        invalidCount = 1;
                    } else {
                        invalidCount++;
                    }

                    int maxInvalid = getResources().getInteger(
                            R.integer.max_allowed_invalid_researchr_credentials);

                    if (invalidCount == maxInvalid) {
                        // if maximum count has been reached
                        // go to app locked screen
                        Intent intent = new Intent(ResearcherSetup.this, AppLocked.class);
                        // close existing activity stack and start new root
                        // to prevent user from going back to this or previous screens
                        intent.setFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        // let activity know this is the first time so it can update shared prefs
                        intent.putExtra(Constants.APP_LOCKED_FIRST_TIME, true);
                        startActivity(intent);
                    } else {
                        // otherwise save updated counter
                        editor.putInt(Constants.INVALID_RESEARCHER_CREDENTIALS_COUNT, invalidCount);
                        editor.apply();
                    }
                }
            }

        }
    }

    /**
     *
     * @return True if data network is available.
     */
    private boolean confirmInternetConnection(){
        boolean isConnected = Utils.isConnectedToInternet(this);

        if (isConnected) {
            this.lytNoInternet.setVisibility(View.GONE);
        } else {
            this.lytNoInternet.setVisibility(View.VISIBLE);
        }

        return isConnected;
    }

}
