package informatics.uk.ac.ed.track.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import informatics.uk.ac.ed.track.Constants;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "informatics.uk.ac.ed.track.db";

    public static final String DATATYPE_INTEGER = "INTEGER";
    public static final String DATATYPE_TEXT = "TEXT";

    public static final String TABLE_NAME_SURVEY_RESPONSES = "Survey Responses";

    public static final String COLUMN_NAME_NOTIFICATION_TIME = "Notification Time";
    public static final String COLUMN_NAME_SURVEY_COMPLETED_TIME = "Survey Completed Time";
    public static final String COLUMN_NAME_SYNCED = "Synced";

    public static final String NOT_NULL = "NOT NULL";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS `" + TABLE_NAME_SURVEY_RESPONSES + "`";

    private Context appContext;

    public DatabaseHelper(Context appContext, int dbVersion) {
        super(appContext, DATABASE_NAME, null, dbVersion);
        this.appContext = appContext;
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
        createTblSb.append("`" + COLUMN_NAME_NOTIFICATION_TIME + "`\t" +
                DATATYPE_TEXT + " " + NOT_NULL + ",\n");
        createTblSb.append("`" + COLUMN_NAME_SURVEY_COMPLETED_TIME + "`\t" +
                DATATYPE_TEXT + " " + NOT_NULL + ",\n");
        createTblSb.append(surveyColumnsSql);
        createTblSb.append("`" + COLUMN_NAME_SYNCED + "`\t" +
                DATATYPE_INTEGER + " " + NOT_NULL + "\n");
        createTblSb.append(")");

        db.execSQL(createTblSb.toString());
    }

    /*
     * invoked when the version number specified in the constructor of the class changes
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // delete existing database
        db.execSQL(SQL_DELETE_ENTRIES);
        // re-create
        onCreate(db);
    }
}
