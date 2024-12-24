package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * This class represents a block in the game.
 * Blocks are immovable objects that can be used to create terrain.
 *
 * @see Terrain
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class Block extends GameObject {

    /**
     * The size of a block in pixels.
     */
    public static final int SIZE = 30; // Block size - Do not change.

    /**
     * Creates a new block of size (SIZE x SIZE) at the specified position with the specified renderable.
     * @param topLeftCorner The top left corner of the block.
     * @param renderable The renderable to render the block with.
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }



}
