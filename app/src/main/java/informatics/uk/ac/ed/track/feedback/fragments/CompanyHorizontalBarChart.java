package informatics.uk.ac.ed.track.feedback.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Random;

import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.feedback.IntegerFormatter;
import informatics.uk.ac.ed.track.feedback.Utils;

public class CompanyHorizontalBarChart extends Fragment {

    public static Fragment newInstance() {
        return new CompanyHorizontalBarChart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company_horizontal_bar_chart, container, false);
        this.initialiseChart(view);
        return view;
    }

    private void initialiseChart(View view) {
        HorizontalBarChart horBarChart = (HorizontalBarChart) view.findViewById(R.id.horBarChart);
        Resources res = getResources();

        horBarChart.setDescription(null);
        horBarChart.setDrawValueAboveBar(false);
        horBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // vertical axis labels on left instead of right
        horBarChart.getAxisLeft().setValueFormatter(new IntegerFormatter());
        horBarChart.getAxisRight().setValueFormatter(new IntegerFormatter());
        horBarChart.getLegend().setEnabled(false);

        String[] companyLabels = res.getStringArray(R.array.companyLabels);

        ArrayList<BarEntry> yVals = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();

        for (int i = (companyLabels.length - 1); i >= 0; i--) {
            // process array in reverse order so values are from top-to-bottom instead of bottom-up
            xVals.add(companyLabels[i]);
            yVals.add(new BarEntry(this.getCompanyCount(companyLabels[i]), i));
        }

        BarDataSet dataSet = new BarDataSet(yVals, null);
        dataSet.setColors(Utils.getDefaultColorTemplate());
        dataSet.setValueTextColor(res.getColor(R.color.text_icons));
        dataSet.setValueTextSize(Utils.getValueTextSize());
        dataSet.setValueFormatter(new IntegerFormatter());

        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        BarData data = new BarData(xVals, dataSets);

        horBarChart.setData(data);
        horBarChart.invalidate();
    }

    private float getCompanyCount(String companyLabel) {
        Random rand = new Random();
        int max = 20;
        int min = 0;
        return rand.nextInt((max - min) + 1) + min;
    }
}
