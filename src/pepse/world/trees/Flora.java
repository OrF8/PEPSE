package pepse.world.trees;

import danogl.GameObject;
import danogl.util.Vector2;
import pepse.util.LocationCalculator;
import pepse.world.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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

    // Flora constants
    private static final double TREE_PLANTING_THRESHOLD = 0.1;
    private static final double LEAF_PLACEMENT_THRESHOLD = 0.4;
    private static final double FRUIT_PLACEMENT_THRESHOLD = 0.05;
    private static final int FOLIAGE_HEIGHT = 8;
    private static final int FOLIAGE_WIDTH = 8;
    private static final int HALF_DIVISION_FACTOR = 2;

    // Private fields
    private final float fruitRespawnCycleLength;
    private final UnaryOperator<Float> floatFunction;
    private final Consumer<Double> fruitCollisionCallback;
    private final Random random;

    /**
     * Constructs a new Flora instance responsible for creating and managing
     * the placement of flora elements (trees, leaves and fruits) in a terrain.
     *
     * @param floatFunction A function that calculates the ground height for a given
     *                      x-coordinate, used to determine where flora should be placed.
     */
    public Flora(
            UnaryOperator<Float> floatFunction,
            Consumer<Double> fruitCollisionCallback,
            float fruitRespawnCycleLength
    ) {
        this.fruitRespawnCycleLength = fruitRespawnCycleLength;
        this.floatFunction = floatFunction;
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
     * Creates foliage (a list of leaves and fruits) for a trunk starting at a specified trunk position.
     * <p>
     *      The method generates an arrangement of leaves and fruits around the trunk based on
     *      the configured foliage dimensions and positions them relative to the trunk.
     * </p>
     *
     * @param trunkXPos The x-coordinate of the trunk position.
     * @param trunkYPos The y-coordinate of the trunk's top position.
     * @return An {@code ArrayList<GameObject>} containing the foliage (leaves & fruits) created for the tree.
     */
    private ArrayList<GameObject> createFoliage(int trunkXPos, int trunkYPos) {
        ArrayList<GameObject> foliage = new ArrayList<>();
        int startingObjY = trunkYPos - FOLIAGE_HEIGHT / HALF_DIVISION_FACTOR * Block.SIZE;
        // Create foliage in a grid of size (FOLIAGE_HEIGHT x FOLIAGE_WIDTH).
        for (int row = 0, objY = startingObjY; row < FOLIAGE_HEIGHT; row++, objY += Block.SIZE) {
            int startingObjX = trunkXPos -
                               (FOLIAGE_WIDTH / HALF_DIVISION_FACTOR * Block.SIZE) -
                               (Block.SIZE / HALF_DIVISION_FACTOR);
            // For each row of leaves, place a leaf at increasing X positions based on shouldAddLeaf's result.
            for (int col = 0, objX = startingObjX; col < FOLIAGE_WIDTH; col++, objX += Block.SIZE) {
                Vector2 topLeftCorner = Vector2.of(objX, objY);
                if (shouldAddLeaf()) { // Add the leaf to the leave list.
                    Leaf leafCreator = new Leaf();
                    GameObject leaf = leafCreator.create(topLeftCorner);
                    foliage.add(leaf);
                } else if (shouldAddFruit(trunkXPos, objX)) { // Add a fruit if a leaf was not added.
                    Fruit fruit = new Fruit(topLeftCorner, fruitCollisionCallback, fruitRespawnCycleLength);
                    foliage.add(fruit);
                }
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
     *         value is an {@code ArrayList<GameObject>} containing the foliage (leaves)
     *         associated with that tree.
     */
    public Map<GameObject, ArrayList<GameObject>> createInRange(int minX, int maxX) {
        Trunk trunkCreator = new Trunk();
        Map<GameObject, ArrayList<GameObject>> floraMap = new HashMap<>();

        // Calculate the position of trunks based on Block.SIZE to ensure alignment.
        int trunkXPos = LocationCalculator.getClosestMultToBlockSize(minX);
        maxX = LocationCalculator.getClosestMultToBlockSize(maxX);

        for (; trunkXPos < maxX; trunkXPos += Block.SIZE) { // Plant trees in the given range
            if (shouldPlantTree()) {
                Vector2 trunkPosition = Vector2.of(trunkXPos, floatFunction.apply((float) trunkXPos));
                GameObject trunk = trunkCreator.create(trunkPosition);
                floraMap.put(trunk, createFoliage(trunkXPos, (int)trunk.getTopLeftCorner().y()));
            }
        }

        return floraMap;
    }

}
