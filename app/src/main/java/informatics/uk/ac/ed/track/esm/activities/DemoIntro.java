package informatics.uk.ac.ed.track.esm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import informatics.uk.ac.ed.track.R;


public class DemoIntro extends AppCompatActivity {

    Button btnStartDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_intro);

        btnStartDemo = (Button) findViewById(R.id.btnStartDemo);

        btnStartDemo.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DemoIntro.this, DemoFreeText.class);
                startActivity(intent);
            }
        });
    }

    public void btnStartDemo_onClick(View view) {
        Intent intent = new Intent(this, DemoFreeText.class);
        startActivity(intent);
    }
}
