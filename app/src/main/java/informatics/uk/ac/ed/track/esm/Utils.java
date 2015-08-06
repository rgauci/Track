package informatics.uk.ac.ed.track.esm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;

import org.w3c.dom.Text;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.activities.Question_FreeText_Multi;
import informatics.uk.ac.ed.track.esm.activities.Question_FreeText_Single;
import informatics.uk.ac.ed.track.esm.activities.Question_LikertScale;
import informatics.uk.ac.ed.track.esm.activities.Question_MultiChoice_Multi;
import informatics.uk.ac.ed.track.esm.activities.Question_MultiChoice_Single;
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

    public static boolean isConnectedToInternet(Context context){
        // query the network and check if we have an Internet connection
        ConnectivityManager cm =
                (ConnectivityManager)(context.getSystemService(Context.CONNECTIVITY_SERVICE));

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());
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

    public static boolean getIsUserLoggedIn(Context appContext) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(appContext);
        return settings.getBoolean(Constants.USER_IS_LOGGED_IN, Constants.DEF_VALUE_BOOL);
    }

    /**
     * Validate plaintext password by hashing it and
     * comparing it to password saved in Shared Preferences.
     * @param context Context (pass from Activity as 'this').
     * @param txtPassword EditText containing user-input password.
     * @param txtPassword_inpLyt TextInputLayout containing txtPassword (for displaying error messages).
     * @return True if the password is valid, false otherwise (with corresponding error message set in txtPassword_inpLyt).
     */
    public static boolean validateUserPassword(Context context, EditText txtPassword, TextInputLayout txtPassword_inpLyt) {
        boolean hasErrors = false;

        Resources resources = context.getResources();

        String password = Utils.getTrimmedText(txtPassword);

        // first check if password has been in put
        if (Utils.isNullOrEmpty(password)) {
            txtPassword_inpLyt.setError(resources.getString(R.string.error_user_login_enter_password_));
            hasErrors = true;
        }

        // if yes, hash & confirm whether it matches saved password
        if (!hasErrors) {
            String hashedPassword = Utils.computeHash(password);

            SharedPreferences settings =
                    PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            String storedHashedPassword = settings.getString(Constants.PARTICIPANT_PASSWORD_HASHED,
                    Constants.DEF_VALUE_STR);

            if (!hashedPassword.equals(storedHashedPassword)) {
                txtPassword_inpLyt.setError(resources.getString(R.string.error_user_login_invalidPassword));
                hasErrors = true;
            } else {
                txtPassword_inpLyt.setError(null);
            }
        }

        return !hasErrors;
    }

    /**
     * Updates the hashed user password stored in SharedPreferences.
     * @param context Context (pass from Activity as 'this').
     * @param password The new password.
     * @param updateResetTime Set to true if the password reset time saved in SharedPreferences
     *                        should be updated
     *                        (user will lose access to data saved prior to password reset).
     */
    public static void saveNewUserPasswordToPreferences(Context context, String password,
                                                        boolean updateResetTime) {
        Calendar calendar = GregorianCalendar.getInstance();
        long passwordResetTime = calendar.getTimeInMillis();

        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(Constants.PARTICIPANT_PASSWORD_HASHED, Utils.computeHash(password));

        if (updateResetTime) {
            editor.putLong(Constants.PARTICIPANT_PASSWORD_RESET_TIME_MILLIS, passwordResetTime);
        }

        editor.apply();
    }

    /**
     * Reads from SharedPreferences and returns true if the user is a research participant.
     * @param appContext The application context.
     * @return true if the user is a research participant.
     */
    public static boolean getIsResearchParticipant(Context appContext){
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(appContext);

        return settings.getBoolean(Constants.IS_RESEARCH_PARTICIPANT, Constants.DEF_VALUE_BOOL);
    }

}
