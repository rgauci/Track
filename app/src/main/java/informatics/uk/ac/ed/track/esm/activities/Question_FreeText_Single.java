package informatics.uk.ac.ed.track.esm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import informatics.uk.ac.ed.track.esm.CharacterCountErrorWatcher;
import informatics.uk.ac.ed.track.esm.Constants;
import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.Utils;
import informatics.uk.ac.ed.track.lib.FreeTextQuestionSingleLine;


public class Question_FreeText_Single extends TrackQuestionActivity {

    private FreeTextQuestionSingleLine question;

    private EditText txtAnswer;
    private TextInputLayout txtAnswer_InpLyt;

    private String answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question__free_text__single);

        // initialise UI controls
        this.txtAnswer = (EditText) findViewById(R.id.txtAnswer);
        this.txtAnswer_InpLyt = (TextInputLayout) findViewById(R.id.txtAnswer_InpLyt);

        /* get question preferences using question ID */
        Intent intent = getIntent();
        SharedPreferences preferences =
                Utils.getQuestionPreferences(getApplicationContext(),
                        intent.getIntExtra(Constants.QUESTION_ID, Constants.DEF_VALUE_INT));

        /* deserialize question JSON string into object */
        Gson gson = new Gson();
        this.question = gson.fromJson(
                preferences.getString(Constants.QUESTION_JSON, Constants.DEF_VALUE_STR),
                FreeTextQuestionSingleLine.class);

        /* display title, question and prefix, if available */
        this.displayTitleQuestionAndPrefix(this.question, R.id.toolbar, R.id.txtVwToolbarTitle,
                R.id.txtVwToolbarSubTitle, R.id.txtVwQuestionText, R.id.txtVwQuestionPrefix);

        /* display back/next (navigation) buttons */
        this.displayNavigationButtons(question, intent,
                R.id.btnPrevious, R.id.btnNext, R.id.btnFinish);

        /* set up character counter for edit text */
        this.txtAnswer.addTextChangedListener(
                new CharacterCountErrorWatcher(this.txtAnswer_InpLyt, 0,
                        this.question.getCharacterLimit()));
    }

    @Override
    public boolean isValid() {
        boolean hasErrors = false;

        this.answer = txtAnswer.getText().toString();

        if (this.question.getIsRequired()) {
            if (TextUtils.isEmpty(this.answer)) {
                Toast toast = Toast.makeText(this,
                        getResources().getString(R.string.error_answerToProceed),
                        Toast.LENGTH_SHORT);
                toast.show();
                hasErrors = true;
            }
        }

        // check if answer text exceeds max character limit
        if (this.answer.length() > this.question.getCharacterLimit()) {
            txtAnswer_InpLyt.setError(String.format(getString(R.string.error_answerTooLong),
                    this.question.getCharacterLimit()));
            hasErrors = true;
        } else {
            txtAnswer_InpLyt.setError(null);
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
        return this.addAnswerToSurveyResponses(this.question.getColumnName(),
                this.answer);
    }
}
