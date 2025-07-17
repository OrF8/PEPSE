package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.MathConstants;

import java.awt.Color;

import java.util.function.UnaryOperator;

/**
 * This class is responsible for creating the sun game object.
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class Sun {

    // Private constants
    private static final float INITIAL_SUN_CYCLE_ANGLE = 0; /* The initial angle of the sun in the cycle. */
    private static final float FINAL_SUN_CYCLE_ANGLE = 360; /* The final angle of the sun in the cycle. */
    private static final String SUN_TAG = "sun"; /* The tag for the sun game object. */
    private static final Vector2 SUN_SIZE = Vector2.of(90, 90); /* The size of the sun game object. */

    /**
     * A private constructor to prevent instantiation of this utility class.
     */
    private Sun() {}

    /**
     * Creates a sun game object.
     * <p>
     *     The sun is a yellow circle that moves in a circular path.
     *     The sun moves in a circular path around the center of the screen.
     *     The sun moves in a circular path with a given cycle length.
     * </p>
     * @param windowDimensions The dimensions of the game window.
     * @param cycleLength The length of the sun cycle in seconds.
     * @param sunHeight The height of the sun as a function of the sun's x coordinate.
     * @return The sun game object.
     */
    public static GameObject create(
            Vector2 windowDimensions, float cycleLength, UnaryOperator<Float> sunHeight
    ) {
        Renderable sunRenderer = new OvalRenderable(Color.YELLOW);
        GameObject sun = new GameObject(Vector2.ZERO, SUN_SIZE, sunRenderer);

        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES); // Set sun to follow camera
        sun.setTag(SUN_TAG);

        float sunX = windowDimensions.x() * MathConstants.HALF_FACTOR;

        Vector2 initialSunCenter = windowDimensions.mult(MathConstants.HALF_FACTOR);
        Vector2 cycleCenter = Vector2.of(sunX, sunHeight.apply(sunX));

        new Transition<>( // Make the sun rotate around the center of the screen
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
