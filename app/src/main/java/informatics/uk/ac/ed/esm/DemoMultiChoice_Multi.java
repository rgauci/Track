package informatics.uk.ac.ed.esm;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.ArrayList;


public class DemoMultiChoice_Multi extends AppCompatActivity {

    private Button btnNext, btnPrevious;
    private ArrayList<CheckBox> checkBoxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_multi_choice__multi);

        // initialise UI controls
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrevious = (Button) findViewById(R.id.btnPrevious);

        checkBoxes = new ArrayList<CheckBox>();
        checkBoxes.add((CheckBox) findViewById(R.id.chkBx1));
        checkBoxes.add((CheckBox) findViewById(R.id.chkBx2));
        checkBoxes.add((CheckBox) findViewById(R.id.chkBx3));
        checkBoxes.add((CheckBox) findViewById(R.id.chkBx4));
        checkBoxes.add((CheckBox) findViewById(R.id.chkBx5));
        checkBoxes.add((CheckBox) findViewById(R.id.chkBx6));
        checkBoxes.add((CheckBox) findViewById(R.id.chkBx7));
        checkBoxes.add((CheckBox) findViewById(R.id.chkBx8));
        checkBoxes.add((CheckBox) findViewById(R.id.chkBx9));

        // set onClick listeners
        btnNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    Intent intent = new Intent(DemoMultiChoice_Multi.this, DemoScale.class);
                    startActivity(intent);
                }
            }
        });

        btnPrevious.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                DemoMultiChoice_Multi.super.onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_demo_multi_choice__multi, menu);
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

    public boolean isValid() {
        boolean hasErrors = false;

        boolean atLeastOneSelected = false;
        int checked = 0;

        while ((!atLeastOneSelected) && (checked < this.checkBoxes.size())) {
            if (this.checkBoxes.get(checked).isChecked()) {
                atLeastOneSelected = true;
            }
            checked++;
        }

        if (!atLeastOneSelected) {
            Toast toast = Toast.makeText(this,
                    getResources().getString(R.string.error_answerToProceed), Toast.LENGTH_SHORT);
            toast.show();
            hasErrors = true;
        }

        return !hasErrors;
    }
}
