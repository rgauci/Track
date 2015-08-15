package informatics.uk.ac.ed.track.esm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import informatics.uk.ac.ed.track.R;


public class DemoMultiChoice_Single extends AppCompatActivity {

    private Button btnNext, btnPrevious;
    private RadioGroup rdGrp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_multi_choice__single);

        // initialise UI controls
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrevious = (Button) findViewById(R.id.btnPrevious);
        rdGrp = (RadioGroup) findViewById(R.id.rdGrp);

        // set onClick listeners
        btnNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    Intent intent =
                            new Intent(DemoMultiChoice_Single.this, DemoMultiChoice_Multi.class);
                    startActivity(intent);
                }
            }
        });

        btnPrevious.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                DemoMultiChoice_Single.super.onBackPressed();
            }
        });
    }

    public boolean isValid() {
        boolean hasErrors = false;

        if (rdGrp.getCheckedRadioButtonId() == -1) {
            Toast toast = Toast.makeText(this,
                    getResources().getString(R.string.error_answerToProceed), Toast.LENGTH_SHORT);
            toast.show();
            hasErrors = true;
        }

        return !hasErrors;
    }
}
