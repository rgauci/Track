package informatics.uk.ac.ed.track.feedback.activities;

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
import informatics.uk.ac.ed.track.feedback.fragments.CompanyHorizontalBarChart;
import informatics.uk.ac.ed.track.feedback.fragments.EmotionsPieChart;
import informatics.uk.ac.ed.track.feedback.fragments.SocialEmotionStackedBarChart;

public class FeedbackViewPager extends AppCompatActivity {

    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_view_pager);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(PageAdapter.NUM_GRAPHS_AVAILABLE);

        PageAdapter pgAdapter = new PageAdapter(getSupportFragmentManager());
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

        private final static int NUM_GRAPHS_AVAILABLE = 3;

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            Fragment f = null;

            switch(pos) {
                case 0:
                    f = EmotionsPieChart.newInstance();
                    break;
                case 1:
                    f = SocialEmotionStackedBarChart.newInstance();
                    break;
                case 2:
                    f = CompanyHorizontalBarChart.newInstance();
                    break;
            }

            return f;
        }

        /**
         *
         * @return Number of views available.
         */
        @Override
        public int getCount() {
            return NUM_GRAPHS_AVAILABLE;
        }
    }
}
