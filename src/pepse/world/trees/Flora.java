package pepse.world.trees;

import danogl.GameObject;
import danogl.util.Vector2;
import pepse.util.LocationCalculator;
import pepse.world.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.UnaryOperator;

/**
 * The Flora class is responsible for generating and managing the placement of flora elements
 * in a terrain, such as trees and leaves.
 * <p>
 * The generated elements are based on a calculated height
 * provided by a given function and thresholds for randomness to determine element
 * placement.
 * </p>
 * <p>
 * Trees consist of trunks and leaves, generated procedurally with specific constraints on
 * height and density.
 * </p>
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class Flora {

    private static final double TREE_PLANTING_THRESHOLD = 0.05;
    private static final double LEAF_PLACEMENT_THRESHOLD = 0.35;
    private static final int FOLIAGE_HEIGHT = 8;
    private static final int FOLIAGE_WIDTH = 8;

    private final UnaryOperator<Float> floatFunction;
    private final Random random;

    /**
     * Constructs a new Flora instance responsible for creating and managing
     * the placement of flora elements (trees and leaves) in a terrain.
     *
     * @param floatFunction A function that calculates the ground height for a given
     *                      x-coordinate, used to determine where flora should be placed.
     */
    public Flora(UnaryOperator<Float> floatFunction) {
        this.floatFunction = floatFunction;
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
     * Creates foliage (a list of leaves) for a trunk starting at a specified trunk position.
     * <p>
     * The method generates an arrangement of leaves around the trunk based on
     * the configured foliage dimensions and positions them relative to the trunk.
     * </p>
     *
     * @param leafCreator A {@link Leaf} object responsible for creating individual leaf instances.
     * @param trunkXPos The x-coordinate of the trunk position.
     * @param trunkYPos The y-coordinate of the trunk's top position.
     * @return An {@code ArrayList<GameObject>} containing the foliage (leaves) created for the tree.
     */
    private ArrayList<GameObject> createFoliage(Leaf leafCreator, int trunkXPos, int trunkYPos) {
        ArrayList<GameObject> foliage = new ArrayList<>();
        int startingLeafHeight = trunkYPos - FOLIAGE_HEIGHT / 2 * Block.SIZE;
        // Create foliage in a grid of size (FOLIAGE_HEIGHT x FOLIAGE_WIDTH).
        for (int row = 0, leafY = startingLeafHeight; row < FOLIAGE_HEIGHT; row++, leafY += Block.SIZE) {
            int startingLeafX = trunkXPos - FOLIAGE_WIDTH / 2 * Block.SIZE;
            // For each row of leaves, place a leaf at increasing X positions based on shouldAddLeaf's result.
            for (int col = 0, leafX = startingLeafX; col < FOLIAGE_WIDTH; col++, leafX += Block.SIZE) {
                if (shouldAddLeaf()) { // Add the leaf to the leave list
                    GameObject leaf = leafCreator.create(Vector2.of(leafX, leafY));
                    foliage.add(leaf);
                }
            }
        }
        return foliage;
    }

    /**
     * Generates a collection of flora elements (trunks and their foliage) within a specified range
     * of x-coordinates.
     * <p>
     * This method calculates the positions where trees should be planted, creates their trunks,
     * and attaches foliage (leaves) to them.
     * </p>
     *
     * @param minX The minimum x-coordinate of the range.
     * @param maxX The maximum x-coordinate of the range.
     * @return A map where the key is a {@code GameObject} representing the trunk of a tree, and the
     *         value is an {@code ArrayList<GameObject>} containing the foliage (leaves)
     *         associated with that tree.
     */
    public Map<GameObject, ArrayList<GameObject>> createInRange(int minX, int maxX) {
        // TODO: We think that A is PepseGameManager, C is Flora, and B is Terrain.
        Trunk trunkCreator = new Trunk();
        Leaf leafCreator = new Leaf();
        Map<GameObject, ArrayList<GameObject>> floraMap = new HashMap<>();

        // Calculate the position of trunks based on Block.SIZE to ensure alignment.
        int trunkXPos = LocationCalculator.getClosestMultToBlockSize(minX);
        maxX = LocationCalculator.getClosestMultToBlockSize(maxX);

        for (; trunkXPos < maxX; trunkXPos += Block.SIZE) { // Plant trees in the given range
            if (shouldPlantTree()) {
                Vector2 trunkPosition = Vector2.of(trunkXPos, floatFunction.apply((float) trunkXPos));
                GameObject trunk = trunkCreator.create(trunkPosition);
                floraMap.put(trunk, createFoliage(leafCreator, trunkXPos, (int)trunk.getTopLeftCorner().y()));
            }
        }

        return floraMap;
    }

}
