package informatics.uk.ac.ed.track;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment
    implements TimePickerDialog.OnTimeSetListener {

    public static final String ARG_PICKER_ID = "pickerId";
    public static final String ARG_HOUR = "hour";
    public static final String ARG_MINUTE = "minute";

    private int pickerId;
    private TimePickerDialogListener callbackListener;

    public interface TimePickerDialogListener {
        void onTimeSet(int pickerId, TimePicker view, int hourOfDay, int minute);
    }

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Bundle args = getArguments();

        int hour, minute;
        hour = args.getInt(ARG_HOUR);
        minute = args.getInt(ARG_MINUTE);

        pickerId = args.getInt(ARG_PICKER_ID);

        // Create a new instance of DatePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callbackListener = (TimePickerDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement TimePickerDialogListener");
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        callbackListener.onTimeSet(pickerId, view, hourOfDay, minute);
    }
}