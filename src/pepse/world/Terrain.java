package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** TODO: Docs */
public class Terrain {

    private static final Color BASE_BACKGROUND_COLOR = new Color(212, 123, 74); // block color
    private static final float TWO_THIRDS_FACTOR = 2 / 3f;
    private final float groundHeightAtX0; // TODO: Is it supposed to be final?
    private static final String BLOCK_TAG = "ground";
    private static final int TERRAIN_DEPTH = 20;

    /** TODO: Docs */
    public Terrain (Vector2 windowDimensions, int seed) {
        groundHeightAtX0 = windowDimensions.y() * TWO_THIRDS_FACTOR;
        // TODO: Use seed when getting to infinite world part
    }

    /** TODO: Docs */
    public float groundHeightAtX0(float x) {
        return groundHeightAtX0;
        // TODO: Think of something more complicated later on
    }

    /**
     * TODO: Docs
     */
    public List<Block> createInRange(int minX, int maxX) {
        List<Block> blockList = new ArrayList<>();
        // Add blocks at increasing X positions to the list
        for (int x = getClosestMultToBlockSize(minX); x < getClosestMultToBlockSize(maxX); x += Block.SIZE) {
            float y = (float) Math.floor(groundHeightAtX0(x) / Block.SIZE) * Block.SIZE;
            for (int i = 0; i < TERRAIN_DEPTH; i++) {
                // Create a rectangle with approximate color
                Renderable rectangleBlock = new RectangleRenderable(
                        ColorSupplier.approximateColor(BASE_BACKGROUND_COLOR));
                Block block = new Block(Vector2.of(x, y + i * Block.SIZE), rectangleBlock);
                block.setTag(BLOCK_TAG); // set block tag to "ground"
                blockList.add(block); // add to block list
            }
        }
        return blockList;
    }

    /** TODO: Docs */
    private static int getClosestMultToBlockSize(int num) {
        return (int) Math.floor((double) num / Block.SIZE) * Block.SIZE;
    }
}
