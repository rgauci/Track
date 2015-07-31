package informatics.uk.ac.ed.track.feedback.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.HorizontalBarChart;

import informatics.uk.ac.ed.track.R;

public class CompanyHorizontalBarChart extends Fragment {

    private HorizontalBarChart horBarChart;

    public static Fragment newInstance() {
        return new EmotionsPieChart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_company_horizontal_bar_chart, container, false);

        horBarChart = (HorizontalBarChart) v.findViewById(R.id.horBarChart);
        /*pieChart.setDescription("");

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");

        pieChart.setCenterTextTypeface(tf);
        pieChart.setCenterText("Revenues");
        pieChart.setCenterTextSize(22f);
        pieChart.setCenterTextTypeface(tf);

        // radius of the center hole in percent of maximum radius
        pieChart.setHoleRadius(45f);
        pieChart.setTransparentCircleRadius(50f);

        Legend l = pieChart.getLegend();
        l.setPosition(LegendPosition.RIGHT_OF_CHART);

        pieChart.setData(generatePieData());*/

        return v;
    }

}
