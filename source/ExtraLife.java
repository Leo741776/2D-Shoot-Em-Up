import javafx.animation.PathTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

/*
* ExtraLife
* Represents an extra life power-up. Spawns with a zigzag path and disappears
* when collected or off-screen.
* */
public class ExtraLife extends ImageView {

    // Randomized once to vary which horizontal lane the pickup spawns in
    int randomValueForStartingX = (int) (Math.random() * 100);
    int startingX = 0;

    public ExtraLife() {

        // Load and configure extra life sprite
        Image saltImage = new Image(getClass().getResource("/assets/sprite/extra_life.png").toExternalForm());
        this.setImage(saltImage);
        this.setPreserveRatio(true);
        this.setFitWidth(75);
    }

    /*
    * Spawns the extra-life pickup and moves it down the screen
    * in a slow zigzag pattern.
    * */
    public void spawn(Pane gamePane) {

        // Choose one of two predefined spawn lanes
        startingX = (randomValueForStartingX <= 50) ? 256 : 512;

        this.setX(startingX);
        this.setY(-100);

        gamePane.getChildren().add(this);

        // Clamp values prevent zigzag movement from leaving the screen
        double minX = 0;
        double maxX = 768 - this.getFitWidth();
        double amplitude = 250;

        // Predefined zigzag path moving downward
        Polyline zigzagPattern = new Polyline(
                clamp(startingX, minX, maxX),
                -100,
                clamp(startingX + amplitude, minX, maxX),
                128,
                clamp(startingX, minX, maxX),
                256,
                clamp(startingX - amplitude, minX, maxX),
                384,
                clamp(startingX, minX, maxX),
                512,
                clamp(startingX + amplitude, minX, maxX),
                640,
                clamp(startingX, minX, maxX),
                768,
                clamp(startingX - amplitude, minX, maxX),
                896,
                clamp(startingX, minX, maxX),
                1024);

        // Slower movement than enemies to make the pickup easier to grab
        PathTransition zigzagMovement = new PathTransition(Duration.seconds(20), zigzagPattern, this);

        // Remove pickup once it exits the screen
        zigzagMovement.setOnFinished(e -> {
            gamePane.getChildren().remove(this);
        });
        zigzagMovement.play();
    }

    /*
    * Keeps movement coordinates within screen bounds.
    * */
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}