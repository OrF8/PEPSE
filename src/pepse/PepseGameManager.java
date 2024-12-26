package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Flora;
import pepse.world.trees.Fruit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
    private Vector2 windowDimensions;

    /**
     * Default constructor for the PepseGameManager.
     * Initializes a new instance of the game manager, managing the creation and maintenance of
     * various game elements.
     */
    public PepseGameManager() {}

    /**
     * Creates the sky.
     */
    private void createSky() {
        GameObject sky = Sky.create(windowDimensions);
        gameObjects().addGameObject(sky, Layer.BACKGROUND);
    }

    /**
     * Creates the terrain.
     */
    private void createTerrain() {
        Terrain terrain = new Terrain(windowDimensions, 10);
        // Create terrain made of blocks based on the method createInRange
        List<GameObject> blockList = terrain.createInRange(0, (int) windowDimensions.x());
        // Add the blocks that make up the terrain to the static layer.
        for (GameObject block : blockList) {
            gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
        }
        this.terrain = terrain;
    }

    /**
     * Creates the night.
     */
    private void createNight() {
        GameObject night = Night.create(windowDimensions, SECONDS_IN_A_DAY_CYCLE);
        gameObjects().addGameObject(night, Layer.FOREGROUND); // TODO: Verify layer later
    }

    /**
     * Creates the sun and its halo.
     */
    private void createSunAndHalo() {
        // Create the sun
        GameObject sun = Sun.create(windowDimensions, SECONDS_IN_A_DAY_CYCLE);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);
        // Create its halo
        GameObject sunHalo = SunHalo.create(sun);
        gameObjects().addGameObject(sunHalo, HALO_LAYER_VALUE);
    }

    /**
     * Creates the avatar.
     * @param inputListener The input listener to use for getting user input.
     * @param imageReader The image reader to use for loading images.
     */
    private void createAvatar(UserInputListener inputListener, ImageReader imageReader) {
        // Create the avatar at the middle of the screen
        float avatarXPosition = windowDimensions.x() / AVATAR_X_POS_RATIO;
        // Create the avatar slightly above the ground to prevent creation inside the ground
        float avatarYPosition = terrain.groundHeightAtX0(avatarXPosition) - AVATAR_Y_POS_OFFSET; // TODO: Check later

        Avatar avatar = new Avatar(
                Vector2.of(avatarXPosition, avatarYPosition),
                inputListener,
                imageReader
        );
        gameObjects().addGameObject(avatar, Layer.DEFAULT);

        // Make the camera follow the avatar
        Vector2 distanceFromCenter = windowDimensions.mult(0.5f).add(
                Vector2.of(-avatarXPosition, -avatarYPosition)
        );
        setCamera(new Camera(avatar, distanceFromCenter, windowDimensions, windowDimensions));

        this.avatar = avatar;
    }

    /**
     * Creates and initializes the energy display UI element in the game.
     * The energy display dynamically updates to reflect the current energy
     * level of the avatar.
     */
    private void createEnergyDisplay() {
        TextRenderable energyTextRenderable = new TextRenderable(INITIAL_ENERGY_STRING);
        GameObject energyDisplay = new GameObject(
                ENERGY_DISPLAY_TOP_LEFT_CORNER, ENERGY_DISPLAY_DIMENSIONS, energyTextRenderable
        );
        gameObjects().addGameObject(energyDisplay, Layer.UI);

        energyDisplay.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);

        // Add a component to the display such that at update() the energy amount will be updated.
        energyDisplay.addComponent(
                deltaTime -> energyTextRenderable.setString(Math.round(avatar.getEnergy()) + PERCENT)
        );
    }

    /**
     * Creates and initializes the flora objects, such as trees and their associated components
     * (fruits and leaves) within the specified range of the game world.
     * <p></p>
     * Adds them to the appropriate game object layers.
     */
    private void createFlora() {
        Flora flora = new Flora(terrain::groundHeightAtX0, avatar::addEnergy, SECONDS_IN_A_DAY_CYCLE);
        // Create a map that maps trunks to its flora
        Map<GameObject, ArrayList<GameObject>> trees = flora.createInRange(0, (int) windowDimensions.x());
        // Add each trunk to the game.
        for (GameObject trunk : trees.keySet()) {
            gameObjects().addGameObject(trunk, Layer.STATIC_OBJECTS);
            // For each trunk, add its flora (fruits and foliage) to the game.
            for (GameObject obj : trees.get(trunk)) {
                if (obj.getTag().equals(Fruit.FRUIT_TAG)) { // If the object is a fruit
                    gameObjects().addGameObject(obj, Layer.DEFAULT);
                } else { // If the object is a leaf
                    gameObjects().addGameObject(obj, LEAF_LAYER);
                }
            }
        }
    }

    /**
     * Creates and initializes cloud game objects within the game world.
     * <p>
     *      The created clouds contribute to the atmospheric appearance of the game,
     *      enhancing visual realism and design aesthetics.
     * </p>
     */
    private void createCloud() {
        Cloud cloudCreator = new Cloud(this::addGameObject, this::removeGameObject);
        List<GameObject> cloud = cloudCreator.createInRange(0, (int) windowDimensions.x());
        for (GameObject block : cloud) {
            gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
        }
        avatar.addOnJumpComponent(cloudCreator.pourRain());
    }

    /**
     * Initializes the game objects.
     *
     * @see #createSky()
     * @see #createTerrain()
     * @see #createNight()
     * @see #createSunAndHalo()
     * @see #createAvatar(UserInputListener, ImageReader)
     * @see #createEnergyDisplay()
     * @see #createFlora()
     * @see #createCloud()
     */
    private void initGameObjects(UserInputListener inputListener, ImageReader imageReader) {
        createSky(); // create sky
        createTerrain(); // create terrain
        createNight(); // create night
        createSunAndHalo(); // create sun and halo
        createAvatar(inputListener, imageReader); // create avatar
        createEnergyDisplay(); // create energy display
        createFlora(); // create the flora of the game
        createCloud(); // create the cloud
    }

    private void addGameObject(GameObject gameObject, int layer) {
        gameObjects().addGameObject(gameObject, layer);
    }

    private void removeGameObject(GameObject gameObject, int layer) {
        gameObjects().removeGameObject(gameObject, layer);
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
        this.windowDimensions = windowController.getWindowDimensions();
        initGameObjects(inputListener, imageReader);
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
