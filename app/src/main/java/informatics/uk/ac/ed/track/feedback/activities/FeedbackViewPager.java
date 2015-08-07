package informatics.uk.ac.ed.track.feedback.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.Constants;
import informatics.uk.ac.ed.track.esm.Utils;
import informatics.uk.ac.ed.track.feedback.fragments.CompanyHorizontalBarChart;
import informatics.uk.ac.ed.track.feedback.fragments.DailyEmotionsPieChart;
import informatics.uk.ac.ed.track.feedback.fragments.EmotionsPieChart;
import informatics.uk.ac.ed.track.feedback.fragments.RegulationHorizontalBarChart;
import informatics.uk.ac.ed.track.feedback.fragments.SocialEmotionStackedBarChart;

public class FeedbackViewPager extends AppCompatActivity {

    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_view_pager);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);

        PageAdapter pgAdapter = new PageAdapter(getSupportFragmentManager());
        pager.setOffscreenPageLimit(pgAdapter.numGraphsAvailable);
        pager.setAdapter(pgAdapter);

        Snackbar.make(findViewById(R.id.lytSnackbar), getResources().getString(R.string.feedbackPagerInstructions), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feedback_view_pager, menu);
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

    private class PageAdapter extends FragmentPagerAdapter {

        private final static int RESEARCH_PARTICIPANT_MINIMUM_NUM_GRAPHS = 3;
        private final static int NON_RESEARCH_PARTICIPANT_MINIMUM_NUM_GRAPHS = 4;

        private boolean isResearchParticipant;

        public int numGraphsAvailable;

        public PageAdapter(FragmentManager fm) {
            super(fm);

            this.isResearchParticipant = Utils.getIsResearchParticipant(getApplicationContext());

            if (this.isResearchParticipant) {
                this.numGraphsAvailable = RESEARCH_PARTICIPANT_MINIMUM_NUM_GRAPHS +
                        this.getNumberOfEmotionDayGraphsAvailable();
            } else {
                this.numGraphsAvailable = NON_RESEARCH_PARTICIPANT_MINIMUM_NUM_GRAPHS +
                        this.getNumberOfEmotionDayGraphsAvailable();
            }
        }

        @Override
        public Fragment getItem(int pos) {
            Fragment f;

            if (this.isResearchParticipant) {
                switch (pos) {
                    case 0:
                        f = EmotionsPieChart.newInstance();
                        break;
                    case 1:
                        f = SocialEmotionStackedBarChart.newInstance();
                        break;
                    case 2:
                        f = RegulationHorizontalBarChart.newInstance();
                        break;
                    default:
                        f = getDailyEmotionsPieChartInstance(pos);
                        break;
                }
            } else {
                // Company Horizontal Bar Chart only available for non-participants
                switch (pos) {
                    case 0:
                        f = EmotionsPieChart.newInstance();
                        break;
                    case 1:
                        f = SocialEmotionStackedBarChart.newInstance();
                        break;
                    case 2:
                        f = RegulationHorizontalBarChart.newInstance();
                        break;
                    case 3:
                        f = CompanyHorizontalBarChart.newInstance();
                        break;
                    default:
                        f = getDailyEmotionsPieChartInstance(pos);
                        break;
                }
            }

            return f;
        }

        /**
         *
         * @return Number of views available.
         */
        @Override
        public int getCount() {
            return this.numGraphsAvailable;
        }

        private Fragment getDailyEmotionsPieChartInstance(int pos) {
            if (this.isResearchParticipant) {
                return DailyEmotionsPieChart.newInstance(pos - RESEARCH_PARTICIPANT_MINIMUM_NUM_GRAPHS + 1);
            } else {
                return DailyEmotionsPieChart.newInstance(pos - NON_RESEARCH_PARTICIPANT_MINIMUM_NUM_GRAPHS + 1);
            }
        }

        private int getNumberOfEmotionDayGraphsAvailable() {

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            // get value from SharedPreferences
            // will never exceed study end time since this value is set by the AlarmReceiver
            // (so the last time it is set is at the time of the last notification)
            int dayNum = settings.getInt(Constants.CURRENT_STUDY_DAY_NUMBER, Constants.DEF_VALUE_INT);

            if (dayNum == Constants.DEF_VALUE_INT) {
                return 0;
            } else {
                return dayNum;
            }
        }

    }
}
