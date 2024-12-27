package pepse.world.trees;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.Color;
import java.util.Random;

/**
 * The Trunk class is responsible for creating tree trunks in a game environment.
 * Tree trunks are vertical structures composed of stacked blocks, and their height is generated
 * randomly within a predefined range.
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
class Trunk {

    // Private constants
    private static final int MIN_TREE_HEIGHT = 4; /* Minimum tree height in blocks */
    private static final int MAX_TREE_HEIGHT = 10; /* Maximum tree height in blocks */
    private static final int TRUNK_COLOR_DELTA = 15; /* Maximum color difference between trunks */
    /* Base color of tree trunks */
    private static final Color BASE_TRUNK_COLOR = new Color(100, 50, 20);

    /**
     * Constructs a new instance of the Trunk class.
     * This constructor initializes the internal random number generator
     * used for creating tree trunks with randomized characteristics, such as height.
     */
    Trunk() {}

    /**
     * Creates a GameObject representing a tree trunk at the specified position.
     * The height of the trunk is determined randomly within a predefined range,
     * and the trunk is rendered as a rectangular object with a fixed color.
     * <p>
     * The created trunk object is immovable and prevents intersections from any direction.
     * </p>
     *
     * @param position The bottom-center position (x, y) where the trunk should be created.
     *                 The trunk will extend upwards from this position.
     * @return A GameObject representing the created tree trunk with specified properties.
     */
     GameObject create(Vector2 position) {
         Random random = new Random((long) position.x());
         float treeHeight = random.nextInt(MIN_TREE_HEIGHT, MAX_TREE_HEIGHT) * Block.SIZE;
         Vector2 topLeftCorner = Vector2.of(position.x(), position.y() - treeHeight);
         Vector2 dimensions = Vector2.of(Block.SIZE, treeHeight);
         GameObject trunk = new GameObject(
                 topLeftCorner,
                 dimensions,
                 new RectangleRenderable(ColorSupplier.approximateColor(BASE_TRUNK_COLOR, TRUNK_COLOR_DELTA))
         );
         trunk.physics().preventIntersectionsFromDirection(Vector2.ZERO);
         trunk.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
         return trunk;
     }

}
