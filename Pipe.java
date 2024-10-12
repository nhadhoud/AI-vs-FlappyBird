import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;

public class Pipe {
    private int positionX, positionY;
    private static int width, height;
    private boolean passed = false;
    private boolean flipped;
    
    private static float speed;
    private static Image image;

    public Pipe(int positionX, int positionY, boolean flipped) {
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

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }
    
    public static int getWidth() {
        return width;
    }
    
    public static void setWidth(int newWidth) {
        width = newWidth;
    }
    
    public static int getHeight() {
        return height;
    }
    
    public static void setHeight(int newHeight) {
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
