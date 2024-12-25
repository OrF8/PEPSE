package pepse.world;

import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

/**
 * The avatar class represents the player's character in the game.
 */
public class Avatar extends GameObject {

    private static final int MAX_ENERGY_VALUE = 100;
    private static final int MIN_ENERGY_VALUE = 0; // TODO: Do we need min value?
    private static final int JUMP_ENERGY_CONSUMPTION = 10;
    private static final int IDLE_ENERGY_REGENERATION_RATE = 1;
    private static final int AVATAR_SIZE = 50;
    private static final float VELOCITY_X = 400;
    private static final float VELOCITY_Y = -650;
    private static final float GRAVITY = 600;
    private static final float TIME_BETWEEN_JUMP_IDLE_CLIPS = 0.35f;
    private static final float TIME_BETWEEN_RUN_CLIPS = 0.15f;
    private static final double HORIZONTAL_MOVEMENT_ENERGY_CONSUMPTION = 0.5;
    private static final String AVATAR_TAG = "avatar";

    // Paths to animations
    private static final String IDLE_0_ANIMATION_PATH = "assets\\idle_0.png";
    private static final String IDLE_1_ANIMATION_PATH = "assets\\idle_1.png";
    private static final String IDLE_2_ANIMATION_PATH = "assets\\idle_2.png";
    private static final String IDLE_3_ANIMATION_PATH = "assets\\idle_3.png";
    private static final String JUMP_0_ANIMATION_PATH = "assets\\jump_0.png";
    private static final String JUMP_1_ANIMATION_PATH = "assets\\jump_1.png";
    private static final String JUMP_2_ANIMATION_PATH = "assets\\jump_2.png";
    private static final String JUMP_3_ANIMATION_PATH = "assets\\jump_3.png";
    private static final String RUN_0_ANIMATION_PATH = "assets\\run_0.png";
    private static final String RUN_1_ANIMATION_PATH = "assets\\run_1.png";
    private static final String RUN_2_ANIMATION_PATH = "assets\\run_2.png";
    private static final String RUN_3_ANIMATION_PATH = "assets\\run_3.png";
    private static final String RUN_4_ANIMATION_PATH = "assets\\run_4.png";
    private static final String RUN_5_ANIMATION_PATH = "assets\\run_5.png";

    // private fields
    private final UserInputListener inputListener;
    private double energy = MAX_ENERGY_VALUE;
    private AnimationRenderable idleAnimationRenderable;
    private AnimationRenderable runAnimationRenderable;
    private AnimationRenderable jumpAnimationRenderable;

    /**
     * Constructor for the Avatar class.
     * @param topLeftCorner The top left corner of the avatar.
     * @param inputListener The input listener for the avatar.
     * @param imageReader The image reader for the avatar.
     */
    public Avatar(Vector2 topLeftCorner, UserInputListener inputListener, ImageReader imageReader) {
        super(topLeftCorner, Vector2.ONES.mult(AVATAR_SIZE), null);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        this.inputListener = inputListener;
        this.setTag(AVATAR_TAG);

        createAnimationRenderables(imageReader); // create animation renderables for all 3 states
        this.renderer().setRenderable(idleAnimationRenderable); // set starting animation to idle
    }

    /**
     * Creates the idle animation for the avatar using a series of images.
     * This method reads image files for each frame of the idle animation,
     * combines them into an AnimationRenderable object, and sets it as the
     * avatar's idle animation.
     *
     * @param imageReader The ImageReader instance used to load images for the idle animation.
     */
    private void createIdleAnimation(ImageReader imageReader) {
        Renderable[] clips = {
                imageReader.readImage(IDLE_0_ANIMATION_PATH, false),
                imageReader.readImage(IDLE_1_ANIMATION_PATH, false),
                imageReader.readImage(IDLE_2_ANIMATION_PATH, false),
                imageReader.readImage(IDLE_3_ANIMATION_PATH, false)
        };
        this.idleAnimationRenderable = new AnimationRenderable(clips, TIME_BETWEEN_RUN_CLIPS);
    }

    /**
     * Creates the jump animation for the avatar using a series of images.
     * This method reads image files for each frame of the jump animation,
     * combines them into an AnimationRenderable object, and sets it as the
     * avatar's jump animation.
     *
     * @param imageReader The ImageReader instance used to load images for the jump animation.
     */
    private void createJumpAnimation(ImageReader imageReader) {
        Renderable[] clips = {
                imageReader.readImage(JUMP_0_ANIMATION_PATH, false),
                imageReader.readImage(JUMP_1_ANIMATION_PATH, false),
                imageReader.readImage(JUMP_2_ANIMATION_PATH, false),
                imageReader.readImage(JUMP_3_ANIMATION_PATH, false)
        };
        this.jumpAnimationRenderable = new AnimationRenderable(clips, TIME_BETWEEN_JUMP_IDLE_CLIPS);
    }

    /**
     * Creates the run animation for the avatar using a series of images.
     * This method reads image files for each frame of the run animation,
     * combines them into an AnimationRenderable object, and sets it as the
     * avatar's run animation.
     *
     * @param imageReader The ImageReader instance used to load images for the run animation.
     */
    private void createRunAnimation(ImageReader imageReader) {
        Renderable[] clips = {
                imageReader.readImage(RUN_0_ANIMATION_PATH, false),
                imageReader.readImage(RUN_1_ANIMATION_PATH, false),
                imageReader.readImage(RUN_2_ANIMATION_PATH, false),
                imageReader.readImage(RUN_3_ANIMATION_PATH, false),
                imageReader.readImage(RUN_4_ANIMATION_PATH, false),
                imageReader.readImage(RUN_5_ANIMATION_PATH, false)
        };
        this.runAnimationRenderable = new AnimationRenderable(clips, TIME_BETWEEN_JUMP_IDLE_CLIPS);
    }

