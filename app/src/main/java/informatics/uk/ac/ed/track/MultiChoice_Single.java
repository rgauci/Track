package informatics.uk.ac.ed.track;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import informatics.uk.ac.ed.track.Constants;
import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.Utils;
import informatics.uk.ac.ed.track.lib.BranchableAnswerOption;
import informatics.uk.ac.ed.track.lib.MultipleChoiceMultipleAnswer;
import informatics.uk.ac.ed.track.lib.MultipleChoiceSingleAnswer;
import informatics.uk.ac.ed.track.lib.TrackQuestion;

public class MultiChoice_Single extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_choice__single);

        Intent intent = getIntent();
        SharedPreferences preferences =
                Utils.getQuestionPreferences(getApplicationContext(),
                        intent.getIntExtra(Constants.QUESTION_ID, Constants.DEF_VALUE_INT));

        Gson gson = new Gson();
        MultipleChoiceSingleAnswer question = gson.fromJson(preferences.getString(Constants.QUESTION_JSON,
                Constants.DEF_VALUE_STR), MultipleChoiceSingleAnswer.class);

        String title = question.getTitle();
        String questionText = question.getQuestionText();

        if ((title == null) || title.isEmpty()) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) findViewById(R.id.txtVwToolbarTitle);
            textView.setText(title);
        }

        TextView txtVwQuestionText = (TextView) findViewById(R.id.txtVwQuestionText);
        if ((title == null) || title.isEmpty()) {
            txtVwQuestionText.setVisibility(View.GONE);
        } else {
            txtVwQuestionText.setText(questionText);
        }

        RadioGroup rdGrp = (RadioGroup) findViewById(R.id.rdGrp);

        for (BranchableAnswerOption option : question.getAnswerOptions()) {
            RadioButton rdBtn = new RadioButton(this);
            rdBtn.setText(option.getOption());
            rdBtn.setId(option.getOptionId());
            rdGrp.addView(rdBtn);
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
}
