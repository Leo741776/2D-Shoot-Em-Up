import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.Set;
import java.util.function.Consumer;

/*
* CollisionManager
* Handles all collision detection and responses in the game, including:
* - Player vs enemy collisions
* - Player vs enemy projectiles
* - Player vs power-ups (Salt, Pepper, ExtraLife)
* - Enemy hit by player projectiles
* Updates score, lives, and triggers explosions/sounds as needed.
* */
public class CollisionManager {

    /*
    * Main collision-processing method.
    * This method is called every frame and it:
    * - Handles all collision detection
    * - Applies damage, power-ups, and score updates
    * - Updates UI and checks for game-over
    * */
    public static void update(
                              UIManager uiManager,
                              Pane gamePane,
                              Pane uiPane,
                              PizzaSprite pizzaMain,
                              GameState state,
                              Text scoreText,
                              Text highScoreText,
                              LifeIcon lifeIcon1,
                              LifeIcon lifeIcon2,
                              LifeIcon lifeIcon3,
                              AnimationTimer timer,
                              Runnable onGameOver,
                              Set<Enemy> activeEnemies,
                              Consumer<Long> setFireCooldown,   // Allows weapon cooldown to be modified
                              Runnable enablePepperShot,        // Enables special weapon behavior
                              Runnable disablePepperShot) {     // Disables special weapon behavior

        long damageCooldown = 1000; // Minimum time (ms) between taking damage to prevent instant death

        // Iterate over all game objects currently on screen
        for (var node : gamePane.getChildren()) {

            // Player colliding with enemy
            if (node instanceof Enemy enemy) {
                if (CollisionUtils.intersects(pizzaMain, enemy) && System.currentTimeMillis() - state.timeSinceLastTookDamage >= damageCooldown) {
                    SoundManager.playExplosionSound();
                    pizzaMain.flash();
                    state.life--;
                    state.timeSinceLastTookDamage = System.currentTimeMillis();
                }
            }

            // Player colliding with enemy projectile
            if (node instanceof EnemyProjectile enemyProjectile) {
                if (CollisionUtils.intersects(pizzaMain, enemyProjectile) && System.currentTimeMillis() - state.timeSinceLastTookDamage >= damageCooldown) {
                    SoundManager.playExplosionSound();
                    pizzaMain.flash();
                    state.life--;
                    state.timeSinceLastTookDamage = System.currentTimeMillis();
                }
            }

            // Player projectile hitting an enemy
            if (node instanceof Projectile projectile) {
                for (Enemy enemy : activeEnemies) {

                    // Prevents enemies off-screen from being hit
                    if (enemy.getBoundsInParent().getMinY() >= 0 && CollisionUtils.intersects(projectile, enemy)) {
                        state.score += 5;
                        state.highScore = Math.max(state.highScore, state.score);
                        CollisionManager.spawnExplosion(gamePane, enemy.getBoundsInParent().getCenterX(), enemy.getBoundsInParent().getCenterY());
                        SoundManager.playExplosionSound();
                        gamePane.getChildren().removeAll(enemy, projectile);
                        break;
                    }
                }
            }

            // Salt power up, temporarily increases fire rate
            if (node instanceof Salt salt) {
                if (CollisionUtils.intersects(salt, pizzaMain)) {
                    SoundManager.playPowerUpSound();
                    pizzaMain.flash();
                    setFireCooldown.accept(250L);
                    gamePane.getChildren().remove(salt);
                    PauseTransition reset = new PauseTransition(Duration.seconds(15));
                    reset.setOnFinished(e -> setFireCooldown.accept(750L));
                    reset.play();
                }
            }

            // Pepper power up, temporarily adds extra slices that also shoot
            if (node instanceof Pepper pepper) {
                if (CollisionUtils.intersects(pepper, pizzaMain)) {
                    SoundManager.playPowerUpSound();
                    pizzaMain.flash();
                    gamePane.getChildren().remove(pepper);
                    enablePepperShot.run();
                    PauseTransition reset = new PauseTransition(Duration.seconds(15));
                    reset.setOnFinished(e -> disablePepperShot.run());
                    reset.play();
                }
            }

            // Extra life pickup (capped at 3 lives)
            if (node instanceof ExtraLife extraLife) {
                if (CollisionUtils.intersects(extraLife, pizzaMain)) {
                    if (state.life > 0 && state.life < 3) {
                        state.life++;
                        uiManager.updateLives();
                        SoundManager.playPowerUpSound();
                        pizzaMain.flash();
                    }
                    gamePane.getChildren().remove(extraLife);
                }
            }
        }

        // Update score UI every frame
        scoreText.setText("score\t\t" + state.score);
        highScoreText.setText("hi score\t\t" + state.highScore);

        // Update life icons and handle game-over state
        switch (state.life) {
            case 3 -> {
                lifeIcon1.setVisible(true);
                lifeIcon2.setVisible(true);
                lifeIcon3.setVisible(true);
            }
            case 2 -> {
                lifeIcon1.setVisible(true);
                lifeIcon2.setVisible(true);
                lifeIcon3.setVisible(false);
            }
            case 1 -> {
                lifeIcon1.setVisible(true);
                lifeIcon2.setVisible(false);
                lifeIcon3.setVisible(false);
            }
            case 0 -> {
                // Game over cleanup
                lifeIcon1.setVisible(false);
                lifeIcon2.setVisible(false);
                lifeIcon3.setVisible(false);

                pizzaMain.setVisible(false);
                CollisionManager.spawnExplosion(gamePane, pizzaMain.getBoundsInParent().getCenterX(), pizzaMain.getBoundsInParent().getCenterY());
                SoundManager.playGameOverSound();

                gamePane.getChildren().removeIf(node -> node instanceof PizzaSprite && node != pizzaMain);
                timer.stop();
                onGameOver.run();
            }
        }
    }

    /*
    * Spawns a short-lived explosion effect at a given position.
    * */
    public static void spawnExplosion(Pane gamePane, double centerX, double centerY) {

        // Center the explosion image on the given coordinates
        ImageView explosion = new ImageView(new Image(CollisionManager.class.getResource("/assets/effect/explosion.png").toExternalForm()));
        explosion.setFitWidth(64);
        explosion.setFitHeight(64);
        explosion.setPreserveRatio(true);
        explosion.setX(centerX - explosion.getFitWidth() / 2);
        explosion.setY(centerY - explosion.getFitHeight() / 2);

        gamePane.getChildren().add(explosion);

        // Automatically remove explosion after a short delay
        PauseTransition explosion1Animation = new PauseTransition(Duration.millis(300));
        explosion1Animation.setOnFinished(e -> gamePane.getChildren().remove(explosion));
        explosion1Animation.play();
    }
}

