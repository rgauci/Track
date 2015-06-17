package informatics.uk.ac.ed.esm;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class StudyConfiguration extends FragmentActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerFragment.TimePickerDialogListener {

    private final static int START_TIMEPICKER_ID = 0;
    private final static int END_TIMEPICKER_ID = 1;

    private TextView txtVwStartDate;
    private TextView txtVwStartTime;
    private TextView txtVwEndTime;

    private Calendar startDate;
    private int startTime_hour;
    private int startTime_minute;
    private int endTime_hour;
    private int endTime_minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_configuration);

        /* initalise UI controls */
        txtVwStartDate = (TextView) findViewById(R.id.txtStartDate);
        txtVwStartTime = (TextView) findViewById(R.id.txtStartTime);
        txtVwEndTime = (TextView) findViewById(R.id.txtEndTime);

        /* set start date to the following day */
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        this.setStartDate(calendar);

        /* set start & end times using default values */
        Resources res = getResources();
        setStartTime(res.getInteger(R.integer.defaultStartTime_Hour),
                res.getInteger(R.integer.defaultStartTime_Minute));
        setEndTime(res.getInteger(R.integer.defaultEndTime_Hour),
                res.getInteger(R.integer.defaultEndTime_Minute));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_study_configuration, menu);
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

    private void setStartDate(Calendar date) {
        this.startDate = date;
        SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.defaultDateFormat));
        txtVwStartDate.setText(sdf.format(this.startDate.getTime()));
    }

    private void setStartDate(int year, int month, int date) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(year, month, date);
        this.setStartDate(calendar);
    }

    private void setStartTime(int hour, int minute) {
        this.startTime_hour = hour;
        this.startTime_minute = minute;
        this.setTimeTextViewText(this.txtVwStartTime, this.startTime_hour, this.startTime_minute);
    }

    private void setEndTime(int hour, int minute) {
        this.endTime_hour = hour;
        this.endTime_minute = minute;
        this.setTimeTextViewText(this.txtVwEndTime, this.endTime_hour, this.endTime_minute);
    }

    private void setTimeTextViewText(TextView txtView, int hour, int minute) {
        txtView.setText(String.format("%02d", hour) + ":" + String.format("%02d", minute));
    }

    public void btnNext_onClick(View view) {
        Intent intent = new Intent(this, UserAccountSetup.class);
        startActivity(intent);
    }

    public void lytStartDate_onClick(View view) {
        this.showDatePicker(this.startDate);
    }

    public void lytStartTime_OnClick(View view) {
        this.showTimePicker(this.startTime_hour, this.startTime_minute, START_TIMEPICKER_ID);
    }

    public void lytEndTime_OnClick(View view) {
        this.showTimePicker(this.endTime_hour, this.endTime_minute, END_TIMEPICKER_ID);
    }

    public void showDatePicker(Calendar dateCalendar) {
        Bundle args = new Bundle();
        args.putInt(DatePickerFragment.ARG_YEAR, dateCalendar.get(Calendar.YEAR));
        args.putInt(DatePickerFragment.ARG_MONTH, dateCalendar.get(Calendar.MONTH));
        args.putInt(DatePickerFragment.ARG_DAY, dateCalendar.get(Calendar.DAY_OF_MONTH));

        DialogFragment datePicker = new DatePickerFragment();
        datePicker.setArguments(args);
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePicker(int hour, int minute, int pickerId) {
        Bundle args = new Bundle();
        args.putInt(TimePickerFragment.ARG_HOUR, hour);
        args.putInt(TimePickerFragment.ARG_MINUTE, minute);
        args.putInt(TimePickerFragment.ARG_PICKER_ID, pickerId);

        DialogFragment timePicker = new TimePickerFragment();
        timePicker.setArguments(args);
        timePicker.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.setStartDate(year, monthOfYear, dayOfMonth);
    }

    @Override
    public void onTimeSet(int pickerId, TimePicker view, int hourOfDay, int minute) {
        TextView timeTxtView;

        switch (pickerId) {
            case START_TIMEPICKER_ID:
                this.setStartTime(hourOfDay, minute);
                break;
            case END_TIMEPICKER_ID:
                this.setEndTime(hourOfDay, minute);
                break;
        }
    }
}
