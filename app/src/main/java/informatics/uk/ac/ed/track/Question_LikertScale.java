package informatics.uk.ac.ed.track;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Layout;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import informatics.uk.ac.ed.track.lib.BranchableAnswerOption;
import informatics.uk.ac.ed.track.lib.LikertScaleQuestion;


public class Question_LikertScale extends TrackQuestionActivity {

    private final static int LYT_BUTTON_INDEX = 0;
    private final static int LYT_TEXT_VIEW_INDEX = 1;

    private LikertScaleQuestion question;
    private LinearLayout checkedOption;
    private int accentColor, lightBackgroundColor, primaryTextColor, textIconsColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question__likert_scale);

        // get colours for styling likert options on selection / deselection
        this.accentColor = getResources().getColor(R.color.accent);
        this.lightBackgroundColor = getResources().getColor(R.color.background_light);
        this.primaryTextColor = getResources().getColor(R.color.primary_text);
        this.textIconsColor = getResources().getColor(R.color.text_icons);

        /* get question preferences using question ID */
        Intent intent = getIntent();
        SharedPreferences preferences =
                Utils.getQuestionPreferences(getApplicationContext(),
                        intent.getIntExtra(Constants.QUESTION_ID, Constants.DEF_VALUE_INT));

        /* deserialize question JSON string into object */
        Gson gson = new Gson();
        this.question = gson.fromJson(
                preferences.getString(Constants.QUESTION_JSON, Constants.DEF_VALUE_STR),
                LikertScaleQuestion.class);

        /* display title, question and prefix, if available */
        this.displayTitleQuestionAndPrefix(this.question, R.id.toolbar, R.id.txtVwToolbarTitle,
                R.id.txtVwQuestionText, R.id.txtVwQuestionPrefix);

        /* display back/next (navigation) buttons */
        this.displayNavigationButtons(question, intent,
                R.id.btnPrevious, R.id.btnNext, R.id.btnFinish);

        /* add likert scale options */
        LinearLayout lytScale = (LinearLayout) findViewById(R.id.lytScale);
        Resources res = getResources();
        ArrayList<BranchableAnswerOption> options = this.question.getAnswerOptions();
        ButtonOnClickListener btnOnClickListener = new ButtonOnClickListener();
        LayoutOnClickListener lytOnClickListener = new LayoutOnClickListener();
        for (BranchableAnswerOption option : options) {
            this.addLikertOption(res, lytScale, option, lytOnClickListener, btnOnClickListener);
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
    public boolean isValid() {
        return true;
    }

    @Override
    public void launchNextQuestion() {
        if (this.question.getIsBranchable()) {
            // TODO handle branchable
        } else {
            Intent intent = Utils.getLaunchQuestionIntent(this, this.question.getNextQuestionId());
            startActivity(intent);
        }
    }

    public void addLikertOption(Resources res, LinearLayout lytScale,
                                BranchableAnswerOption option,
                                LayoutOnClickListener lytOnClickListener,
                                ButtonOnClickListener btnOnClickListener) {

        // create horizonatl layout for button and anchor text
        LinearLayout lytOption = new LinearLayout(this);
        lytOption.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        lytOption.setOrientation(LinearLayout.HORIZONTAL);

        // create button
        Button btnLikert =
                (Button) getLayoutInflater().inflate(R.layout.template_likert_button, null);
        LinearLayout.LayoutParams btnLayoutParams = new LinearLayout.LayoutParams(
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        res.getInteger(R.integer.likert_button_width_dp),
                        res.getDisplayMetrics()),
                (int)TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        res.getInteger(R.integer.likert_button_height_dp),
                        res.getDisplayMetrics()));
        btnLayoutParams.setMargins(0, 0,
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        res.getInteger(R.integer.likert_button_margin_right_dp),
                        res.getDisplayMetrics()),
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        res.getInteger(R.integer.likert_button_margin_bottom_dp),
                        res.getDisplayMetrics()));
        btnLikert.setLayoutParams(btnLayoutParams);

        // create textview
        TextView txtVwOption =
                (TextView) getLayoutInflater().inflate(R.layout.template_likert_button_anchor, null);
        txtVwOption.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // set button and anchor text
        btnLikert.setText(Integer.toString(option.getOptionId()));
        txtVwOption.setText(option.getOption());

        // add onClickListeners listeners
        lytOption.setOnClickListener(lytOnClickListener);
        btnLikert.setOnClickListener(btnOnClickListener);

        // add views to layout(s)
        lytOption.addView(btnLikert);
        lytOption.addView(txtVwOption);
        lytScale.addView(lytOption);
    }

    public class LayoutOnClickListener implements LinearLayout.OnClickListener {
        @Override
        public void onClick(View view) {
            lytLikertOption_onClick(view);
        }
    }

    private class ButtonOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            lytLikertOption_onClick((LinearLayout)view.getParent());
        }
    }

    public void lytLikertOption_onClick(View view){
        this.setCheckedOption((LinearLayout) view);
    }

    private void setCheckedOption(LinearLayout lyt) {
        if (this.checkedOption != null) {
            this.styleLikertOption(this.checkedOption, false);
        }

        this.checkedOption = lyt;
        this.styleLikertOption(this.checkedOption, true);
    }

    private void styleLikertOption(LinearLayout lyt, boolean selected) {
        Button checkedButton = (Button) lyt.getChildAt(LYT_BUTTON_INDEX);
        TextView checkedTextView = (TextView) lyt.getChildAt(LYT_TEXT_VIEW_INDEX);

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
}
