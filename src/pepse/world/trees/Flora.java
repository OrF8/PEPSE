package pepse.world.trees;

import danogl.GameObject;
import danogl.util.Vector2;
import pepse.util.LocationCalculator;
import pepse.util.MathConstants;
import pepse.world.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * The Flora class is responsible for generating and managing the placement of flora elements
 * in a terrain, such as trees and leaves.
 * <p>
 *      The generated elements are based on a calculated height
 *      provided by a given function and thresholds for randomness to determine element
 *      placement.
 * </p>
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class Flora {

    /**
     * A constant representing the tag for marking objects as fruits in the game.
     * Used to differentiate and identify fruit objects by their specific tag.
     */
    public static final String FRUIT_TAG = "Fruit";

    // Private constants
    private static final double TREE_PLANTING_THRESHOLD = 0.075; /* Probability to plant a tree */
    private static final double LEAF_PLACEMENT_THRESHOLD = 0.65; /* Probability to place a leaf */
    private static final double FRUIT_PLACEMENT_THRESHOLD = 0.05; /* Probability to place a fruit */
    private static final int FOLIAGE_HEIGHT = 8; /* Number of rows of leaves */
    private static final int FOLIAGE_WIDTH = 8; /* Number of columns of leaves */


    // Private final fields
    private final float fruitRespawnCycleLength; /* Time in seconds for a fruit to respawn */
    private final int seed; /* Seed for random number generation */
    private final UnaryOperator<Float> groundHeightAtX; /* Function to calculate ground height */
    private final Consumer<Double> fruitCollisionCallback; /* Callback for fruit collision */

    // Private fields
    private final Random random; /* Random number generator */

    /**
     * Constructs a new Flora instance responsible for creating and managing
     * the placement of flora elements (trees, leaves and fruits) in a terrain.
     *
     * @param groundHeightAtX A function that calculates the ground height for a given
     *                        x-coordinate, used to determine where flora should be placed.
     * @param fruitCollisionCallback A callback function to be called when a fruit collides with
     *                               another object.
     *                               The function should accept a double representing the x-coordinate
     *                               of the collision.
     *                               The function should not return anything.
     *                               The function should not throw any exceptions.
     *                               The function should not be null.
     * @param fruitRespawnCycleLength The time in seconds it takes for
     *                                a fruit to respawn after being collected.
     * @param seed The seed used for random number generation.
     */
    public Flora(
            UnaryOperator<Float> groundHeightAtX,
            Consumer<Double> fruitCollisionCallback,
            float fruitRespawnCycleLength, int seed
    ) {
        this.fruitRespawnCycleLength = fruitRespawnCycleLength;
        this.seed = seed;
        this.groundHeightAtX = groundHeightAtX;
        this.fruitCollisionCallback = fruitCollisionCallback;
        this.random = new Random();
    }

    /**
     * Determines whether a tree should be planted based on a random threshold.
     *
     * @return {@code true} if we should plant the tree, otherwise {@code false}.
     */
    private boolean shouldPlantTree() {
        return random.nextDouble(0, 1) < TREE_PLANTING_THRESHOLD;
    }

    /**
     * Determines whether a leaf should be added based on a random threshold.
     *
     * @return {@code true} if we should add the leaf, otherwise {@code false}.
     */
    private boolean shouldAddLeaf() {
        return random.nextDouble(0, 1) < LEAF_PLACEMENT_THRESHOLD;
    }

    /**
     * Determines whether a fruit should be added at the given position based on
     * the position of the trunk and a random threshold.
     *
     * @param trunkXPos The x-coordinate position of the tree trunk.
     * @param fruitX The x-coordinate position of the fruit being evaluated.
     * @return {@code true} if a fruit should be added based on the conditions, otherwise {@code false}.
     */
    private boolean shouldAddFruit(int trunkXPos, int fruitX) {
        return fruitX != trunkXPos && random.nextDouble(0, 1) < FRUIT_PLACEMENT_THRESHOLD;
    }

    /**
     * Places a leaf or fruit at the specified position based on certain conditions.
     * <p>
     *     This method checks whether a leaf should be added based on a threshold.
     *     If a leaf is added, it creates a new Leaf object at the specified position.
     * </p>
     * <p>
     *     If a leaf is not added, it checks if a fruit should be placed based on the trunk's x-coordinate.
     *     If so, it creates a new Fruit object at the specified position.
     * </p>
     * @param foliage The list of foliage (leaves and fruits) to which the new object will be added.
     * @param topLeftCorner The top-left corner position where the leaf or fruit will be placed.
     * @param trunkXPos The x-coordinate position of the tree trunk,
     *                  used to determine if a fruit should be placed.
     * @param objX The x-coordinate position of the object being placed,
     */
    private void placeLeafOrFruit(
         List<GameObject> foliage, Vector2 topLeftCorner, int trunkXPos, int objX
    ) {
        if (shouldAddLeaf()) { // Add the leaf to the leave list.
            Leaf leafCreator = new Leaf();
            GameObject leaf = leafCreator.create(topLeftCorner);
            foliage.add(leaf);
        } else if (shouldAddFruit(trunkXPos, objX)) { // Add a fruit if a leaf was not added.
            Fruit fruit = new Fruit(topLeftCorner, fruitCollisionCallback, fruitRespawnCycleLength);
            foliage.add(fruit);
        }
    }

    /**
     * Creates foliage (a list of leaves and fruits) for a trunk starting at a specified trunk position.
     * <p>
     *      The method generates an arrangement of leaves and fruits around the trunk based on
     *      the configured foliage dimensions and positions them relative to the trunk.
     * </p>
     *
     * @param trunkXPos The x-coordinate of the trunk position.
     * @param trunkYPos The y-coordinate of the trunk's top position.
     * @return An {@code List<GameObject>} containing the foliage
     *         (leaves and fruits) created for the tree.
     */
    private List<GameObject> createFoliage(int trunkXPos, int trunkYPos) {
        List<GameObject> foliage = new ArrayList<>();
        int startingObjY = trunkYPos - (int) (FOLIAGE_HEIGHT * MathConstants.HALF_FACTOR) * Block.SIZE;
        // Create foliage in a grid of size (FOLIAGE_HEIGHT x FOLIAGE_WIDTH).
        for (int row = 0, objY = startingObjY; row < FOLIAGE_HEIGHT; row++, objY += Block.SIZE) {
            int startingObjX = trunkXPos -
                               (int) (FOLIAGE_WIDTH * MathConstants.HALF_FACTOR * Block.SIZE) -
                               (int) (Block.SIZE * MathConstants.HALF_FACTOR);
            // For each row of leaves, place a leaf at increasing X positions based on shouldAddLeaf's result.
            for (int col = 0, objX = startingObjX; col < FOLIAGE_WIDTH; col++, objX += Block.SIZE) {
                // Set the random seed based on the current position and the seed provided,
                // to ensure consistent "random" behavior for each position.
                random.setSeed(Objects.hash(objX, objY, seed));
                placeLeafOrFruit(foliage, Vector2.of(objX, objY), trunkXPos, objX);
            }
        }
        return foliage;
    }

    /**
     * Generates a collection of flora elements (trunks and their foliage) within a specified range
     * of x-coordinates.
     * <p>
     *      This method calculates the positions where trees should be planted, creates their trunks,
     *      and attaches foliage (leaves and fruits) to them.
     * </p>
     *
     * @param minX The minimum x-coordinate of the range.
     * @param maxX The maximum x-coordinate of the range.
     * @return A map where the key is a {@code GameObject} representing the trunk of a tree, and the
     *         value is a {@code List<GameObject>} containing the foliage (leaves and fruits)
     *         associated with that tree.
     */
    public Map<GameObject, List<GameObject>> createInRange(int minX, int maxX) {
        Map<GameObject, List<GameObject>> floraMap = new HashMap<>();

        // Calculate the position of trunks based on Block.SIZE to ensure alignment.
        int trunkXPos = LocationCalculator.getClosestMultToBlockSize(minX);
        maxX = LocationCalculator.getClosestMultToBlockSize(maxX);

        for (; trunkXPos < maxX; trunkXPos += Block.SIZE) { // Plant trees in the given range
            // Set the random seed based on the trunk position and the seed provided,
            // to ensure consistent "random" behavior for each trunk position.
            random.setSeed(Objects.hash(trunkXPos, seed));
            if (shouldPlantTree()) {
                Vector2 trunkPosition = Vector2.of(trunkXPos, groundHeightAtX.apply((float) trunkXPos));
                // Create a trunk at the calculated position.
                GameObject trunk = Trunk.create(trunkPosition);
                // Create foliage for the trunk and add it to the map.
                floraMap.put(trunk, createFoliage(trunkXPos, (int) trunk.getTopLeftCorner().y()));
            }
        }

        return floraMap;
    }

}
