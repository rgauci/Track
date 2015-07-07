package informatics.uk.ac.ed.track;

import android.content.Context;
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
import java.util.HashMap;
import java.util.Map;

import informatics.uk.ac.ed.track.lib.FreeTextQuestion;
import informatics.uk.ac.ed.track.lib.LikertScaleQuestion;
import informatics.uk.ac.ed.track.lib.MultipleChoiceMultipleAnswer;
import informatics.uk.ac.ed.track.lib.MultipleChoiceSingleAnswer;
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
                // import survey: cretae shared preference file for each question
                if (!surveyImportComplete) {
                    importSurvey();
                }

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

                    // if survey is available
                    // shower user login screen to start survey
                    if (surveyAvailable) {
                        intent = new Intent(SplashScreen.this, UserLogin.class);
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

    private void importSurvey() {
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

        // create a HashMap indexed by questionId
        // to enable easy access to each question by Id
        Map<Integer,TrackQuestion> map = new HashMap<>();
        for (TrackQuestion i : questions) {
            map.put(i.getId(), i);
        }

        Gson questionGson = new Gson();

        // for each question, create a preferences file containing:
        // - the question type
        // - the next question's type (to know which activity to launch)
        // - the question object serialised in JSON format

        for (TrackQuestion question : questions) {
            SharedPreferences preferences = getSharedPreferences(
                    Constants.QUESTION_PREFERENCES_PREFIX + question.getId(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            // save object as JSON string
            editor.putString(Constants.QUESTION_JSON, questionGson.toJson(question));
            // also save question type so that previous question can determine type
            // without having to deserialize the whole object string
            editor.putInt(Constants.QUESTION_TYPE, TrackQuestionType.toInt(question.getQuestionType()));
            editor.commit();
        }

        // TODO mark survey import as complete
    }
}
