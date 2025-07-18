package pepse.world;

import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import danogl.components.Component;

import pepse.util.ColorSupplier;
import pepse.util.LocationCalculator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

/**
 * Represents a Cloud game object composed of multiple blocks.
 * The cloud moves horizontally and can generate raindrops.
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class Cloud {

    // Private Constants
    private static final double RAIN_CREATION_PROBABILITY = 0.4; /* chance of raindrop creation */
    private static final float BASE_CLOUD_HEIGHT = 100; /* The base height of the cloud */
    private static final int CLOUD_X_MOVEMENT = 3; /* The horizontal movement speed of the cloud */
    private static final String CLOUD_TAG = "cloud"; /* The tag for cloud GameObjects */
    /* The base color of the cloud */
    private static final Color BASE_CLOUD_COLOR = new Color(255, 255, 255);

    // 2 variations of cloud shapes
    private static final List<List<Boolean>> blockPositionsCloudOne = List.of( /* Cloud shape 1 - (4x11) */
            List.of(false, true, true, false, false, false, true, true, true, true, false),
            List.of(true, true, true, true, false, true, true, true, true, true, true),
            List.of(true, true, true, true, true, true, true, true, true, true, true),
            List.of(false, true, true, true, true, true, true, true, true, true, false)
    );
    private static final List<List<Boolean>> blockPositionsCloudTwo = List.of( /* Cloud shape 2 - (3x8) */
            List.of(false, false, false, true, true, true, false, false),
            List.of(false, true, true, true, true, true, true, false),
            List.of(true, true, true, true, true, true, true, true)
    );

    // Private final field
    /* A BiConsumer function to add a GameObject to the game at a specified layer */
    private final BiConsumer<GameObject, Integer> addToGame;
    /* A BiConsumer function to remove a GameObject from the game at a specified layer */
    private final BiConsumer<GameObject, Integer> removeFromGame;

    // Private fields
    private List<GameObject> cloud; /* The list of GameObject instances representing the cloud */

    /**
     * Constructs a Cloud instance, which manages cloud-related behavior in the game.
     *
     * @param addToGame A BiConsumer function for adding a GameObject to the game at a specified layer.
     * @param removeFromGame A BiConsumer function for removing
     *                       a GameObject from the game at a specified layer.
     */
    public Cloud(
            BiConsumer<GameObject, Integer> addToGame, BiConsumer<GameObject, Integer> removeFromGame
    ) {
        this.addToGame = addToGame;
        this.removeFromGame = removeFromGame;
    }

    /**
     * Creates a block object at a specified position, configures its properties,
     * and sets up a horizontal movement transition for the block within a given range.
     *
     * @param position The initial position of the block.
     * @param startingX The starting x-coordinate for the horizontal movement range.
     * @param maxX The maximum x-coordinate for the horizontal movement range.
     * @return The newly created block GameObject.
     */
    private GameObject createCloudBlock(Vector2 position, float startingX, float maxX) {
        Block block = new Block(
                position,
                new RectangleRenderable(ColorSupplier.approximateMonoColor(BASE_CLOUD_COLOR))
        );
        block.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        block.setTag(CLOUD_TAG);

        // Handle the movement of a cloud block across the screen
        new Transition<>(
                block,
                _ -> {
                    float newX = block.getCenter().x() + CLOUD_X_MOVEMENT;
                    if (newX > maxX) { // Reset the block's position if it exceeds the maximum x-coordinate
                        newX = startingX;
                    }
                    block.setCenter(Vector2.of(newX, block.getCenter().y()));
                },
                startingX,
                maxX,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                /* Cloud will not be deleted if it exited the screen, so we let it move infinitely as long
                as it didn't and the avatar is following it */
                Float.POSITIVE_INFINITY,
                Transition.TransitionType.TRANSITION_LOOP,
                null
        );

        return block;
    }

    /**
     * Creates a cloud by iterating through the provided block positions
     * and generating GameObject instances for each block.
     *
     * @param blockPositions A list of lists representing the positions of blocks in the cloud.
     * @param startingX The starting X-coordinate for the cloud's position.
     * @param maxX The maximum X-coordinate the cloud can be in.
     */
    private void createCloud(List<List<Boolean>> blockPositions, int startingX, int maxX) {
        // Create cloud blocks according to the blockPositions list
        for (int row = 0; row < blockPositions.size(); row++) {
            for (int col = 0; col < blockPositions.get(row).size(); col++) {
                if (blockPositions.get(row).get(col)) { // If the position is true, create a block
                    // Create the cloud block and add it to the game
                    GameObject cloudBlock = createCloudBlock(
                            Vector2.of(
                                    startingX + col * Block.SIZE,
                                    BASE_CLOUD_HEIGHT + row * Block.SIZE
                            ),
                            startingX, maxX
                    );
                    cloud.add(cloudBlock);
                }
            }
        }
    }

    /**
     * Creates and returns a list of GameObject instances representing
     * a cloud within the specified X-coordinate range.
     * <p>
     *      The method calculates the appropriate starting and ending positions
     *      based on block sizes and arranges the blocks to form a cloud-like structure.
     * </p>
     *
     * @param minX The minimum X-coordinate of the range in which the cloud is to be created.
     * @param maxX The maximum X-coordinate the cloud can be in.
     * @return A list of GameObject instances representing the blocks that form the created cloud.
     */
    public List<GameObject> createInRange(int minX, int maxX) {
        boolean cloudOne = new Random().nextBoolean();
        // Decide which cloud shape to create
        List<List<Boolean>> blockPositions = cloudOne ? blockPositionsCloudOne : blockPositionsCloudTwo;
        int cloudWidth = blockPositions.getFirst().size() * Block.SIZE;
        // Set up a starting and ending X position for the cloud
        int startingX = LocationCalculator.getClosestMultToBlockSize(minX) - cloudWidth;
        maxX = LocationCalculator.getClosestMultToBlockSize(maxX) + cloudWidth;

        this.cloud = new ArrayList<>();

        createCloud(blockPositions, startingX, maxX);

        return cloud;
    }

    /**
     * Creates rain droplets from cloud game objects based on a probabilistic chance.
     * This method iterates through each block in the cloud and uses a random generator
     * to decide whether to create a new raindrop originating from the block's center.
     *
     * @param deltaTime The time interval since the last invocation of this method, used
     *                  for time-based calculations (currently unused in this implementation).
     */
    private void createRainDrops(float deltaTime) {
        Random random = new Random();
        // For each cloud block, decide if it creates a raindrop based on the given probability
        for (GameObject block : cloud) {
            if (random.nextDouble() < RAIN_CREATION_PROBABILITY) {
                new RainDrop(block.getCenter(), addToGame, removeFromGame);
            }
        }
    }

    /**
     * Initiates the rain creation process for the cloud by returning
     * a component function that triggers the creation of raindrops.
     * This method facilitates linking the rain creation logic
     * to other components that may invoke it.
     *
     * @return A {@link Component} that represents the rain creation process
     *         for the cloud.
     */
    public Component pourRain() {
        return this::createRainDrops;
    }

    /**
     * Represents a raindrop in the game, responsible for simulating falling rain.
     * <p>
     *      A Raindrop GameObject is created with a defined size, color, and gravitational acceleration.
     * </p>
     * <p>
     *     Upon creation, the raindrop gradually fades out and is removed
     *     from the game after a fixed duration.
     * </p>
     * <p>
     *      The Raindrop utilizes the {@link Transition} mechanism to handle the fading of its opacity
     *      over time and ensures that once it becomes fully transparent, it is removed from the game.
     * </p>
     *
     * @author Noam Kimhi
     * @author Or Forshmit
     */
    private static class RainDrop extends GameObject {

        // Private constants
        private static final float SIZE = Block.SIZE / 3f; /* The size of the raindrop */
        private static final float GRAVITY = 300; /* The gravitational acceleration of the raindrop */
        private static final float RAINDROP_FALL_DURATION = 2; /* The duration of the raindrop's fall */
        private static final float STARTING_OPACITY = 1; /* The starting opacity of the raindrop */
        private static final float ENDING_OPACITY = 0; /* The ending opacity of the raindrop */
        private static final String RAIN_DROP_TAG = "raindrop"; /* The tag for raindrop GameObjects */
        /* The color of the raindrop */
        private static final Color RAIN_COLOR = new Color(4, 137, 241);

        /**
         * Constructs a new RainDrop object representing a falling and fading raindrop in the game.
         * The raindrop moves with gravitational acceleration and becomes gradually transparent over time
         * before being removed from the game.
         *
         * @param topLeftCorner The top-left corner position of the raindrop.
         * @param addToGame A BiConsumer function to add the raindrop to the game,
         *                  associated with a specific layer.
         * @param removeFromGame A BiConsumer function to remove the raindrop from the game,
         *                       associated with a specific layer.
         */
        RainDrop(
                Vector2 topLeftCorner,
                BiConsumer<GameObject, Integer> addToGame,
                BiConsumer<GameObject, Integer> removeFromGame
        ) {
            super(
                    topLeftCorner,
                    Vector2.ONES.mult(SIZE),
                    new RectangleRenderable(RAIN_COLOR)
            );

            transform().setAccelerationY(GRAVITY); // Set raindrop's gravity
            this.setTag(RAIN_DROP_TAG); // Set raindrop's tag
            addToGame.accept(this, Layer.BACKGROUND); // Add the raindrop to the game
            this.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);

            // Handle the change in opaqueness. Upon reaching final value, remove from the game
            new Transition<>(
                    this,
                    this.renderer()::setOpaqueness,
                    STARTING_OPACITY,
                    ENDING_OPACITY,
                    Transition.LINEAR_INTERPOLATOR_FLOAT,
                    RAINDROP_FALL_DURATION,
                    Transition.TransitionType.TRANSITION_ONCE,
                    () -> removeFromGame.accept(this, Layer.BACKGROUND)
            );
        }
    }

}
