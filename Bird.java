import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;

public class Bird {
    private float positionX, positionY;
    public static final int HEIGHT = 50, WIDTH = 70;
    private float velocity = 0;
    private float maxVelocity = 100f;
    private static final float GRAVITY = -0.2f;
    private static final float JUMP_HEIGHT = -5.5f;
    
    private long lastJumpTime = 0;
    private static final long JUMP_COOLDOWN_MS = 150;
    
    private int score = 0;
    
    private static Image image;
    
    private NeuralNetwork neuralNetwork;
    
    public Bird(float positionX, float positionY, NeuralNetwork neuralNetwork) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.neuralNetwork = neuralNetwork;
        this.neuralNetwork.mutate(0.8f,0.4f);
    }
    
    public void update(int floor, int ceiling) {  
        if (Math.abs(velocity - GRAVITY) < maxVelocity) {
            velocity -= GRAVITY;
            positionY += velocity;
        }
        if (positionY > ceiling - HEIGHT - floor) {
            positionY = ceiling - HEIGHT - floor;
            velocity = 0;
        }
        if (positionY < 0) {
            positionY = 0;
            velocity = 0;
        }
    }
    
    public void act(float[] inputs) {
        float[] networkPrediction = neuralNetwork.processOutputs(inputs);
        if (jumpAllowed() && networkPrediction[0] > networkPrediction[1]) {
            jump();
        }
    }
    
    public void draw(GraphicsContext gc) {
        gc.save();
        gc.translate(positionX + WIDTH / 2, positionY + HEIGHT / 2);
        gc.rotate(velocity * 4);
        gc.drawImage(image, -WIDTH / 2, -HEIGHT / 2, WIDTH, HEIGHT);
        gc.restore();
    }
    
    public boolean findCollision(Pipe pipe) {
        return (positionX + WIDTH > pipe.getPositionX() && (positionX < pipe.getPositionX() + pipe.getWidth()))
        && ((positionY < pipe.getPositionY() + pipe.getHeight() && positionY + HEIGHT > pipe.getPositionY()));
    }
    
    private void jump() {
        velocity += -5;
        lastJumpTime = System.currentTimeMillis();
    }
    
    private boolean jumpAllowed() {
        return System.currentTimeMillis() - lastJumpTime > JUMP_COOLDOWN_MS;
    }
    
    public float getPositionX() {
        return positionX;
    }
    
    public float getPositionY() {
        return positionY;
    }
    
    public static void setImage(Image img) {
        image = img;
    }
    
    public int getScore() {
        return score;
    }
    
    public float getNormalisedVelocity() {
        return velocity / maxVelocity;
    }
    
    public void incrementScore(int scoreIncrement) { 
        score += scoreIncrement;
    }
    
    public NeuralNetwork getNeuralNetwork() {
        return neuralNetwork;
    }
}