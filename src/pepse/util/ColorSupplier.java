package pepse.util;

import java.awt.*;
import java.util.Random;

/**
 * Provides procedurally generated colors around a pivot.
 * @author Dan Nirel, Modifications by Noam Kimhi and Or Forshmit
 */
public final class ColorSupplier {

    // Private constants
    /* The default color delta for the approximateColor methods. */
    private static final int DEFAULT_COLOR_DELTA = 10;
    private final static Random random = new Random(); /* A random number generator. */

    /**
     * Constructs a new ColorSupplier instance.
     * This constructor initializes the class, allowing access to methods
     * for generating procedurally adjusted colors.
     */
    public ColorSupplier() {}

    /**
     * Returns a color similar to baseColor, with a default delta.
     *
     * @param baseColor A color that we wish to approximate.
     * @return A color similar to baseColor.
     */
    public static Color approximateColor(Color baseColor) {
        return approximateColor(baseColor, DEFAULT_COLOR_DELTA);
    }


    /**
     * Returns a color similar to baseColor, with a difference of at most colorDelta.
     * Where the difference is equal along all channels
     *
     * @param baseColor A color that we wish to approximate.
     * @param colorDelta The maximal difference (per channel) between the sampled color and the base color.
     * @return A color similar to baseColor.
     */
    public static Color approximateMonoColor(Color baseColor, int colorDelta){
        int channel = randomChannelInRange(baseColor.getRed()-colorDelta, baseColor.getRed()+colorDelta);
        return new Color(channel, channel, channel);
    }



    /**
     * Returns a color similar to baseColor, with a default delta.
     * Where the difference is equal along all channels
     *
     * @param baseColor A color that we wish to approximate.
     * @return A color similar to baseColor.
     */
    public static Color approximateMonoColor(Color baseColor) {
        return approximateMonoColor(baseColor, DEFAULT_COLOR_DELTA);
    }


    /**
     * Returns a color similar to baseColor, with a difference of at most colorDelta.
     *
     * @param baseColor A color that we wish to approximate.
     * @param colorDelta The maximal difference (per channel) between the sampled color and the base color.
     * @return A color similar to baseColor.
     */
    public static Color approximateColor(Color baseColor, int colorDelta) {
        int red = baseColor.getRed();
        int green = baseColor.getGreen();
        int blue = baseColor.getBlue();

        return new Color(
                randomChannelInRange(red - colorDelta, red + colorDelta),
                randomChannelInRange(green - colorDelta, green + colorDelta),
                randomChannelInRange(blue - colorDelta, blue + colorDelta)
        );
    }

    /**
     * This method generates a random value for a color channel within the given range [min, max].
     *
     * @param min The lower bound of the given range.
     * @param max The upper bound of the given range.
     * @return A random number in the range [min, max], clipped to [0,255].
     */
    private static int randomChannelInRange(int min, int max) {
        int channel = random.nextInt(max - min + 1) + min;
        return Math.min(255, Math.max(channel, 0));
    }
}
