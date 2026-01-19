import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/*
* EnemyProjectile
* Represents a projectile fired by enemies. Handles its downward movement
* and automatic removal when leaving the screen.
* */
public class EnemyProjectile extends ImageView {
    private AnimationTimer behaviorTimer;   // Controls per-frame movement of the projectile

    public EnemyProjectile() {

        // Load and congifure projectile sprite
        Image enemyProjectileImage = new Image(getClass().getResource("/assets/projectile/enemy_projectile.png").toExternalForm());
        this.setImage(enemyProjectileImage);
        this.setPreserveRatio(true);
        this.setFitWidth(50);

        // Automatically stop animation if this node is removed from the scene graph
        this.parentProperty().addListener((unusedObs, unusedOldParent, newParent) -> {
            if (newParent == null) {
                stopAnimation();
            }
        });
    }

    /*
    * Spawns and launches the projectile downward from a given position
    * */
    public void fire(double currentX, double currentY, Pane gamePane) {

        // Center projectile horizontally on firing point
        this.setX(currentX - this.getFitWidth() / 2);
        this.setY(currentY);

        gamePane.getChildren().add(this);

        // Simple per-frame movement using AnimationTimer
        behaviorTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {

                // Move projectile downward each frame
                setY(getY() + 2.5);

                // Remove projectile once it leaves the screen
                if (getY() > 1024) {
                    gamePane.getChildren().remove(EnemyProjectile.this);
                    stopAnimation();
                }
            }
        };
        behaviorTimer.start();
        SoundManager.playEnemyBlasterSound();
    }

    /*
    * Stops the movement animation safely.
    * */
    public void stopAnimation() {
        if (behaviorTimer != null) {
            behaviorTimer.stop();
        }
    }
}