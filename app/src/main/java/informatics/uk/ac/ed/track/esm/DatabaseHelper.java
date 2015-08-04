package informatics.uk.ac.ed.track.esm;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int FALSE = 0;
    public static final int TRUE = 1;

    public static final String DATABASE_NAME = "informatics.uk.ac.ed.track.db";

    public static final String DATATYPE_INTEGER = "INTEGER";
    public static final String DATATYPE_TEXT = "TEXT";

    public static final String TABLE_NAME_SURVEY_RESPONSES = "SurveyResponses";

    public static final String COLUMN_NAME_ROW_ID = "ROWID";
    public static final String COLUMN_NAME_NOTIFICATION_TIME = "NotificationTime";
    public static final String COLUMN_NAME_SURVEY_COMPLETED_TIME = "SurveyCompletedTime";
    public static final String COLUMN_NAME_SYNCED = "Synced";

    public static final int COLUMN_ID_ROW_ID = 0;
    public static final int COLUMN_ID_NOTIFICATION_TIME = 1;
    public static final int COLUMN_ID_SURVEY_COMPLETED_TIME = 2;
    public static final int COLUMN_ID_SYNCED = 3;

    public static final String NOT_NULL = "NOT NULL";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS `" + TABLE_NAME_SURVEY_RESPONSES + "`";

    private static final String DATE_ISO_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    private Context appContext;

    public DatabaseHelper(Context appContext, int dbVersion) {
        super(appContext, DATABASE_NAME, null, dbVersion);
        this.appContext = appContext;
    }

    public DatabaseHelper(Context appContext) {
        this(appContext, PreferenceManager.getDefaultSharedPreferences(appContext)
                .getInt(Constants.DATABASE_VERSION, 1));
    }

    /*
     * Invoked when the database is created, this is where we can create tables and columns to them, create views or triggers.
     * This method is invoked when the database does not exist on the disk, it’s executed only once on the same device the first time the application is run on the device.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder createTblSb = new StringBuilder();

        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(this.appContext);

        String surveyColumnsSql = settings.getString(Constants.DATABASE_SURVEY_COLUMNS_SQL,
                Constants.DEF_VALUE_STR);

        createTblSb.append("CREATE TABLE `" + TABLE_NAME_SURVEY_RESPONSES + "` (\n");
        createTblSb.append("`" + COLUMN_NAME_ROW_ID + "`\t" + DATATYPE_INTEGER + " PRIMARY KEY,\n");
        createTblSb.append("`" + COLUMN_NAME_NOTIFICATION_TIME + "`\t" +
                DATATYPE_TEXT + " " + NOT_NULL + ",\n");
        createTblSb.append("`" + COLUMN_NAME_SURVEY_COMPLETED_TIME + "`\t" +
                DATATYPE_TEXT + " " + NOT_NULL + ",\n");
        createTblSb.append("`" + COLUMN_NAME_SYNCED + "`\t" +
                DATATYPE_INTEGER + " " + NOT_NULL + ",\n");
        createTblSb.append(surveyColumnsSql);
        createTblSb.append(")");

        SQLiteStatement stmt = db.compileStatement(createTblSb.toString());
        stmt.execute();
    }

    /*
     * invoked when the version number specified in the constructor of the class changes
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // delete existing database
        SQLiteStatement stmt = db.compileStatement(SQL_DELETE_ENTRIES);
        stmt.execute();
        // re-create
        onCreate(db);
    }

    public ArrayList<SurveyResponse> getUnsyncedResponses() {
        ArrayList<SurveyResponse> responses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String table = "`" + DatabaseHelper.TABLE_NAME_SURVEY_RESPONSES +  "`";
        String selection = "`" + DatabaseHelper.COLUMN_NAME_SYNCED + "` = ?";
        String[] selectionArgs = { Integer.toString(DatabaseHelper.FALSE) }; // matched to "?" in selection

        Cursor cursor =
                db.query(table, null, selection, selectionArgs, null, null, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                responses.add(this.getSurveyResponseFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return responses;
    }

    public SurveyResponse getResponseById(long rowId) {
        SurveyResponse response = null;
        SQLiteDatabase db = this.getReadableDatabase();

        String table = "`" + DatabaseHelper.TABLE_NAME_SURVEY_RESPONSES +  "`";
        String selection = "`" + DatabaseHelper.COLUMN_NAME_ROW_ID + "` = ?";
        String[] selectionArgs = { Long.toString(rowId) }; // matched to "?" in selection

        Cursor cursor =
                db.query(table, null, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            response = this.getSurveyResponseFromCursor(cursor);
            cursor.close();
        }

        db.close();

        return response;
    }

    public void setResponseAsSynced (long rowId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("`" + DatabaseHelper.COLUMN_NAME_SYNCED + "`", getIntFromBoolean(true));

        String whereClause = "`" + COLUMN_NAME_ROW_ID + "` = " + rowId;

        db.update("`" + DatabaseHelper.TABLE_NAME_SURVEY_RESPONSES + "`", contentValues, whereClause, null);
        db.close();
    }

    private SurveyResponse getSurveyResponseFromCursor(Cursor cursor) {
        SurveyResponse response = new SurveyResponse();

        response.setRowId(
                Integer.parseInt(cursor.getString(DatabaseHelper.COLUMN_ID_ROW_ID)));
        response.setNotificationTimeIso(
                cursor.getString(DatabaseHelper.COLUMN_ID_NOTIFICATION_TIME));
        response.setSurveyCompletedTimeIso(
                cursor.getString(DatabaseHelper.COLUMN_ID_SURVEY_COMPLETED_TIME));
        response.setSynced(
                getBooleanFromInt(Integer.parseInt(
                        cursor.getString(DatabaseHelper.COLUMN_ID_SYNCED))));

        ContentValues questionAnswers = new ContentValues();
        for (int i = (DatabaseHelper.COLUMN_ID_SYNCED + 1); i < cursor.getColumnCount(); i++) {
            questionAnswers.put(cursor.getColumnName(i), cursor.getString(i));
        }

        response.setQuestionAnswers(questionAnswers);

        return response;
    }

    private boolean getBooleanFromInt(int value) {
        switch (value) {
            case FALSE:
                return false;
            case TRUE:
                return true;
            default:
                return false;
        }
    }

    private int getIntFromBoolean(boolean value) {
        if (value) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    public String getDateInIsoFormat(long timeInMillis) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(timeInMillis);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_ISO_FORMAT);
        return sdf.format(cal.getTime());
    }
}
