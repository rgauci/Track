package informatics.uk.ac.ed.track.services;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import informatics.uk.ac.ed.track.Constants;
import informatics.uk.ac.ed.track.DatabaseHelper;
import informatics.uk.ac.ed.track.SurveyResponse;

public class ExternalDatabaseService extends IntentService {

    public ExternalDatabaseService() {
        super("ExternalDatabaseService");
    }

    public ExternalDatabaseService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int rowId = intent.getIntExtra(Constants.SURVEY_RESPONSE_ROW_ID, Constants.DEF_VALUE_INT);

        if (rowId == Constants.DEF_VALUE_INT) {
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
    }

    private void syncResponse(int rowId) {

    }
}
