package informatics.uk.ac.ed.track.feedback;

import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;

public class IntegerFormatter implements ValueFormatter {

    private DecimalFormat mFormat;

    public IntegerFormatter() {
        mFormat = new DecimalFormat("0"); // Integer, no decimal places
    }

    @Override
    public String getFormattedValue(float value) {
        if (value == 0) {
            return "";
        } else {
            return mFormat.format(value);
        }
    }
}
