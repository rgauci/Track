package informatics.uk.ac.ed.track.esm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import informatics.uk.ac.ed.track.esm.Constants;
import informatics.uk.ac.ed.track.esm.DatabaseHelper;
import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.Utils;
import informatics.uk.ac.ed.track.lib.SurveyHelper;
import informatics.uk.ac.ed.track.lib.TrackQuestion;
import informatics.uk.ac.ed.track.lib.TrackQuestionType;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_TIME_MILLIS = 1000;
    private static final String LOG_TAG = "TRACK.SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                // get settings
                SharedPreferences settings =
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                boolean surveyImportComplete =
                        settings.getBoolean(Constants.SURVEY_IMPORT_COMPLETE, Constants.DEF_VALUE_BOOL);
                boolean setupComplete =
                        settings.getBoolean(Constants.SETUP_COMPLETE, Constants.DEF_VALUE_BOOL);

                // set user as logged out
                logUserOut(settings);

                // if survey has not yet been imported from survey_json.txt
                // import survey: create shared preference file for each question
                if (!surveyImportComplete) {
                    importSurvey(settings); // NOTE - re-importing survey will DROP & re-create DB!
                }

                // check whether app has been locked due to invalid researcher credentials
                // before doing anything else
                boolean appLocked = false;
                if (settings.getBoolean(Constants.IS_APP_LOCKED, Constants.DEF_VALUE_BOOL)) {
                    Calendar calendar = GregorianCalendar.getInstance();
                    long currentTimeMillis = calendar.getTimeInMillis();

                    long maxLockedTimeMillis =
                            getResources().getInteger(R.integer.app_locked_hours) * 60 * 60 * 1000;
                    long lockedTimeMillis = settings.getLong(Constants.APP_LOCKED_TIME_MILLIS, Constants.DEF_VALUE_LNG);

                    if ((lockedTimeMillis + maxLockedTimeMillis) > currentTimeMillis) {
                        appLocked = true;
                        Intent intent = new Intent(SplashScreen.this, AppLocked.class);
                        startActivity(intent);
                    } else {
                        // if enough time has passed, set app as unlocked
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean(Constants.IS_APP_LOCKED, false);
                        editor.apply();
                    }
                }

                if (!appLocked) {
                    // if setup is complete
                    if (setupComplete) {
                        boolean surveyAvailable = false;

                        long lastNotifTime =
                                settings.getLong(Constants.LAST_NOTIFICATION_TIME_MILLIS,
                                        Constants.DEF_VALUE_LNG);
                        long lastCompletedTime =
                                settings.getLong(Constants.LAST_SURVEY_COMPLETED_TIME_MILLIS,
                                        Constants.DEF_VALUE_LNG);

                        if (lastCompletedTime < lastNotifTime) {
                            long notifWindowMillis =
                                    settings.getInt(Constants.NOTIFICATION_WINDOW_MINUTES,
                                            Constants.DEF_VALUE_INT) * 60 * 1000;

                            Calendar cal = GregorianCalendar.getInstance();
                            long currentTime = cal.getTimeInMillis();

                            boolean expired = (currentTime > (lastNotifTime + notifWindowMillis));

                            if (!expired) {
                                surveyAvailable = true;
                            }
                        }

                        Intent intent;

                        if (surveyAvailable) {
                            // if survey is available, launch
                            intent = Utils.getLaunchSurveyIntent(getApplicationContext());
                        } else {
                            // otherwise show no survey currently available screen
                            intent = new Intent(SplashScreen.this, HomeActivity.class);
                        }

                        startActivity(intent);
                    } else {
                        // otherwise start setup
                        Intent intent = new Intent(SplashScreen.this, ResearchParticipation.class);
                        startActivity(intent);
                    }
                }
            }
        }, SPLASH_DISPLAY_TIME_MILLIS);
    }

    private void logUserOut(SharedPreferences settings) {
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putBoolean(Constants.USER_IS_LOGGED_IN, false);
        settingsEditor.apply();
    }

    private void importSurvey(SharedPreferences settings) {
        boolean resourceFileRead = false;

        // read survey from raw resource file
        InputStream inpStream = getResources().openRawResource(R.raw.survey_json);
        Writer writer = new StringWriter();
        try {
            char[] buffer = new char[inpStream.available()];
            Reader reader = new BufferedReader(new InputStreamReader(inpStream));
            int numCharsRead;
            while ((numCharsRead = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, numCharsRead);
            }
            resourceFileRead = true;
        } catch (IOException ioe) {
            Log.e(LOG_TAG, "Error while trying to parse surve file.", ioe);
        } finally {
            try {
                inpStream.close();
            } catch (IOException ioe) {
                Log.e(LOG_TAG, "Error trying to close input stream.", ioe);
            }
        }

        if (!resourceFileRead) {
            return;
        }

        /* convert from JSON string to JSON objects */
        String jsonSurvey = writer.toString();
        ArrayList<TrackQuestion> questions = SurveyHelper.fromJson(jsonSurvey);

        // for each question, create a preferences file containing:
        // - the question object serialised in JSON format
        // and save the question type in the shared preferences
        SharedPreferences.Editor settingsEditor = settings.edit();
        Gson questionGson = new Gson();

        // save SQL-formatted String for columns to be created
        StringBuilder surveyColumnsSqlSb = new StringBuilder();

        for (TrackQuestion question : questions) {
            SharedPreferences preferences = Utils.getQuestionPreferences(this, question.getId());
            SharedPreferences.Editor editor = preferences.edit();

            // save object as JSON string
            editor.putString(Constants.QUESTION_JSON, questionGson.toJson(question));
            editor.commit();

            TrackQuestionType qType = question.getQuestionType();

            // also save question type so that we can determine type of activity to launch
            // without having to deserialize the whole object string
            settingsEditor.putInt(Constants.QUESTION_TYPE_PREFIX + question.getId(),
                    TrackQuestionType.toInt(qType));

            // prepare SQL for question column
            if (surveyColumnsSqlSb.length() > 0) {
                surveyColumnsSqlSb.append(",\n");
            }
            surveyColumnsSqlSb.append("`");
            surveyColumnsSqlSb.append(question.getColumnName());
            surveyColumnsSqlSb.append("`\t");

            switch (qType) {
                case FREE_TEXT_SINGLE_LINE:
                case FREE_TEXT_MULTI_LINE:
                case MULTIPLE_CHOICE_SINGLE_ANSWER:
                case MULTIPLE_CHOICE_MULTI_ANSWER:
                    surveyColumnsSqlSb.append(DatabaseHelper.DATATYPE_TEXT);
                    break;
                case LIKERT_SCALE:
                    surveyColumnsSqlSb.append(DatabaseHelper.DATATYPE_INTEGER);
                    break;
            }
        }

        // set first question id to know which one to launch on survey start
        settingsEditor.putInt(Constants.FIRST_QUESTION_ID, questions.get(0).getId());
        settingsEditor.putBoolean(Constants.SURVEY_IMPORT_COMPLETE, true);

        // save db version and survey columns sql to preferences
        int dbVersion = settings.getInt(Constants.DATABASE_VERSION, Constants.DEF_VALUE_INT);
        dbVersion = (dbVersion == Constants.DEF_VALUE_INT) ? 1 : dbVersion + 1; // 1 if new db, +1 if upgrade
        settingsEditor.putInt(Constants.DATABASE_VERSION, dbVersion);
        settingsEditor.putString(Constants.DATABASE_SURVEY_COLUMNS_SQL, surveyColumnsSqlSb.toString());

        // commit changes to shared preferences
        settingsEditor.apply();

        // create database table
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext(), dbVersion);
    }
}
