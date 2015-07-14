package informatics.uk.ac.ed.track;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;

import informatics.uk.ac.ed.track.lib.BranchableAnswerOption;
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

        /* add likert scale options */
        LinearLayout lytScale = (LinearLayout) findViewById(R.id.lytScale);
        Resources res = getResources();
        ArrayList<BranchableAnswerOption> options = this.question.getAnswerOptions();
        for (BranchableAnswerOption option : options) {
            this.addLikertOption(res, lytScale, option);
        }
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

    public void addLikertOption(Resources res, LinearLayout lytScale, BranchableAnswerOption option) {

        LinearLayout lytOption = new LinearLayout(this);
        lytOption.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        lytOption.setOrientation(LinearLayout.HORIZONTAL);

        Button btnLikert = new Button(this);
        btnLikert.setLayoutParams(new LinearLayout.LayoutParams(
            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    res.getInteger(R.integer.likert_button_width_dp), res.getDisplayMetrics()),
            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    res.getInteger(R.integer.likert_button_height_dp), res.getDisplayMetrics())));
        btnLikert.setText(Integer.toString(option.getOptionId()));

        TextView txtVwOption = new TextView(this);
        txtVwOption.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        txtVwOption.setText(option.getOption());

        lytOption.addView(btnLikert);
        lytOption.addView(txtVwOption);
        lytScale.addView(lytOption);
    }
}
