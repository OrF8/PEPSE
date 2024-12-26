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

    // Sky constants
    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");
    private static final String SKY_TAG = "sky";

    /**
     * Default constructor for the Sky class.
     * Initializes an instance of the Sky class, which is responsible for managing
     * the creation of the sky GameObject that covers the entire game window.
     */
    public Sky() {}

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
