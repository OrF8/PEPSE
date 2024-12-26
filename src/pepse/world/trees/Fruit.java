package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.world.Avatar;
import pepse.world.Block;

import java.awt.*;
import java.util.function.Consumer;

// TODO: docs
public class Fruit extends GameObject {

    /**
     * A constant representing the tag for marking objects as fruits in the game.
     * Used to differentiate and identify fruit objects by their specific tag.
     */
    public static final String FRUIT_TAG = "Fruit";

    private static final double FRUIT_ENERGY_VALUE = 10;
    private static final Color BASE_FRUIT_COLOR = new Color(67, 45, 159);
    private static final Renderable fruitRenderable = new OvalRenderable(BASE_FRUIT_COLOR);

    private final Consumer<Double> collisionAction;

    // TODO: docs
    public Fruit(Vector2 topLeftCorner, Consumer<Double> collisionAction) {
        super(
                topLeftCorner,
                Vector2.of(Block.SIZE, Block.SIZE),
                fruitRenderable
        );
        this.setTag(FRUIT_TAG);
        this.collisionAction = collisionAction;
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (other.getTag().equals(Avatar.AVATAR_TAG)) {
            collisionAction.accept(FRUIT_ENERGY_VALUE);
            this.renderer().setRenderable(null);
            new ScheduledTask(
                    this,
                    PepseGameManager.SECONDS_IN_A_DAY_CYCLE,
                    false,
                    () -> this.renderer().setRenderable(fruitRenderable)
            );
        }
    }
}
