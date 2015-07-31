package informatics.uk.ac.ed.track.feedback.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;

import informatics.uk.ac.ed.track.R;

public class SocialEmotionStackedBarChart extends Fragment {

    private BarChart barChart;

    public static Fragment newInstance() {
        return new EmotionsPieChart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_social_emotion_stacked_bar_chart, container, false);

        barChart = (BarChart) v.findViewById(R.id.barChart);
        /*barChart.setDescription("");

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");

        barChart.setCenterTextTypeface(tf);
        barChart.setCenterText("Revenues");
        barChart.setCenterTextSize(22f);
        barChart.setCenterTextTypeface(tf);

        // radius of the center hole in percent of maximum radius
        barChart.setHoleRadius(45f);
        barChart.setTransparentCircleRadius(50f);

        Legend l = barChart.getLegend();
        l.setPosition(LegendPosition.RIGHT_OF_CHART);

        barChart.setData(generatePieData());*/

        return v;
    }

}
