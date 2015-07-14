package informatics.uk.ac.ed.track;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import informatics.uk.ac.ed.track.lib.TrackQuestion;

public abstract class TrackQuestionActivity extends AppCompatActivity {

    private static final String LOG_TAG = "TRACK.TrackQuestActvty";

    public void displayTitleQuestionAndPrefix(TrackQuestion question, int toolbarViewId, int toolbarTxtViewId,
                                              int questionTextTxtViewId,
                                              int questionPrefixTextViewId) {
        if (question == null) {
            String eMsg = "Question cannot be null";
            Exception npe = new NullPointerException("question cannot be null");
            Log.e(LOG_TAG, eMsg, npe);
        }

        this.displayTitle(question, toolbarViewId, toolbarTxtViewId);
        this.displayQuestionPrefix(question, questionPrefixTextViewId);
        this.displayQuestionText(question, questionTextTxtViewId);
    }

    private void displayTitle(TrackQuestion question, int toolbarViewId, int toolbarTxtViewId) {
        String title = question.getTitle();

        if (Utils.isNullOrEmpty(title)) {
            Toolbar toolbar = (Toolbar) findViewById(toolbarViewId);
            toolbar.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) findViewById(toolbarTxtViewId);
            textView.setText(title);
        }
    }

    private void displayQuestionText(TrackQuestion question, int questionTextTxtViewId) {
        String questionText = question.getQuestionText();
        TextView txtVwQuestionText = (TextView) findViewById(questionTextTxtViewId);

        if (Utils.isNullOrEmpty(questionText)) {
            txtVwQuestionText.setVisibility(View.GONE);
        } else {
            txtVwQuestionText.setText(questionText);
        }
    }

    private void displayQuestionPrefix(TrackQuestion question, int questionPrefixTextViewId) {
        String questionPrefix = question.getQuestionPrefix();
        TextView txtVwQuestionPrefix = (TextView) findViewById(questionPrefixTextViewId);

        if (Utils.isNullOrEmpty(questionPrefix)) {
            txtVwQuestionPrefix.setVisibility(View.GONE);
        } else {
            txtVwQuestionPrefix.setText(questionPrefix);
        }
    }

    public void displayNavigationButtons(Intent activityIntent,
                                         int btnPreviousViewId, int btnNextViewId) {
        boolean isFirstQuestion =
                activityIntent.getBooleanExtra(Constants.IS_FIRST_QUESTION,
                        Constants.DEF_VALUE_BOOL);

        Button btnPrevious = (Button) findViewById(btnPreviousViewId);
        Button btnNext = (Button) findViewById(btnNextViewId);

        if (isFirstQuestion) {
            btnPrevious.setVisibility(View.GONE);
        } else {
            btnPrevious.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TrackQuestionActivity.super.onBackPressed();
                }
            });
        }

        // set onClick listeners
        btnNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchNextQuestion();
            }
        });

        // TODO "Next" Button on Final question
    }

    public abstract void launchNextQuestion();
}
