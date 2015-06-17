package informatics.uk.ac.ed.esm;

import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class StudyConfiguration extends FragmentActivity {

    private TextView txtStartDate;
    private Calendar startDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_configuration);

        /* set start date to the following day */
        txtStartDate = (TextView) findViewById(R.id.txtStartDate);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        this.setStartDate(calendar);
    }

    private void setStartDate(Calendar date) {
        /* set startDate variable */
        startDate = date;

        /* update UI */
        SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.defaultDateFormat));
        txtStartDate.setText(sdf.format(this.startDate.getTime()));
    }

    private Calendar getStartDate() {
        return this.startDate;
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
        Intent intent = new Intent(this, UserAccountSetup.class);
        startActivity(intent);
    }

    public void lytStartDate_onClick(View view) {
        Calendar date = this.getStartDate();
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DAY_OF_MONTH);

        Bundle args = new Bundle();
        args.putInt(DatePickerFragment.ARG_YEAR, year);
        args.putInt(DatePickerFragment.ARG_MONTH, month);
        args.putInt(DatePickerFragment.ARG_DAY, day);

        this.showDatePicker(args);
    }

    public void showDatePicker(Bundle dateArgs) {
        DialogFragment dateDatePicker = new DatePickerFragment();
        dateDatePicker.setArguments(dateArgs);
        dateDatePicker.show(getSupportFragmentManager(), "datePicker");
    }
}
