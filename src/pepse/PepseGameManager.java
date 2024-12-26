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
import pepse.world.trees.Fruit;

import java.util.ArrayList;
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

    private static final int SECONDS_IN_A_DAY_CYCLE = 30;
    private static final int HALO_LAYER = -150; // Sun layer is -100, set halo in front of it
    private static final int LEAF_LAYER = -50;
    private static final int CLOUD_LAYER = -125;
    private static final float AVATAR_Y_POS_OFFSET = 100;
    private static final float AVATAR_X_POS_RATIO = 2;
    private static final String INITIAL_ENERGY_STRING = "100%";
    private static final String PERCENT = "%";
    private static final Vector2 ENERGY_DISPLAY_TOP_LEFT_CORNER = Vector2.of(10, 20);
    private static final Vector2 ENERGY_DISPLAY_DIMENSIONS = Vector2.of(50, 50);
    private static final float OFFSET = 150;

    private int seed;
    private float outOfWindowThreshold;
    private Terrain terrain;
    private Flora flora;
    private Avatar avatar;
    private Vector2 windowDimensions;
    private List<Integer> layers;
    private Component rainPourComponent;

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
     * Creates the terrain.
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
        float avatarYPosition = terrain.groundHeightAt(avatarXPosition) - AVATAR_Y_POS_OFFSET; // TODO: Check later

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
     * <p>
     *      Adds them to the appropriate game object layers.
     * </p>
     */
    private void createFlora(int rangeStart, int rangeEnd) {
        // Create a map that maps trunks to its flora
        Map<GameObject, ArrayList<GameObject>> trees = flora.createInRange(rangeStart, rangeEnd);
        // Add each trunk to the game.
        for (GameObject trunk : trees.keySet()) {
            if (addIfLocationIsNotTaken(trunk, Layer.STATIC_OBJECTS)) {
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
        this.rainPourComponent = cloudCreator.pourRain();
        List<GameObject> cloud = cloudCreator.createInRange(0, (int) windowDimensions.x());
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
     * @see #createSky()
     * @see #createTerrain(int, int)
     * @see #createNight()
     * @see #createSunAndHalo()
     * @see #createAvatar(UserInputListener, ImageReader)
     * @see #createEnergyDisplay()
     * @see #createFlora(int, int)
     * @see #createCloud()
     */
    private void initGameObjects(UserInputListener inputListener, ImageReader imageReader) {
        this.terrain = new Terrain(windowDimensions, seed);
        createSky(); // create sky
        createTerrain(0, (int) windowDimensions.x()); // create terrain
        createNight(); // create night
        createSunAndHalo(); // create sun and halo
        createAvatar(inputListener, imageReader); // create avatar
        this.flora = new Flora(terrain::groundHeightAt, avatar::addEnergy, SECONDS_IN_A_DAY_CYCLE, seed);
        createFlora(0, (int) windowDimensions.x());
        createEnergyDisplay(); // create energy display
        createCloud(); // create the cloud
    }

    private void createLayerList() {
        this.layers = new ArrayList<>();
        layers.add(Layer.STATIC_OBJECTS);
        layers.add(LEAF_LAYER);
        layers.add(Layer.DEFAULT);
    }

    private boolean isOutOfScreen(GameObject gameObject) {
        return Math.abs(
                gameObject.getCenter().x() - avatar.getCenter().x()
        ) > outOfWindowThreshold;
    }

    private void handleOutOfScreenObjects() {
        for (GameObject gameObject : gameObjects()) {
            if (isOutOfScreen(gameObject)) {
                for (int layer : layers) {
                    if (gameObjects().removeGameObject(gameObject, layer)) {
                        break;
                    }
                }
            }
        }
    }

    private void createObjectsInScreen() {
        int avatarX = (int) avatar.getCenter().x();
        int windowWidth = (int) windowDimensions.x();
        createTerrain(avatarX - windowWidth, avatarX + windowWidth);
        createFlora(avatarX - windowWidth, avatarX + windowWidth);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
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
        this.outOfWindowThreshold = windowDimensions.x() / AVATAR_X_POS_RATIO + OFFSET;
        this.seed = new Random().nextInt();
        createLayerList();
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
