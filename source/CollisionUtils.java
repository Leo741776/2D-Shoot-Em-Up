import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;

/*
* CollisionUtils
* Utility class providing collision detection methods for ImageView objects.
* Supports optional padding to shrink hitboxes for more forgiving collisions.
* */
public class CollisionUtils {

    /*
    * Checks collision between two ImageViews with optional padding.
    * Padding shrinks the second object's hitbox, allowing for more
    * forgiving or visually accurate collisions.
    * */
    public static boolean intersects(ImageView a, ImageView b, double padding) {

        // Actual on-screen bounds of both objects
        Bounds b1 = a.getBoundsInParent();
        Bounds b2 = b.getBoundsInParent();

        // Create a smaller hitbox for 'b' by trimming all sides equally
        Bounds shrunkBounds = new BoundingBox(
                b2.getMinX() + padding,
                b2.getMinY() + padding,
                b2.getWidth() - 2 * padding,
                b2.getHeight() - 2 * padding);

        // Check if 'a' intersects the adjusted bounds of 'b'
        return b1.intersects(shrunkBounds);
    }

    /*
    * Convenience overload with no padding
    * */
    public static boolean intersects(ImageView a, ImageView b) {
        return intersects(a, b, 0);
    }
}
