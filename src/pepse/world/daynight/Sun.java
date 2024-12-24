package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.Color;

/**TODO: Docs */
public class Sun {

    private static final Vector2 SUN_SIZE = Vector2.of(90, 90); // TODO: verify size
    private static final String SUN_TAG = "sun";
    private static final float INITIAL_SUN_CYCLE_ANGLE = 0;
    private static final float FINAL_SUN_CYCLE_ANGLE = 360;

    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        Vector2 sunPosition = windowDimensions.mult(0.5f);
        Renderable sunRenderer = new OvalRenderable(Color.YELLOW);
        GameObject sun = new GameObject(sunPosition, SUN_SIZE, sunRenderer);
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);

        Vector2 cycleCenter = sunPosition.subtract(SUN_SIZE.mult(0.5f));

        new Transition<>(
                sun,
                (Float angle) -> sun.setCenter(sunPosition.subtract(cycleCenter).rotated(angle).add(cycleCenter)),
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
