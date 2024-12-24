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
import pepse.world.daynight.Sun;

import java.util.List;

/**
 * The main class for the game.
 * <p>
 *     This class is responsible for initializing the game and starting the game loop.
 *     It also contains the main method to start the game.
 *     The game is a simple 2D world where the player can move around and interact with the environment.
 *     The game has a day-night cycle,
 *     and the player can interact with the environment to change the time of day.
 * </p>
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class PepseGameManager extends GameManager {

    private static final int SECONDS_IN_A_DAY_CYCLE = 30;

    /**
     * Initializes the game objects.
     * @param windowDimensions The dimensions of the game window.
     */
    private void initGameObjects(Vector2 windowDimensions) {
        GameObject sky = Sky.create(windowDimensions); // create sky
        gameObjects().addGameObject(sky, Layer.BACKGROUND); // add sky to the background layer

        // create terrain
        Terrain terrain = new Terrain(windowDimensions, 10);
        List<Block> blockList = terrain.createInRange(0, (int) windowDimensions.x());
        for (Block block : blockList) {
            gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
        }

        // create night
        GameObject night = Night.create(windowDimensions, SECONDS_IN_A_DAY_CYCLE);
        gameObjects().addGameObject(night, Layer.FOREGROUND); // TODO: Verify layer later

        // create sun
        GameObject sun = Sun.create(windowDimensions, SECONDS_IN_A_DAY_CYCLE);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);
    }

    /**
     * Initializes the game.
     * @param imageReader The image reader to use for loading images.
     * @param soundReader The sound reader to use for loading sounds.
     * @param inputListener The input listener to use for getting user input.
     * @param windowController The window controller to use for creating the game window.
     *                         The window controller is also used for getting the window dimensions.
     */
    @Override
    public void initializeGame(
            ImageReader imageReader,
            SoundReader soundReader,
            UserInputListener inputListener,
            WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        initGameObjects(windowController.getWindowDimensions());
    }

    /**
     * The main method to start the game.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
