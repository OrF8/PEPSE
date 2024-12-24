package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;

import danogl.util.Vector2;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.Block;
import pepse.world.daynight.Night;

import java.util.List;

/**
 * TODO: Docs
 */
public class PepseGameManager extends GameManager {

    private static final int SECONDS_IN_A_DAY_CYCLE = 30;

    /**
     * TODO: Docs
     */
    @Override
    public void initializeGame(
            ImageReader imageReader,
            SoundReader soundReader,
            UserInputListener inputListener,
            WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        Vector2 windowDimensions = windowController.getWindowDimensions();

        GameObject sky = Sky.create(windowDimensions); // create sky
        gameObjects().addGameObject(sky, Layer.BACKGROUND); // add sky to background layer

        Terrain terrain = new Terrain(windowDimensions, 10);
        List<Block> blockList = terrain.createInRange(0, (int) windowDimensions.x());
        for (Block block : blockList) {
            gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
        }

        GameObject night = Night.create(windowDimensions, SECONDS_IN_A_DAY_CYCLE);
        gameObjects().addGameObject(night, Layer.FOREGROUND); // TODO: Verify layer later

    }

    /**
     * TODO: Docs
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
