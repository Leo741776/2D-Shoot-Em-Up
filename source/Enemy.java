import javafx.animation.PathTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

import java.util.Set;

/*
* Enemy
* Represents an enemy sprite. Handles spawning, zigzag movement patterns,
* and firing enemy projectiles at the player.
* */
public class Enemy extends ImageView {

    // Random values used once to vary spawn position and movement direction
    private int randomValueForStartingX = (int) (Math.random() * 100);
    private int randomValueForDirection = (int) (Math.random() * 100);
    private int startingX = 0;

    // Used to limit how often the enemy can shoot
    private long timeSinceLastFired = 0;
    private final long FIRE_COOLDOWN = 750;

    public Enemy() {
        // Load and configure enemy sprite
        Image enemyImage = new Image(getClass().getResource("/assets/sprite/enemy.png").toExternalForm());
        this.setImage(enemyImage);
        this.setPreserveRatio(true);
        this.setFitWidth(75);
    }

    /*
    * Spawns the enemy above the screen and starts a zigzag path animation.
    * */
    public void spawn(Pane gamePane) {

        // Choose one of two horizontal spawn lanes
        startingX = (randomValueForStartingX <= 50) ? 256 : 512;

        this.setX(startingX);
        this.setY(-100);

        gamePane.getChildren().add(this);

        // Horizontal movement limits (prevents leaving screen)
        double minX = 0;
        double maxX = 768 - this.getFitWidth();
        double amplitude = 250;

        Polyline zigzagPattern;

        // Randomize whether zigzag starts left or right
        if (randomValueForDirection <= 50) {
            zigzagPattern = new Polyline(
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
        } else {
            zigzagPattern = new Polyline(
                    clamp(startingX, minX, maxX),
                    -100,
                    clamp(startingX - amplitude, minX, maxX),
                    128,
                    clamp(startingX, minX, maxX),
                    256,
                    clamp(startingX + amplitude, minX, maxX),
                    384,
                    clamp(startingX, minX, maxX),
                    512,
                    clamp(startingX - amplitude, minX, maxX),
                    640,
                    clamp(startingX, minX, maxX),
                    768,
                    clamp(startingX + amplitude, minX, maxX),
                    896,
                    clamp(startingX, minX, maxX), 1024);
        }

        // Move enemy along the zigzag path over time
        PathTransition zigzagMovement = new PathTransition(Duration.seconds(15), zigzagPattern, this);

        // Remove enemy once it finishes moving off-screen
        zigzagMovement.setOnFinished(e -> gamePane.getChildren().remove(this));
        zigzagMovement.play();
    }

    /*
    * Ensures a value stays within screen bounds.
    * */
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /*
    * Fires a projectile downward if cooldown has elapsed
    * and the enemy is currently visible on screen.
    * */
    public void fire(Pane gamePane, Set<EnemyProjectile> projectileSet) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - timeSinceLastFired >= FIRE_COOLDOWN) {

            // Convert local bounds to scene space for accurate positioning
            var scenePosition = this.localToScene(this.getBoundsInLocal());
            double currentX = scenePosition.getMinX() + (this.getFitWidth() / 2);
            double currentY = scenePosition.getMaxY();

            // Prevent firing while off-screen
            if (currentY > 0 && currentY < gamePane.getHeight()) {
                if (currentTime - timeSinceLastFired >= FIRE_COOLDOWN) {
                    EnemyProjectile projectile = new EnemyProjectile();
                    projectile.fire(currentX, currentY, gamePane);
                    projectileSet.add(projectile);
                    timeSinceLastFired = currentTime;
                }
            }
        }
    }
}