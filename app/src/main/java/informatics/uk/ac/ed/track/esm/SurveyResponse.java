package informatics.uk.ac.ed.track.esm;

import android.content.ContentValues;

public class SurveyResponse {

    private int rowId;
    private String notificationTimeIso;
    private String surveyCompletedTimeIso;
    private boolean synced;
    private ContentValues questionAnswers;

    public void setRowId(int value) {
        this.rowId = value;
    }

    public void setNotificationTimeIso(String value) {
        this.notificationTimeIso = value;
    }

    public void setSurveyCompletedTimeIso(String value) {
        this.surveyCompletedTimeIso = value;
    }

    public void setSynced(boolean value) {
        this.synced = value;
    }

    public void setQuestionAnswers(ContentValues values) {
        this.questionAnswers = values;
    }

    public String getNotificationTimeIso() {
        return this.notificationTimeIso;
    }

    public String getSurveyCompletedTimeIso() {
        return this.surveyCompletedTimeIso;
    }

    public long getRowId() {
        return this.rowId;
    }

    public ContentValues getQuestionAnswers() {
        return this.questionAnswers;
    }
}
