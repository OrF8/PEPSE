package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.MathConstants;

import java.awt.Color;

/**
 * The Night class represents the night cycle in a game. It creates a game object
 * that transitions between a transparent state (day) and an opaque state (midnight),
 * simulating a day-night cycle. The object follows the camera and alternates its
 * opacity over time based on the configured cycle length.
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class Night {

    // Private constants
    private static final float MIDNIGHT_OPACITY = 0.5f; /* The opacity of the middle of the night */
    private static final float DAY_OPACITY = 0; /* The opacity of the middle of the day */
    private static final String NIGHT = "night"; /* The tag for the night object */

    /**
     * A private constructor to prevent instantiation of the Night class.
     */
    private Night() {}

    /**
     * Creates a night object that cycles between day and night.
     * @param windowDimensions The dimensions of the window.
     * @param cycleLength The length of the cycle in seconds.
     * @return The night object.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        GameObject night = new GameObject(
                Vector2.ZERO,
                windowDimensions,
                new RectangleRenderable(Color.BLACK)
        );

        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES); // force the night to follow the camera
        night.setTag(NIGHT); // set "night" tag

        // Set up a transition to cycle day and night
        new Transition<>(
                night,
                night.renderer()::setOpaqueness,
                DAY_OPACITY,
                MIDNIGHT_OPACITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                cycleLength * MathConstants.HALF_FACTOR,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );

        return night;
    }

}
