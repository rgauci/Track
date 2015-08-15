package informatics.uk.ac.ed.track.esm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import informatics.uk.ac.ed.track.R;

public class SetupComplete extends AppCompatActivity {

    private Button btnDemoNo;
    private Button btnDemoYes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_complete);

        btnDemoNo = (Button) findViewById(R.id.btnDemoNo);
        btnDemoYes = (Button) findViewById(R.id.btnDemoYes);

        btnDemoYes.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SetupComplete.this, DemoIntro.class);
                startActivity(intent);
            }
        });

        btnDemoNo.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SetupComplete.this, BriefingComplete.class);
                startActivity(intent);
            }
        });
    }
}
