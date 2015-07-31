package informatics.uk.ac.ed.track.feedback.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;
import java.util.Random;

import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.feedback.IntegerFormatter;
import informatics.uk.ac.ed.track.feedback.Utils;

public class EmotionsPieChart extends Fragment {

    public static Fragment newInstance() {
        return new EmotionsPieChart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emotions_pie_chart, container, false);
        this.initialiseChart(view);
        return view;
    }

    private void initialiseChart(View view) {
        PieChart pieChart = (PieChart) view.findViewById(R.id.pieChart);
        Resources res = getResources();

        pieChart.setDescription(null);
        pieChart.setCenterText(res.getString(R.string.emotionsPieChartCenterText));
        pieChart.setCenterTextSize(16f);
        pieChart.setCenterTextColor(res.getColor(R.color.primary_text));
        pieChart.getLegend().setEnabled(false); // won't fit on small screens (overlaps on chart)

        // Get Frequency Count for Each Emotion
        String[] emotionColumns = res.getStringArray(R.array.emotionColumns);
        String[] emotionLabels = res.getStringArray(R.array.emotionLabels);

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();

        for (int i = 0; i < emotionColumns.length; i++) {
            int count = this.getEmotionCount(emotionColumns[i]);
            if (count > 0) {
                entries.add(new Entry(count, entries.size()));
                xVals.add(emotionLabels[i]);
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, null);
        dataSet.setColors(Utils.getDefaultColorTemplate());
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextColor(res.getColor(R.color.text_icons));
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new IntegerFormatter());

        PieData data = new PieData(xVals, dataSet);
        //d.setValueTypeface(tf);

        pieChart.setData(data);
        pieChart.animateY(1500, Easing.EasingOption.EaseInOutQuad);

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

        pieChart.setData(generatePieData()); */
    }

    private int getEmotionCount(String emotionCol) {
        Random rand = new Random();
        int max = 20;
        int min = 0;
        return rand.nextInt((max - min) + 1) + min;
    }

}
