package informatics.uk.ac.ed.track;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import informatics.uk.ac.ed.track.activities.Question_FreeText_Multi;
import informatics.uk.ac.ed.track.activities.Question_FreeText_Single;
import informatics.uk.ac.ed.track.activities.Question_LikertScale;
import informatics.uk.ac.ed.track.activities.Question_MultiChoice_Multi;
import informatics.uk.ac.ed.track.activities.Question_MultiChoice_Single;
import informatics.uk.ac.ed.track.lib.TrackQuestionType;

public class Utils {

    private static final int MINIMUM_PASSWORD_LENGTH = 8;
    private static final int MAXIMUM_PASSWORD_LENGTH = 20;
    private static final String LOG_TAG = "TRACK.Utils";

    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-zA-Z])(?=\\S+$))" + // at least 1 digit, 1 letter, and no whitespace
                    ".{" + MINIMUM_PASSWORD_LENGTH + "," + MAXIMUM_PASSWORD_LENGTH + "}";

    public static String getTrimmedText(EditText textView) {
        return textView.getText().toString().trim();
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isValidPasswordLength (String password) {
        return (password.length() >= MINIMUM_PASSWORD_LENGTH)
                && (password.length() <= MAXIMUM_PASSWORD_LENGTH);
    }

    public static boolean isValidPassword (String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean isNullOrEmpty(String str) {
        return ((str == null) || (str.length() == 0) || (str.trim().length() == 0));
    }

    public static String computeHash(String plaintext) {
        if ((plaintext == null) || (plaintext.length() == 0)) {
            return plaintext;
        }

        MessageDigest digest;
        String hash;

        try
        {
            digest = MessageDigest.getInstance(Constants.HASHING_ALGORITHM);
        } catch (NoSuchAlgorithmException nsae) {
            Log.e(LOG_TAG, "Error initializing SHA1 message digest.", nsae);
            return plaintext;
        }

        digest.update(plaintext.getBytes());
        byte[] byteHash = digest.digest();

        hash = Base64.encodeToString(byteHash, Base64.DEFAULT);
        return hash;
    }

    /**
     *
     * @param appContext Application context, used for retrieving default shared preferences.
     * @return Intent to launch Activity with startActivity()
     */
    public static Intent getLaunchSurveyIntent(Context appContext) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(appContext);
        int firstQuestionId = settings.getInt(Constants.FIRST_QUESTION_ID, Constants.DEF_VALUE_INT);
        return getLaunchQuestionIntent(appContext, settings, firstQuestionId, true);
    }

    public static SharedPreferences getQuestionPreferences(Context appContext, int questionId) {
        return appContext.getSharedPreferences(
                Constants.QUESTION_PREFERENCES_PREFIX + questionId, Context.MODE_PRIVATE);
    }

    public static TrackQuestionType getQuestionType(SharedPreferences settings, int questionId) {
        int qType = settings.getInt(Constants.QUESTION_TYPE_PREFIX + questionId, Constants.DEF_VALUE_INT);
        return TrackQuestionType.fromInt(qType);
    }

    public static Intent getLaunchQuestionIntent(Context activityContext,
                                                 int questionId, boolean isFirstQuestion) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(
                activityContext.getApplicationContext());
        return getLaunchQuestionIntent(activityContext, settings, questionId, isFirstQuestion);
    }

    public static Intent getLaunchQuestionIntent(Context activityContext,
                                                 int questionId) {
        return getLaunchQuestionIntent(activityContext, questionId, false);
    }

    public static Intent getLaunchQuestionIntent(Context activityContext, SharedPreferences settings,
                                                 int questionId, boolean isFirstQuestion) {
        TrackQuestionType qType = getQuestionType(settings, questionId);

        Intent intent = null;

        switch (qType) {
            case FREE_TEXT_SINGLE_LINE:
                intent = new Intent(activityContext, Question_FreeText_Single.class);
                break;
            case FREE_TEXT_MULTI_LINE:
                intent = new Intent(activityContext, Question_FreeText_Multi.class);
                break;
            case MULTIPLE_CHOICE_SINGLE_ANSWER:
                intent = new Intent(activityContext, Question_MultiChoice_Single.class);
                break;
            case MULTIPLE_CHOICE_MULTI_ANSWER:
                intent = new Intent(activityContext, Question_MultiChoice_Multi.class);
                break;
            case LIKERT_SCALE:
                intent = new Intent(activityContext, Question_LikertScale.class);
                break;
        }

        intent.putExtra(Constants.QUESTION_ID, questionId);
        intent.putExtra(Constants.IS_FIRST_QUESTION, isFirstQuestion);

        return intent;
    }

}
