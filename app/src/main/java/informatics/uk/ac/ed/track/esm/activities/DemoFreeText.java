package informatics.uk.ac.ed.track.esm.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.CharacterCountErrorWatcher;
import informatics.uk.ac.ed.track.esm.Utils;


public class DemoFreeText extends AppCompatActivity {

    private Button btnNext;
    private EditText txtAnswer;
    private TextInputLayout txtAnswer_InpLyt;

    private final static int MAX_ANSWER_LENGTH = 140;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_free_text);

        // initialise UI controls
        this.btnNext = (Button) findViewById(R.id.btnNext);
        this.txtAnswer = (EditText) findViewById(R.id.txtAnswer);
        this.txtAnswer_InpLyt = (TextInputLayout) findViewById(R.id.txtAnswer_InpLyt);

        this.txtAnswer.addTextChangedListener(
                new CharacterCountErrorWatcher(this.txtAnswer_InpLyt, 0, MAX_ANSWER_LENGTH));

        btnNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    Intent intent = new Intent(DemoFreeText.this, DemoMultiChoice_Single.class);
                    startActivity(intent);
                }
            }
        });
    }

    public boolean isValid() {
        boolean hasErrors = false;

        String answer = txtAnswer.getText().toString();

        // check if empty
        if (TextUtils.isEmpty(answer)) {
            Toast toast = Toast.makeText(this,
                    getString(R.string.error_answerToProceed), Toast.LENGTH_SHORT);
            toast.show();
            hasErrors = true;
        }

        // check if answer text exceeds max character limit
        if (answer.length() > MAX_ANSWER_LENGTH) {
            txtAnswer_InpLyt.setError(String.format(getString(R.string.error_answerTooLong),
                    MAX_ANSWER_LENGTH));
            hasErrors = true;
        } else {
            txtAnswer_InpLyt.setError(null);
        }

        return !hasErrors;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_demo_free_text, menu);
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
