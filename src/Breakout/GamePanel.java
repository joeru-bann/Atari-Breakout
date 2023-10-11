package Breakout;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.Timer;
import java.awt.event.*;  //user input controls
import java.awt.image.*;
import javax.sound.sampled.*;
import java.util.ArrayList;
import java.awt.Toolkit;
import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

    public class GamePanel extends JPanel implements Runnable, MouseListener, MouseMotionListener, ActionListener, KeyListener {
    private final Background bg;

    static final int GAME_WIDTH = 700;
    static final int GAME_HEIGHT = (int) (GAME_WIDTH * (1.1));
    private final int screenWidth;
    private final int screenHeight;

    private double scaleX;
    private double scaleY;

    static final Dimension SCREEN_SIZE1 = new Dimension(GAME_WIDTH, GAME_HEIGHT);

    ArrayList<Ball> balls = new ArrayList<>();
    ArrayList<PowerUpBall> pballs = new ArrayList<>();

    Point cursorPos;

    int PADDLE_WIDTH = 100;
    int PADDLE_HEIGHT = 10;

    Timer time;
    private boolean running;
    private boolean paused;
    private boolean processingP = false;
    private long lastPauseTime = 0;
    private long totalPausedTime = 0;

    int r, gr, b; //bg values
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

    private final UI livesUI;
    private final UI scoreUI;
    private final UI hScoreUI;
    private final UI bLeftUI;

    int[] highScore = {0, 0, 0};
    int[] leaderboard = new int[3]; // Array to store the top 3 scores

    private static final String FILE_PATH = "data/highscores.txt";

    String welcomeMessage = "WELCOME TO BRUMBLY BREAKOUT \n SPACE to play";
    String powerTypeMessage = "Press 'P' To see power up types";
    String hScoreDisplay = "hscore" + (highScore);
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
    boolean keyPressed = false;

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

    GamePanel(int screenWidth, int screenHeight)  {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        addMouseMotionListener(this);
        addMouseListener(this);

        calculateScale();
        readHighScores();

        time = new Timer(80,this);
        random = new Random();

        bg = new Background();
        brick = new Brick[rows][columns];

        livesUI = new UI(GAME_WIDTH - 600, GAME_HEIGHT - 20, Color.RED, "Lives: ", atari);
        scoreUI = new UI(GAME_WIDTH - 450, GAME_HEIGHT - 20,  Color.GREEN, "Score: ", atari);
        hScoreUI = new UI(GAME_WIDTH - 120, GAME_HEIGHT - 20, Color.MAGENTA, "High: ", atari);
        bLeftUI = new UI(GAME_WIDTH - 300, GAME_HEIGHT - 20, Color.YELLOW, "bricks: ", atari);

        try {
            InputStream fontLocation = getClass().getResourceAsStream("atariFonts/Atari.ttf");
            atari = Font.createFont(Font.TRUETYPE_FONT, fontLocation).deriveFont(13f);

        } catch (Exception e) {
            e.printStackTrace();
        }

        running = true;
        menuModePaddles();
        newBricks();
        newBall(ballType);
        newWelcome();

        this.setFocusable(true);
        this.setPreferredSize(SCREEN_SIZE1);
        this.addKeyListener(this);

        gameThread = new Thread(this);
        gameThread.start();


    }

    private void calculateScale() {
        scaleX = (double) screenWidth / GAME_WIDTH;
        scaleY = (double) screenHeight / GAME_HEIGHT;
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
        if ((ballType == "default")) {
            Ball ball = new Ball(ballX, ballY, BALL_DIAMETER, BALL_DIAMETER, 3);
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
        instructionMessage = "the aim of the game is to \n destroy all blocks \n on the screen using the paddle \n to bounce the ball into the bricks \n \n for control use: \n Mouse input, 'A' + 'D' or <-  -> keys  \n \n 'space' to play or 'Q' go back";
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
        time.start();
        newPaddles();
        newBall(ballType);
        destroyWelcome();
        newBricks();

        lives = 10;//= baseLives - level;
        score = 0;
        ballColour = Color.white;
        running = true;
        menuActive = false;
        System.out.println("begingame");

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

        Graphics2D g2d = (Graphics2D) g;

        buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        graphics = buffer.getGraphics();

        draw(graphics);
        g2d.scale(scaleX, scaleY);
        g.drawImage(buffer, 0, 0, this);
    }
    public void draw(Graphics g) {
        allCleared = true;

        if (!menuActive) {

            switch (choice) {  //altering choice anywhere in gamePanel allows the ball the change
                case 0 -> ballColour = Color.cyan;
                case 1 -> ballColour = Color.magenta;
                case 2 -> ballColour = Color.red;
                case 3 -> ballColour = Color.orange;
                case 4 -> ballColour = Color.yellow;
                case 5 -> ballColour = Color.green;
                default -> ballColour = Color.white;
            }
        }
        bg.draw(g, r, gr, b, GAME_WIDTH , GAME_HEIGHT);
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

            switch (powerUp) {
                case 0 -> pball.draw(g, Color.green);
                case 1 -> pball.draw(g, Color.magenta);
                case 2 -> pball.draw(g, Color.orange);
                case 3 -> pball.draw(g, Color.yellow);
            }

        }
        if (allCleared) {
            allCleared = false;
            writeLeaderBoard();
            beginMenuMode();
            welcomeMessage = "YOU WON! \n  ";
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
        int middleX = (paddle1.x) + ((10 - lives) *10);
        if (lives > 0) {
            lives--;
            playSound("lose_life.wav");
            if (!menuActive) {
                choice = random.nextInt(6);
                paddleSize();
                paddle1 = new Paddle(middleX, GAME_HEIGHT - (PADDLE_HEIGHT - DISTANCE / 2) - 50, PADDLE_WIDTH, PADDLE_HEIGHT);
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
            int ballTrueY = arrayBall.y - arrayBall.height;

            if (ballTrueY <= paddle1.y && arrayBall.intersects(paddle1)) {

                double inclination = relativePosition * 1.6; // Maximum inclination angle of 1.6

                if (menuActive) {
                    inclination = getRandomInclination();
                }
                arrayBall.dy = -Math.abs(arrayBall.dy);

                // Calculate the rate of change of the vertical component
                double previousY = arrayBall.y - arrayBall.dy;
                double verticalChange = Math.abs(arrayBall.y - previousY);

                // Check if the ball has a low vertical change (slow rebound)
                if (verticalChange < 1.0) { //
                    // Increase the vertical motion of the ball
                    arrayBall.setDX(inclination);
                    arrayBall.dy *= 1.3; // You can adjust this factor to control vertical speed
                } else {
                    normalizeDirection(i);
                    arrayBall.setDX(inclination);
                }
                playSound("paddle_hit.wav");

            }
        }
        if (createPowerUp && pball != null && (pball.y > GAME_HEIGHT || pball.x > 950 || pball.x < 0)) {
            pballs.remove(pball);
            pball = null;
        } else if (createPowerUp && pball != null && pball.intersects(paddle1)) {
            pballs.remove(pball);
            pball = null;
            switch (powerUp) {
                case 0 -> {
                    paddle1 = new Paddle(paddle1.x, GAME_HEIGHT - (PADDLE_HEIGHT - DISTANCE / 2) - 50, GAME_WIDTH / 4, PADDLE_HEIGHT);
                }
                case 1 -> {
                    newBall(ballType);
                }
                case 2 -> {
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
                }
                case 3 -> {
                    System.out.println("explode area on next impact");
                    explosiveBall = balls.get(0);
                }
                default -> System.out.println("Error on powerUp switch case");
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

        return switch (inclinationSelection) {
            case 0 -> 1.6;
            case 1 -> 1.4;
            case 2 -> 1;
            default -> 0.7;
        };
    }
    private void normalizeDirection(int i) {
        Ball arrayBall = balls.get(i);
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
                        playSound("brick_hit.wav");
                        arrayBall.dy = -arrayBall.dy;

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
                                newPowerUpBall(i);
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

    public void newPowerUpBall(int i) {
        Ball arrayBall = balls.get(i);
        pball = new PowerUpBall(arrayBall.x, arrayBall.y, BALL_DIAMETER, BALL_DIAMETER, 3);
        pball.setDY(1);
        pballs.add(pball);
        createPowerUp = true;
    }

    private void handleBrickScore(int brickIndex) {
        switch (brickIndex) {
            case 0, 1 -> score += 7;
            case 2, 3 -> score += 5;
            case 4, 5 -> score += 3;
            default -> score += 1;
        }
    }

    private void setCursorPos(int x, int y){
        try{
            Robot robot = new Robot();
            robot.mouseMove(x, y);
        } catch (AWTException ex){
            ex.printStackTrace();
        }

    }
        @Override
        public void run() {
            int dx = 0;
            int dy = 0;
            int speed = 0;
            int x = 0;
            int y = 0;
            for (int i = 0; i < balls.size(); i++) {
                Ball arrayBall = balls.get(i);
                dx = (int) arrayBall.dx;
                dy = (int) arrayBall.dy;
                speed = (int) 0.6;
                x = arrayBall.x;
                y = arrayBall.y;
            }


            double desiredFPS = 240.0; // Increase the frame rate
            double desiredFrameTime = 1_000_000_000 / desiredFPS;

            long lastTime = System.nanoTime();
            double deltaTime = 0;

            while (true) {
                long now = System.nanoTime();
                long elapsedTime = now - lastTime;
                lastTime = now;

                if (running && !paused) {
                    // Update the game logic based on deltaTime
                    deltaTime += elapsedTime;

                    while (deltaTime >= desiredFrameTime) {
                        move(); // Convert deltaTime to seconds for time-based movement

                        // Implement continuous collision detection and response
                        double alpha = (deltaTime / desiredFrameTime);
                        int dFrameTime = (int) desiredFrameTime;
                        interpolatePosition(alpha, dx, dy, speed,x,y, dFrameTime);
                        checkCollision();
                        interpolatePosition(1.0 - alpha, dx, dy, speed, x, y, dFrameTime);
                    powerUpEnder();
                        deltaTime -= desiredFrameTime;
                    }

                    repaint();
                } else if (paused) {
                    // Calculate the paused time
                    long pauseDuration = now - lastPauseTime - totalPausedTime;
                    try {
                        Thread.sleep(1); // Add a delay to avoid busy-waiting
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    totalPausedTime += pauseDuration;
                }
            }
        }

        // Interpolate the ball's position
        public void interpolatePosition(double alpha, int dx, int dy, int speed, int x, int y, int desiredFrameTime) {

            double interpolatedX = x + alpha * dx * speed * desiredFrameTime / 1_000_000_000;
            double interpolatedY = y + alpha * dy * speed * desiredFrameTime / 1_000_000_000;
            x = (int) interpolatedX;
            y = (int) interpolatedY;


            if (alpha ==1.0) {
                for (int i = 0; i < balls.size(); i++) {
                    Ball arrayBall = balls.get(i);
                    arrayBall.setDX(dx);
                    arrayBall.setDY(dy);
                }

            }
        }


        //MouseListening methods for movement interactions
        public void mouseMoved(MouseEvent e) { //1:1 mouse moving ratio
        if(!keyPressed && !paused) {
            cursorPos = e.getPoint();
             int mouseX = e.getX();
            int paddleWidth = (int) paddle1.getWidth();
            int paddleX = mouseX - paddleWidth / 2;

            // Make sure the paddle does not move out of the panel
            paddleX = Math.max(0, Math.min(paddleX, GAME_WIDTH - paddleWidth));

            paddle1.x = paddleX;
            }
            else {}
        }
        public void mousePressed(MouseEvent e) {
        }
        public void mouseDragged(MouseEvent e) {
        }
        public void mouseEntered(MouseEvent e) {
            System.out.println("entered");
        }
        public void mouseExited(MouseEvent e) {
            System.out.println("exited");
        }
        public void mouseClicked(MouseEvent e) {
        }
        public void mouseReleased(MouseEvent e) {
        }
        public void actionPerformed(ActionEvent e) { //placeholder
        }
        public void keyTyped(KeyEvent e) {
        }
        public void keyPressed(KeyEvent e) { //Keyboard inputs for movement + navigation
            keyPressed = true;
            if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && !menuActive) {
                paddle1.setDeltaX((int) -1.4);
            }

            if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && !menuActive) {
                paddle1.setDeltaX((int) +1.4);
            }
            if (e.getKeyCode() == KeyEvent.VK_I && menuActive) {
                destroyWelcome();
                showInstructions();
            }
            if (e.getKeyCode() == KeyEvent.VK_P && menuActive) {
                    destroyWelcome();
                    showPowerUpTypes();
            }
            //navigating to menu from instructions
            if (e.getKeyCode() == KeyEvent.VK_Q && (menuActive) && (instructionsShown)) {
                resetWelcome(); //sets strings to default messages
            }
            if (e.getKeyCode() == KeyEvent.VK_L && menuActive) {
                destroyWelcome();
                showLeaderBoard();
            }
                if (e.getKeyCode() == KeyEvent.VK_SPACE && !processingP) {
                    processingP = true; // Set the flag to true while processing the "P" key

                    if (running && !menuActive) {
                        if (!paused) { //pausing
                            running = false;
                            paused = true;
                            lastPauseTime = System.nanoTime();
                            System.out.println("pause");
                            pauseMenu();
                        }
                    } else if (paused) { //unpausing
                        long now = System.nanoTime();
                        running = true;
                        paused = false;
                        long pauseDuration = now - lastPauseTime;
                        totalPausedTime += pauseDuration;
                        System.out.println("unpause");
                        pauseMenu();
                        if (cursorPos !=null){
                            Point windowXY = getLocationOnScreen();
                            int x = windowXY.x + cursorPos.x;
                            int y = windowXY.y + cursorPos.y;
                            setCursorPos(x, y);
                        }
                    }
                    else if(menuActive){
                        for(int i = 0; i < balls.size(); i++){
                            balls.remove(i);//default ball colour
                        }
                        beginGame();
                    }
                }
            if (e.getKeyCode() == KeyEvent.VK_Q && (menuActive) && ((powerUpTypesShown)||(leaderBoardShown))) {
                resetWelcome();
            }
        }

        //stopping paddle after releasing key
        public void keyReleased(KeyEvent e) {
            keyPressed = false;

            if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) & !menuActive) {
                paddle1.setDeltaX(0);
            }

            if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && !menuActive) {
                paddle1.setDeltaX(0);
            }
            if(e.getKeyCode() == KeyEvent.VK_SPACE){
                processingP = false;
            }
        }

        public void pauseMenu(){
            if (paused){ //pausing
                    welcomeMessage = "PAUSED";
                    powerTypeMessage = "press SPACE to continue";
                }
                else if (!paused){  //unpausing
                    destroyWelcome();

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

        if (lives < 1) { //if lose/lost
            int ran = 0;
            level = 1;
            brickCount = 232;
            PADDLE_WIDTH = PADDLE_WIDTH + lives * 10;
            ran = random.nextInt(2);

            switch (ran) {
                case 0 -> playSound("deep_you_lose.wav");
                case 1 -> playSound("you_losew.wav");
            }
            writeLeaderBoard();
            beginMenuMode();
        }
    }
    public void writeLeaderBoard() {
        if (score > highScore[0]) { //replacing 2nd
            int thirdPlace;
            thirdPlace = highScore[1];
            highScore[1] = highScore[0];
            highScore[0] = score;

            highScore[2] = thirdPlace;
            System.out.println("CIL wrote: " + highScore[0]);
            System.out.println("2nd place: " + highScore[1]);

        } else if (score > highScore[1]) { //replacing 2nd and 3rd
            highScore[2] = highScore[1];
            highScore[1] = score;
            System.out.println("3rd place: " + highScore[2]);
        } else if (score > highScore[2]) { //replacing 3rd
            highScore[2] = score;
            System.out.println("3rd place: " + highScore[2]);
        }
        writeHighScore();
    }

    public void beginMenuMode() {
        menuActive = true;
        newBricks();
        menuModePaddles();
        newWelcome();
        readHighScores(); //reading the most recent h score

        resetWelcome();
        welcomeMessage = "PRESS SPACE TO TRY AGAIN";
    }

    public void menuModePaddles() {
        paddle1 = new Paddle(0, GAME_HEIGHT - (PADDLE_HEIGHT - DISTANCE / 2) - 50, GAME_WIDTH, PADDLE_HEIGHT);
    } 

    //this method is not my own, I have referenced the source below, I give credit for the base method to the OP
    //https://stackoverflow.com/questions/34832069/creating-a-highscore-with-file-io-in-java

    public void writeHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (int i = 0; i <= 2; i++) {
                int currentHScore;
                        currentHScore = highScore[i];

                    writer.write(String.valueOf(currentHScore));
                    writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int[] readHighScores() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {

            for (int i = 0; i < 3; i++) {
                String line = reader.readLine();
                if (line != null) {
                    leaderboard[i] = Integer.parseInt(line);
                    highScore[i] = leaderboard[i];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return leaderboard;
    }
}