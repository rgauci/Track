package informatics.uk.ac.ed.track;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import informatics.uk.ac.ed.track.lib.MultipleChoiceMultipleAnswer;
import informatics.uk.ac.ed.track.lib.TrackQuestion;


public class MultiChoice_Multi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_choice__multi);

        Intent intent = getIntent();
        SharedPreferences preferences =
                Utils.getQuestionPreferences(getApplicationContext(),
                        intent.getIntExtra(Constants.QUESTION_ID, Constants.DEF_VALUE_INT));

        Gson gson = new Gson();
        TrackQuestion question = gson.fromJson(preferences.getString(Constants.QUESTION_JSON, Constants.DEF_VALUE_STR), MultipleChoiceMultipleAnswer.class);
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
}
