package informatics.uk.ac.ed.track;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.lib.FreeTextQuestionMultiLine;

public class Question_FreeText_Multi extends TrackQuestionActivity {

    private FreeTextQuestionMultiLine question;
    private EditText txtAnswer;

    private String answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question__free_text__multi);

        this.txtAnswer = (EditText) findViewById(R.id.txtAnswer);

        /* get question preferences using question ID */
        Intent intent = getIntent();
        SharedPreferences preferences =
                Utils.getQuestionPreferences(getApplicationContext(),
                        intent.getIntExtra(Constants.QUESTION_ID, Constants.DEF_VALUE_INT));

        /* deserialize question JSON string into object */
        Gson gson = new Gson();
        this.question = gson.fromJson(
                preferences.getString(Constants.QUESTION_JSON, Constants.DEF_VALUE_STR),
                FreeTextQuestionMultiLine.class);

        /* display title, question and prefix, if available */
        this.displayTitleQuestionAndPrefix(this.question, R.id.toolbar, R.id.txtVwToolbarTitle,
                R.id.txtVwToolbarSubTitle, R.id.txtVwQuestionText, R.id.txtVwQuestionPrefix);

        /* display back/next (navigation) buttons */
        this.displayNavigationButtons(question, intent,
                R.id.btnPrevious, R.id.btnNext, R.id.btnFinish);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question__free_text__multi, menu);
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

        this.answer = Utils.getTrimmedText(txtAnswer);

        if (this.question.getIsRequired()) {
            if (TextUtils.isEmpty(this.answer)) {
                Toast toast = Toast.makeText(this,
                        getResources().getString(R.string.error_answerToProceed), Toast.LENGTH_SHORT);
                toast.show();
                hasErrors = true;
            }
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
