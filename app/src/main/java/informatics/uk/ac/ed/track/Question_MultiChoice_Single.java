package informatics.uk.ac.ed.track;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
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

import informatics.uk.ac.ed.track.lib.BranchableAnswerOption;
import informatics.uk.ac.ed.track.lib.MultipleChoiceSingleAnswer;

public class Question_MultiChoice_Single extends TrackQuestionActivity {

    private MultipleChoiceSingleAnswer question;
    private HashMap<Integer, BranchableAnswerOption> optionsMap;
    private LinearLayout lytMain;

    private RadioGroup rdGrp;
    private RadioButton rdBtnOther;
    private EditText txtOther;

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
                R.id.txtVwQuestionText, R.id.txtVwQuestionPrefix);

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

            this.txtOther = (EditText)
                    getLayoutInflater().inflate(R.layout.template_edit_text_plain, null);
            this.txtOther.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            this.txtOther.setId(R.id.txtOther);
            this.txtOther.setVisibility(View.INVISIBLE);

            this.rdGrp.addView(rdBtnOther);
            this.lytMain.addView(txtOther);

            // hide / show "Other" textbox depending on whether option is selected
            this.rdGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    RadioButton rb = (RadioButton) radioGroup.findViewById(checkedId);
                    EditText txtOther = (EditText) findViewById(R.id.txtOther);
                    if (checkedId == R.id.rdBtnOther) {
                        txtOther.setVisibility(View.VISIBLE);
                    } else {
                        txtOther.setVisibility(View.GONE);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_multi_choice__single, menu);
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
        if (this.question.getAddOther() && this.rdBtnOther.isChecked()) {
            String otherText = Utils.getTrimmedText(this.txtOther);
            if (otherText.isEmpty()) {
                errorText = String.format(res.getString(R.string.error_enterOtherOptionText),
                                res.getString(R.string.other));
                hasErrors = true;
            }
        }

        // if question is required, make sure an option has been selected
        if (!hasErrors && this.question.getIsRequired()) {
            if (this.rdGrp.getCheckedRadioButtonId() == -1) {
                errorText = res.getString(R.string.error_answerToProceed);
                hasErrors = true;
            }
        }

        if (hasErrors) {
            Toast toast = Toast.makeText(this, errorText, Toast.LENGTH_SHORT);
            toast.show();
        }

        return !hasErrors;
    }

    @Override
    public void launchNextQuestion() {
        int nextQuestionId;

        if (this.question.getIsBranchable()) {
            int checkedOptionId = this.rdGrp.getCheckedRadioButtonId();
            BranchableAnswerOption checkedOption = optionsMap.get(checkedOptionId);
            nextQuestionId = checkedOption.getNextQuestionId();
        } else {
            nextQuestionId = this.question.getNextQuestionId();
        }

        Intent intent = Utils.getLaunchQuestionIntent(this, nextQuestionId);
        startActivity(intent);
    }
}
