package informatics.uk.ac.ed.track.esm.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

import informatics.uk.ac.ed.track.esm.Constants;
import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.Utils;
import informatics.uk.ac.ed.track.lib.TrackQuestion;
import informatics.uk.ac.ed.track.esm.services.LocalDatabaseService;

public abstract class TrackQuestionActivity extends AppCompatActivity {

    private static final String LOG_TAG = "TRACK.TrackQuestActvty";

    protected String getIntentSurveyResponses() {
        Intent intent = getIntent();

        /* get survey responses so far */
        return intent.getStringExtra(Constants.SURVEY_RESPONSES);
    }

    protected String addAnswerToSurveyResponses(String columnName, String response) {
        String surveyResponses = this.getIntentSurveyResponses();

        if (surveyResponses == null) {
            surveyResponses = "";
        } else {
            surveyResponses += Constants.SURVEY_RESPONSES_RESPONSE_DELIMITER;
        }

        surveyResponses += columnName + Constants.SURVEY_RESPONSES_CONTENT_VALUE_DELIMITER+
                response;

        return surveyResponses;
    }

    public void displayTitleQuestionAndPrefix(TrackQuestion question, int toolbarViewId,
                                              int toolbarTitleTxtViewId,
                                              int toolbarSubTitleTxtViewId,
                                              int questionTextTxtViewId,
                                              int questionPrefixTextViewId) {
        if (question == null) {
            String eMsg = "Question cannot be null";
            Exception npe = new NullPointerException("question cannot be null");
            Log.e(LOG_TAG, eMsg, npe);
        }

        this.displayTitle(question, toolbarViewId, toolbarTitleTxtViewId, toolbarSubTitleTxtViewId);
        this.displayQuestionPrefix(question, questionPrefixTextViewId);
        this.displayQuestionText(question, questionTextTxtViewId);
    }

    private void displayTitle(TrackQuestion question, int toolbarViewId,
                              int toolbarTxtViewId, int toolbarSubTitleTxtViewId) {
        String title = question.getTitle();
        String subTitle = question.getSubTitle();

        if (Utils.isNullOrEmpty(title) && (Utils.isNullOrEmpty(subTitle))) {
            // if no title or sub-title, hide toolbar
            Toolbar toolbar = (Toolbar) findViewById(toolbarViewId);
            toolbar.setVisibility(View.GONE);
        } else {
            // show/hide title
            TextView titleTxtVw = (TextView) findViewById(toolbarTxtViewId);
            if (Utils.isNullOrEmpty(title)) {
                titleTxtVw.setVisibility(View.GONE);
            } else {
                titleTxtVw.setText(title);
            }
            // show/hide sub-title
            TextView subTitleTxtView = (TextView) findViewById(toolbarSubTitleTxtViewId);
            if (Utils.isNullOrEmpty(subTitle)) {
                subTitleTxtView.setVisibility(View.GONE);
            } else {
                subTitleTxtView.setText(subTitle);
            }
        }
    }

    private void displayQuestionText(TrackQuestion question, int questionTextTxtViewId) {
        String questionText = question.getQuestionText();
        TextView txtVwQuestionText = (TextView) findViewById(questionTextTxtViewId);

        if (Utils.isNullOrEmpty(questionText)) {
            txtVwQuestionText.setVisibility(View.GONE);
        } else {
            txtVwQuestionText.setText(questionText);
        }
    }

    private void displayQuestionPrefix(TrackQuestion question, int questionPrefixTextViewId) {
        String questionPrefix = question.getQuestionPrefix();
        TextView txtVwQuestionPrefix = (TextView) findViewById(questionPrefixTextViewId);

        if (Utils.isNullOrEmpty(questionPrefix)) {
            txtVwQuestionPrefix.setVisibility(View.GONE);
        } else {
            txtVwQuestionPrefix.setText(questionPrefix);
        }
    }

    public void displayNavigationButtons(TrackQuestion question, Intent activityIntent,
                                         int btnPreviousViewId, int btnNextViewId,
                                         int btnFinishViewId) {
        boolean isFirstQuestion =
                activityIntent.getBooleanExtra(Constants.IS_FIRST_QUESTION,
                        Constants.DEF_VALUE_BOOL);

        boolean isLastQuestion =
                question.getNextQuestionId() == -1;

        Button btnPrevious = (Button) findViewById(btnPreviousViewId);
        Button btnNext = (Button) findViewById(btnNextViewId);
        Button btnFinish = (Button) findViewById(btnFinishViewId);

        if (isFirstQuestion) {
            btnPrevious.setVisibility(View.GONE);
        } else {
            btnPrevious.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TrackQuestionActivity.super.onBackPressed();
                }
            });
        }

        if (isLastQuestion) {
            btnFinish.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.INVISIBLE); // do not set to GONE as scrollview is relatively positioned with btnNext
            // set onClick listener
            btnFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isValid()) {
                        Resources res = getResources();

                        // show dialog to confirm whether use wants to submit
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                TrackQuestionActivity.this, R.style.AlertDialogTheme);
                        // Setting Dialog Title
                        alertDialog.setTitle(res.getString(R.string.submitDialogTitle));
                        // Setting Dialog Message
                        alertDialog.setMessage(res.getString(R.string.submitDialogMessage));
                        // Setting Positive "Yes" Button
                        alertDialog.setPositiveButton(
                                res.getString(R.string.submitDialogPositiveBtn),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // get survey completed time
                                        Calendar cal = GregorianCalendar.getInstance();
                                        long surveyCompletedTime = cal.getTimeInMillis();

                                        // save survey completed time to shared preferences
                                        SharedPreferences settings = PreferenceManager.
                                                getDefaultSharedPreferences(getApplicationContext());
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putLong(Constants.LAST_SURVEY_COMPLETED_TIME_MILLIS,
                                                surveyCompletedTime);
                                        editor.apply();

                                        // launch two intents:

                                        // one an Intent Service to save to Local & External DBs
                                        // (using Background Thread)
                                        Intent localDbService = new Intent(TrackQuestionActivity.this,
                                                LocalDatabaseService.class);
                                        localDbService.putExtra(Constants.LAST_NOTIFICATION_TIME_MILLIS,
                                                settings.getLong(Constants.LAST_NOTIFICATION_TIME_MILLIS,
                                                        Constants.DEF_VALUE_LNG));
                                        localDbService.putExtra(Constants.LAST_SURVEY_COMPLETED_TIME_MILLIS,
                                                surveyCompletedTime);
                                        localDbService.putExtra(Constants.SURVEY_RESPONSES,
                                                getSurveyResponsesForNextIntent());
                                        startService(localDbService);

                                        // and one to notify user that survey has been complete
                                        Intent intent = new Intent(TrackQuestionActivity.this,
                                                SurveyComplete.class);
                                        // close existing activity stack and start new root
                                        // to prevent user from going back to survey after submit
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                });
                        alertDialog.setNegativeButton(
                                res.getString(R.string.submitDialogNegativeBtn),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        alertDialog.show();
                    }
                }
            });
        } else {
            btnNext.setVisibility(View.VISIBLE);
            btnFinish.setVisibility(View.GONE);
            // set onClick listener
            btnNext.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isValid()) {
                        launchNextQuestion();
                    }
                }
            });
        }

    }

    public abstract boolean isValid();

    public abstract void launchNextQuestion();

    public abstract String getSurveyResponsesForNextIntent();
}
