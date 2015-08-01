package informatics.uk.ac.ed.track.feedback;

import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class FeedbackUtils {

    public static int[] getDefaultColorTemplate() {
        return ColorTemplate.COLORFUL_COLORS;
    }

    public static ArrayList<Integer> getExtendedColorTemplate() {
        ArrayList<Integer> colors = new ArrayList<>();

        for (int color : ColorTemplate.COLORFUL_COLORS) {
            colors.add(color);
        }

        for (int color : ColorTemplate.JOYFUL_COLORS) {
            colors.add(color);
        }

        return colors;
    }

    public static float getValueTextSize(){
        return 12f;
    }

}
