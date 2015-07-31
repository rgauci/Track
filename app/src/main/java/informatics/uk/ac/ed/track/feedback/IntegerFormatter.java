package informatics.uk.ac.ed.track.feedback;

import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;

public class IntegerFormatter implements ValueFormatter {

    private DecimalFormat mFormat;

    public IntegerFormatter() {
        mFormat = new DecimalFormat("0"); // use one decimal
    }

    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value); // append a dollar-sign
    }
}