    /**
     * Creates the renderables for avatar animations, including idle, jump, and run animations.
     *
     * @param imageReader The ImageReader instance used to load images for the animations.
     */
    private void createAnimationRenderables(ImageReader imageReader) {
        createIdleAnimation(imageReader);
        createJumpAnimation(imageReader);
        createRunAnimation(imageReader);
    }



    /**
     * Returns true if the avatar is idle, false otherwise.
     * @param isPressingLeft The boolean value of whether the left arrow key is pressed.
     * @param isPressingRight The boolean value of whether the right arrow key is pressed.
     * @param isPressingJump The boolean value of whether the space key is pressed.
     * @return True if the avatar is idle, false otherwise.
     */
    private boolean isIdle(boolean isPressingLeft, boolean isPressingRight, boolean isPressingJump) {
        return this.getVelocity().isZero() && !isPressingLeft && !isPressingRight && !isPressingJump;
    }

    /**
     * Handles the horizontal movement of the avatar.
     * @param isPressingLeft The boolean value of whether the left arrow key is pressed.
     * @param isPressingRight The boolean value of whether the right arrow key is pressed.
     * @param xVel The current x velocity of the avatar.
     * @return The updated x velocity of the avatar.
     */
    private float handleHorizontalMovement(boolean isPressingLeft, boolean isPressingRight, float xVel) {
        if (isPressingLeft && !isPressingRight && energy >= HORIZONTAL_MOVEMENT_ENERGY_CONSUMPTION) {
            xVel -= VELOCITY_X;
            energy -= HORIZONTAL_MOVEMENT_ENERGY_CONSUMPTION;
            this.renderer().setIsFlippedHorizontally(true);
            this.renderer().setRenderable(runAnimationRenderable);
        }
        if (isPressingRight && !isPressingLeft && energy >= HORIZONTAL_MOVEMENT_ENERGY_CONSUMPTION) {
            xVel += VELOCITY_X;
            energy -= HORIZONTAL_MOVEMENT_ENERGY_CONSUMPTION;
            this.renderer().setIsFlippedHorizontally(false);
            this.renderer().setRenderable(runAnimationRenderable);
        }
        return xVel;
    }

    /**
     * Handles the avatar's jump.
     * @param isPressingJump The boolean value of whether the space key is pressed.
     */
    private void handleJump(boolean isPressingJump) {
        if (isPressingJump && getVelocity().y() == 0 && energy >= JUMP_ENERGY_CONSUMPTION) {
            transform().setVelocityY(VELOCITY_Y);
            energy -= JUMP_ENERGY_CONSUMPTION;
            this.renderer().setRenderable(jumpAnimationRenderable);
        }
    }

    /**
     * Handles the regeneration of the avatar's energy.
     * @param isIdle The boolean value of whether the avatar is idle.
     */
    private void handleEnergyRegeneration(boolean isIdle) {
        if (isIdle && energy <= MAX_ENERGY_VALUE - IDLE_ENERGY_REGENERATION_RATE) {
            energy += IDLE_ENERGY_REGENERATION_RATE;
            this.renderer().setRenderable(idleAnimationRenderable);
        }
    }

    /**
     * Updates the avatar's position and energy value.
     * <p>
     *     The avatar's position is updated based on the user's input.
     *     The avatar's energy value is updated based on the user's input.
     *     <li>
     *         If the user is pressing the left or right arrow keys and the energy value is sufficient,
     *         the avatar moves in the corresponding direction and consumes 0.5 a point of energy.
     *     </li>
     *     <li>
     *         If the user is pressing both keys, the avatar does not move and does not consume energy.
     *     </li>
     *     <li>
     *         If the user is pressing the space key and the energy value is sufficient,
     *         the avatar jumps and consumes 10 points of energy.
     *         The avatar can only jump if it is on the ground.
     *      </li>
     *      <li>
     *          If the user is not pressing any keys and the energy value is less than 100,
     *          the energy value increases by 1 point.
     *      </li>
     * </p>
     * @param deltaTime The time since the last update.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = 0;

        boolean isPressingLeft = inputListener.isKeyPressed(KeyEvent.VK_LEFT);
        boolean isPressingRight = inputListener.isKeyPressed(KeyEvent.VK_RIGHT);
        boolean isPressingJump = inputListener.isKeyPressed(KeyEvent.VK_SPACE);
        boolean isIdle = isIdle(isPressingLeft, isPressingRight, isPressingJump);

        xVel = handleHorizontalMovement(isPressingLeft, isPressingRight, xVel);
        transform().setVelocityX(xVel);

        handleJump(isPressingJump);

        handleEnergyRegeneration(isIdle);
    }

    /**
     * Returns the energy value of the avatar.
     * @return The energy value of the avatar.
     */
    public double getEnergy() {
        return energy;
    }

    /**
     * Add a given amount of energy to the Avatar's energy, as long as it is within the max boundary.
     * @param energyAmountToAdd The amount of energy to add.
     */
    public void addEnergy(double energyAmountToAdd) {
        this.energy = Math.min(MAX_ENERGY_VALUE, this.energy + energyAmountToAdd);
    }
}
