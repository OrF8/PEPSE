package pepse.world;

import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

/**
 * The avatar class represents the player's character in the game.
 */
public class Avatar extends GameObject {
    private static final float VELOCITY_X = 400;
    private static final float VELOCITY_Y = -650;
    private static final float GRAVITY = 600;
    private static final int MAX_ENERGY_VALUE = 100;
    private static final int MIN_ENERGY_VALUE = 0; // TODO: Do we need min value?
    private static final double HORIZONTAL_MOVEMENT_ENERGY_CONSUMPTION = 0.5;
    private static final int JUMP_ENERGY_CONSUMPTION = 10;
    private static final int IDLE_ENERGY_REGENERATION_RATE = 1;
    private static final String AVATAR_TAG = "avatar";
    private static final int AVATAR_SIZE = 50;

    private final UserInputListener inputListener;
    private double energy = MAX_ENERGY_VALUE;

    /**
     * Constructor for the Avatar class.
     * @param topLeftCorner The top left corner of the avatar.
     * @param inputListener The input listener for the avatar.
     * @param imageReader The image reader for the avatar.
     */
    public Avatar(Vector2 topLeftCorner, UserInputListener inputListener, ImageReader imageReader) {
        super(
                topLeftCorner,
                Vector2.ONES.mult(AVATAR_SIZE),
                imageReader.readImage("assets\\idle_0.png", false)
        );
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        this.inputListener = inputListener;
        this.setTag(AVATAR_TAG);
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
        boolean isIdle = this.getVelocity().isZero() && !isPressingLeft &&
                         !isPressingRight && !isPressingJump;
        /* TODO: can we make the logic of energy consumption better here? */

        // Check if user is trying to go left and energy value is sufficient
        if(isPressingLeft && energy >= HORIZONTAL_MOVEMENT_ENERGY_CONSUMPTION) {
            xVel -= VELOCITY_X;
            if (!isPressingRight) { // if not trying to move in the other direction, consume energy
                energy -= HORIZONTAL_MOVEMENT_ENERGY_CONSUMPTION;
            }
        }
        // Check if user is trying to go right and energy value is enough
        if(isPressingRight && energy >= HORIZONTAL_MOVEMENT_ENERGY_CONSUMPTION) {
            xVel += VELOCITY_X;
            if (!isPressingLeft) { // if not trying to move in the other direction, consume energy
                energy -= HORIZONTAL_MOVEMENT_ENERGY_CONSUMPTION;
            }
        }
        transform().setVelocityX(xVel);
        // Check if user is trying to jump and energy value is sufficient
        if(isPressingJump && getVelocity().y() == 0 && energy >= JUMP_ENERGY_CONSUMPTION) {
            transform().setVelocityY(VELOCITY_Y);
            energy -= JUMP_ENERGY_CONSUMPTION;
        }
        // Handle energy regeneration when not moving
        if(isIdle && energy <= MAX_ENERGY_VALUE - IDLE_ENERGY_REGENERATION_RATE) {
            energy += IDLE_ENERGY_REGENERATION_RATE;
        }

        System.out.println(Math.round(energy)); // TODO: get rid of that when there's numeric value on screen
    }
}
