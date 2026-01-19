import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.HashSet;
import java.util.Set;

/*
* UIManager
* Handles all user interface elements including:
* - Score display
* - High score display
* - Life icons
* - Start screen instructions
* - Game over / continue screen
* */
public class UIManager {

    private final Pane gamePane;        // Main game content pane
    private final Pane uiPane;          // UI overlay pane
    private final GameState gameState;
    private final Font pixelFont;

    private ImageView logo;                     // Title/logo image
    private Text instructionText;               // "Press Enter to start"
    private FadeTransition instructionPulse;    // Blinking animation for instruction

    // Nodes used in "continue" overlay
    private Set<Node> continueScreenNodes = new HashSet<>();

    private Text scoreText;
    private Text highScoreText;
    private Text lifeText;
    private LifeIcon lifeIcon1, lifeIcon2, lifeIcon3;

    public UIManager(Pane gamePane, Pane uiPane, GameState gameState, Font pixelFont) {
        this.gamePane = gamePane;
        this.uiPane = uiPane;
        this.gameState = gameState;
        this.pixelFont = pixelFont;
    }

    /*
    * Sets up the initial title screen with logo, blinking instruction, and scores.
    * */
    public void setupInitialScreen() {

        // Display game logo
        logo = new ImageView(new Image(getClass().getResource("/assets/background/logo.png").toExternalForm()));
        logo.setFitWidth(500);
        logo.setPreserveRatio(true);
        logo.setX(384 - (500 / 2.0));
        logo.setY(400);
        uiPane.getChildren().add(logo);

        // Blinking instruction text
        instructionText = new Text("press enter to start");
        instructionText.setFill(Color.RED);
        instructionText.setStroke(Color.BLACK);
        instructionText.setFont(pixelFont);
        instructionText.setX((768 - instructionText.getLayoutBounds().getWidth()) / 2);
        instructionText.setY(700);
        uiPane.getChildren().add(instructionText);

        instructionPulse = new FadeTransition(Duration.seconds(0.5), instructionText);
        instructionPulse.setFromValue(1.0);
        instructionPulse.setToValue(0.25);
        instructionPulse.setCycleCount(Animation.INDEFINITE);
        instructionPulse.setAutoReverse(true);
        instructionPulse.play();

        // Display high score and current score
        highScoreText = new Text("hi score\t\t" + gameState.highScore);
        highScoreText.setFill(Color.RED);
        highScoreText.setStroke(Color.BLACK);
        highScoreText.setFont(pixelFont);
        highScoreText.setX(50);
        highScoreText.setY(50);
        uiPane.getChildren().add(highScoreText);

        scoreText = new Text("score\t\t" + gameState.score);
        scoreText.setFill(Color.RED);
        scoreText.setStroke(Color.BLACK);
        scoreText.setFont(pixelFont);
        scoreText.setX(50);
        scoreText.setY(100);
        uiPane.getChildren().add(scoreText);
    }

    /*
    * Sets up the in-game UI: life icons and labels.
    * Removes initial screen elements if present.
    * */
    public void setupGameUI() {
        uiPane.getChildren().removeAll(logo, instructionText);

        if (instructionPulse != null) {
            instructionPulse.stop();
        }

        // Create and display three life icons
        lifeIcon1 = new LifeIcon(20, 950);
        lifeIcon2 = new LifeIcon(70, 950);
        lifeIcon3 = new LifeIcon(120, 950);
        uiPane.getChildren().addAll(lifeIcon1, lifeIcon2, lifeIcon3);

        double firstIconX = 20;
        double lastIconX = 120;
        double iconWidth = 50;
        double centerX = firstIconX + ((lastIconX + iconWidth - firstIconX) / 2);

        lifeText = new Text("life");
        lifeText.setFont(pixelFont);
        lifeText.setFill(Color.RED);
        lifeText.setStroke(Color.BLACK);
        lifeText.setX(centerX - lifeText.getLayoutBounds().getWidth() / 2);
        lifeText.setY(930);
        uiPane.getChildren().add(lifeText);
    }

    /*
    * Updates the score display; updates high score if current score exceeds it.
    * */
    public void updateScore() {
        scoreText.setText("score\t\t" + gameState.score);

        if (gameState.score > gameState.highScore) {
            highScoreText.setText("hi score\t\t" + gameState.score);
        }
    }

    /*
    * Updates the visibility of life icons based on the player's remaining lives.
    * */
    public void updateLives() {
        lifeIcon1.setVisible(gameState.life >= 1);
        lifeIcon2.setVisible(gameState.life >= 2);
        lifeIcon3.setVisible(gameState.life >= 3);
    }

    /*
    * Shows the "game over" overlay with dimmed background and instructions.
    * */
    public void showContinueScreen() {
        Rectangle dimBackground = new Rectangle(gamePane.getWidth(), gamePane.getHeight(), Color.color(0, 0, 0, 0.6));

        Text gameOverText = new Text("game over");
        gameOverText.setFont(Font.font(pixelFont.getFamily(), 80));
        gameOverText.setFill(Color.RED);
        gameOverText.setStroke(Color.BLACK);
        gameOverText.setX(gamePane.getWidth() / 2 - gameOverText.getLayoutBounds().getWidth() / 2);
        gameOverText.setY(400);

        Text continueText = new Text("press enter to continue");
        continueText.setFont(pixelFont);
        continueText.setFill(Color.WHITE);
        continueText.setStroke(Color.BLACK);
        continueText.setX(gamePane.getWidth() / 2 - continueText.getLayoutBounds().getWidth() / 2);
        continueText.setY(500);

        Text escapeText = new Text("press esc to quit");
        escapeText.setFont(pixelFont);
        escapeText.setFill(Color.WHITE);
        escapeText.setStroke(Color.BLACK);
        escapeText.setX(gamePane.getWidth() / 2 - escapeText.getLayoutBounds().getWidth() / 2);
        escapeText.setY(600);

        uiPane.getChildren().addAll(dimBackground, gameOverText, continueText, escapeText);
        continueScreenNodes.addAll(Set.of(dimBackground, gameOverText, continueText, escapeText));
    }

    /*
    * Hides the "game over / continue" overlay.
    * */
    public void hideContinueScreen() {
        uiPane.getChildren().removeAll(continueScreenNodes);
        continueScreenNodes.clear();
    }

    // Getter methods for other classes to access UI elements
    public Text getScoreText() {
        return scoreText;
    }

    public Text getHighScoreText() {
        return highScoreText;
    }

    public LifeIcon getLifeIcon1() {
        return lifeIcon1;
    }

    public LifeIcon getLifeIcon2() {
        return lifeIcon2;
    }

    public LifeIcon getLifeIcon3() {
        return lifeIcon3;
    }
}
