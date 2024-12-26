package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;

import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.Block;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Flora;
import pepse.world.trees.Leaf;
import pepse.world.trees.Trunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private static final int HALO_LAYER_VALUE = -150; // Sun layer is -100, set halo in front of it
    private static final int LEAF_LAYER = -50;
    private static final float AVATAR_Y_POS_OFFSET = 100;
    private static final float AVATAR_X_POS_RATIO = 2;
    private static final String INITIAL_ENERGY_STRING = "100%";
    private static final String PERCENT = "%";
    private static final Vector2 ENERGY_DISPLAY_TOP_LEFT_CORNER = Vector2.of(10, 20);
    private static final Vector2 ENERGY_DISPLAY_DIMENSIONS = Vector2.of(50, 50);

    private Terrain terrain;
    private Avatar avatar;

    /**
     * Default constructor for the PepseGameManager.
     * Initializes a new instance of the game manager, managing the creation and maintenance of
     * various game elements.
     */
    public PepseGameManager() {}

    /**
     * Creates the sky.
     * @param windowDimensions The dimensions of the game window.
     */
    private void createSky(Vector2 windowDimensions) {
        GameObject sky = Sky.create(windowDimensions); // create sky
        gameObjects().addGameObject(sky, Layer.BACKGROUND); // add sky to the background layer
    }

    /**
     * Creates the terrain.
     * @param windowDimensions The dimensions of the game window.
     */
    private void createTerrain(Vector2 windowDimensions) {
        Terrain terrain = new Terrain(windowDimensions, 10);
        List<Block> blockList = terrain.createInRange(0, (int) windowDimensions.x());
        for (Block block : blockList) {
            gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
        }
        this.terrain = terrain;
    }

    /**
     * Creates the night.
     * @param windowDimensions The dimensions of the game window.
     */
    private void createNight(Vector2 windowDimensions) {
        GameObject night = Night.create(windowDimensions, SECONDS_IN_A_DAY_CYCLE);
        gameObjects().addGameObject(night, Layer.FOREGROUND); // TODO: Verify layer later
    }

    /**
     * Creates the sun and its halo.
     * @param windowDimensions The dimensions of the game window.
     */
    private void createSunAndHalo(Vector2 windowDimensions) {
        GameObject sun = Sun.create(windowDimensions, SECONDS_IN_A_DAY_CYCLE);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);
        GameObject sunHalo = SunHalo.create(sun);
        gameObjects().addGameObject(sunHalo, HALO_LAYER_VALUE);
    }

    /**
     * Creates the avatar.
     * @param windowDimensions The dimensions of the game window.
     * @param inputListener The input listener to use for getting user input.
     * @param imageReader The image reader to use for loading images.
     */
    private void createAvatar(
            Vector2 windowDimensions, UserInputListener inputListener, ImageReader imageReader
    ) {
        float avatarXPosition = windowDimensions.x() / AVATAR_X_POS_RATIO;
        float avatarYPosition = terrain.groundHeightAtX0(avatarXPosition) - AVATAR_Y_POS_OFFSET; // TODO: Check later
        Avatar avatar = new Avatar(
                Vector2.of(avatarXPosition, avatarYPosition),
                inputListener,
                imageReader
        );
        gameObjects().addGameObject(avatar, Layer.DEFAULT);
        this.avatar = avatar;
    }

    /**
     * Creates the energy display.
     */
    private void createEnergyDisplay() {
        // Handle numeric energy count display
        TextRenderable energyTextRenderable = new TextRenderable(INITIAL_ENERGY_STRING);
        GameObject energyDisplay = new GameObject(
                ENERGY_DISPLAY_TOP_LEFT_CORNER, ENERGY_DISPLAY_DIMENSIONS, energyTextRenderable
        );
        gameObjects().addGameObject(energyDisplay, Layer.UI);
        energyDisplay.addComponent(
                deltaTime -> energyTextRenderable.setString(Math.round(avatar.getEnergy()) + PERCENT)
        );
    }

    private void createFlora(Vector2 windowDimensions) {
        Flora flora = new Flora(terrain::groundHeightAtX0);
        Map<GameObject, ArrayList<GameObject>> trees = flora.createInRange(0, (int) windowDimensions.x());
        for (GameObject trunk : trees.keySet()) {
            gameObjects().addGameObject(trunk, Layer.STATIC_OBJECTS);
            for (GameObject leaf : trees.get(trunk)) {
                gameObjects().addGameObject(leaf, LEAF_LAYER);
            }
        }
    }

    /**
     * Initializes the game objects.
     * @param windowDimensions The dimensions of the game window.
     *
     * @see #createSky(Vector2)
     * @see #createTerrain(Vector2)
     * @see #createNight(Vector2)
     * @see #createSunAndHalo(Vector2)
     * @see #createAvatar(Vector2, UserInputListener, ImageReader)
     * @see #createEnergyDisplay()
     * @see #createFlora(Vector2)
     */
    private void initGameObjects(
            Vector2 windowDimensions,
            UserInputListener inputListener,
            ImageReader imageReader
    ) {
        createSky(windowDimensions); // create sky
        createTerrain(windowDimensions); // create terrain
        createNight(windowDimensions); // create night
        createSunAndHalo(windowDimensions); // create sun and halo
        createAvatar(windowDimensions, inputListener, imageReader); // create avatar
        createEnergyDisplay(); // create energy display
        createFlora(windowDimensions); // create the flora of the game
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

        initGameObjects(windowController.getWindowDimensions(), inputListener, imageReader);
    }

    /**
     * The main method to start the game.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
        // TODO: Check position of numeric energy (size, location on screen)
    }
}
