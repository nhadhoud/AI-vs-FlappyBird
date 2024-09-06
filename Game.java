import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Random;

public class Game extends Application {
    private int screenWidth = 1200;
    private int screenHeight = 600;
    private int floorPositionX = 0;
    private final int FLOOR_POSITION_Y = 40;
    private final float FLOOR_AND_PIPE_SPEED = 3.2f;
    private int backgroundPositionX = 0;
    
    private int bestScore = 0;
    private final int SCORE_INCREMENT = 5;
    
    private int generations = 1;

    private LinkedList<Pipe> pipes = new LinkedList<>();
    private final float PIPE_INTERVAL = 700; 
    private final float PIPE_GAP = 160;
    
    private Pipe nextTopPipe;
    private Pipe nextBottomPipe;

    private int[] networkShape = {4,7,2};

    private LinkedList<Bird> birds = new LinkedList<>();
    private int numBirds = 200;
    private Bird bestBird;
    
    private Image backgroundImage;
    private Image floorImage;
    private Image birdImage;
    private Image pipeImage;
    private Font pressStart2P;

    public static void main(String[] args) {
        launch(args);
    }
    
    public void start(Stage stage) {
        newBirds(new NeuralNetwork(networkShape), numBirds); 
        
        Canvas canvas = new Canvas(screenWidth, screenHeight);  
        GraphicsContext gc = canvas.getGraphicsContext2D();
    
        loadAssets();
        
        Pipe.setHeight(screenHeight);
        Pipe.setWidth(screenWidth/10);
        Pipe.setImage(pipeImage);
        Pipe.setSpeed(FLOOR_AND_PIPE_SPEED);
        
        Bird.setImage(birdImage);
        
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), e -> updateGame(gc)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        
        Scene scene = new Scene(new Group(canvas), screenWidth, screenHeight);
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {screenWidth = newVal.intValue(); canvas.setWidth(screenWidth);});
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {screenHeight = newVal.intValue(); canvas.setHeight(screenHeight); Pipe.setHeight(screenHeight); Pipe.setWidth(screenWidth/10); pipes.clear();});
    
        stage.setScene(scene);
        stage.setMinHeight(400);
        stage.setMinWidth(600);
        stage.setTitle("Flappy Bird");
        stage.getIcons().add(new Image("file:images/bird.png"));
        stage.show();
    }
    
    private void loadAssets() {
        try {
            backgroundImage = new Image("file:images/background.png");
            floorImage = new Image("file:images/floor.png");
            birdImage = new Image("file:images/bird.png");
            pipeImage = new Image("file:images/pipe.png");
            pressStart2P = Font.loadFont("file:fonts/PressStart2P.ttf", 20);
        } catch (Exception e) {
            System.err.println("Could not load assets: " + e.getMessage());
        }
    }
    
    private void updateGame(GraphicsContext gc) {
        gc.clearRect(0, 0, screenWidth, screenHeight);
        updateEnvironment(gc);
    
        boolean restart = true;
        boolean pipePassed = false;
        
        if (nextTopPipe == null || nextBottomPipe == null) {
            getNextPipes(birds.getFirst().getPositionX());
        }
        
        Iterator<Bird> iterator = birds.iterator();
        
        while (iterator.hasNext()) {
            Bird bird = iterator.next();
            
            bird.update(FLOOR_POSITION_Y, screenHeight);
            bird.act(getNeuralNetworkInputs(bird));
            bird.draw(gc);
            
            if (bird.findCollision(nextTopPipe) || bird.findCollision(nextBottomPipe)) {
                iterator.remove();
            } 
            else {
                restart = false; // At least one bird is alive
                if (!nextTopPipe.isPassed() && bird.getPositionX() > nextTopPipe.getPositionX() + nextTopPipe.getWidth()) {
                    bird.incrementScore(SCORE_INCREMENT);
                    pipePassed = true;
                }
            }
        }
    
        if (restart) {
            restartGame();
        }
        else if (pipePassed) {
            nextTopPipe.setPassed();
            nextBottomPipe.setPassed();
            updateScoreAndBestBird(birds.getFirst());
            getNextPipes(birds.getFirst().getPositionX());
        }
        
        displayScoreAndGen(gc);
    }
    
    private void restartGame() {
        nextTopPipe = null;
        nextBottomPipe = null;
        pipes.clear();
        bestScore = 0;
        NeuralNetwork newNeuralNetwork = new NeuralNetwork(networkShape);
        if (bestBird != null) {
            newNeuralNetwork = bestBird.getNeuralNetwork().clone();
        }
        newBirds(newNeuralNetwork,numBirds);
        generations++;
    }
    
    private void updateEnvironment(GraphicsContext gc) {
        updateBackground(gc);
        updatePipes(gc);
        updateFloor(gc);
    }
    
    private void updateBackground(GraphicsContext gc) {
        gc.drawImage(backgroundImage, backgroundPositionX + screenWidth, 0, screenWidth, screenHeight);
        gc.drawImage(backgroundImage, backgroundPositionX, 0, screenWidth, screenHeight);
        backgroundPositionX--;
        if (backgroundPositionX < -screenWidth) {
            backgroundPositionX = 0;
        }
    }
    
    private void updateFloor(GraphicsContext gc) {
        gc.drawImage(floorImage, floorPositionX + screenWidth, screenHeight - FLOOR_POSITION_Y, screenWidth, FLOOR_POSITION_Y);
        gc.drawImage(floorImage, floorPositionX, screenHeight - FLOOR_POSITION_Y, screenWidth, FLOOR_POSITION_Y);
        floorPositionX -= 3;
        if (floorPositionX < -screenWidth) {
            floorPositionX = 0;
        }
    }
    
    private void newPipes() {
        Random rand = new Random();
        if (pipes.isEmpty() || pipes.getLast().getPositionX() <  screenWidth - PIPE_INTERVAL) {
            float positionY = (float) rand.nextDouble(-Pipe.getHeight(), -PIPE_GAP) - FLOOR_POSITION_Y;
            pipes.add(new Pipe(screenWidth, positionY, true));
            pipes.add(new Pipe(screenWidth, positionY + PIPE_GAP + Pipe.getHeight(), false));
        };
    }
    
    private void updatePipes(GraphicsContext gc) {
        newPipes();
        
        //check bottom and top pipe are on screen
        for (int i = 0; i < 2; i++) {
            if (pipes.getFirst().isOffScreen()) {
                pipes.removeFirst();
            }
        }
        
        for (Pipe pipe : pipes) {
            pipe.update();
            pipe.draw(gc);
        }
    }
    
    private void getNextPipes(float birdPositionX) {
        nextTopPipe = null;
        nextBottomPipe = null;
        
        for (Pipe pipe : pipes) {
            if (!pipe.isPassed()) {
                if (pipe.isFlipped() && nextTopPipe == null) {
                    nextTopPipe = pipe;
                } 
                else if (!pipe.isFlipped() && nextBottomPipe == null) {
                    nextBottomPipe = pipe;
                }
                
                // exit once both pipes are found
                if (nextTopPipe != null && nextBottomPipe != null) {
                    break;
                }
            }
        }
    }
    
    private void newBirds(NeuralNetwork neuralNetwork, int numNewBirds) {
        birds.clear();
        for (int i = 0; i < numNewBirds; i++) {
            NeuralNetwork newNeuralNetwork = neuralNetwork.clone();
            newNeuralNetwork.mutate(0.2f,0.17f - Math.min(0.1f, bestScore/100));
            birds.add(new Bird(50, screenHeight/2, newNeuralNetwork));
        }
    }
    
    private void updateScoreAndBestBird(Bird bird) {
        if (bird.getScore() > bestScore) {
            bestScore = bird.getScore();
        }
        //assume new gen bird with same score as old gen is better
        if (bestBird == null || bestBird.getScore() <= bird.getScore()) {
            bestBird = bird;
        }
    }
    
    private void displayScoreAndGen(GraphicsContext gc) {
        gc.setFont(pressStart2P);
        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + bestScore + ", Generation: " + generations, 10, 30);
    }
    
    private float[] getNeuralNetworkInputs(Bird bird) {
        return new float[]{
            getNormalisedHorizontalDistanceToNextPipe(bird),
            getNormalisedVerticalDistanceToNextTopPipe(bird),
            getNormalisedVerticalDistanceToNextBottomPipe(bird),
            bird.getNormalisedVelocity()
        };
    }
    
    private float getNormalisedHorizontalDistanceToNextPipe(Bird bird) {
        if (nextTopPipe != null) {
            return (nextTopPipe.getPositionX() - bird.getPositionX()) / screenWidth;
        }
        return 1f;
    }
    
    private float getNormalisedVerticalDistanceToNextTopPipe(Bird bird) {
        if (nextTopPipe != null) {
            return (bird.getPositionY() - nextTopPipe.getPositionY() - nextTopPipe.getHeight()) / screenHeight;
        }
        return 1f;
    }
    
    private float getNormalisedVerticalDistanceToNextBottomPipe(Bird bird) {
        if (nextBottomPipe != null) {
            return (nextBottomPipe.getPositionY() - (bird.getPositionY() + bird.HEIGHT)) / screenHeight;
        }
        return 1f;
    }
}