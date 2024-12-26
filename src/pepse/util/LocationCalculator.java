package pepse.util;

import pepse.world.Block;

/**
 * The LocationCalculator class provides utility methods for calculating location-based values
 * in alignment with predefined block sizes.
 */
public final class LocationCalculator {

    /**
     * Private constructor for LocationCalculator.
     * This constructor is defined to prevent instantiation of the class, ensuring
     * that it can only be used as a utility class with static methods.
     */
    private LocationCalculator() {}

    /**
     * Returns the closest (smaller) multiple of the block size to the given number.
     * <p>
     * This method is used to ensure that the blocks and trunks are created in
     * multiples of the block size.
     * This is done to ensure that the blocks and trunks are aligned properly.
     * </p>
     *
     * @param num The number.
     * @return The closest (smaller) multiple of the block size to the given number.
     *
     * @see Block#SIZE
     */
    public static int getClosestMultToBlockSize(int num) {
        return (int) Math.floor((double) num / Block.SIZE) * Block.SIZE;
    }

}
