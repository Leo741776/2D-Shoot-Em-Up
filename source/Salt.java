import javafx.animation.PathTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

/*
* Salt
* Represents the salt power-up. Spawns with a zigzag path and reduces
* the player’s fire cooldown temporarily when collected.
* */
public class Salt extends ImageView {

    // Randomized once to determine horizontal lane for spawning
    int randomValueForStartingX = (int) (Math.random() * 100);
    int startingX = 0;

    public Salt() {

        // Load and configure salt power-up sprite
        Image saltImage = new Image(getClass().getResource("/assets/sprite/salt.png").toExternalForm());
        this.setImage(saltImage);
        this.setPreserveRatio(true);
        this.setFitWidth(75);
    }

    /*
    * Spawns the salt power-up in a zigzag pattern from top to bottom.
    * */
    public void spawn(Pane gamePane) {

        // Pick one of two spawn lanes for variety
        startingX = (randomValueForStartingX <= 50) ? 256 : 512;

        this.setX(startingX);
        this.setY(-100);

        gamePane.getChildren().add(this);

        // Define horizontal limits to prevent sprite leaving screen
        double minX = 0;
        double maxX = 768 - this.getFitWidth();
        double amplitude = 250;

        // Define zigzag path for smooth downward movement
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

        // Move the sprite along the path slowly for easier collection
        PathTransition zigzagMovement = new PathTransition(Duration.seconds(20), zigzagPattern, this);

        // Remove sprite from scene once it leaves visible area
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