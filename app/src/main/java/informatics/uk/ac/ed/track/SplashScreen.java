package informatics.uk.ac.ed.track;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

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

                // if survey has not yet been imported from survey_json.txt
                // import survey: create shared preference file for each question
                //if (!surveyImportComplete) {
                    importSurvey(settings);
                //}

                // if setup is complete
                if (setupComplete) {
                    boolean surveyAvailable = false;

                    long lastNotifTime =
                            settings.getLong(Constants.LAST_NOTIFICATION_TIME_MILLIS,
                                    Constants.DEF_VALUE_LNG);
                    long lastCompletedTime =
                            // TODO make sure to set value upon survey completion
                            settings.getLong(Constants.LAST_SURVEY_COMPLETED_TIME_MILLIS,
                                    Constants.DEF_VALUE_LNG);

                    if (lastCompletedTime < lastNotifTime) {
                        long notifWindowMillis =
                                settings.getInt(Constants.NOTIFICATION_WINDOW_MINUTES,
                                        Constants.DEF_VALUE_INT) * 1000;

                        Calendar cal = GregorianCalendar.getInstance();
                        long currentTime = cal.getTimeInMillis();

                        boolean expired = ((lastNotifTime + notifWindowMillis) > currentTime);

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
                        intent = new Intent(SplashScreen.this, DefaultActivity.class);
                    }

                    startActivity(intent);
                } else {
                    // otherwise start setup
                    Intent intent = new Intent(SplashScreen.this, ResearcherSetup.class);
                    startActivity(intent);
                }
            }
        }, SPLASH_DISPLAY_TIME_MILLIS);
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

        // create RunTimeAdapterFactory to support Polymorphic types in lists
        RuntimeTypeAdapterFactory<TrackQuestion> factory =
                informatics.uk.ac.ed.track.lib.Utils.getRuntimeAdapterFactory();

        Type listType = new TypeToken<ArrayList<TrackQuestion>>() {}.getType();

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(factory);
        Gson gson = builder.create();

        ArrayList<TrackQuestion> questions = gson.fromJson(jsonSurvey, listType);

        // for each question, create a preferences file containing:
        // - the question object serialised in JSON format
        // and save the question type in the shared preferences

        SharedPreferences.Editor settingsEditor = settings.edit();
        Gson questionGson = new Gson();

        for (TrackQuestion question : questions) {
            SharedPreferences preferences = Utils.getQuestionPreferences(this, question.getId());
            SharedPreferences.Editor editor = preferences.edit();

            // save object as JSON string
            editor.putString(Constants.QUESTION_JSON, questionGson.toJson(question));
            editor.commit();

            // also save question type so that we can determine type of activity to launch
            // without having to deserialize the whole object string
            settingsEditor.putInt(Constants.QUESTION_TYPE_PREFIX + question.getId(), TrackQuestionType.toInt(question.getQuestionType()));
        }

        // set first question id to know which one to launch on survey start
        settingsEditor.putInt(Constants.FIRST_QUESTION_ID, questions.get(0).getId());
        settingsEditor.putBoolean(Constants.SURVEY_IMPORT_COMPLETE, true);
        settingsEditor.commit();
    }
}
