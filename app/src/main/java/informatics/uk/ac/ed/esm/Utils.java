package informatics.uk.ac.ed.esm;

import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.widget.EditText;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final int MINIMUM_PASSWORD_LENGTH = 8;
    private static final int MAXIMUM_PASSWORD_LENGTH = 20;

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

}
