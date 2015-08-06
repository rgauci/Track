package informatics.uk.ac.ed.track.feedback.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.Constants;
import informatics.uk.ac.ed.track.esm.DatabaseHelper;
import informatics.uk.ac.ed.track.feedback.FeedbackUtils;
import informatics.uk.ac.ed.track.feedback.IntegerFormatter;

public class RegulationHorizontalBarChart extends Fragment {

    public static android.support.v4.app.Fragment newInstance() {
        return new RegulationHorizontalBarChart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_regulation_horizontal_bar_chart, container, false);
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

        String[] regulationLabels = res.getStringArray(R.array.regulationLabels);
        String[][] regulationColumns = {
                res.getStringArray(R.array.reappraisingColumns),
                res.getStringArray(R.array.calmingColumns),
                res.getStringArray(R.array.sharingColumns),
                res.getStringArray(R.array.suppressingColumns),
                res.getStringArray(R.array.distractingColumns),
                res.getStringArray(R.array.ruminatingColumns)
        };

        ArrayList<String> xVals = new ArrayList<>();

        for (int i = (regulationLabels.length - 1); i >= 0; i--) {
            // process array in reverse order so values are from top-to-bottom instead of bottom-up
            xVals.add(regulationLabels[i]);
        }

        ArrayList<BarEntry> yVals = this.getRegulationStrategyCounts(regulationColumns, res);

        BarDataSet dataSet = new BarDataSet(yVals, null);
        dataSet.setColors(FeedbackUtils.getExtendedColorTemplate());
        dataSet.setValueTextColor(res.getColor(R.color.text_icons));
        dataSet.setValueTextSize(FeedbackUtils.getValueTextSize());
        dataSet.setValueFormatter(new IntegerFormatter());

        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        BarData data = new BarData(xVals, dataSets);

        horBarChart.setData(data);
        horBarChart.invalidate();
    }

    private ArrayList<BarEntry> getRegulationStrategyCounts(String[][] regulationColumns, Resources res) {
        ArrayList<BarEntry> yVals = new ArrayList<>();

        int likertThreshold = res.getInteger(R.integer.likertThreshold);

        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());

        long passwordResetTime = settings.getLong(
                Constants.PARTICIPANT_PASSWORD_RESET_TIME_MILLIS,
                Constants.DEF_VALUE_LNG);

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity().getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String passwordResetTime_IsoFormat = dbHelper.getDateInIsoFormat(passwordResetTime);

        for (int i = (regulationColumns.length - 1); i >= 0; i--) {
            yVals.add(new BarEntry(
                    getRegulationStrategyCount(db, regulationColumns[i], likertThreshold,
                            passwordResetTime_IsoFormat),
                    yVals.size()));
        }

        db.close();

        return yVals;
    }

    private float getRegulationStrategyCount(SQLiteDatabase db,
                                             String[] regulationColumns, int likertThreshold,
                                             String passwordResetTime_IsoFormat) {
        float count = 0;

        String[] columnsToReturn = new String[regulationColumns.length];
        for (int i = 0; i < regulationColumns.length; i++) {
            columnsToReturn[i] = "`" + regulationColumns[i] + "`";
        }

        String table = "`" + DatabaseHelper.TABLE_NAME_SURVEY_RESPONSES +  "`";
        String selection = "`" + DatabaseHelper.COLUMN_NAME_SURVEY_COMPLETED_TIME + "` > DATETIME(?)";
        String[] selectionArgs = { passwordResetTime_IsoFormat }; // matched to "?" in selection

        Cursor cursor = db.query(table, columnsToReturn, selection, selectionArgs, null, null, null);

        // loop through rows
        if (cursor.moveToFirst()) {
            do {
                // loop through columns
                for (int i = 0; i < columnsToReturn.length; i++) {
                    if (!cursor.isNull(i)) {
                        // if not null
                        // and likert value exceeds (or is equal) to threshold
                        // increment count by 1
                        int likertValue = cursor.getInt(i);
                        if (likertValue >= likertThreshold) {
                            count++;
                        }
                    }
                }
            } while (cursor.moveToNext());
        }

        cursor.close();

        return count;
    }

}
