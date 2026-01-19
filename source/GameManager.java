import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

import java.util.HashSet;
import java.util.Set;

/*
* GameManager
* Core game logic manager. Handles:
* - Player input and movement
* - Player firing mechanics
* - Enemy spawning and firing
* - Power-up spawning
* - Game difficulty scaling
* - Game reset
* */
public class GameManager {

    // Core scene layers and shared state
    private final Pane gamePane;
    private final Pane uiPane;
    private final UIManager uiManager;
    private final GameState gameState;

    // Tracks currently pressed keys for smooth movement
    private final Set<KeyCode> keysPressed = new HashSet<>();

    // Active game entities tracked separately from scene graph
    private final Set<Enemy> activeEnemies = new HashSet<>();
    private final Set<EnemyProjectile> activeEnemyProjectiles = new HashSet<>();
    private final Set<Projectile> activePlayerProjectiles = new HashSet<>();

    // Horizontal offsets used for multi-shot (pepper power-up)
    private final Set<Double> fireOffsets = new HashSet<>();

    private AnimationTimer gameTimer;

    // Main player sprite and optional visual clones
    private PizzaSprite pizzaMain;
    private PizzaSprite leftClone;
    private PizzaSprite rightClone;

    // Cached space key state to prevent repeated key events
    private boolean spacePressed = false;

    // Timing values used for cooldowns and difficulty scaling
    private long timeSinceLastFired = 0;
    private long timeSinceLastSpawned = 0;
    private long timeSincePowerUpLastSpawned = 0;
    private long lastSpawnDifficultyIncrease = 0;
    private long FIRE_COOLDOWN = 750;
    private long SPAWN_COOLDOWN = 1700;
    private final long POWER_UP_SPAWN_COOLDOWN = 20000;

    // Callback used to show the continue / game-over screen
    private final Runnable showContinueScreenCallback;

    public GameManager(
            Pane gamePane,
            Pane uiPane,
            UIManager uiManager,
            GameState gameState,
            Runnable showContinueScreenCallback) {
        this.gamePane = gamePane;
        this.uiPane = uiPane;
        this.uiManager = uiManager;
        this.gameState = gameState;
        this.showContinueScreenCallback = showContinueScreenCallback;
    }

    // Record key presses for polling-based input handling
    public void handleKeyPress(KeyCode code) {
        keysPressed.add(code);

        if (code == KeyCode.SPACE) {
            spacePressed = true;
        }
    }

    public void handleKeyRelease(KeyCode code) {
        keysPressed.remove(code);

        if (code == KeyCode.SPACE) {
            spacePressed = false;
        }
    }

