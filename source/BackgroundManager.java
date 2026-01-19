import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/*
* BackgroundManager
* Handles the infinite scrolling background effect.
* It works by using two ImageViews stacked vertically.
* As one background scrolls off the screen, it is repositioned above the other,
* creating the illusion of infinite scrolling.
* */
public class BackgroundManager {
    private final Pane gamePane;            // The main pane backgrounds are rendered onto
    private final ImageView background1;    // First background image
    private final ImageView background2;    // Second background image, used for seamless looping

    /*
    * Constructs the BackGroundManager and initializes the background images
    * */
    public BackgroundManager(Pane gamePane) {
        this.gamePane = gamePane;

        // Load the background image from the resources folder
        Image backgroundImage = new Image(getClass().getResource("/assets/background/background_image.png").toExternalForm());

        // Create two ImageViews using the same image
        this.background1 = new ImageView(backgroundImage);
        this.background2 = new ImageView(backgroundImage);

        setupBackground();                                          // Configure sizing and initial positions
        gamePane.getChildren().addAll(background1, background2);    // Add backgrounds to the pane in order
    }

    /*
    * Configures background sizing and initial vertical placement.
    * Both backgrounds are stretched to fill the pane and placed
    * on-screen and one directly below it
    * */
    private void setupBackground() {
        background1.setPreserveRatio(false);
        background1.fitWidthProperty().bind(gamePane.widthProperty());
        background1.fitHeightProperty().bind(gamePane.heightProperty());
        background1.setY(0);

        background2.setPreserveRatio(false);
        background2.fitWidthProperty().bind(gamePane.widthProperty());
        background2.fitHeightProperty().bind(gamePane.heightProperty());
        background2.setY(gamePane.getPrefHeight());
    }

    /*
    * Starts the background scrolling animation.
    * A Timeline updates the background positions at 60 FPS.
    * When one background scrolls off-screen, it is repositioned
    * above the other to maintain continuos scrolling
    * */
    public void startScrolling() {

        // Timelline running at 60 FPS (16 ms per frame)
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            double paneHeight = gamePane.getHeight();

            // Move both backgrounds downward by a fixed speed
            background1.setY(background1.getY() + 2);
            background2.setY(background2.getY() + 2);

            // Repositioning logic used for both backgrounds
            if (background1.getY() >= paneHeight) {
                background1.setY(background2.getY() - paneHeight);
            }
            if (background2.getY() >= paneHeight) {
                background2.setY(background1.getY() - paneHeight);
            }
        }));

        // Run the animation forever
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
