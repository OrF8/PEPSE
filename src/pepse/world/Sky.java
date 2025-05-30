package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.Color;

/**
 * This class is responsible for creating the sky GameObject.
 * The sky is a GameObject that is a rectangle that covers the entire window.
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class Sky {

    // Private constants
    private static final String SKY_TAG = "sky"; /* The tag of the sky GameObject. */
    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5"); /* The base color of the sky. */

    /**
     * Private constructor to prevent instantiation.
     */
    private Sky() {}

    /**
     * Creates a sky GameObject.
     * The sky is a GameObject that is a rectangle that covers the entire window.
     * @param windowDimensions The dimensions of the window.
     * @return The sky GameObject.
     */
    public static GameObject create(Vector2 windowDimensions) {
        GameObject sky = new GameObject(
                Vector2.ZERO, windowDimensions,
                new RectangleRenderable(BASIC_SKY_COLOR)
        );

        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sky.setTag(SKY_TAG);

        return sky;
    }

}
