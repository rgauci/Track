package informatics.uk.ac.ed.track.esm.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import informatics.uk.ac.ed.track.R;

public class FeedbackGraphs extends AppCompatActivity {

    PieChart emotionsPieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_graphs);

        emotionsPieChart = (PieChart) findViewById(R.id.emotionsPieChart);

        ArrayList<Entry> pieChartEntries = new ArrayList<>();
        pieChartEntries.add(new Entry((float)3, 0));
        pieChartEntries.add(new Entry((float)5, 1));
        pieChartEntries.add(new Entry((float)6, 2));
        pieChartEntries.add(new Entry((float)1, 3));
        ArrayList<String> pieChartLabels = new ArrayList<>();
        pieChartLabels.add("Happy");
        pieChartLabels.add("Sad");
        pieChartLabels.add("Guilty");
        pieChartLabels.add("Anxious");

        /*
        final int[] My_COLORS = { Color.rgb(206, 37, 42), Color.rgb(0, 255, 0), Color.rgb(0, 0, 255) };
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : My_COLORS)
            colors.add(c);
*/


        //pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        //pieDataSet.setColor(getResources().getColor(R.color.primary));
        //pieDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        //pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        //pieDataSet.setColor(ColorTemplate.getHoloBlue());

        PieDataSet pieDataSet = new PieDataSet(pieChartEntries, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData(pieChartLabels, pieDataSet);
        emotionsPieChart.setUsePercentValues(true);
        emotionsPieChart.setDescription("COLORFUL_COLORS");
        emotionsPieChart.setData(pieData);
        emotionsPieChart.invalidate();

        PieDataSet pieDataSet1 = new PieDataSet(pieChartEntries, "");
        pieDataSet1.setColors(ColorTemplate.JOYFUL_COLORS);
        PieData pieData1 = new PieData(pieChartLabels, pieDataSet1);
        PieChart emotionsPieChart1 = (PieChart) findViewById(R.id.emotionsPieChart1);
        emotionsPieChart1.setUsePercentValues(true);
        emotionsPieChart1.setDescription("JOYFUL_COLORS");
        emotionsPieChart1.setData(pieData1);
        emotionsPieChart1.invalidate();

        PieDataSet pieDataSet2 = new PieDataSet(pieChartEntries, "");
        pieDataSet2.setColors(ColorTemplate.PASTEL_COLORS);
        PieData pieData2 = new PieData(pieChartLabels, pieDataSet2);
        PieChart emotionsPieChart2 = (PieChart) findViewById(R.id.emotionsPieChart2);
        //emotionsPieChart2.setUsePercentValues(true);
        emotionsPieChart2.setDescription("PASTEL_COLORS");
        emotionsPieChart2.setData(pieData2);
        emotionsPieChart2.invalidate();

        PieDataSet pieDataSet3 = new PieDataSet(pieChartEntries, "");
        pieDataSet3.setColors(ColorTemplate.LIBERTY_COLORS);
        PieData pieData3 = new PieData(pieChartLabels, pieDataSet3);
        PieChart emotionsPieChart3 = (PieChart) findViewById(R.id.emotionsPieChart3);
        emotionsPieChart3.setUsePercentValues(true);
        emotionsPieChart3.setDescription("LIBERTY_COLORS");
        emotionsPieChart3.setData(pieData3);
        emotionsPieChart3.invalidate();

        PieDataSet pieDataSet4 = new PieDataSet(pieChartEntries, "");
        pieDataSet4.setColors(ColorTemplate.VORDIPLOM_COLORS);
        PieData pieData4 = new PieData(pieChartLabels, pieDataSet4);
        PieChart emotionsPieChart4 = (PieChart) findViewById(R.id.emotionsPieChart4);
        emotionsPieChart4.setUsePercentValues(true);
        emotionsPieChart4.setDescription("VORDIPLOM_COLORS");
        emotionsPieChart4.setData(pieData4);
        emotionsPieChart4.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feedback_graphs, menu);
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
}
