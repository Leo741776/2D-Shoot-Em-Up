import javafx.animation.PathTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

/*
* Pepper
* Represents the pepper power-up. Spawns with a zigzag path and enables
* multi-shot (clones) for a limited time.
* */
public class Pepper extends ImageView {

    // Randomized once to vary which horizontal lane the power-up spawns in
    int randomValueForStartingX = (int) (Math.random() * 100);
    int startingX = 0;

    public Pepper() {

        // Load and configure pepper power-up sprite
        Image pepperImage = new Image(getClass().getResource("/assets/sprite/pepper.png").toExternalForm());
        this.setImage(pepperImage);
        this.setPreserveRatio(true);
        this.setFitWidth(75);
    }

    /*
    * Spawns the pepper power-up and moves it downward
    * in a predictable zigzag pattern.
    * */
    public void spawn(Pane gamePane) {

        // Choose one of two predefined spawn lanes
        startingX = (randomValueForStartingX <= 50) ? 256 : 512;

        this.setX(startingX);
        this.setY(-100);

        gamePane.getChildren().add(this);

        // Clamp ensures zigzag motion stays within screen bounds
        double minX = 0;
        double maxX = 768 - this.getFitWidth();
        double amplitude = 250;

        // Fixed zigzag path for consistent player expectations
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
                clamp(startingX, minX, maxX), 1024);

        // Slower movement so the power-up is easier to collect
        PathTransition zigzagMovement = new PathTransition(Duration.seconds(20), zigzagPattern, this);

        // Remove power-up once it exits the screen
        zigzagMovement.setOnFinished(e -> {
            gamePane.getChildren().remove(this);
        });

        zigzagMovement.play();
    }

    /*
    * Keeps movement coordinates within screen limits.
    * */
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}