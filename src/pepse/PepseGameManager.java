package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.Component;
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

import java.util.List;
import java.util.Map;
import java.util.Random;

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

    // Private constants
    private static final int SECONDS_IN_A_DAY_CYCLE = 30; /* Number of seconds in a day-night cycle */
    private static final int HALO_LAYER = -150; /* The layer of the sun halo, which is behind the sun */
    private static final int LEAF_LAYER = -50; /* The layer of the leaves */
    private static final int CLOUD_LAYER = -125; /* The layer of the clouds */
    private static final float AVATAR_Y_POS_OFFSET = 100; /* The offset of the avatar from the ground */
    private static final float AVATAR_X_POS_RATIO = 2; /* The ratio of the avatar's x position */
    private static final float OFFSET = 150; /* The offset for the out of window threshold */
    private static final String INITIAL_ENERGY_STRING = "100%"; /* The initial energy string */
    private static final String PERCENT = "%"; /* The percent sign */
    private static final String TITLE = "Ghosty PEPSENautics - The Game"; /* The title of the game :) */
    /* The top left corner of the energy display */
    private static final Vector2 ENERGY_DISPLAY_TOP_LEFT_CORNER = Vector2.of(10, 20);
    /* The dimensions of the energy display */
    private static final Vector2 ENERGY_DISPLAY_DIMENSIONS = Vector2.of(50, 50);

    // Private fields
    private int seed; /* The seed for the random number generator */
    private float outOfWindowThreshold; /* The threshold for objects out of the window */
    private Terrain terrain; /* The terrain of the game */
    private Flora flora; /* The flora of the game */
    private Avatar avatar; /* The avatar of the game */
    private Vector2 windowDimensions; /* The dimensions of the game window */
    private List<Integer> layers; /* The layers that objects should be deleted from */
    private Component rainPourComponent; /* The rain pour component */

    /**
     * Default constructor for the PepseGameManager.
     * Initializes a new instance of the game manager, managing the creation and maintenance of
     * various game elements.
     *
     * @param title The title of the game.
     */
    public PepseGameManager(String title) {
        super(title);
    }

    /**
     * Creates the sky.
     */
    private void createSky() {
        GameObject sky = Sky.create(windowDimensions);
        gameObjects().addGameObject(sky, Layer.BACKGROUND);
    }

    /**
     * Adds a game object to the game if the location is not taken.
     * If the location is taken, the object is not added.
     * @param gameObject The game object to add.
     * @param layer The layer to add the object to.
     * @return {@code true} if the object was added, {@code false} otherwise.
     */
    private boolean addIfLocationIsNotTaken(GameObject gameObject, int layer) {
        for (GameObject obj : gameObjects()) {
            if (obj.getCenter().equals(gameObject.getCenter())) {
                return false;
            }
        }
        gameObjects().addGameObject(gameObject, layer);
        return true;
    }

    /**
     * Creates the terrain. The terrain will be created in the range [rangeStart, rangeEnd].
     *
     * @param rangeStart The start of the range to create the terrain.
     * @param rangeEnd The end of the range to create the terrain.
     */
    private void createTerrain(int rangeStart, int rangeEnd) {
        // Create terrain made of blocks based on the method createInRange
        List<GameObject> blockList = terrain.createInRange(rangeStart, rangeEnd);
        // Add the blocks that make up the terrain to the static layer.
        for (GameObject block : blockList) {
            addIfLocationIsNotTaken(block, Layer.STATIC_OBJECTS);
        }
    }

    /**
     * Creates the night.
     */
    private void createNight() {
        GameObject night = Night.create(windowDimensions, SECONDS_IN_A_DAY_CYCLE);
        gameObjects().addGameObject(night, Layer.FOREGROUND);
    }

    /**
     * Creates the sun and its halo.
     */
    private void createSunAndHalo() {
        // Create the sun
        GameObject sun = Sun.create(windowDimensions, SECONDS_IN_A_DAY_CYCLE, terrain::groundHeightAt);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);
        // Create its halo
        GameObject sunHalo = SunHalo.create(sun);
        gameObjects().addGameObject(sunHalo, HALO_LAYER);
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
        float avatarYPosition = terrain.groundHeightAt(avatarXPosition) - AVATAR_Y_POS_OFFSET;

        Avatar avatar = new Avatar(
                Vector2.of(avatarXPosition, avatarYPosition),
                inputListener,
                imageReader
        );
        gameObjects().addGameObject(avatar, Layer.DEFAULT);

        // Make the camera follow the avatar
        Vector2 distanceFromCenter = windowDimensions.mult(1 / AVATAR_X_POS_RATIO).add(
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
     * <p>
     *      Adds them to the appropriate game object layers.
     * </p>
     * The flora will be created in the range [rangeStart, rangeEnd].
     *
     * @param rangeStart The start of the range to create the flora.
     * @param rangeEnd The end of the range to create the flora.
     */
    private void createFlora(int rangeStart, int rangeEnd) {
        // Create a map that maps trunks to its flora
        Map<GameObject, List<GameObject>> trees = flora.createInRange(rangeStart, rangeEnd);
        // Add each trunk to the game.
        for (GameObject trunk : trees.keySet()) {
            if (addIfLocationIsNotTaken(trunk, Layer.STATIC_OBJECTS)) {
                // For each trunk, add its flora (fruits and foliage) to the game.
                for (GameObject obj : trees.get(trunk)) {
                    if (obj.getTag().equals(Flora.FRUIT_TAG)) { // If the object is a fruit
                        addIfLocationIsNotTaken(obj, Layer.DEFAULT);
                    } else { // If the object is a leaf
                        addIfLocationIsNotTaken(obj, LEAF_LAYER);
                    }
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
        Cloud cloudCreator = new Cloud(gameObjects()::addGameObject, gameObjects()::removeGameObject);
        // Create the jump component for the avatar to activate upon jumping
        this.rainPourComponent = cloudCreator.pourRain();
        // List of blocks to create a cloud
        List<GameObject> cloud = cloudCreator.createInRange(0, (int) windowDimensions.x());
        /* For each block, add to the game and remove the jump component from the avatar if the cloud left
        the screen */
        for (GameObject cloudBlock : cloud) {
            gameObjects().addGameObject(cloudBlock, CLOUD_LAYER);
            cloudBlock.addComponent(
                    delta -> {
                        if (cloudCreator.getMostLeftX() > windowDimensions.x()) {
                            gameObjects().removeGameObject(cloudBlock, CLOUD_LAYER);
                            avatar.removeOnJumpComponent(rainPourComponent);
                        }
                    }
            );
        }
        avatar.addOnJumpComponent(rainPourComponent);
    }

    /**
     * Initializes the game objects.
     *
     * @param inputListener The input listener to use for getting user input.
     * @param imageReader The image reader to use for loading images.
     *
     * @see #createSky()
     * @see #createNight()
     * @see #createSunAndHalo()
     * @see #createAvatar(UserInputListener, ImageReader)
     * @see #createEnergyDisplay()
     * @see #createCloud()
     */
    private void initGameObjects(UserInputListener inputListener, ImageReader imageReader) {
        this.terrain = new Terrain(windowDimensions, seed); // create terrain
        createSky(); // create sky
        createNight(); // create night
        createSunAndHalo(); // create sun and halo
        createAvatar(inputListener, imageReader); // create avatar
        // Create flora
        this.flora = new Flora(terrain::groundHeightAt, avatar::addEnergy, SECONDS_IN_A_DAY_CYCLE, seed);
        createEnergyDisplay(); // create energy display
        createCloud(); // create the cloud
    }

    /**
     * Checks if a game object is out of the screen.
     * @param gameObject The game object to check.
     * @return {@code true} if the game object is out of the screen, {@code false} otherwise.
     */
    private boolean isOutOfScreen(GameObject gameObject) {
        return Math.abs(gameObject.getCenter().x() - avatar.getCenter().x()) > outOfWindowThreshold;
    }

    /**
     * Handles out of screen objects.
     * If an object is out of the screen, it is removed from the game.
     * The object is removed from the first layer it is found in.
     */
    private void handleOutOfScreenObjects() {
        for (GameObject gameObject : gameObjects()) {
            if (isOutOfScreen(gameObject)) {
                // Try to remove the object from each layer (will do nothing if the layer is wrong)
                for (int layer : layers) {
                    if (gameObjects().removeGameObject(gameObject, layer)) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Creates objects in the screen.
     * Objects are created in the screen based on the avatar's position.
     * The objects are created in the range [avatarX - creationField, avatarX + creationField].
     */
    private void createObjectsInScreen() {
        int avatarX = (int) avatar.getCenter().x();
        int creationField = (int) (windowDimensions.x() / AVATAR_X_POS_RATIO + OFFSET);
        // Create terrain and flora in the given range
        createTerrain(avatarX - creationField, avatarX + creationField);
        createFlora(avatarX - creationField, avatarX + creationField);
    }

    /**
     * Updates the game.
     * The method is responsible for updating the game state and handling game logic.
     * It is called once per frame.
     * The method updates the game objects, handles out of screen objects, and creates objects in the screen.
     * @param deltaTime The time elapsed since the last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // Handle deletion and creation of objects in the game as the avatar moves
        handleOutOfScreenObjects();
        createObjectsInScreen();
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
        this.outOfWindowThreshold = windowDimensions.x();
        this.seed = new Random().nextInt();
        this.layers = List.of(Layer.STATIC_OBJECTS, LEAF_LAYER, Layer.DEFAULT);
        initGameObjects(inputListener, imageReader);
    }

    /**
     * The main method to start the game.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        new PepseGameManager(TITLE).run();
    }
}
