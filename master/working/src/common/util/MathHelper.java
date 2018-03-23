package common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * A utility class for {@link Float} objects.
 */
public final class MathHelper {

    /**
     * Rounds a floating point number, to the specified decimal places.
     * Source: https://stackoverflow.com/questions/8911356/whats-the-best-practice-to-round-a-float-to-2-decimals/8911683#8911683
     */
    public static float round(float d, int decimalPlaces) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    /**
     * Rounds a double number, to the 2 decimal places.
     */
    public static double round(double d) {
        return round(d, 2);
    }

    /**
     * Rounds a double number, to the specified decimal places.
     * Source: https://www.quora.com/How-can-I-round-a-double-number-to-4-decimal-digits-in-Java
     * Source: https://docs.oracle.com/javase/7/docs/api/java/text/DecimalFormatSymbols.html#DecimalFormatSymbols(java.util.Locale)
     * Source: https://stackoverflow.com/questions/5054132/how-to-change-the-decimal-separator-of-decimalformat-from-comma-to-dot-point/5054217#5054217
     */
    public static double round(double d, int decimalPlaces) {
        // Construct pattern
        String s = "#.";

        for (int i = 0; i < decimalPlaces; i++)
            s += "#";

        // Perform conversion
        DecimalFormat df = new DecimalFormat(s);
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.UK));
        return Double.parseDouble(df.format(d));
    }
}
