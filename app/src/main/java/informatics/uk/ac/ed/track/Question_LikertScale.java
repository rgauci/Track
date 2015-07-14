package informatics.uk.ac.ed.track;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import informatics.uk.ac.ed.track.lib.LikertScaleQuestion;
import informatics.uk.ac.ed.track.lib.MultipleChoiceSingleAnswer;


public class Question_LikertScale extends TrackQuestionActivity {

    private LikertScaleQuestion question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question__likert_scale);

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
                LikertScaleQuestion.class);

        /* display title, question and prefix, if available */
        this.displayTitleQuestionAndPrefix(this.question, R.id.toolbar, R.id.txtVwToolbarTitle,
                R.id.txtVwQuestionText, R.id.txtVwQuestionPrefix);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question__likert_scale, menu);
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
    public void launchNextQuestion() {

    }
}
