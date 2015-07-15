package informatics.uk.ac.ed.track;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import informatics.uk.ac.ed.track.lib.AnswerOption;
import informatics.uk.ac.ed.track.lib.MultipleChoiceMultipleAnswer;
import informatics.uk.ac.ed.track.lib.TrackQuestion;


public class Question_MultiChoice_Multi extends TrackQuestionActivity {

    private MultipleChoiceMultipleAnswer question;
    private LinearLayout lytMain;

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
                R.id.txtVwQuestionText, R.id.txtVwQuestionPrefix);

        /* display back/next (navigation) buttons */
        this.displayNavigationButtons(question, intent,
                R.id.btnPrevious, R.id.btnNext, R.id.btnFinish);

        // display multiple choice options (checkboxes)
        for (AnswerOption option : question.getAnswerOptions()) {
            int optionId = option.getOptionId();

            CheckBox checkBox = (CheckBox)
                    getLayoutInflater().inflate(R.layout.template_check_box, null);
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            checkBox.setText(option.getOption());
            checkBox.setId(optionId);
            this.lytMain.addView(checkBox);
        }

        /* add "Other" if necessary */
        if (this.question.getAddOther()) {
            // show "Other" check box
            CheckBox chkBxOther = (CheckBox)
                    getLayoutInflater().inflate(R.layout.template_check_box, null);
            chkBxOther.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            chkBxOther.setId(R.id.chkBxOther);
            chkBxOther.setText(getResources().getString(R.string.other));

            EditText txtOther = (EditText)
                    getLayoutInflater().inflate(R.layout.template_edit_text_plain, null);
            txtOther.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            txtOther.setId(R.id.txtOther);
            txtOther.setVisibility(View.INVISIBLE);

            this.lytMain.addView(chkBxOther);
            this.lytMain.addView(txtOther);

            // hide / show "Other" textbox depending on whether option is selected
            chkBxOther.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    EditText txtOther = (EditText) findViewById(R.id.txtOther);
                    if (compoundButton.isChecked()) {
                        txtOther.setVisibility(View.VISIBLE);
                    } else {
                        txtOther.setVisibility(View.INVISIBLE);
                    }
                }
            });
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
        return true;

        // TODO "Other" Validation (make sure edit text is non-empty)
        // TODO validate only if required
    }

    @Override
    public void launchNextQuestion() {
        Intent intent = Utils.getLaunchQuestionIntent(this, this.question.getNextQuestionId());
        startActivity(intent);
    }
}
