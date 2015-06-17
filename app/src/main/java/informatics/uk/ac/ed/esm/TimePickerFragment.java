package informatics.uk.ac.ed.esm;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
    implements TimePickerDialog.OnTimeSetListener {

    public static final String ARG_PICKER_ID = "pickerId";
    public static final String ARG_HOUR = "hour";
    public static final String ARG_MINUTE = "minute";

    private int pickerId;
    private TimePickerDialogListener listener;

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Bundle args = getArguments();

        int hour, minute;
        hour = args.getInt(ARG_HOUR);
        minute = args.getInt(ARG_MINUTE);

        pickerId = args.getInt(ARG_PICKER_ID);
        listener =
                getActivity() instanceof  TimePickerDialogListener ?
                        (TimePickerDialogListener) getActivity()
                        : null;

        // Create a new instance of DatePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (listener != null) {
            listener.onTimeSet(pickerId, view, hourOfDay, minute);
        }
    }

    public interface TimePickerDialogListener {
        void onTimeSet(int pickerId, TimePicker view, int hourOfDay, int minute);
    }
}