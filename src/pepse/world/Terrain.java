package pepse.world;

import danogl.GameObject;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.LocationCalculator;
import pepse.util.NoiseGenerator;

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

    // Terrain constants
    private static final float TWO_THIRDS_FACTOR = 2 / 3f;
    private final float groundHeightAtX0; // TODO: Is it supposed to be final?
    private static final int OFFSET = 100;
    private static final int TERRAIN_DEPTH = 20;
    private static final double NOISE_GENERATION_FACTOR = 1;
    private static final Color BASE_BACKGROUND_COLOR = new Color(212, 123, 74); // block color

    // Private fields
    private final NoiseGenerator perlinNoiseGenerator;

    /**
     * Constructor for the Terrain class.
     * @param windowDimensions The dimensions of the window.
     * @param seed The seed for the random number generator.
     */
    public Terrain (Vector2 windowDimensions, int seed) {
        groundHeightAtX0 = windowDimensions.y() * TWO_THIRDS_FACTOR;
        this.perlinNoiseGenerator = new NoiseGenerator(seed, (int) windowDimensions.x());
    }

    /**
     * Returns the height of the ground at the given x position.
     * @param x The x position.
     * @return The height of the ground at the given x position.
     */
    public float groundHeightAt(float x) {
        float generatedNoise = (float) perlinNoiseGenerator.noise(x, NOISE_GENERATION_FACTOR);
        return groundHeightAtX0 * generatedNoise + groundHeightAtX0 + OFFSET;
    }

    /**
     * Creates a list of blocks in the given range.
     * @param minX The minimum x position.
     * @param maxX The maximum x position.
     * @return A list of blocks in the given range.
     */
    public List<GameObject> createInRange(int minX, int maxX) {
        List<GameObject> blockList = new ArrayList<>();

        int startX = LocationCalculator.getClosestMultToBlockSize(minX);
        maxX = LocationCalculator.getClosestMultToBlockSize(maxX);

        // Add blocks at increasing X positions to the list
        for (int x = startX; x < maxX; x += Block.SIZE) {

            float y = (float) Math.floor(groundHeightAt(x) / Block.SIZE) * Block.SIZE;

            for (int i = 0; i < TERRAIN_DEPTH; i++) {

                Renderable blockRenderer = new RectangleRenderable(
                        ColorSupplier.approximateColor(BASE_BACKGROUND_COLOR)
                ); // Create a rectangle with approximate color

                Block block = new Block(Vector2.of(x, y + i * Block.SIZE), blockRenderer);
                block.setTag(BLOCK_TAG); // set block tag to "ground"
                blockList.add(block); // add to blockList
            }
        }
        return blockList;
    }
}
