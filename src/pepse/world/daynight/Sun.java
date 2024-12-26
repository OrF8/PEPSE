package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.Color;

/**
 * This class is responsible for creating the sun game object.
 */
public class Sun {

    private static final Vector2 SUN_SIZE = Vector2.of(90, 90); // TODO: verify size
    private static final String SUN_TAG = "sun";
    private static final float INITIAL_SUN_CYCLE_ANGLE = 0;
    private static final float FINAL_SUN_CYCLE_ANGLE = 360;
    private static final float TWO_THIRDS_FACTOR = 2 / 3f;
    private static final float HALF_FACTOR = 0.5f;

    /**
     * Default constructor for the Sun class.
     * This constructor initializes a new instance of the Sun class.
     * The Sun class is responsible for creating the sun game object that moves
     * in a circular path as part of the game's visual and dynamic elements.
     */
    public Sun() {}

    /**
     * Creates a sun game object.
     * <p>
     *     The sun is a yellow circle that moves in a circular path.
     *     The sun moves in a circular path around the center of the screen.
     *     The sun moves in a circular path with a given cycle length.
     * </p>
     * @param windowDimensions The dimensions of the game window.
     * @param cycleLength The length of the sun cycle in seconds.
     * @return The sun game object.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        Renderable sunRenderer = new OvalRenderable(Color.YELLOW);
        GameObject sun = new GameObject(Vector2.ZERO, SUN_SIZE, sunRenderer);

        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);

        Vector2 initialSunCenter = windowDimensions.mult(HALF_FACTOR);
        Vector2 cycleCenter = Vector2.of(
                windowDimensions.x() * HALF_FACTOR, windowDimensions.y() * TWO_THIRDS_FACTOR
        );

        new Transition<>(
                sun,
                (Float angle) ->
                        sun.setCenter(initialSunCenter.subtract(cycleCenter).rotated(angle).add(cycleCenter)),
                INITIAL_SUN_CYCLE_ANGLE,
                FINAL_SUN_CYCLE_ANGLE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null
        );

        return sun;
    }
}
