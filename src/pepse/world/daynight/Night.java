package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.Color;

/**
 * TODO: Docs
 */
public class Night {

    private static final float MIDNIGHT_OPACITY = 0.5f;
    private static final float DAY_OPACITY = 0;
    private static final String NIGHT = "night";
    private static final float HALF_FACTOR = 0.5f;

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
                new RectangleRenderable(Color.black)
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
                cycleLength * HALF_FACTOR,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );

        return night;
    }

}
