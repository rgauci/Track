package informatics.uk.ac.ed.track.feedback.fragments;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;

import informatics.uk.ac.ed.track.R;
import informatics.uk.ac.ed.track.esm.Constants;
import informatics.uk.ac.ed.track.esm.DatabaseHelper;
import informatics.uk.ac.ed.track.feedback.FeedbackUtils;
import informatics.uk.ac.ed.track.feedback.IntegerFormatter;

public class DailyEmotionsPieChart extends Fragment {

    private final static String DAY_NUMBER = "dayNumber";

    public static android.support.v4.app.Fragment newInstance(int dayNumber) {
        DailyEmotionsPieChart f = new DailyEmotionsPieChart();
        Bundle args = new Bundle();
        args.putInt(DAY_NUMBER, dayNumber);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_emotions_pie_chart, container, false);
        this.initialiseChart(view);
        return view;
    }

    private void initialiseChart(View view) {
        int dayNum = getArguments().getInt(DAY_NUMBER);

        PieChart pieChart = (PieChart) view.findViewById(R.id.pieChart);
        Resources res = getResources();

        pieChart.setDescription(null);
        pieChart.setCenterText(
                res.getString(R.string.emotionsPieChartCenterText) + "\nDay " + dayNum);
        pieChart.setCenterTextSize(16f);
        pieChart.setCenterTextColor(res.getColor(R.color.primary_text));
        pieChart.getLegend().setEnabled(false); // won't fit on small screens (overlaps on chart)// Get Frequency Count for Each Emotion
        String[] emotionColumns = res.getStringArray(R.array.emotionColumns);
        String[] emotionLabels = res.getStringArray(R.array.emotionLabels);

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();

        for (int i = 0; i < emotionColumns.length; i++) {
            float count = this.getEmotionCount(emotionColumns[i], dayNum);
            if (count > 0) {
                entries.add(new Entry(count, entries.size()));
                xVals.add(emotionLabels[i]);
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, null);
        dataSet.setColors(FeedbackUtils.getExtendedColorTemplate());
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextColor(res.getColor(R.color.text_icons));
        dataSet.setValueTextSize(FeedbackUtils.getValueTextSize());
        dataSet.setValueFormatter(new IntegerFormatter());

        PieData data = new PieData(xVals, dataSet);

        pieChart.setData(data);
        pieChart.invalidate();
    }

    private float getEmotionCount(String emotionCol, int dayNum) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());

        long passwordResetTime = settings.getLong(
                Constants.PARTICIPANT_PASSWORD_RESET_TIME_MILLIS,
                Constants.DEF_VALUE_LNG);

        Resources res = getResources();

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity().getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT COUNT(*) FROM `" + DatabaseHelper.TABLE_NAME_SURVEY_RESPONSES + "` " +
                "WHERE `" + emotionCol + "` = " +
                "'" + res.getString(R.string.emotionColumnYesValue) + "' " +
                "AND `" + DatabaseHelper.COLUMN_NAME_DAY_NUMBER + "` " +
                "= " + dayNum + " " +
                "AND `" + DatabaseHelper.COLUMN_NAME_SURVEY_COMPLETED_TIME + "` " +
                "> DATETIME('" + dbHelper.getDateInIsoFormat(passwordResetTime) + "')";

        SQLiteStatement statement = db.compileStatement(sql);
        float count = statement.simpleQueryForLong();
        db.close();

        return count;
    }
}
