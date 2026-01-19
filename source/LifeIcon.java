import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/*
* LifeIcon
* Represents a single life in the player's UI. Displays a small pizza sprite
* at a specified position in the HUD/life bar.
* */
public class LifeIcon extends ImageView {

    // Constructure for the image used in the life bar in the UI
    public LifeIcon(double x, double y) {

        Image lifeIconImage = new Image(getClass().getResource("/assets/sprite/pizza.png").toExternalForm());
        this.setImage(lifeIconImage);
        this.setPreserveRatio(true);
        this.setFitWidth(50);

        this.setX(x);
        this.setY(y);
    }
}