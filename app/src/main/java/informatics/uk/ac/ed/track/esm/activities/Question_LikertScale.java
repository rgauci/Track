package informatics.uk.ac.ed.track.esm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import informatics.uk.ac.ed.track.esm.Constants;
import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.Utils;
import informatics.uk.ac.ed.track.lib.BranchableAnswerOption;
import informatics.uk.ac.ed.track.lib.LikertScaleQuestion;


public class Question_LikertScale extends TrackQuestionActivity {

    private final static int LYT_BUTTON_INDEX = 0;
    private final static int LYT_TEXT_VIEW_INDEX = 1;

    private LikertScaleQuestion question;
    private HashMap<Integer,BranchableAnswerOption> optionsMap;

    private LinearLayout checkedOptionLyt;
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

        // if question is branchable, make sure to mark it as required
        // or we will not know which next activity to launch
        if (this.question.getIsBranchable()) {
            this.question.setIsRequired(true);
        }

        /* display title, question and prefix, if available */
        this.displayTitleQuestionAndPrefix(this.question, R.id.toolbar, R.id.txtVwToolbarTitle,
                R.id.txtVwToolbarSubTitle, R.id.txtVwQuestionText, R.id.txtVwQuestionPrefix);

        /* display back/next (navigation) buttons */
        this.displayNavigationButtons(question, intent,
                R.id.btnPrevious, R.id.btnNext, R.id.btnFinish);

        /* add likert scale options */
        LinearLayout lytScale = (LinearLayout) findViewById(R.id.lytScale);
        Resources res = getResources();
        ArrayList<BranchableAnswerOption> options = this.question.getAnswerOptions();
        optionsMap = new HashMap<>();
        ButtonOnClickListener btnOnClickListener = new ButtonOnClickListener();
        LayoutOnClickListener lytOnClickListener = new LayoutOnClickListener();
        for (BranchableAnswerOption option : options) {
            this.addLikertOption(res, lytScale, option, lytOnClickListener, btnOnClickListener);
        }
    }

    @Override
    public boolean isValid() {
        boolean hasErrors = false;

        // if question is required, make sure an option has been selected
        if (this.question.getIsRequired()) {
            if (this.checkedOptionLyt == null) {
                Toast toast = Toast.makeText(this,
                        getResources().getString(R.string.error_answerToProceed), Toast.LENGTH_SHORT);
                toast.show();
                hasErrors = true;
            }
        }

        return !hasErrors;
    }

    @Override
    public void launchNextQuestion() {
        int nextQuestionId;

        if (this.question.getIsBranchable()) {
            BranchableAnswerOption checkedOption = optionsMap.get(this.checkedOptionLyt.getId());
            nextQuestionId = checkedOption.getNextQuestionId();
        } else {
            nextQuestionId = this.question.getNextQuestionId();
        }

        Intent intent = Utils.getLaunchQuestionIntent(this, nextQuestionId);
        intent.putExtra(Constants.SURVEY_RESPONSES, this.getSurveyResponsesForNextIntent());
        startActivity(intent);
    }

    @Override
    public String getSurveyResponsesForNextIntent() {
        String surveyResponses;
        if (this.checkedOptionLyt != null) {
            // if answered, add selected Likert Anchor ID to response
            surveyResponses = this.addAnswerToSurveyResponses(this.question.getColumnName(),
                    Integer.toString(this.checkedOptionLyt.getId()));
        } else {
            // otherwise, add nothing
            surveyResponses = this.getIntentSurveyResponses();
        }
        return surveyResponses;
    }

    public void addLikertOption(Resources res, LinearLayout lytScale,
                                BranchableAnswerOption option,
                                LayoutOnClickListener lytOnClickListener,
                                ButtonOnClickListener btnOnClickListener) {

        // create horizontal layout for button and anchor text
        LinearLayout lytOption = new LinearLayout(this);
        lytOption.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        lytOption.setOrientation(LinearLayout.HORIZONTAL);

        // set layout Id to option Id
        lytOption.setId(option.getOptionId());

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

        // add option to HashMap
        this.optionsMap.put(option.getOptionId(), option);
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
        this.setCheckedOptionLyt((LinearLayout) view);
    }

    private void setCheckedOptionLyt(LinearLayout lyt) {
        if (this.checkedOptionLyt != null) {
            this.styleLikertOption(this.checkedOptionLyt, false);
        }

        this.checkedOptionLyt = lyt;
        this.styleLikertOption(this.checkedOptionLyt, true);
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
