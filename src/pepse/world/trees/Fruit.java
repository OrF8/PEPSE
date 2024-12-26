package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Block;

import java.awt.*;
import java.util.function.Consumer;

/**
 * The Fruit class represents a fruit object in the game.
 * Fruits are collectible items that provide energy to the Avatar upon collision.
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class Fruit extends GameObject {

    /**
     * A constant representing the tag for marking objects as fruits in the game.
     * Used to differentiate and identify fruit objects by their specific tag.
     */
    public static final String FRUIT_TAG = "Fruit";

    // Fruit constants
    private static final double FRUIT_ENERGY_VALUE = 10;
    private static final Color BASE_FRUIT_COLOR = new Color(67, 45, 159);
    private static final Renderable fruitRenderable = new OvalRenderable(BASE_FRUIT_COLOR);

    // Private fields
    private final float respawnCycleLength;
    private final Consumer<Double> collisionAction;

    /**
     * Constructs a new Fruit instance.
     *
     * @param topLeftCorner The top-left corner position of the fruit in the game world.
     * @param collisionAction A consumer specifying the action to perform when
     *                        the fruit collides with an avatar.
     * @param respawnCycleLength The time interval (in seconds) after which the fruit should reappear
     *                           following a collision or disappearance.
     */
    public Fruit(Vector2 topLeftCorner, Consumer<Double> collisionAction, float respawnCycleLength) {
        super(
                topLeftCorner,
                Vector2.of(Block.SIZE, Block.SIZE),
                fruitRenderable
        );
        this.setTag(FRUIT_TAG);
        this.respawnCycleLength = respawnCycleLength;
        this.collisionAction = collisionAction;
    }

    /**
     * Handles the behavior of the fruit upon collision with another game object.
     * When the fruit collides with an avatar, it provides energy to the avatar,
     * disappears, and respawns after a specified delay.
     *
     * @param other The game object that the fruit collided with.
     * @param collision Information about the collision event between this fruit
     *                  and the other game object.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (other.getTag().equals(Avatar.AVATAR_TAG)) {
            collisionAction.accept(FRUIT_ENERGY_VALUE);
            // Make the fruit disappear
            this.renderer().setRenderable(null);
            // Make the fruit respawn after respawnCycleLength time has elapsed.
            new ScheduledTask(
                    this,
                    this.respawnCycleLength,
                    false,
                    () -> this.renderer().setRenderable(fruitRenderable)
            );
        }
    }
}
