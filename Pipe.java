import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;

public class Pipe {
    private float positionX, positionY;
    private static float width, height;
    private boolean passed = false;
    private boolean flipped;
    
    private static float speed;
    private static Image image;

    public Pipe(float positionX, float positionY, boolean flipped) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.flipped = flipped;
    }
    
    public void update() {
        positionX -= speed;
    }
    
    public void draw(GraphicsContext gc) {
        if (flipped) {
            gc.save();
            gc.translate(positionX + width / 2, positionY + height / 2);
            gc.scale(1, -1);
            gc.drawImage(image, -width / 2, -height / 2, width, height);
            gc.restore();
        } else {
            gc.drawImage(image, positionX, positionY, width, height);
        }
    }

    public float getPositionX() {
        return positionX;
    }

    public float getPositionY() {
        return positionY;
    }
    
    public static float getWidth() {
        return width;
    }
    
    public static void setWidth(float newWidth) {
        width = newWidth;
    }
    
    public static float getHeight() {
        return height;
    }
    
    public static void setHeight(float newHeight) {
        height = newHeight;
    }

    public static void setImage(Image img) {
        image = img;
    }
    
    public static void setSpeed(float newSpeed) {
        speed = newSpeed;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed() {
        this.passed = true;
    }

    public boolean isOffScreen() {
        return positionX + width < 0;
    }
    
    public boolean isFlipped() {
        return flipped;
    }
}