package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;

import java.awt.Color;

/**
 * This class is responsible for creating the sun halo.
 * <p>
 *     The sun halo is a yellow halo around the sun.
 *     The halo is created as an oval game object that follows the sun.
 * </p>
 */
public class SunHalo {

    private static final Color BASIC_HALO_COLOR = new Color(255, 255, 0, 20);
    private static final String SUN_HALO_TAG = "sunHalo";
    private static final int SUN_HALO_FACTOR = 2;

    /**
     * Creates a sun halo game object.
     * @param sun The sun game object.
     * @return The sun halo game object.
     */
    public static GameObject create(GameObject sun) {
        GameObject sunHalo = new GameObject(
                sun.getTopLeftCorner(), sun.getDimensions().mult(SUN_HALO_FACTOR),
                new OvalRenderable(BASIC_HALO_COLOR));
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.setTag(SUN_HALO_TAG);

        // Make the halo follow the sun rotation
        sunHalo.addComponent(
                _ -> sunHalo.setCenter(sun.getCenter())
        );
        return sunHalo;
    }
}
