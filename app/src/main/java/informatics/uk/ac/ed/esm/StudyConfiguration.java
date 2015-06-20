package informatics.uk.ac.ed.esm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.res.Resources;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class StudyConfiguration extends AppCompatActivity
        implements DatePickerFragment.DatePickerDialogListener,
            TimePickerFragment.TimePickerDialogListener {

    private final static int START_DATEPICKER_ID = 0;
    private final static int START_TIMEPICKER_ID = 1;
    private final static int END_TIMEPICKER_ID = 2;

    private Calendar minimumStartDate; // will be set to the following day

    private Calendar startDate;
    private int startTime_hour;
    private int startTime_minute;
    private int endTime_hour;
    private int endTime_minute;

    private EditText txtDuration, txtSamplesPerDay, txtNotificationWindow;
    private TextView txtVwStartDate, txtVwStartTime, txtVwEndTime;
    private TextView txtStartDate_errorMsg, txtDuration_errorMsg,
            txtSamplesPerDay_errorMsg, txtNotificationWindow_errorMsg,
            txtStarTime_errorMsg, txtEndTime_errorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_configuration);

        /* initialise UI controls */
        txtDuration = (EditText) findViewById(R.id.txtDuration);
        txtSamplesPerDay = (EditText) findViewById(R.id.txtSamplesPerDay);
        txtNotificationWindow = (EditText) findViewById(R.id.txtNotificationWindow);

        txtVwStartDate = (TextView) findViewById(R.id.txtStartDate);
        txtVwStartTime = (TextView) findViewById(R.id.txtStartTime);
        txtVwEndTime = (TextView) findViewById(R.id.txtEndTime);

        txtStartDate_errorMsg = (TextView) findViewById(R.id.txtStartDate_errorMsg);
        txtDuration_errorMsg = (TextView) findViewById(R.id.txtDuration_errorMsg);
        txtSamplesPerDay_errorMsg = (TextView) findViewById(R.id.txtSamplesPerDay_errorMsg);
        txtNotificationWindow_errorMsg = (TextView) findViewById(R.id.txtNotificationWindow_errorMsg);
        txtStarTime_errorMsg = (TextView) findViewById(R.id.txtStartTime_errorMsg);
        txtEndTime_errorMsg = (TextView) findViewById(R.id.txtEndTime_errorMsg);

        /* set start date & minimum start date to the following day */
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        this.minimumStartDate = calendar;
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

    public void btnNext_onClick(View view) {
        boolean hasErrors = false;

        // validate start date
        if (this.startDate.before(this.minimumStartDate)) {
            this.showError(this.txtStartDate_errorMsg, getString(R.string.error_startDate));
            hasErrors = true;
        } else {
            this.hideError(this.txtStartDate_errorMsg);
        }

        // validate duration
        if (!validateNumber(this.txtDuration, this.txtDuration_errorMsg,
                getString(R.string.error_missingDuration))) {
            hasErrors = true;
        }

        // validate #samples/day
        if (!validateNumber(this.txtSamplesPerDay, this.txtSamplesPerDay_errorMsg,
                getString(R.string.error_missingSamplesPerDay))) {
            hasErrors = true;
        }

        // validate notification window
        if (!validateNumber(this.txtNotificationWindow, this.txtNotificationWindow_errorMsg,
                getString(R.string.error_missingNotificationWindow))) {
            hasErrors = true;
        }

        // validate end time (must be after start time)
        if (endTime_hour < startTime_hour) {
            showError(this.txtEndTime_errorMsg, getString(R.string.error_dailyEndTime));
            hasErrors = true;
        } else {
            this.hideError(this.txtEndTime_errorMsg);
        }

        if (!hasErrors) {
            setupNotifications();
            Intent intent = new Intent(this, UserAccountSetup.class);
            startActivity(intent);
        }
    }

    private void setupNotifications() {
        int requestCode = 1;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.startDate.getTimeInMillis());
        calendar.set(Calendar.HOUR_OF_DAY, this.startTime_hour);
        calendar.set(Calendar.MINUTE, this.startTime_minute);

        Long alertTime = new GregorianCalendar().getTimeInMillis() + 5 * 1000;

        // A pending intent that fires when the alarm is triggered.
        // When you set a second alarm that uses the same pending intent, it replaces the original alarm.
        Intent alarmReceiverIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        // RTC_WAKEUP — Wakes up the device to fire the pending intent at the specified time.
        // setInexactRepeating() - Android synchronizes repeating alarms from multiple apps
        // and fires them at the same time. This reduces the total number of times
        // the system must wake the device, thus reducing drain on the battery.
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alertTime, AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);

        requestCode++;
    }

    private boolean validateNumber(EditText txtView, TextView txtVwError, String emptyErrorMsg) {
        String numberString = Utils.getTrimmedText(txtView);
        boolean valid = true;

        if (numberString.isEmpty()) {
            this.showError(txtVwError, emptyErrorMsg);
            valid = false;
        } else if (!TextUtils.isDigitsOnly(numberString)) {
            this.showError(txtVwError, getString(R.string.error_enterValidNumber));
            valid = false;
        } else {
            this.hideError(txtVwError);
        }

        return valid;
    }

    private void showError(TextView txtVwError, String msg){
        txtVwError.setText(msg);
        txtVwError.setVisibility(View.VISIBLE);
    }

    private void hideError(TextView txtVwError) {
        txtVwError.setVisibility(View.GONE);
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

    public void lytStartDate_onClick(View view) {
        this.showDatePicker(this.startDate, START_DATEPICKER_ID);
    }

    public void lytStartTime_OnClick(View view) {
        this.showTimePicker(this.startTime_hour, this.startTime_minute, START_TIMEPICKER_ID);
    }

    public void lytEndTime_OnClick(View view) {
        this.showTimePicker(this.endTime_hour, this.endTime_minute, END_TIMEPICKER_ID);
    }

    public void showDatePicker(Calendar dateCalendar, int pickerId) {
        Bundle args = new Bundle();
        args.putInt(DatePickerFragment.ARG_PICKER_ID, pickerId);
        args.putInt(DatePickerFragment.ARG_YEAR, dateCalendar.get(Calendar.YEAR));
        args.putInt(DatePickerFragment.ARG_MONTH, dateCalendar.get(Calendar.MONTH));
        args.putInt(DatePickerFragment.ARG_DAY, dateCalendar.get(Calendar.DAY_OF_MONTH));
        args.putLong(DatePickerFragment.ARG_MIN_DATE, this.minimumStartDate.getTimeInMillis());

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
    public void OnDateSet(int pickerId, DatePicker view, int year, int monthOfYear, int dayOfMonth) {
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
