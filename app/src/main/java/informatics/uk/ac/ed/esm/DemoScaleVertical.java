package informatics.uk.ac.ed.esm;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class DemoScaleVertical extends AppCompatActivity {

    private final static int BUTTON_INDEX = 0;
    private final static int TEXTVIEW_INDEX = 1;

    private Button btnNext, btnPrevious;
    private Button btnLikert1, btnLikert2, btnLikert3, btnLikert4, btnLikert5;
    private LinearLayout checkedOption;

    private int accentColor, lightBackgroundColor, primaryTextColor, textIconsColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_scale_vertical);

        this.accentColor = getResources().getColor(R.color.accent);
        this.lightBackgroundColor = getResources().getColor(R.color.background_light);
        this.primaryTextColor = getResources().getColor(R.color.primary_text);
        this.textIconsColor = getResources().getColor(R.color.text_icons);

        // initialise UI controls
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrevious = (Button) findViewById(R.id.btnPrevious);

        btnLikert1 = (Button) findViewById(R.id.btnLikert1);
        btnLikert2 = (Button) findViewById(R.id.btnLikert2);
        btnLikert3 = (Button) findViewById(R.id.btnLikert3);
        btnLikert4 = (Button) findViewById(R.id.btnLikert4);
        btnLikert5 = (Button) findViewById(R.id.btnLikert5);

        // set onClick listeners
        btnNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    // TODO proceed to next activity
                    //Intent intent = new Intent(DemoScaleVertical.this, DemoScaleVertical.class);
                    //startActivity(intent);
                }
            }
        });

        btnPrevious.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                DemoScaleVertical.super.onBackPressed();
            }
        });

        ButtonOnClickListener btnOnClickListener = new ButtonOnClickListener();
        btnLikert1.setOnClickListener(btnOnClickListener);
        btnLikert2.setOnClickListener(btnOnClickListener);
        btnLikert3.setOnClickListener(btnOnClickListener);
        btnLikert4.setOnClickListener(btnOnClickListener);
        btnLikert5.setOnClickListener(btnOnClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_demo_scale_vertical, menu);
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

    private boolean isValid(){
        boolean hasErrors = false;

        if (this.checkedOption == null) {
            Toast toast = Toast.makeText(this,
                    getResources().getString(R.string.error_answerToProceed), Toast.LENGTH_SHORT);
            toast.show();
            hasErrors = true;
        }

        return !hasErrors;
    }

    public void lytLikertOption_onClick(View view){
        this.setCheckedOption((LinearLayout) view);
    }

    private void setCheckedOption(LinearLayout lyt) {
        Button checkedButton;
        TextView checkedTextView;

        if (this.checkedOption != null) {
            this.styleLikertOption(this.checkedOption, false);
        }

        this.checkedOption = lyt;
        this.styleLikertOption(this.checkedOption, true);
    }

    private void styleLikertOption(LinearLayout lyt, boolean selected) {
        Button checkedButton = (Button) lyt.getChildAt(BUTTON_INDEX);
        TextView checkedTextView = (TextView) lyt.getChildAt(TEXTVIEW_INDEX);

        if (selected) {
            checkedButton.setBackgroundColor(this.accentColor);
            checkedButton.setTextColor(this.textIconsColor);
            checkedTextView.setTextColor(this.accentColor);
        } else {
            checkedButton.setBackgroundColor(this.lightBackgroundColor);
            checkedButton.setTextColor(this.primaryTextColor);
            checkedTextView.setTextColor(this.primaryTextColor);
        }
    }

    private class ButtonOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            lytLikertOption_onClick((LinearLayout)view.getParent());
        }
    }
}
