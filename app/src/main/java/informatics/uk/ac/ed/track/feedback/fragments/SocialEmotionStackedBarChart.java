package informatics.uk.ac.ed.track.feedback.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
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
        barChart.getAxisLeft().setValueFormatter(new IntegerFormatter());

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
            yVals.add(new BarEntry(this.getSocialEmotionCounts(socialLabels[i], emotionLabels), i));
        }

        BarDataSet dataSet = new BarDataSet(yVals, null);
        dataSet.setColors(this.getColors(emotionLabels.length));
        dataSet.setStackLabels(emotionLabels);
        dataSet.setValueTextColor(res.getColor(R.color.text_icons));
        dataSet.setValueTextSize(Utils.getValueTextSize());

        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        BarData data = new BarData(xVals, dataSets);
        data.setValueFormatter(new IntegerFormatter());

        barChart.setData(data);
        barChart.invalidate();
    }

    private float[] getSocialEmotionCounts(String socialLabel, String[] emotionLabels) {
        float[] counts = new float[emotionLabels.length];

        for (int i = 0; i < emotionLabels.length; i++) {
            float count = this.getSocialEmotionCount(socialLabel, emotionLabels[i]);
            if (count > 0) {
                counts[i] = count;
            }
        }
        return counts;
    }

    private float getSocialEmotionCount(String socialLabel, String emotionLabel) {
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
