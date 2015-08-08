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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;

import informatics.uk.ac.ed.track.esm.CharacterCountErrorWatcher;
import informatics.uk.ac.ed.track.esm.Constants;
import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.DatabaseHelper;
import informatics.uk.ac.ed.track.esm.Utils;
import informatics.uk.ac.ed.track.lib.AnswerOption;
import informatics.uk.ac.ed.track.lib.MultipleChoiceMultipleAnswer;


public class Question_MultiChoice_Multi extends TrackQuestionActivity {

    private MultipleChoiceMultipleAnswer question;
    private LinearLayout lytMain;

    private ArrayList<CheckBox> checkBoxes;
    private CheckBox chkBxOther;
    private EditText txtOther;
    private TextInputLayout txtOther_InpLyt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question__multi_choice__multi);

        // initialise UI controls
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
                MultipleChoiceMultipleAnswer.class);

        /* display title, question and prefix, if available */
        this.displayTitleQuestionAndPrefix(this.question, R.id.toolbar, R.id.txtVwToolbarTitle,
                R.id.txtVwToolbarSubTitle, R.id.txtVwQuestionText, R.id.txtVwQuestionPrefix);

        /* display back/next (navigation) buttons */
        this.displayNavigationButtons(question, intent,
                R.id.btnPrevious, R.id.btnNext, R.id.btnFinish);

        // display multiple choice options (checkboxes)
        checkBoxes = new ArrayList<>();
        for (AnswerOption option : question.getAnswerOptions()) {
            int optionId = option.getOptionId();

            CheckBox checkBox = (CheckBox)
                    getLayoutInflater().inflate(R.layout.template_check_box, null);
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            checkBox.setText(option.getOption());
            checkBox.setId(optionId);
            this.lytMain.addView(checkBox);

            // add to list
            this.checkBoxes.add(checkBox);
        }

        /* add "Other" if necessary */
        if (this.question.getAddOther()) {
            // show "Other" check box
            this.chkBxOther = (CheckBox)
                    getLayoutInflater().inflate(R.layout.template_check_box, null);
            this.chkBxOther.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            this.chkBxOther.setId(R.id.chkBxOther);
            this.chkBxOther.setText(getResources().getString(R.string.other));

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
            this.lytMain.addView(chkBxOther);
            this.lytMain.addView(txtOther_InpLyt);

            // hide / show "Other" textbox depending on whether option is selected
            chkBxOther.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    TextInputLayout txtOther_InpLyt =
                            (TextInputLayout) findViewById(R.id.txtOther_InpLyt);
                    if (compoundButton.isChecked()) {
                        txtOther_InpLyt.setVisibility(View.VISIBLE);
                    } else {
                        txtOther_InpLyt.setVisibility(View.INVISIBLE);
                    }
                }
            });

            // add "Other" option to checkbox list
            this.checkBoxes.add(chkBxOther);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_multi_choice__multi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean isValid() {
        boolean hasErrors = false;

        String errorText = null;
        Resources res = getResources();

        // if "Other" is selected, make sure 'Other text' is non-empty
        if (this.question.getAddOther() && this.chkBxOther.isChecked()) {
            String otherText = this.txtOther.getText().toString();

            if (otherText.isEmpty()) {
                errorText = String.format(res.getString(R.string.error_enterOtherOptionText),
                        res.getString(R.string.other));
                hasErrors = true;
            } else if (otherText.contains(Constants.SURVEY_RESPONSES_MULTI_CHOICE_MULTI_ANSWER_DELIMITER)) {
                errorText = String.format(res.getString(R.string.error_invalidCharacterInOther),
                        res.getString(R.string.other),
                        Constants.SURVEY_RESPONSES_MULTI_CHOICE_MULTI_ANSWER_DELIMITER);
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

        // if question is required, make sure at least one checkbox is selected
        if (!hasErrors && this.question.getIsRequired()) {
            boolean atLeastOneSelected = false;
            int checked = 0;

            while ((!atLeastOneSelected) && (checked < this.checkBoxes.size())) {
                if (this.checkBoxes.get(checked).isChecked()) {
                    atLeastOneSelected = true;
                }
                checked++;
            }

            if (!atLeastOneSelected) {
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
        Intent intent = Utils.getLaunchQuestionIntent(this, this.question.getNextQuestionId());
        intent.putExtra(Constants.SURVEY_RESPONSES, this.getSurveyResponsesForNextIntent());
        startActivity(intent);
    }

    @Override
    public String getSurveyResponsesForNextIntent() {
        // add all selected options to response
        StringBuilder responseSb = new StringBuilder();

        for (CheckBox checkBox : this.checkBoxes) {
            if (checkBox.isChecked()) {
                if (responseSb.length() > 0) {
                    responseSb.append(Constants.SURVEY_RESPONSES_MULTI_CHOICE_MULTI_ANSWER_DELIMITER);
                }
                if ((this.chkBxOther != null) && (checkBox.getId() == this.chkBxOther.getId())) {
                    // if 'Other' checkbox is selected, use Other textbox's text
                    responseSb.append(this.txtOther.getText());
                } else {
                    responseSb.append(checkBox.getText());
                }
            }
        }

        return this.addAnswerToSurveyResponses(this.question.getColumnName(),
                responseSb.toString());
    }
}
