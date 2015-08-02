package informatics.uk.ac.ed.track.feedback.fragments;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
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
import java.util.HashMap;
import java.util.Random;

import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.Constants;
import informatics.uk.ac.ed.track.esm.DatabaseHelper;
import informatics.uk.ac.ed.track.feedback.IntegerFormatter;
import informatics.uk.ac.ed.track.feedback.FeedbackUtils;

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
        horBarChart.getAxisLeft().setEnabled(false); // remove top axis labels
        horBarChart.getAxisRight().setValueFormatter(new IntegerFormatter());
        horBarChart.getLegend().setEnabled(false);

        String[] companyLabels = res.getStringArray(R.array.companyLabels);
        int companyLabelAloneIndex = res.getInteger(R.integer.companyLabelAloneIndex);
        int companyLabelOtherIndex = res.getInteger(R.integer.companyLabelOtherIndex);
        String[] companyColumnValues = res.getStringArray(R.array.companyColumnValues);

        ArrayList<BarEntry> yVals = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();

        HashMap<String, Float> companyCounts = new HashMap<>();
        String aloneKey = companyLabels[companyLabelAloneIndex];
        String otherKey = companyLabels[companyLabelOtherIndex];

        for (int i = (companyLabels.length - 1); i >= 0; i--) {
            // process array in reverse order so values are from top-to-bottom instead of bottom-up
            xVals.add(companyLabels[i]);
            if (i == companyLabelAloneIndex) {
                companyCounts.put(aloneKey, this.getAloneCount());
            } else if (i == companyLabelOtherIndex) {
                companyCounts.put(otherKey, 0f);
            } else {
                companyCounts.put(companyColumnValues[i], 0f);
            }
        }

        this.getCompanyCounts(companyCounts, otherKey);

        for (int i = (companyLabels.length - 1); i >= 0; i--) {
            if (i == companyLabelAloneIndex) {
                yVals.add(new BarEntry(companyCounts.get(aloneKey), yVals.size()));
            } else if (i == companyLabelOtherIndex) {
                yVals.add(new BarEntry(companyCounts.get(otherKey), yVals.size()));
            } else {
                yVals.add(new BarEntry(companyCounts.get(companyColumnValues[i]), yVals.size()));
            }
        }

        BarDataSet dataSet = new BarDataSet(yVals, null);
        dataSet.setColors(FeedbackUtils.getDefaultColorTemplate());
        dataSet.setValueTextColor(res.getColor(R.color.text_icons));
        dataSet.setValueTextSize(FeedbackUtils.getValueTextSize());
        dataSet.setValueFormatter(new IntegerFormatter());

        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        BarData data = new BarData(xVals, dataSets);

        horBarChart.setData(data);
        horBarChart.invalidate();
    }

    private float getAloneCount() {
        Resources res = getResources();

        String[] socialColumnValues = res.getStringArray(R.array.socialColumnValues);
        int socialColumnValueAloneIndex = res.getInteger(R.integer.socialColumnValueAloneIndex);
        String socialColumnValueAlone = socialColumnValues[socialColumnValueAloneIndex];

        SQLiteDatabase db =
                new DatabaseHelper(getActivity().getApplicationContext()).getReadableDatabase();

        String sql = "SELECT COUNT(*) FROM `" + DatabaseHelper.TABLE_NAME_SURVEY_RESPONSES + "` " +
                "WHERE `" + res.getString(R.string.socialColumn) + "` = " +
                "'" + socialColumnValueAlone + "'";

        SQLiteStatement statement = db.compileStatement(sql);
        float count = statement.simpleQueryForLong();
        db.close();

        return count;
    }

    /**
     * Returns company counts in HashMap where values are indexed by
     * actual String value in database.
     * @param companyCounts HashMap with company values as keys and counts initialised to zero.
     * @param otherKey The key used for the 'Other' option in the companyCounts HashMap.
     */
    private void getCompanyCounts(HashMap<String, Float> companyCounts, String otherKey) {
        Resources res = getResources();

        String[] socialColumnValues = res.getStringArray(R.array.socialColumnValues);
        int socialColumnValueWithOthersIndex = res.getInteger(R.integer.socialColumnValueWithOthersIndex);
        String socialColumnValueWithOthers = socialColumnValues[socialColumnValueWithOthersIndex];
        String companyColumn = res.getString(R.string.companyColumnn);
        String socialColumn = res.getString(R.string.socialColumn);

        SQLiteDatabase db =
                new DatabaseHelper(getActivity().getApplicationContext()).getReadableDatabase();

        String table = "`" + DatabaseHelper.TABLE_NAME_SURVEY_RESPONSES +  "`";
        String[] columnsToReturn = { "`" + companyColumn + "`"};
        String selection = "`" + socialColumn + "` = ?";
        String[] selectionArgs = { socialColumnValueWithOthers }; // matched to "?" in selection

        Cursor cursor = db.query(table, columnsToReturn, selection, selectionArgs, null, null, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String columnValue = cursor.getString(0);
                String[] companyStrings = columnValue.split(Constants.SURVEY_RESPONSES_MULTI_CHOICE_MULTI_ANSWER_DELIMITER);
                for (String company : companyStrings) {
                    if (companyCounts.containsKey(company)) {
                        companyCounts.put(company, companyCounts.get(company) + 1);
                    } else {
                        // will not contain key if 'Other'
                        companyCounts.put(otherKey, companyCounts.get(otherKey) + 1);
                    }
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }
}
