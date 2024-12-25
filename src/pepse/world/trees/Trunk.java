package pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.awt.Color;
import java.util.Random;

public class Trunk {

    private static final Color BASE_TRUNK_COLOR = new Color(100, 50, 20);

    public GameObject create(Vector2 position) {
        Random random = new Random();
        return new GameObject(
                position,Vector2.of(Block.SIZE,
                                 random.nextInt(4, 7) * Block.SIZE),
                                    new RectangleRenderable(BASE_TRUNK_COLOR)
        );
    }

}
