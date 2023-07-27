package Breakout;

import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;  //user input controls
import java.awt.image.*;
import javax.sound.sampled.*;
import java.util.ArrayList;
import javax.swing.Timer;
import java.awt.Toolkit;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {
    private final Background bg;

    double screenFraction = 0.8; //80% of the screen size

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    // Get the user true screen size (i.e usable bounds including taskbar)
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    Rectangle screenBounds = ge.getMaximumWindowBounds();


    int screenWidth = (int) (screenBounds.getWidth() * screenFraction);
    int screenHeight = (int) (screenBounds.getHeight() * screenFraction);

    // Desired aspect ratio for the game (should match the screen aspect ratio)
    static double GAME_ASPECT_RATIO = 1.5; // 1.5 is the aspect ratio used in your example

    // Adjust GAME_HEIGHT to match the screen aspect ratio
    int GAME_WIDTH = 700;
    int GAME_HEIGHT = (int) (GAME_WIDTH * GAME_ASPECT_RATIO);

    // Calculate the window size while maintaining the aspect ratio
    double widthScaleFactor = (double) screenWidth / GAME_WIDTH;
    double heightScaleFactor = (double) screenHeight / GAME_HEIGHT;

    // Calculate the scale factor based on the desired aspect ratio
    double scaleFactor = Math.min(widthScaleFactor, heightScaleFactor);



    // Calculate the new width and height using the scaleFactor
    int newWidth = (int) (GAME_WIDTH * scaleFactor);
    int newHeight = (int) (GAME_HEIGHT * scaleFactor);


    // Calculate the margins to center the window for breakout frame
    int horizontalMargin = (screenBounds.width - newWidth) / 2;
    int verticalMargin = (screenBounds.height - newHeight) / 2;

    static final double UI_POSITION_RATIO = 0.9; // 90% from the top (10% from the bottom)

    ArrayList<Ball> balls = new ArrayList<Ball>();

    ArrayList<PowerUpBall> pballs = new ArrayList<PowerUpBall>();

    int scaleX;
    int scaleY;

    int PADDLE_WIDTH = 100;
    int PADDLE_HEIGHT = 10;

    int r;
    int gr; //bg values
    int b;

    int uiHeight = 160;
    int uiSpacing  = 150;

    static final int BALL_DIAMETER = 8;
    int level = 1;
    int brickCount = 232;
    int spawnChance;
    int powerUp;
    boolean powerUpStart = false;
    final int rows = Math.round(GAME_WIDTH / brickWidth);
    static final int columns = 8;

    static final int brickWidth = 32;
    static final int brickHeight = 10;

    static final int DISTANCE = 20;  // 0 == edge

    int lives;
    int score = 0;
    int choice = 0;

    private UI livesUI;
    private UI scoreUI;
    private UI hScoreUI;
    private UI bLeftUI;

    int inclinationSelection = 0;

    int[] highScore = {0, 0, 0};
    int[] leaderboard = new int[3]; // Array to store the top 3 scores

    private static final String FILE_PATH = "data/highscores.txt";

    String welcomeMessage = "WELCOME TO BRUMBLY BREAKOUT \n";
    String powerTypeMessage = "Press 'P' To see power up types";
    String hScoreDisplay = "hscore" + highScore;
    String instructionMessage = "Press 'I' to see instructions";

    String lBoard = "press 'L' to see leader board";

    String ballType = "default";

    boolean menuActive = true;
    boolean soundPlaying;
    boolean allCleared;

    boolean instructionsShown = false;
    boolean leaderBoardShown = false;
    boolean powerUpTypesShown = false;

    boolean createPowerUp = false;

    Thread gameThread;
    BufferedImage buffer;
    Graphics graphics;
    Paddle paddle1;
    PowerUpBall pball;
    Ball explosiveBall;
    Brick[][] brick;
    Welcome welcome;
    Font atari;
    Color ballColour;

    Random random;
    Clip sound;
    long powerUpEnd = 0;

    GamePanel() {
        calculateScale();
        readHighScores();
        updateUIPositions();
        setDoubleBuffered(true);
        random = new Random();

        bg = new Background();
        brick = new Brick[rows][columns];

        try {
            InputStream fontLocation = getClass().getResourceAsStream("atariFonts/Atari.ttf");
            atari = Font.createFont(Font.TRUETYPE_FONT, fontLocation).deriveFont(12f);
        } catch (Exception e) {
            e.printStackTrace();
        }


        menuModePaddles();
        newBricks();
        newBall(ballType);
        newWelcome();

        this.setFocusable(true);
        //this.setPreferredSize(screenSize);
        this.addKeyListener(new AL());

        gameThread = new Thread(this);
        gameThread.start();

        Color[] rainbowColours = {
                Color.RED,
                Color.ORANGE,
                Color.YELLOW,
                Color.GREEN,
                Color.BLUE,
                new Color(75, 0, 130), // Indigo
                new Color(238, 130, 238) // Violet
        };

        while (createPowerUp) {
            for (Color rgbcolour : rainbowColours) {
                //pball.setColor(rgbcolour);
                System.out.println(rgbcolour + " = rgb c");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void updateUIPositions() {
        int visibleHeight = newHeight;
        int visibleWidth = Math.min(getWidth(), GAME_WIDTH);

        int uiPositionX = (int) ((visibleWidth));
        int uiPositionY = (visibleHeight - uiHeight); // Place UI elements at the bottom of the screen

        System.out.println(uiPositionX + " UI X");
        System.out.println(uiPositionY + " UI Y");


        livesUI = new UI(uiPositionX + (uiSpacing * 4), uiPositionY, Color.RED, "Lives: ", atari);
        scoreUI = new UI(uiPositionX + (uiSpacing * 3), uiPositionY, Color.GREEN, "Score: ", atari);
        hScoreUI = new UI(uiPositionX + (uiSpacing * 2), uiPositionY, Color.MAGENTA, "HighScore: ", atari);
        bLeftUI = new UI(uiPositionX + (uiSpacing) - 50, uiPositionY, Color.YELLOW, "bricks: ", atari);
    }

    private void calculateScale() {
        scaleX = newWidth;
        scaleY = newHeight;
    }

    public void newPaddles() {
        //new paddle instance from class
        PADDLE_WIDTH = 100; //resetting paddle to default width
        paddle1 = new Paddle((GAME_WIDTH - PADDLE_WIDTH) / 2, GAME_HEIGHT - (PADDLE_HEIGHT - DISTANCE / 2) - 50, PADDLE_WIDTH, PADDLE_HEIGHT);

    }

    public void newBricks() {
        for (int p = 0; p < rows; p++) {
            for (int l = 0; l < columns; l++) {
                brick[p][l] = new Brick(p, l, brickWidth, brickHeight);
            }
        }
    }

    // Spawns a new Ball, makes it go to the bottom  resetting the hits.
    public void newBall(String ballType) {
        int ballX = (GAME_WIDTH / 2) - (BALL_DIAMETER / 2);
        int ballY = (GAME_HEIGHT / 2) - (BALL_DIAMETER / 2);
        if (ballType == "default") {
            Ball ball = new Ball(ballX, ballY, BALL_DIAMETER, BALL_DIAMETER, 5);
            ball.setDY(1);
            balls.add(ball);
            //hits = 0;
        }
    }


    public void newWelcome() {
        welcome = new Welcome(0, 0, 0, 0);
    }

    public void destroyWelcome() {
        welcomeMessage = " ";  //clear message
        powerTypeMessage = " ";
        instructionMessage = " ";
        lBoard = " ";
        instructionsShown = false;
        leaderBoardShown = false;

    }

    public void resetWelcome() {
        welcomeMessage = "WELCOME TO BRUMBLY BREAKOUT \n";
        powerTypeMessage = "'P' To see power up types";
        instructionMessage = "'I' to see instructions";
        lBoard = "'L' to see leader board";
        instructionsShown = false;
        leaderBoardShown = false;
    }

    public void showInstructions() {
        instructionsShown = true;
        instructionMessage = "the aim of the game is to \n destroy all blocks \n on the screen using the paddle \n to bounce the ball into the bricks \n \n for control use: \n 'A' + 'D' or <-  -> keys \n \n 'space' to play or 'Q' go back";
    }
    public void showPowerUpTypes() {
        powerUpTypesShown = true;
        powerTypeMessage = "Orange = background colour change \n " + "Magenta = extra ball \n Green = expand paddle \n yellow = explosive ball ";
    }

    public void showLeaderBoard() {
        readHighScores();
        leaderBoardShown = true;
        lBoard = "1st " + leaderboard[0] + "\n" + "2nd " + leaderboard[1] + "\n" + "3rd " + leaderboard[2] + "\n";
    }

    public void beginGame() {
        ballType = "default";

        newPaddles();
        newBall(ballType);
        destroyWelcome();
        newBricks();

        lives = 10;//= baseLives - level;
        score = 0;
        ballColour = Color.white;
    }

    public void playSound(String fileName) {

        if (!soundPlaying) {
            try {
                sound = AudioSystem.getClip();
                sound.open(AudioSystem.getAudioInputStream(getClass().getResource("audio/" + fileName)));
                soundPlaying = true;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error occurred whilst attempting to play Breakout.audio");
            }
        }
        if (soundPlaying) {
            sound.start();
        }

        soundPlaying = false;
    }


        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Calculate the scale factors for width and height
            double scaleX = (double) getWidth() / GAME_WIDTH;
            double scaleY = (double) getHeight() / GAME_HEIGHT;
            double scale = Math.min(scaleX, scaleY);

            // Calculate the new scaled dimensions
            int newScaledWidth = (int) (GAME_WIDTH * scale);
            int newScaledHeight = (int) (GAME_HEIGHT * scale);

            // Create a new BufferedImage with the scaled dimensions
            BufferedImage scaledImage = new BufferedImage(newScaledWidth, newScaledHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = scaledImage.createGraphics();

            // Clear the scaled image
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, newScaledWidth, newScaledHeight);

            // Draw the game content onto the scaled image with scaling
            g2d.scale(scale, scale);
            draw(g2d);

            // Dispose the graphics object used for the scaled image
            g2d.dispose();

            // Draw the scaled image to the screen using Graphics.drawImage
            g.drawImage(scaledImage, 0, 0, this);
        }

    public void draw(Graphics g) {
        allCleared = true;

        if (!menuActive) {

            switch (choice) {  //altering choice anywhere in gamePanel allows the ball the change
                case 0:
                    ballColour = Color.cyan;
                    break;
                case 1:
                    ballColour = Color.magenta;
                    break;
                case 2:
                    ballColour = Color.red;
                    break;
                case 3:
                    ballColour = Color.orange;
                    break;
                case 4:
                    ballColour = Color.yellow;
                    break;
                case 5:
                    ballColour = Color.green;
                    break;
                default:
                    ballColour = Color.white;
                    break;
            }
        }
        bg.draw(g, r, gr, b, newWidth, newHeight);
        paddle1.draw(g);
        for (int i = 0; i < balls.size(); i++) {
            Ball arrayBall = balls.get(i);
            arrayBall.draw(g, Color.WHITE);//default ball colour

        }
        if (explosiveBall != null) {
            explosiveBall.draw(g, Color.RED);
        }

        welcome.draw(g, atari, GAME_WIDTH, GAME_HEIGHT, welcomeMessage, instructionMessage, lBoard, powerTypeMessage);

        for (int p = 0; p < rows; p++) {
            for (int l = 0; l < columns; l++) {
                if (brick[p][l] != null) {
                    brick[p][l].draw(g);
                    allCleared = false;
                }
            }
        }
        if (createPowerUp && pball != null) {

            switch (powerUp){
                case 0:
                    pball.draw(g, Color.green);
                    //System.out.println("green");
                    break;
                case 1:
                    pball.draw(g, Color.magenta);
                    //System.out.println("magenta");
                    break;
                case 2:
                    pball.draw(g, Color.orange);
                    //System.out.println("orange");
                    break;
                case 3:
                    pball.draw(g, Color.yellow);
                    // System.out.println("Yellow");
                    break;


            }


        }
        if (allCleared) {
            beginMenuMode();
            welcomeMessage = "YOU WON! YIPEEE";
            hScoreDisplay = ("High score: " + highScore);

        }
        //Keep draw statements here for atari font to work
        livesUI.draw((Graphics2D) g, lives);
        scoreUI.draw((Graphics2D) g, score);
        hScoreUI.draw((Graphics2D) g, highScore[0]);
        bLeftUI.draw((Graphics2D) g, brickCount);

        Toolkit.getDefaultToolkit().sync();
        // Making sure display refreshes real-time for paint method

    }

    public void move() {

        paddle1.move();
        for (int i = 0; i < balls.size(); i++) {
            Ball arrayBall = balls.get(i);
            arrayBall.move();//default ball color
        }


        if (createPowerUp && pball != null) {
            pball.move();
        }
    }

    public void checkCollision() {
        handleBoundaryCollision();
        handlePaddleCollision();
        handleBrickCollision();
    }

    private void handleBoundaryCollision() { //walls and roof

        if (paddle1.x <= 0)
            paddle1.x = 0;

        if (paddle1.x >= GAME_WIDTH - paddle1.width)
            paddle1.x = GAME_WIDTH - paddle1.width;
        for (int i = 0; i < balls.size(); i++) {
            Ball arrayBall = balls.get(i);
            if (arrayBall.y <= 0) { //roof
                arrayBall.dy = -arrayBall.dy;
                playSound("boundary_hit.wav");
                paddle1.getPaddleWidth();

            }

            if ((arrayBall.y >= GAME_HEIGHT - BALL_DIAMETER) || arrayBall.x > 953 || (arrayBall.x < -BALL_DIAMETER - 2)) {
                //System.out.println("handle out "+ ball.x + " y: "+ ball.y);
                balls.remove(i);
                if (balls.size() == 0) {
                    handleBallOut();
                }
                if (explosiveBall == arrayBall) {
                    explosiveBall = null;
                }
            }
            if (arrayBall.x <= 0 || arrayBall.x >= GAME_WIDTH - BALL_DIAMETER) {
                arrayBall.dx = -arrayBall.dx;
                playSound("boundary_hit.wav");
            }

            if (menuActive) {
                choice = random.nextInt(6);
            }
        }
    }

    public void paddleSize() {
        if (PADDLE_WIDTH > 40) {
            PADDLE_WIDTH = PADDLE_WIDTH - 10; // 1/10th of size
            paddle1.setPaddleWidth(PADDLE_WIDTH);
        } else {
        } //stops reduction @ 4 lives
    }

    private void handleBallOut() { //below screen
        paddle1.x = GAME_WIDTH / 2 - 50; //resetting paddle to middle
        if (lives > 0) {
            lives--;
            playSound("lose_life.wav");
            if (!menuActive) {
                choice = random.nextInt(6);
                paddleSize();
                paddle1 = new Paddle(paddle1.x, GAME_HEIGHT - (PADDLE_HEIGHT - DISTANCE / 2) - 50, PADDLE_WIDTH, PADDLE_HEIGHT);
            }
        }
        checkIfLost(lives);
        newBall(ballType);
        playSound("boundary_hit.wav");

    }

    private void handlePaddleCollision() { //hitting paddle
        for (int i = 0; i < balls.size(); i++) {
            Ball arrayBall = balls.get(i);
            double ballCenterX = arrayBall.x + arrayBall.width / 2.0;
            double paddleCenterX = paddle1.x + paddle1.width / 2.0;

            double relativePosition = (ballCenterX - paddleCenterX) / (paddle1.width / 2.0);

            if (arrayBall.y > (paddle1.y) -5 && arrayBall.intersects(paddle1)) {  //further down == bigger number so using > operator

                double inclination = relativePosition * 1.6; // Maximum inclination angle of 1.6

                if (menuActive) {
                    inclination = getRandomInclination();
                }
                arrayBall.dy = -arrayBall.dy;

                normalizeDirection(i);

                arrayBall.setDX(inclination); //go into diagonal motion
                playSound("paddle_hit.wav");

            }
        }
        if (createPowerUp && pball != null && (pball.y > GAME_HEIGHT - 54 || pball.x > 950 || pball.x < 0)) {
            pballs.remove(pball);
            pball = null;
            //System.out.println("pball doesnt intersect");
        } else if (createPowerUp && pball != null && pball.intersects(paddle1)) {
            pballs.remove(pball);
            pball = null;

            switch (powerUp) {
                case 0:
                    System.out.println("expand paddle");
                    paddle1 = new Paddle(paddle1.x, GAME_HEIGHT - (PADDLE_HEIGHT - DISTANCE / 2) - 50, GAME_WIDTH / 4, PADDLE_HEIGHT);
                    break;
                case 1:
                    System.out.println("add another ball");
                    newBall(ballType);
                    break;
                case 2:
                    System.out.println("change bg");
                    int powerTime = 4000; //2 secs
                    int ranR = changeBG(1, 255);
                    int ranG = changeBG(1, 255);
                    int ranB = changeBG(1, 255);
                    r = ranR;
                    gr = ranG;
                    b = ranB;

                    System.out.println("r= " + ranR + " g= " + ranG + " b= " + ranB);
                    Timer timer = new Timer(powerTime, new ActionListener() {
                        public void actionPerformed(ActionEvent e) { //resetting
                           r = 0;
                           gr = 0;
                           b = 0;
                        }
                    });
                    timer.setRepeats(false); //false to run once
                    timer.start();

                    break;
                case 3:
                    System.out.println("explode area on next impact");
                    explosiveBall = balls.get(0);

                    break;

                default:
                    System.out.println("Error on powerUp switch case");
            }
            long fiveseconds = 5000000000L;
            powerUpEnd = System.nanoTime() + fiveseconds;
            powerUpStart = true;

        }

    }

    public int changeBG(int min, int max) {
        int range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
    }

    private double getRandomInclination() {
        int inclinationSelection = random.nextInt(3);

        switch (inclinationSelection) {
            case 0:
                return 1.6;
            case 1:
                return 1.4;
            case 2:
                return 1;
            default:
                return 0.7;
        }
    }

    private void normalizeDirection(int i) {
        Ball arrayBall = balls.get(i);
        //System.out.println("normalizeDirection");
        double magnitude = Math.sqrt((arrayBall.dx * arrayBall.dx + arrayBall.dy * arrayBall.dy));
        arrayBall.dx /= ((magnitude) * 3);
        arrayBall.dy /= magnitude / 1.2;
    }

    private void handleBrickCollision() {
        for (int i = 0; i < balls.size(); i++) {
            Ball arrayBall = balls.get(i);
            for (int r = 0; r < rows; r++) {
                for (int t = 0; t < columns; t++) {
                    if (brick[r][t] != null && arrayBall.intersects(brick[r][t])) {
                        arrayBall.dy = -arrayBall.dy;
                        playSound("brick_hit.wav");
                        //normalizeDirection();


                        if (!menuActive) { //if game is played
                            if (arrayBall == explosiveBall) {
                                for (int y = -1; y <= 1; y++) {
                                    for (int x = -1; x <= 1; x++) {
                                        if (r + y >= 0 && t + x >= 0 && r + y < rows && t + x < columns) {
                                            brick[r + y][t + x] = null;
                                            brickCount--;
                                            handleBrickScore(t + x);
                                        }
                                    }
                                }
                                explosiveBall = null;
                            } else {
                                handleBrickScore(t);
                                brickCount--;
                                brick[r][t] = null;
                            }
                            spawnChance = random.nextInt(10);
                            if (spawnChance > 5 && pball == null) {
                                newPowerUpBall(graphics, i);
                                powerUp = random.nextInt(4);
                            }
                        } else { //if main menu
                            choice = random.nextInt(4);
                        }

                    }
                }
            }
        }
    }

    public void newPowerUpBall(Graphics g, int i) {
        Ball arrayBall = balls.get(i);
        pball = new PowerUpBall(arrayBall.x, arrayBall.y, BALL_DIAMETER, BALL_DIAMETER, 5);
        pball.setDY(1);
        pballs.add(pball);
        createPowerUp = true;
    }

    private void handleBrickScore(int brickIndex) {
        switch (brickIndex) {
            case 0:
            case 1:
                score += 7;
                break;
            case 2:
            case 3:
                score += 5;
                break;
            case 4:
            case 5:
                score += 3;
                break;
            default:
                score += 1;
                break;
        }
    }

    @Override
    public void run() {
        double desiredFPS = 100.0;
        double desiredFrameTime = 1_000_000_000 / desiredFPS; // Calculate the desired frame time in nanoseconds

        long lastTime = System.nanoTime();
        double deltaTime = 0;

        while (true) {
            long now = System.nanoTime();
            long elapsedTime = now - lastTime;
            lastTime = now;

            deltaTime += elapsedTime;

            // Update the game logic based on deltaTime
            while (deltaTime >= desiredFrameTime) {
                move(); // Convert deltaTime to seconds for time-based movement
                checkCollision();
                powerUpEnder();
                deltaTime -= desiredFrameTime;
            }

            repaint();
        }
    }


    public class AL extends KeyAdapter {
        public void keyPressed(KeyEvent e) { //Player inputs for movement + navigation

            if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && !menuActive) {
                paddle1.setDeltaX((int) -1.4);
            }

            if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && !menuActive) {
                paddle1.setDeltaX((int) +1.4);
            }

            if (e.getKeyCode() == KeyEvent.VK_SPACE && menuActive) {
                menuActive = false;

                for(int i = 0; i < balls.size(); i++){
                    Ball arrayBall = balls.get(i);
                    balls.remove(i);//default ball colour
                }
                beginGame();
            }
            if (e.getKeyCode() == KeyEvent.VK_I && menuActive) {
                destroyWelcome();
                showInstructions();
            }
            //navigating back to menu screen from instructions message
            if (e.getKeyCode() == KeyEvent.VK_Q && (menuActive) && (instructionsShown)) {
                resetWelcome(); //sets strings to default messages
            }
            if (e.getKeyCode() == KeyEvent.VK_L && menuActive) {
                destroyWelcome();
                showLeaderBoard();
            }
            if (e.getKeyCode() == KeyEvent.VK_P && menuActive) {
                destroyWelcome();
                showPowerUpTypes();
            }
            if (e.getKeyCode() == KeyEvent.VK_Q && (menuActive) && (powerUpTypesShown)) {
                resetWelcome(); //sets strings to default messages
            }
            if (e.getKeyCode() == KeyEvent.VK_Q && (menuActive) && (leaderBoardShown)) {
                resetWelcome(); //sets strings to default messages
            }
        }


        //stopping paddle after releasing key - resetting deltaX
        public void keyReleased(KeyEvent e) {

            if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) & !menuActive) {
                paddle1.setDeltaX(0);
            }

            if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && !menuActive) {
                paddle1.setDeltaX(0);
            }
        }
    }
    public void powerUpEnder(){
        if(powerUpStart) {
            if (powerUpEnd <= System.nanoTime()) {
                paddle1 = new Paddle(paddle1.x, GAME_HEIGHT - (PADDLE_HEIGHT - DISTANCE / 2) - 50, PADDLE_WIDTH, PADDLE_HEIGHT);
                powerUpStart = false;
            }
        }

    }
    public void checkIfLost(int lives) {
        int remainingLives = lives;
        
        if (remainingLives < 1) { //if lose/lost
            int ran = 0;
            level = 1;
            brickCount = 232;
            PADDLE_WIDTH = PADDLE_WIDTH + lives * 10;
            ran = random.nextInt(2);

            switch (ran){
                case 0 : playSound("deep_you_lose.wav");
                break;
                case 1: playSound("you_losew.wav");
            }

                if (score > highScore[0]) { //replacing 2nd
                    int thirdPlace;
                    thirdPlace = highScore[1];
                    highScore[1] = highScore[0];

                    writeHighScore(highScore[1], 1);
                    highScore[0] = score;

                    highScore[2] = thirdPlace;
                    writeHighScore(highScore[2], 2);

                    writeHighScore(highScore[0], 0);
                    System.out.println("CIL wrote: " + highScore[0]);
                    System.out.println("2nd place: " + highScore[1]);

                } else if (score > highScore[1] && score <= highScore[0]) { //replacing 2nd and 3rd
                    highScore[2] = highScore[1];
                    highScore[1] = score;
                    writeHighScore(highScore[1], 1);
                    writeHighScore(highScore[2], 2);
                    System.out.println("3rd place: " + highScore[2]);
                }else if (score <= highScore[1] && score > highScore[2]) { //replacing 3rd
                    highScore[2] = score;
                    writeHighScore(highScore[2], 2);
                    System.out.println("3rd place: " + highScore[2]);
                }


            beginMenuMode();
        }


    }

    public void beginMenuMode() {
        menuModePaddles();
        newWelcome();
        readHighScores(); //reading the most recent h score

        menuActive = true;
        resetWelcome();
        welcomeMessage = "PRESS SPACE TO TRY AGAIN";

    }

    public void menuModePaddles() {
        paddle1 = new Paddle(0, GAME_HEIGHT - (PADDLE_HEIGHT - DISTANCE / 2) - 50, GAME_WIDTH, PADDLE_HEIGHT);
    } 

    //this method is not fully my own, I have referenced the source below, I give partial credit for the base method to the OP
    //https://stackoverflow.com/questions/34832069/creating-a-highscore-with-file-io-in-java

    public int [] writeHighScore(int hScpre, int place) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (int i = 0; i <= 2; i++, place++) {
                int currentHScore;
                        currentHScore = highScore[i];
                if (place == i) {
                    writer.write(String.valueOf(currentHScore));
                    writer.newLine();
                    //System.out.println("wrote score: " + i + highScore[i]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return highScore;
    }
    public int[] readHighScores() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {

            for (int i = 0; i < 3; i++) {
                String line = reader.readLine();
                if (line != null) {
                    leaderboard[i] = Integer.parseInt(line);
                    System.out.println("read score: " + line);
                    highScore[i] = leaderboard[i];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return leaderboard;
    }

}
