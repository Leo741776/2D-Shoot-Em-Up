import javafx.animation.FadeTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/*
* PizzaSprite
* Represents the player’s pizza ship. Handles position, sprite display,
* and visual feedback (flash) when hit or collecting power-ups.
* */
public class PizzaSprite extends ImageView {

    /*
    * Creates a pizza sprite positioned at the given coordinates.
    * Used for visual effects (e.g., flashing on damage).
    * */
    public PizzaSprite(double x, double y) {

        // Load and configure pizza sprite image
        Image pizzaSpriteImage = new Image(getClass().getResource("/assets/sprite/pizza.png").toExternalForm());
        this.setImage(pizzaSpriteImage);
        this.setPreserveRatio(true);
        this.setFitWidth(75);
        this.setX(x);
        this.setY(y);
    }

    /*
    * Rapid flash effect to indicate damage or impact.
    * */
    public void flash() {

        // Very fast fade in/out for a "hit" visual cue
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(50), this);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setCycleCount(10);
        fadeTransition.setAutoReverse(true);

        fadeTransition.play();
    }
}
