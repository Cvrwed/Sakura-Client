package cc.unknown.util.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtil {

    public double getRandom(double min, double max) {
        if (min == max) {
            return min;
        } else if (min > max) {
            final double d = min;
            min = max;
            max = d;
        }
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public double round(final double value, final int places) {
        try {
            final BigDecimal bigDecimal = BigDecimal.valueOf(value);

            return bigDecimal.setScale(places, RoundingMode.HALF_UP).doubleValue();
        } catch (Exception exception) {
            return 0;
        }
    }
    
    public static int randomizeInt(double min, double max) {
        return (int) randomizeDouble(min, max);
    }

    public static double randomizeDouble(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    public double roundWithSteps(final double value, final double steps) {
        double a = ((Math.round(value / steps)) * steps);
        a *= 1000;
        a = (int) a;
        a /= 1000;
        return a;
    }

    public double lerp(final double a, final double b, final double c) {
        return a + c * (b - a);
    }

    public float lerp(final float a, final float b, final float c) {
        return a + c * (b - a);
    }

    public double clamp(double min, double max, double n) {
        return Math.max(min, Math.min(max, n));
    }

    public double wrappedDifference(double number1, double number2) {
        return Math.min(Math.abs(number1 - number2), Math.min(Math.abs(number1 - 360) - Math.abs(number2 - 0), Math.abs(number2 - 360) - Math.abs(number1 - 0)));
    }
}