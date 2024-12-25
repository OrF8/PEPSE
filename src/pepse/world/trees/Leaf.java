package pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.awt.Color;

public class Leaf {

    private static final Color BASE_LEAF_COLOR = new Color(50, 200, 30);

    public GameObject create(Vector2 position) {
        return new GameObject(
                position, Vector2.of(Block.SIZE, Block.SIZE), new RectangleRenderable(BASE_LEAF_COLOR)
        );
    }

}
