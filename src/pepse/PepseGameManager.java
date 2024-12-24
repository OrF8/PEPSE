package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import pepse.world.Sky;

/** TODO: Docs */
public class PepseGameManager extends GameManager {

    /** TODO: Docs */
    @Override
    public void initializeGame(
            ImageReader imageReader,
            SoundReader soundReader,
            UserInputListener inputListener,
            WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        GameObject sky = Sky.create(windowController.getWindowDimensions()); // create sky
        gameObjects().addGameObject(sky, Layer.BACKGROUND); // add sky to background layer
    }

    /** TODO: Docs */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
