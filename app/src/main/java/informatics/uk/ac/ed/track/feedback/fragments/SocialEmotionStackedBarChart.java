package informatics.uk.ac.ed.track.feedback.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Random;

import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.feedback.IntegerFormatter;
import informatics.uk.ac.ed.track.feedback.Utils;

public class SocialEmotionStackedBarChart extends Fragment {

    public static Fragment newInstance() {
        return new SocialEmotionStackedBarChart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_social_emotion_stacked_bar_chart, container, false);
        this.initialiseChart(view);
        return view;
    }

    private void initialiseChart(View view) {
        BarChart barChart = (BarChart) view.findViewById(R.id.barChart);
        Resources res = getResources();

        barChart.setDescription(null);
        barChart.setDrawValueAboveBar(false);
        barChart.getAxisRight().setEnabled(false); // hide right y-axis

        XAxis xLabels = barChart.getXAxis();
        xLabels.setPosition(XAxis.XAxisPosition.BOTTOM);

        Legend legend = barChart.getLegend();
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        legend.setTextColor(res.getColor(R.color.primary_text));
        legend.setForm(Legend.LegendForm.CIRCLE);

        String[] emotionLabels = res.getStringArray(R.array.emotionLabels);
        String[] socialLabels = res.getStringArray(R.array.socialLabels);

        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals = new ArrayList<>();

        for (int i = 0; i < socialLabels.length; i++) {
            xVals.add(socialLabels[i]);
            yVals.add(new BarEntry(this.getEmotionSocialCounts(socialLabels[i], emotionLabels), i));
        }

        BarDataSet dataSet = new BarDataSet(yVals, null);
        dataSet.setColors(this.getColors(emotionLabels.length));
        dataSet.setStackLabels(emotionLabels);
        dataSet.setValueTextColor(res.getColor(R.color.text_icons));
        dataSet.setValueTextSize(12f);

        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        BarData data = new BarData(xVals, dataSets);
        data.setValueFormatter(new IntegerFormatter());

        barChart.setData(data);
        barChart.invalidate();

        /*
        ArrayList<BarEntry> yVals = new ArrayList<>();
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
        dataSet.setValueTextSize(android.R.attr.textAppearanceSmall);
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

    private float[] getEmotionSocialCounts(String emotionLabel, String[] socialLabels) {
        float[] counts = new float[socialLabels.length];

        for (int i = 0; i < socialLabels.length; i++) {
            float count = this.getEmotionSocialCounts(emotionLabel, socialLabels[i]);
            if (count > 0) {
                // TODO fix legend labels to correspond with non-zero values
                counts[i] = count;
            }
        }
        return counts;
    }

    private float getEmotionSocialCounts(String emotionLabel, String socialLabel) {
        Random rand = new Random();
        int max = 20;
        int min = 0;
        return rand.nextInt((max - min) + 1) + min;
    }

    private int[] getColors(int stackSize) {
        // have as many colors as stack-values per entry
        int[] colors = new int[stackSize];

        ArrayList<Integer> colorTemplate = Utils.getExtendedColorTemplate();

        for (int i = 0; i < stackSize; i++) {
            colors[i] = colorTemplate.get(i);
        }

        return colors;
    }

}
