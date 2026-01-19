import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/*
* Projectile
* Represents a projectile fired by the player. Handles upward movement
* and removal when leaving the screen.
* */
public class Projectile extends ImageView {

    /*
    * Player-fired projectile moving upward.
    * */
    public Projectile() {

        // Load and configure projectile sprite
        Image projectileImage = new Image(getClass().getResource("/assets/projectile/projectile.png").toExternalForm());
        this.setImage(projectileImage);
        this.setPreserveRatio(true);
        this.setFitWidth(50);
    }

    /*
    * Fires the projectile from the given world position.
    * */
    public void fire(double currentX, double currentY, Pane gamePane) {

        // Center projectile horizontally and offset vertically from the shooter
        this.setX(currentX - this.getFitWidth() / 2);
        this.setY(currentY - 25);

        gamePane.getChildren().add(this);

        // Timeline updates projectile movement every frame (~60 FPS)
        Timeline projectileTimeline = new Timeline(new KeyFrame(Duration.millis(16), ev -> {
            this.setY(this.getY() - 10);
            if (this.getY() < -100) {
                gamePane.getChildren().remove(this);
            }
        }));
        projectileTimeline.setCycleCount(Animation.INDEFINITE);
        projectileTimeline.play();
    }
}