    /*
    * Initializes game state and starts the main game loop.
    * */
    public void startGame() {
        lastSpawnDifficultyIncrease = System.currentTimeMillis();

        pizzaMain = new PizzaSprite((384 - (75 / 2.0)), 800);
        gamePane.getChildren().add(pizzaMain);
        uiManager.setupGameUI();
        uiManager.updateLives();

        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                updateGameLoop();
            }
        };
        gameTimer.start();
    }

    /*
    * Main per-frame update method.
    * */
    private void updateGameLoop() {
        long now = System.currentTimeMillis();

        updateDifficulty(now);
        handlePlayerMovement();
        handlePlayerFiring(now);
        handleEnemySpawning(now);
        handlePowerUpSpawning(now);

        // Remove projectiles that have already left the scene
        activeEnemyProjectiles.removeIf(enemyProjectile -> !gamePane.getChildren().contains(enemyProjectile));
        activePlayerProjectiles.removeIf(projectile -> !gamePane.getChildren().contains(projectile));

        uiManager.updateScore();
        uiManager.updateLives();

        // Assign collision handling to a dedicated manager
        CollisionManager.update(
                uiManager,
                gamePane,
                uiPane,
                pizzaMain,
                gameState,
                uiManager.getScoreText(),
                uiManager.getHighScoreText(),
                uiManager.getLifeIcon1(),
                uiManager.getLifeIcon2(),
                uiManager.getLifeIcon3(),
                gameTimer,
                showContinueScreenCallback,
                activeEnemies,
                newCooldown -> FIRE_COOLDOWN = newCooldown,
                this::enablePepperShot,
                this::disablePepperShot);
    }

    /*
    * Gradually increases difficulty by spawning enemies faster.
    * */
    private void updateDifficulty(long now) {
        if (now - lastSpawnDifficultyIncrease >= 15000 && SPAWN_COOLDOWN > 1000) {
            SPAWN_COOLDOWN -= 100;
            lastSpawnDifficultyIncrease = now;
        }
    }

    /*
    * Moves the player while keeping all visible sprites on-screen,
    * including pepper-shot clones.
    * */
    private void handlePlayerMovement() {
        double dx = 0;
        double dy = 0;
        double speed = 3;

        if (keysPressed.contains(KeyCode.LEFT)) dx -= speed;
        if (keysPressed.contains(KeyCode.RIGHT)) dx += speed;

        double mainX = pizzaMain.getX();
        double mainY = pizzaMain.getY();
        double mainWidth = pizzaMain.getBoundsInParent().getWidth();
        double mainHeight = pizzaMain.getBoundsInParent().getHeight();

        // Determine the furthest visual extents due to clone offsets
        double leftOffset = 0;
        double rightOffset = 0;

        if (leftClone != null)
            leftOffset = fireOffsets.stream().filter(o -> o < 0).min(Double::compareTo).orElse(-80.0);
        if (rightClone != null)
            rightOffset = fireOffsets.stream().filter(o -> o > 0).max(Double::compareTo).orElse(80.0);

        double leftEdge = mainX + leftOffset;
        double rightEdge = mainX + mainWidth + rightOffset;
        double topEdge = mainY;
        double bottomEdge = mainY + mainHeight;

        // Clamp movement to pane bounds
        if (leftEdge + dx < 0) dx += -(leftEdge + dx);
        if (rightEdge + dx > gamePane.getWidth()) dx -= (rightEdge + dx - gamePane.getWidth());

        if (topEdge + dy < 0) dy += -(topEdge + dy);
        if (bottomEdge + dy > gamePane.getHeight()) dy -= (bottomEdge + dy - gamePane.getHeight());

        pizzaMain.setX(mainX + dx);
        pizzaMain.setY(mainY + dy);
    }

    /*
    * Handles player firing, including multi-shot offsets.
    * */
    private void handlePlayerFiring(long now) {
        if (!spacePressed || now - timeSinceLastFired < FIRE_COOLDOWN) {
            return;
        }

        double baseX = pizzaMain.getX();
        double baseY = pizzaMain.getY();
        double halfWidth = pizzaMain.getFitWidth() / 2;

        // Fire center shot
        fireProjectile(baseX + halfWidth, baseY);

        // Fire additional shots if power-up is active
        for (double offset : fireOffsets) {
            fireProjectile(baseX + halfWidth + offset, baseY);
        }

        SoundManager.playBlasterSound();
        timeSinceLastFired = now;
    }

    /*
    * Spawns enemies and lets active enemies fire.
    * */
    private void handleEnemySpawning(long now) {
        if (now - timeSinceLastSpawned >= SPAWN_COOLDOWN) {
            Enemy enemy = new Enemy();
            enemy.spawn(gamePane);
            activeEnemies.add(enemy);
            timeSinceLastSpawned = now;
        }

        // Remove enemies that have left the scene
        activeEnemies.removeIf(enemy -> !gamePane.getChildren().contains(enemy));

        for (Enemy enemy : activeEnemies) {
            enemy.fire(gamePane, activeEnemyProjectiles);
        }
    }

    private void fireProjectile(double x, double y) {
        Projectile projectile = new Projectile();
        projectile.fire(x, y, gamePane);
        activePlayerProjectiles.add(projectile);
    }

    /*
    * Randomly spawns one of several power-ups.
    * */
    private void handlePowerUpSpawning(long now) {
        if (now - timeSincePowerUpLastSpawned >= POWER_UP_SPAWN_COOLDOWN) {
            double random = Math.random();

            if (random < 0.10) {
                ExtraLife extraLife = new ExtraLife();
                extraLife.spawn(gamePane);
            } else if (random < 0.55) {
                Pepper pepper = new Pepper();
                pepper.spawn(gamePane);
            } else {
                Salt salt = new Salt();
                salt.spawn(gamePane);
            }
            timeSincePowerUpLastSpawned = now;
        }
    }

    /*
    * Enables triple-shot behavior using visual clones and fire offsets.
    * */
    public void enablePepperShot() {
        if (leftClone != null) {
            return;
        }

        fireOffsets.clear();
        fireOffsets.add(-80.0);
        fireOffsets.add(80.0);

        leftClone = new PizzaSprite(0, 0);
        rightClone = new PizzaSprite(0, 0);

        // Bind clone positions to the main player
        leftClone.xProperty().bind(pizzaMain.xProperty().add(-80));
        leftClone.yProperty().bind(pizzaMain.yProperty());

        rightClone.xProperty().bind(pizzaMain.xProperty().add(80));
        rightClone.yProperty().bind(pizzaMain.yProperty());

        gamePane.getChildren().addAll(leftClone, rightClone);
    }

    /*
    * Disables pepper-shot visuals and offsets.
    * */
    public void disablePepperShot() {
        fireOffsets.clear();

        if (leftClone != null) {
            gamePane.getChildren().remove(leftClone);
            leftClone = null;
        }

        if (rightClone != null) {
            gamePane.getChildren().remove(rightClone);
            rightClone = null;
        }
    }

    /*
    * Resets game state after game over without touching the background.
    * */
    public void resetGame() {
        gameState.life = 3;
        gameState.score = 0;
        FIRE_COOLDOWN = 750;
        SPAWN_COOLDOWN = 1700;
        lastSpawnDifficultyIncrease = System.currentTimeMillis();

        fireOffsets.clear();

        if (leftClone != null) {
            gamePane.getChildren().remove(leftClone);
        }

        if (rightClone != null) {
            gamePane.getChildren().remove(rightClone);
        }

        // Remove only gameplay entities
        gamePane.getChildren().removeIf(
                node ->
                        node instanceof Enemy ||
                        node instanceof Projectile ||
                        node instanceof EnemyProjectile ||
                        node instanceof Salt ||
                        node instanceof Pepper ||
                        node instanceof ExtraLife ||
                        node instanceof LifeIcon);

        activeEnemies.clear();
        activeEnemyProjectiles.clear();
        activePlayerProjectiles.clear();

        pizzaMain.setX(384 - 75 / 2.0);
        pizzaMain.setY(800);
        pizzaMain.setVisible(true);

        uiManager.hideContinueScreen();
        uiManager.setupGameUI();
        uiManager.updateLives();
        uiManager.updateScore();

        gameTimer.start();
    }
}
