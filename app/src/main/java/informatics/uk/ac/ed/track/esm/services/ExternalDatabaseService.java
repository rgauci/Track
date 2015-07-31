package informatics.uk.ac.ed.track.esm.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import informatics.uk.ac.ed.track.esm.Constants;
import informatics.uk.ac.ed.track.esm.DatabaseHelper;
import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.SurveyResponse;
import informatics.uk.ac.ed.track.esm.util.WebServiceHelper;

public class ExternalDatabaseService extends IntentService {

    private final static String LOG_TAG = "ExternalDBService";

    public ExternalDatabaseService() {
        super("ExternalDatabaseService");
    }

    public ExternalDatabaseService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long rowId = intent.getLongExtra(Constants.SURVEY_RESPONSE_ROW_ID, Constants.DEF_VALUE_LNG);

        if (rowId == Constants.DEF_VALUE_LNG) {
            // if intent extra does not contain a row id
            // try to sync all un-sycned records to external db
            this.syncAllUnsyncedResponses();
        } else {
            // otherwise sync only that record (survey has just been completed)
            this.syncResponse(rowId);
        }
    }

    private void syncAllUnsyncedResponses(){
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        ArrayList<SurveyResponse> responses = dbHelper.getUnsyncedRespones();

        String url = this.getAddSurveyResponseWebMethodUrl();
        int participantId = this.getParticipantId();

        for (SurveyResponse response : responses) {
            sendResponseToWebServer(url, participantId, response);
        }
    }

    private void syncResponse(long rowId) {
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SurveyResponse response = dbHelper.getResponseById(rowId);
        int participantId = this.getParticipantId();
        this.sendResponseToWebServer(
                this.getAddSurveyResponseWebMethodUrl(), this.getParticipantId(), response);
    }

    private int getParticipantId() {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return settings.getInt(Constants.PARTICIPANT_ID, Constants.DEF_VALUE_INT);
    }

    private String getAddSurveyResponseWebMethodUrl() {
        Resources res = getResources();
        return (res.getString(R.string.trackWebServiceUrl)
                + res.getString(R.string.addSurveyResponseWebMethod));
    }

    private void sendResponseToWebServer(String url, int participantId, SurveyResponse response) {
        Resources res = getResources();

        // Build Parameters
        ContentValues params = new ContentValues();
        params.put(WebServiceHelper.PARAM_PARTICIPANT_ID, participantId);
        params.put(WebServiceHelper.PARAM_NOTIFICATION_TIME,
                response.getNotificationTimeIso());
        params.put(WebServiceHelper.PARAM_SURVEY_COMPLETED_TIME,
                response.getSurveyCompletedTimeIso());

        // Survey Response Column Names
        JSONArray columnNames = new JSONArray();
        JSONArray columnValues = new JSONArray();
        ContentValues questionAnswers =  response.getQuestionAnswers();

        for (String colName : questionAnswers.keySet()) {
            String colVal = questionAnswers.getAsString(colName);
            // only send answers (non-null) to web server
            if (colVal != null) {
                columnNames.put(colName);
                columnValues.put(questionAnswers.getAsString(colName));
            }
        }

        params.put(WebServiceHelper.PARAM_SURVEY_COLUMN_NAMES, columnNames.toString());
        params.put(WebServiceHelper.PARAM_SURVEY_RESPONSES, columnValues.toString());

        // make request and get response
        JSONObject jsonObject =  WebServiceHelper.makeHttpRequest(url,
                WebServiceHelper.RequestMethod.POST, params);

        // check if request was successful
        boolean success = false;
        if (jsonObject != null) {
            try {
                int successCode = jsonObject.getInt(WebServiceHelper.OUT_PARAM_SUCCESS);
                if (successCode == WebServiceHelper.SUCCESS_CODE) {
                    success = true;
                } else {
                    String errorMsg = jsonObject.getString(WebServiceHelper.SUCCESS_MESSAGE);
                    Log.e(LOG_TAG, "Web Service error: " + errorMsg);
                }
            } catch (JSONException je) {
                Log.e(LOG_TAG, "Error parsing JSON object from server.", je);
            }
        }

        if (success) {
            this.setReponseAsSynced(response.getRowId());
        }
    }

    private void setReponseAsSynced(long rowId) {
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        dbHelper.setResponseAsSynced(rowId);
    }

}
