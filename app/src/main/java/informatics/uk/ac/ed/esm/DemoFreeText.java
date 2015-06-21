package informatics.uk.ac.ed.esm;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class DemoFreeText extends AppCompatActivity {

    private EditText txtAnswer;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_free_text);

        btnNext = (Button) findViewById(R.id.btnNext);
        txtAnswer = (EditText) findViewById(R.id.txtAnswer);

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

        String answer = Utils.getTrimmedText(txtAnswer);
        if (TextUtils.isEmpty(answer)) {
            Toast toast = Toast.makeText(this,
                    getResources().getString(R.string.error_answerToProceed), Toast.LENGTH_SHORT);
            toast.show();
            hasErrors = true;
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
