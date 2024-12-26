package pepse.world.trees;

import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.Color;
import java.util.Random;

/**
 * The Leaf class is responsible for creating leaf objects in the game.
 * Each leaf object is represented as a static block with a certain color pattern.
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class Leaf {

    private static final float INITIAL_LEAF_ANGLE = -10;
    private static final float FINAL_LEAF_ANGLE = 10;
    private static final float LEAF_TRANSITION_TIME_IN_SECONDS = 2;
    private static final float SCHEDULED_TASK_DELAY_BOUND = 2;
    private static final Color BASE_LEAF_COLOR = new Color(50, 200, 30);
    private static final Vector2 DIMENSIONS_GROWTH = Vector2.of(3, 3);

    private final Random random;
    private GameObject leaf;
    private Vector2 dimensions;

    /**
     * Constructs a new instance of the Leaf class.
     * This constructor initializes the Leaf class and allows for the creation
     * of leaf objects with predefined characteristics.
     */
    public Leaf() {
        random = new Random();
    }

    private void createAngleTransition() {
        new Transition<>(
                leaf,
                (Float angle) -> leaf.renderer().setRenderableAngle(angle),
                INITIAL_LEAF_ANGLE,
                FINAL_LEAF_ANGLE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                LEAF_TRANSITION_TIME_IN_SECONDS,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );
    }

    private void createDimensionsTransition() {
        new Transition<>(
                leaf,
                leaf::setDimensions,
                dimensions,
                dimensions.add(DIMENSIONS_GROWTH),
                Transition.LINEAR_INTERPOLATOR_VECTOR,
                LEAF_TRANSITION_TIME_IN_SECONDS,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );
    }

    /**
     * Creates a new leaf GameObject at the specified position.
     *
     * @param position The top-left corner of the newly created leaf GameObject.
     * @return A GameObject representing a leaf with predefined size and color.
     */
    public GameObject create(Vector2 position) {
        dimensions = Vector2.of(Block.SIZE, Block.SIZE);
        GameObject leaf =  new GameObject(
                position,
                dimensions,
                new RectangleRenderable(ColorSupplier.approximateColor(BASE_LEAF_COLOR))
        );

        this.leaf = leaf;

        new ScheduledTask(
                leaf,
                random.nextFloat(0, SCHEDULED_TASK_DELAY_BOUND),
                false,
                this::createAngleTransition
        );

        new ScheduledTask(
                leaf,
                random.nextFloat(0, SCHEDULED_TASK_DELAY_BOUND),
                false,
                this::createDimensionsTransition
        );

        return leaf;
    }

}
