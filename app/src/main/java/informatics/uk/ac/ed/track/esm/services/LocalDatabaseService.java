package informatics.uk.ac.ed.track.esm.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import informatics.uk.ac.ed.track.esm.Constants;
import informatics.uk.ac.ed.track.esm.DatabaseHelper;
import informatics.uk.ac.ed.track.esm.Utils;

public class LocalDatabaseService extends IntentService {

    public LocalDatabaseService() {
        super("LocalDatabaseService");
    }

    public LocalDatabaseService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long notificationTime = intent.getLongExtra(Constants.LAST_NOTIFICATION_TIME_MILLIS,
                Constants.DEF_VALUE_LNG);
        long surveyCompletedTime = intent.getLongExtra(Constants.LAST_SURVEY_COMPLETED_TIME_MILLIS,
                Constants.DEF_VALUE_LNG);
        String surveyResponses = intent.getStringExtra(Constants.SURVEY_RESPONSES);

        // initialise db helper
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());

        ContentValues columnValues = new ContentValues();
        columnValues.put("`" + DatabaseHelper.COLUMN_NAME_NOTIFICATION_TIME + "`",
                dbHelper.getDateInIsoFormat(notificationTime));
        columnValues.put("`" + DatabaseHelper.COLUMN_NAME_SURVEY_COMPLETED_TIME + "`",
                dbHelper.getDateInIsoFormat(surveyCompletedTime));
        columnValues.put("`" + DatabaseHelper.COLUMN_NAME_SYNCED + "`", DatabaseHelper.FALSE);

        String[] responses = surveyResponses.split(Constants.SURVEY_RESPONSES_RESPONSE_DELIMITER);
        for (String response : responses) {
            String[] contentValue = response.split(Constants.SURVEY_RESPONSES_CONTENT_VALUE_DELIMITER);
            columnValues.put("`" + contentValue[0] + "`", contentValue[1]);
        }

        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long newRowId = db.insert(
                "`" + DatabaseHelper.TABLE_NAME_SURVEY_RESPONSES + "`",
                null,
                columnValues);

        db.close();

        // now that we have saved to local DB
        // if we have an Internet connect, send response to external server
        boolean isConnected = Utils.isConnectedToInternet(this);
        if (!isConnected) {
            // if no connection, nothing left to do
            // connection change receiver will sync all unsycned records
            // once a connection is available
            return;
        }

        // send row ID as Intent extra so service knows which response to sync
        Intent externalDbService = new Intent(this, ExternalDatabaseService.class);
        externalDbService.putExtra(Constants.SURVEY_RESPONSE_ROW_ID, newRowId);
        startService(externalDbService);
    }
}
