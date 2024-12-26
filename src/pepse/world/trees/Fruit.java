package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Avatar;
import pepse.world.Block;

import java.awt.*;
import java.util.function.Consumer;

// TODO: docs
public class Fruit extends GameObject {

    private static final double FRUIT_ENERGY_VALUE = 10.0;
    private static final Color BASE_FRUIT_COLOR = new Color(67, 45, 159);

    // TODO: docs
    public Fruit(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
    }

    // TODO: docs
    public GameObject create(Vector2 position) {
        return new GameObject(
                position,
                Vector2.of(Block.SIZE, Block.SIZE),
                new OvalRenderable(ColorSupplier.approximateColor(BASE_FRUIT_COLOR))
        );
    }

    private void uponFruitCollecting(Consumer<Double> action) {
        action.accept(FRUIT_ENERGY_VALUE);
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (other.getTag().equals("avatar")) {
            uponFruitCollecting(Avatar::addEnergy);
        }
    }
}
