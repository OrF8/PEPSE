package pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.Color;

/**
 * The Leaf class is responsible for creating leaf objects in the game.
 * Each leaf object is represented as a static block with a certain color pattern.
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class Leaf {

    private static final Color BASE_LEAF_COLOR = new Color(50, 200, 30);

    /**
     * Constructs a new instance of the Leaf class.
     * This constructor initializes the Leaf class and allows for the creation
     * of leaf objects with predefined characteristics.
     */
    public Leaf() {}

    /**
     * Creates a new leaf GameObject at the specified position.
     *
     * @param position The top-left corner of the newly created leaf GameObject.
     * @return A GameObject representing a leaf with predefined size and color.
     */
    public GameObject create(Vector2 position) {
        return new GameObject(
                position,
                Vector2.of(Block.SIZE, Block.SIZE),
                new RectangleRenderable(ColorSupplier.approximateColor(BASE_LEAF_COLOR))
        );
    }

}
