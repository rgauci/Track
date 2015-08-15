package informatics.uk.ac.ed.track.esm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;

import informatics.uk.ac.ed.track.esm.CharacterCountErrorWatcher;
import informatics.uk.ac.ed.track.esm.Constants;
import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.Utils;
import informatics.uk.ac.ed.track.lib.BranchableAnswerOption;
import informatics.uk.ac.ed.track.lib.MultipleChoiceSingleAnswer;

public class Question_MultiChoice_Single extends TrackQuestionActivity {

    private MultipleChoiceSingleAnswer question;
    private HashMap<Integer, BranchableAnswerOption> optionsMap;
    private LinearLayout lytMain;

    private RadioGroup rdGrp;
    private RadioButton rdBtnOther;
    private EditText txtOther;
    private TextInputLayout txtOther_InpLyt;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question__multi_choice__single);

        // initialise UI controls
        this.rdGrp = (RadioGroup) findViewById(R.id.rdGrp);
        this.lytMain = (LinearLayout) findViewById(R.id.lytMain);

        /* get question preferences using question ID */
        Intent intent = getIntent();
        SharedPreferences preferences =
                Utils.getQuestionPreferences(getApplicationContext(),
                        intent.getIntExtra(Constants.QUESTION_ID, Constants.DEF_VALUE_INT));

        /* deserialize question JSON string into object */
        Gson gson = new Gson();
        this.question = gson.fromJson(
                preferences.getString(Constants.QUESTION_JSON, Constants.DEF_VALUE_STR),
                MultipleChoiceSingleAnswer.class);

        // if question is branchable, make sure to mark it as required
        // or we will not know which next activity to launch
        if (this.question.getIsBranchable()) {
            this.question.setIsRequired(true);
        }

        /* display title, question and prefix, if available */
        this.displayTitleQuestionAndPrefix(this.question, R.id.toolbar, R.id.txtVwToolbarTitle,
                R.id.txtVwToolbarSubTitle, R.id.txtVwQuestionText, R.id.txtVwQuestionPrefix);

        /* display back/next (navigation) buttons */
        this.displayNavigationButtons(question, intent,
                R.id.btnPrevious, R.id.btnNext, R.id.btnFinish);

        // display multiple choice options (radio buttons)
        // and add to hash map
        optionsMap = new HashMap<>();

        for (BranchableAnswerOption option : question.getAnswerOptions()) {
            int optionId = option.getOptionId();

            RadioButton rdBtn = (RadioButton)
                    getLayoutInflater().inflate(R.layout.template_radio_button, null);
            rdBtn.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            rdBtn.setText(option.getOption());
            rdBtn.setId(optionId);
            this.rdGrp.addView(rdBtn);

            this.optionsMap.put(optionId, option);
        }

        /* add "Other" if necessary */
        if (this.question.getAddOther()) {
            // show "Other" radio button
            this.rdBtnOther = (RadioButton)
                    getLayoutInflater().inflate(R.layout.template_radio_button, null);
            this.rdBtnOther.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            this.rdBtnOther.setId(R.id.rdBtnOther);
            rdBtnOther.setText(getResources().getString(R.string.other));

            this.txtOther_InpLyt = (TextInputLayout)
                    getLayoutInflater().inflate(R.layout.template_text_input_layout, null);
            this.txtOther_InpLyt.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            this.txtOther_InpLyt.setId(R.id.txtOther_InpLyt);
            this.txtOther_InpLyt.setVisibility(View.INVISIBLE);

            this.txtOther = this.txtOther_InpLyt.getEditText();
            this.txtOther.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            this.txtOther.setId(R.id.txtOther);

            /* set up character counter for edit text */
            this.txtOther.addTextChangedListener(
                    new CharacterCountErrorWatcher(this.txtOther_InpLyt, 0,
                            getResources().
                                    getInteger(R.integer.other_option_text_max_char_length)));


            // add to view
            this.rdGrp.addView(rdBtnOther);
            this.lytMain.addView(txtOther_InpLyt);

            // hide / show "Other" textbox depending on whether option is selected
            this.rdGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    TextInputLayout txtOther_InpLyt =
                            (TextInputLayout) findViewById(R.id.txtOther_InpLyt);
                    if (checkedId == R.id.rdBtnOther) {
                        txtOther_InpLyt.setVisibility(View.VISIBLE);
                    } else {
                        txtOther_InpLyt.setVisibility(View.GONE);
                    }
                }
            });

            // add "Other" option to HashMap to support branching
            BranchableAnswerOption otherOption;
            int otherOptionId = rdBtnOther.getId();
            String otherOptionText = rdBtnOther.getText().toString();

            if (this.question.getIsBranchable()) {
                otherOption = new BranchableAnswerOption(otherOptionId, otherOptionText,
                        this.question.getOtherOptionNextQuestionId());
            } else {
                otherOption = new BranchableAnswerOption(otherOptionId, otherOptionText);
            }

            this.optionsMap.put(otherOptionId, otherOption);
        }
    }

    @Override
    public boolean isValid() {
        boolean hasErrors = false;

        String errorText = null;
        Resources res = getResources();

        // if "Other" is selected, make sure 'Other text' is non-empty
        if (this.question.getAddOther() && this.rdBtnOther.isChecked()) {
            String otherText = this.txtOther.getText().toString();

            if (otherText.isEmpty()) {
                errorText = String.format(res.getString(R.string.error_enterOtherOptionText),
                                res.getString(R.string.other));
                hasErrors = true;
            }

            // check if other text exceeds max character limit
            int maxLength = res.getInteger(R.integer.other_option_text_max_char_length);
            if (otherText.length() > maxLength) {
                this.txtOther_InpLyt.setError(String.format(getString(R.string.error_answerTooLong),
                        maxLength));
                hasErrors = true;
            } else {
                this.txtOther_InpLyt.setError(null);
            }
        }

        // if question is required, make sure an option has been selected
        if (!hasErrors && this.question.getIsRequired()) {
            if (this.rdGrp.getCheckedRadioButtonId() == -1) {
                errorText = res.getString(R.string.error_answerToProceed);
                hasErrors = true;
            }
        }

        if ((hasErrors) && (!Utils.isNullOrEmpty(errorText))) {
            Toast toast = Toast.makeText(this, errorText, Toast.LENGTH_SHORT);
            toast.show();
        }

        return !hasErrors;
    }

    @Override
    public void launchNextQuestion() {
        int nextQuestionId;

        if (this.question.getIsBranchable()) {
            BranchableAnswerOption checkedOption = optionsMap.get( this.rdGrp.getCheckedRadioButtonId());
            nextQuestionId = checkedOption.getNextQuestionId();
        } else {
            nextQuestionId = this.question.getNextQuestionId();
        }

        Intent intent = Utils.getLaunchQuestionIntent(this, nextQuestionId);
        intent.putExtra(Constants.SURVEY_RESPONSES, this.getSurveyResponsesForNextIntent());
        startActivity(intent);
    }

    @Override
    public String getSurveyResponsesForNextIntent() {
        String surveyResponses;
        int checkedOptionId = this.rdGrp.getCheckedRadioButtonId();

        if (checkedOptionId != -1) {
            // if selected, add option text
            String response;

            if ((this.rdBtnOther != null) && (checkedOptionId == this.rdBtnOther.getId())) {
                // if other is selected, read from text box
                response = this.txtOther.getText().toString();
            } else {
                // otherwise read from options map
                response = optionsMap.get(checkedOptionId).getOption();
            }

            surveyResponses = this.addAnswerToSurveyResponses(this.question.getColumnName(),
                    response);
        } else {
            surveyResponses = this.getIntentSurveyResponses();
        }

        return surveyResponses;
    }
}
