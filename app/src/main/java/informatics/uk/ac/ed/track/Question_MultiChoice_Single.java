package informatics.uk.ac.ed.track;

import android.content.Intent;
import android.content.SharedPreferences;
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

    @Override
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

        /* display back/next (navigation) buttons */
        this.displayNavigationButtons(intent, R.id.btnPrevious, R.id.btnNext);

        /* deserialize question JSON string into object */
        Gson gson = new Gson();
        this.question = gson.fromJson(
                preferences.getString(Constants.QUESTION_JSON, Constants.DEF_VALUE_STR),
                MultipleChoiceSingleAnswer.class);

        /* display title, question and prefix, if available */
        this.displayTitleQuestionAndPrefix(this.question, R.id.toolbar, R.id.txtVwToolbarTitle,
                R.id.txtVwQuestionText, R.id.txtVwQuestionPrefix);


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
            RadioButton rdBtnOther = (RadioButton)
                    getLayoutInflater().inflate(R.layout.template_radio_button, null);
            rdBtnOther.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            rdBtnOther.setId(R.id.rdBtnOther);
            rdBtnOther.setText(getResources().getString(R.string.other));

            EditText txtOther = (EditText)
                    getLayoutInflater().inflate(R.layout.template_edit_text_plain, null);
            txtOther.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            txtOther.setId(R.id.txtOther);
            txtOther.setVisibility(View.INVISIBLE);

            this.rdGrp.addView(rdBtnOther);
            this.lytMain.addView(txtOther);

            // hide / show "Other" textbox depending on whether option is selected
            rdGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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

        if (this.rdGrp.getCheckedRadioButtonId() == -1) {
            Toast toast = Toast.makeText(this,
                    getResources().getString(R.string.error_answerToProceed), Toast.LENGTH_SHORT);
            toast.show();
            hasErrors = true;
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
