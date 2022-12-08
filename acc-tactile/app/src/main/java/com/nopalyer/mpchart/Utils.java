package com.nopalyer.mpchart;

public class Utils {

    /**
     * Feedback profiles for different patterns of vibrations
     *
     * @param value yvalue or some other value based on which vibration pattern will be decided
     * @return pattern array
     */
    static long[] getFeedbackProfile(final float value) {
        long[] pattern;
        if (value <= 1) {
            pattern = new long[]{0, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500};
        } else if (value > 2 && value <= 4) {
            pattern = new long[]{0, 1000, 1000, 1000};
        } else {
            pattern = new long[]{0, 1000, 1000, 1000, 1000, 1000, 500, 500};
        }
        return pattern;
    }
}
