import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*
* Main
* Entry point for the game. Sets up the stage, scene, game/UI layers,
* font, input handling, and initializes BackgroundManager, UIManager,
* and GameManager. Manages game start and restart logic.
* */
public class Main extends Application {

    private GameState gameState;
    private Font pixelFont;

    private BackgroundManager backgroundManager;
    private UIManager uiManager;
    private GameManager gameManager;

    private boolean gameHasStarted = false;     // Track if game has started
    private boolean gameOver = false;           // Track if game is over

    @Override
    public void start(Stage primaryStage) {

        gameState = new GameState();    // Initialize game state

        // Main game layer where sprites, enemies, and projectiles live
        Pane gamePane = new Pane();
        gamePane.setPrefSize(768, 1024);
        gamePane.setMinSize(768, 1024);
        gamePane.setMaxSize(768, 1024);
        gamePane.setClip(new Rectangle(768, 1024));

        // UI layer for scores, life icons, and menus
        Pane uiPane = new Pane();
        uiPane.setPrefSize(768, 1024);
        uiPane.setMinSize(768, 1024);
        uiPane.setMaxSize(768, 1024);

        // Combine game and UI panes in a single group
        Group gameGroup = new Group(gamePane, uiPane);
        StackPane root = new StackPane(gameGroup);

        // Load custom pixel font for retro style
        pixelFont = Font.loadFont(getClass().getResource("/assets/custom_font/arcade_font.TTF").toExternalForm(), 35);

        // Initialize background scrolling, UI, and game manager
        backgroundManager = new BackgroundManager(gamePane);
        uiManager = new UIManager(gamePane, uiPane, gameState, pixelFont);

        // Callback to show the "continue / game over" screen
        Runnable showContinueScreenCallback = () -> {
            uiManager.showContinueScreen();
            gameOver = true;
        };

        gameManager = new GameManager(gamePane, uiPane, uiManager, gameState, showContinueScreenCallback);

        // Start scrolling background and display initial title screen
        backgroundManager.startScrolling();
        uiManager.setupInitialScreen();

        // Create scene and configure stage
        Scene scene = new Scene(root);
        primaryStage.setTitle("Pizza Time");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setWidth(768);
        primaryStage.setHeight(1024);
        primaryStage.setFullScreen(false);
        primaryStage.show();

        // Key press handling
        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            gameManager.handleKeyPress(code);

            // Start game from initial screen
            if (!gameHasStarted && e.getCode() == KeyCode.ENTER) {
                SoundManager.playGameStartSound();
                gameHasStarted = true;
                gameManager.startGame();
                return;
            }

            // Handle input on "game over / continue" screen
            if (gameOver) {
                if (e.getCode() == KeyCode.ENTER) {
                    SoundManager.playGameStartSound();
                    gameManager.resetGame();
                    gameOver = false;
                } else if (e.getCode() == KeyCode.ESCAPE) {
                    System.exit(0);
                }
            }
        });

        // Key release handling
        scene.setOnKeyReleased(e -> {
            gameManager.handleKeyRelease(e.getCode());
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

