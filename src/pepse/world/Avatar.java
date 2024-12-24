package pepse.world;

import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

/** TODO: Add docs */
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

    private final UserInputListener inputListener;
    private double energy = MAX_ENERGY_VALUE;

    /**TODO: Docs */
    public Avatar(Vector2 topLeftCorner, UserInputListener inputListener, ImageReader imageReader) {
        super(
                topLeftCorner,
                Vector2.ONES.mult(50),
                imageReader.readImage("assets\\idle_0.png", false)
        );
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        this.inputListener = inputListener;
        this.setTag(AVATAR_TAG);
    }

    /**TODO: Docs */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = 0;

        boolean isPressingLeft = inputListener.isKeyPressed(KeyEvent.VK_LEFT);
        boolean isPressingRight = inputListener.isKeyPressed(KeyEvent.VK_RIGHT);
        boolean isPressingJump = inputListener.isKeyPressed(KeyEvent.VK_SPACE);
        boolean isIdle = this.getVelocity().isZero() && !isPressingLeft &&
                         !isPressingRight && !isPressingJump;
        /* TODO: can we make the logic behind energy consumption better here? */

        // Check if user is trying to go left and energy value is sufficient
        if(isPressingLeft && energy >= HORIZONTAL_MOVEMENT_ENERGY_CONSUMPTION) {
            xVel -= VELOCITY_X;
            if (!isPressingRight) { // if not trying to move in the other direction, consume energy
                energy -= HORIZONTAL_MOVEMENT_ENERGY_CONSUMPTION;
            }
        }
        // Check if user is trying to go right and energy value is sufficient
        if(isPressingRight && energy >= HORIZONTAL_MOVEMENT_ENERGY_CONSUMPTION) {
            xVel += VELOCITY_X;
            if (!isPressingLeft) { // if not trying to move in the other direction, consume energy
                energy -= HORIZONTAL_MOVEMENT_ENERGY_CONSUMPTION;
            }
        }
        // Check if user is trying to jump and energy value is sufficient
        transform().setVelocityX(xVel);
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
