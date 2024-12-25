package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The Terrain class is responsible for generating the terrain of the game.
 * The terrain is generated in blocks of a fixed size.
 *
 * @see Block
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class Terrain {

    /**
     * A constant string that represents the tag of a terrain block.
     * Used to identify all ground-related elements within the game world.
     */
    public static final String BLOCK_TAG = "ground";

    private static final Color BASE_BACKGROUND_COLOR = new Color(212, 123, 74); // block color
    private static final float TWO_THIRDS_FACTOR = 2 / 3f;
    private final float groundHeightAtX0; // TODO: Is it supposed to be final?
    private static final int TERRAIN_DEPTH = 20;

    /**
     * Constructor for the Terrain class.
     * @param windowDimensions The dimensions of the window.
     * @param seed The seed for the random number generator.
     */
    public Terrain (Vector2 windowDimensions, int seed) {
        groundHeightAtX0 = windowDimensions.y() * TWO_THIRDS_FACTOR;
        // TODO: Use seed when getting to infinite world part
    }

    /**
     * Returns the height of the ground at the given x position.
     * @param x The x position.
     * @return The height of the ground at the given x position.
     */
    public float groundHeightAtX0(float x) {
        return groundHeightAtX0;
        // TODO: Think of something more complicated later on
    }

    /**
     * Creates a list of blocks in the given range.
     * @param minX The minimum x position.
     * @param maxX The maximum x position.
     * @return A list of blocks in the given range.
     */
    public List<Block> createInRange(int minX, int maxX) {
        List<Block> blockList = new ArrayList<>();

        // Add blocks at increasing X positions to the list
        for (int x = getClosestMultToBlockSize(minX); x < getClosestMultToBlockSize(maxX); x += Block.SIZE) {

            float y = (float) Math.floor(groundHeightAtX0(x) / Block.SIZE) * Block.SIZE;

            for (int i = 0; i < TERRAIN_DEPTH; i++) {

                Renderable rectangleBlock = new RectangleRenderable(
                        ColorSupplier.approximateColor(BASE_BACKGROUND_COLOR)
                ); // Create a rectangle with approximate color

                Block block = new Block(Vector2.of(x, y + i * Block.SIZE), rectangleBlock);
                block.setTag(BLOCK_TAG); // set block tag to "ground"
                blockList.add(block); // add to blockList
            }
        }
        return blockList;
    }

    /**
     * Returns the closest (smaller) multiple of the block size to the given number.
     * @param num The number.
     * @return The closest (smaller) multiple of the block size to the given number.
     *
     * @see Block#SIZE
     * @see Terrain#createInRange(int, int)
     *
     * @implNote This method is used to ensure that the blocks are created in multiples of the block size.
     *           This is done to ensure that the blocks are aligned properly.
     */
    private static int getClosestMultToBlockSize(int num) {
        return (int) Math.floor((double) num / Block.SIZE) * Block.SIZE;
    }
}
