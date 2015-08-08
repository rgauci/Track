package informatics.uk.ac.ed.track.esm;

import android.graphics.Color;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;


/**
 * Material Design Guidlines:
 * http://www.google.com/design/spec/patterns/errors.html#errors-user-input-errors
 * see section headed "Text field input - Over/under character or word count"
 *
 * @author Simon Lightfoot <simon@demondevelopers.com>
 */
public class CharacterCountErrorWatcher
        implements TextWatcher
{
    private final TextInputLayout mTextInputLayout;
    private final ForegroundColorSpan mNormalTextAppearance;
    private final AlignmentSpan mAlignmentSpan = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE);
    private final SpannableStringBuilder mErrorText = new SpannableStringBuilder();
    private int mMinLen;
    private int mMaxLen;

    public CharacterCountErrorWatcher(TextInputLayout textInputLayout, int minLen, int maxLen)
    {
        mTextInputLayout = textInputLayout;
        mNormalTextAppearance = new ForegroundColorSpan(Color.GRAY);
        mMinLen = minLen;
        mMaxLen = maxLen;
        updateErrorText();
    }

    private void updateErrorText()
    {
        mErrorText.clear();
        mErrorText.clearSpans();
        final int length = mTextInputLayout.getEditText().length();
        if(length > 0){
            mErrorText.append(String.valueOf(length));
            mErrorText.append(" / ");
            mErrorText.append(String.valueOf(mMaxLen));
            mErrorText.setSpan(mAlignmentSpan, 0, mErrorText.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            if(hasValidLength()){
                mErrorText.setSpan(mNormalTextAppearance, 0, mErrorText.length(),
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
        mTextInputLayout.setError(mErrorText);
    }

    public boolean hasValidLength()
    {
        final int length = mTextInputLayout.getEditText().length();
        return (length >= mMinLen && length <= mMaxLen);
    }

    @Override
    public void afterTextChanged(Editable s)
    {
        updateErrorText();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
        //
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        //
    }
}
