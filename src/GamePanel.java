
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;  //user input controls
import java.awt.image.*;
import javax.sound.sampled.*;

import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {

    static final int GAME_WIDTH = 461;
    static final int GAME_HEIGHT = (int) (GAME_WIDTH * (1.15));
    static final Dimension SCREEN_SIZE1 = new Dimension(GAME_WIDTH, GAME_HEIGHT);

    static final int PADDLE_WIDTH = 55;
    static final int PADDLE_HEIGHT = 10;

    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    static final int BALL_DIAMETER = 8;

    JPanel optionsPanel = new JPanel();

    int lives;
    int score = 0;
    int hits = 0;
    int choice = 0;
    int modeChoice = 1;

    int inclinationSelection = 0;

    String welcomeMessage = "WELCOME TO BRUMBLY BREAKOUT \n";

    String modeMessage = "PRESS SPACE TO SELECT MODE";

    String mMessage;


    boolean attractModeActive = true;
    boolean soundPlaying;
    boolean allCleared;

    static final int rows = 14;
    static final int columns = 8;

    static final int brickWidth = 32;
    static final int brickHeight = 10;

    static final int BORDER_OFFSET = 20; //preventing paddle from touching upper & lower edges

    static final int DISTANCE = 20;  // 0 == edge

    Thread gameThread;
    BufferedImage buffer;
    Graphics graphics;

    Paddle paddle1;
    Ball ball;
    Brick[][] brick;
    Welcome welcome;

    Mode mode;
    Lives livesUI;
    Score scoreUI;
    Font atari;
    Color ballColour;
    Random random;
    Clip sound;

    GamePanel() {
        random = new Random();

        brick = new Brick[rows][columns];
        livesUI = new Lives(GAME_WIDTH - 20, GAME_HEIGHT - 20, 20, 20);
        scoreUI = new Score(GAME_WIDTH - 20, GAME_HEIGHT - 20, 20, 20);
        ballColour = Color.white;

        try {
            InputStream fontLocation = getClass().getResourceAsStream("atariFonts/Atari.ttf");
            atari = Font.createFont(Font.TRUETYPE_FONT, fontLocation).deriveFont(15f);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setFocusable(true);
        this.setPreferredSize(SCREEN_SIZE1);
        gameThread = new Thread(this);
        gameThread.start();

        attractModePaddles();
        newBricks();
        newBall();
        newWelcome();

        this.setFocusable(true);
        this.setPreferredSize(SCREEN_SIZE1);


        this.addKeyListener(new AL());

        gameThread = new Thread(this);
        gameThread.start();

    }


    public void newPaddles() {
        //new paddle instance from class
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
    public void newBall() {
        ball = new Ball((GAME_WIDTH / 2) - (BALL_DIAMETER / 2), (GAME_HEIGHT / 2) - (BALL_DIAMETER / 2), BALL_DIAMETER, BALL_DIAMETER);
        ball.setDY(1);

        hits = 0;
    }

    public void newWelcome() {
        welcome = new Welcome(GAME_WIDTH / 2, GAME_HEIGHT / 2, GAME_WIDTH / 15, GAME_HEIGHT / 15);
    }

    public void destroyWelcome() {
        welcomeMessage = " ";  //clear message
        modeMessage = " ";
    }

    public void playSound(String fileName) {

        if (!soundPlaying) {
            try {
                sound = AudioSystem.getClip();
                sound.open(AudioSystem.getAudioInputStream(getClass().getResource("audio/" + fileName)));
                soundPlaying = true;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error occured whilist attempting to play audio");
            }
        }

        if (soundPlaying == true) {
            sound.start();
        }

        soundPlaying = false;
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        graphics = buffer.getGraphics();

        draw(graphics);

        g.drawImage(buffer, 0, 0, this);

    }


    public void draw(Graphics g) {
        allCleared = true;

        if (attractModeActive == true) {

            switch (choice) {
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

        paddle1.draw(g);
        ball.draw(g, ballColour);
        welcome.draw(g, atari, GAME_WIDTH, GAME_HEIGHT, welcomeMessage, modeMessage);
        // mode.draw(g, atari, GAME_WIDTH, GAME_HEIGHT, modeMessage);

        for (int p = 0; p < rows; p++) {
            for (int l = 0; l < columns; l++) {
                if (brick[p][l] != null) {
                    brick[p][l].draw(g);
                    allCleared = false;
                }
            }
        }

        if (allCleared == true) {
            beginAttractMode();
            welcomeMessage = "YOU WON! yipeee";
        }

        livesUI.draw(g, atari, GAME_WIDTH, GAME_HEIGHT, lives);
        scoreUI.draw(g, atari, GAME_WIDTH, GAME_HEIGHT, score);


        Toolkit.getDefaultToolkit().sync();
        // Making sure display refreshes real-time for paint method

    }

    public void move() {

        paddle1.move();
        ball.move();

    }

    public void checkCollision() {

        if (paddle1.x <= 0)
            paddle1.x = 0;

        if (paddle1.x >= GAME_WIDTH - PADDLE_WIDTH)
            paddle1.x = GAME_WIDTH - PADDLE_WIDTH;

        if (ball.y <= 0) {
            ball.dy = -ball.dy;
            playSound("boundary_hit.wav");
        }

        if (ball.y >= GAME_HEIGHT - BALL_DIAMETER) {
            ball.dy = -ball.dy;

            if (lives > 0) {
                lives = lives - 1;
            }

            checkIfLost(lives);
            newBall();
            playSound("boundary_hit.wav");
        }
        //bouncing ball when hitting edges (left/right) of window boundaries
        if (ball.x <= 0) {
            ball.dx = -ball.dx;
            playSound("boundary_hit.wav");

            if (attractModeActive == true) {
                choice = random.nextInt(6);
            }
        }

        if (ball.x >= GAME_WIDTH - BALL_DIAMETER) {
            ball.dx = -ball.dx;
            playSound("boundary_hit.wav");

            if (attractModeActive == true) {
                choice = random.nextInt(6);
            }
        }

        // handling collisions with the Paddle.
        if (ball.intersects(paddle1)) {
            double inclination;

            if (attractModeActive != true) {

                hits = hits + 1; //used for increasing score + ball speed

                //making the ball go on an angle/inclination when colliding with paddle (slightly random for intent of fun)
                if (ball.x + (BALL_DIAMETER / 2) <= paddle1.x + PADDLE_WIDTH / 8) {
                    inclination = -1.6;
                } else {
                    if (ball.x + (BALL_DIAMETER / 2) <= paddle1.x + (PADDLE_WIDTH / 8) * 2) {
                        inclination = -1.4;
                    } else {
                        if (ball.x + (BALL_DIAMETER / 2) <= paddle1.x + (PADDLE_WIDTH / 8) * 3) {
                            inclination = -0.7;
                        } else {
                            if (ball.x + (BALL_DIAMETER / 2) <= paddle1.x + (PADDLE_WIDTH / 8) * 5) {
                                inclination = 0.55;

                                if (random.nextInt(2) == 0) {
                                    inclination = inclination * -1;
                                }

                            } else {
                                if (ball.x + (BALL_DIAMETER / 2) <= paddle1.x + (PADDLE_WIDTH / 8) * 6) {
                                    inclination = 0.7;
                                } else {
                                    if (ball.x + (BALL_DIAMETER / 2) <= paddle1.x + (PADDLE_WIDTH / 8) * 7) {
                                        inclination = 1.4;
                                    } else {
                                        inclination = 1.6;
                                    }
                                }
                            }
                        }
                    }
                }

            } else {

                //choose a Random Inclination + ball colour

                choice = random.nextInt(6);

                inclinationSelection = random.nextInt(3);

                switch (inclinationSelection) {
                    case 0:
                        inclination = 1.6;
                        break;
                    case 1:
                        inclination = 1.4;
                        break;
                    case 2:
                        inclination = 0.7;
                        break;
                    default:
                        inclination = 0.55;
                        break;
                }

                inclinationSelection = random.nextInt(2);

                if (inclinationSelection == 0) {
                    inclination = inclination * -1;
                }

            }

            // manipulating ball speed
            if (hits < 4) {
                ball.setDY(1);
            }

            if (hits >= 4 && hits < 12) {
                ball.setDY(1.5);
            }

            if (hits >= 12) {
                ball.setDY(2);
            }

            // setting the values inside the class after calculating the angle
            ball.dy = -ball.dy;
            ball.setDX(inclination);
            playSound("paddle_hit.wav");

        }

        // Colliding with brick
        for (int r = 0; r < rows; r++) {
            for (int t = 0; t < columns; t++) {
                if (brick[r][t] != null) {
                    if (ball.intersects(brick[r][t])) {
                        ball.dy = -ball.dy;
                        playSound("brick_hit.wav");

                        if (attractModeActive != true) {
                            brick[r][t] = null;

                            // Statement gives score based on the bricks position

                            switch (t) {
                                case 0:
                                    score += 7;
                                    break;
                                case 1:
                                    score += 7;
                                    break;
                                case 2:
                                    score += 5;
                                    break;
                                case 3:
                                    score += 5;
                                    break;
                                case 4:
                                    score += 3;
                                    break;
                                case 5:
                                    score += 3;
                                    break;
                                default:
                                    score += 1;
                                    break;
                            }


                        } else {
                            choice = random.nextInt(4);
                        }
                    }
                }
            }
        }

    }

    public void run() {

        long lastTime = System.nanoTime();
        double amountOfFPS = 60.0;
        double duration = 1000000000 / amountOfFPS;
        double delta = 0;

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / duration;
            lastTime = now;

            if (delta >= 1) {
                move();
                checkCollision();

                repaint();
                delta--;
            }
        }

    }

    public class AL extends KeyAdapter {
        //moving paddle horizontally when key pressed
        public void keyPressed(KeyEvent e) {

            //paddle1.keyPressed(e);
            if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && attractModeActive == false) {
                paddle1.setDeltaX(-1);
            }

            if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && attractModeActive == false) {
                paddle1.setDeltaX(+1);
            }

            if (e.getKeyCode() == KeyEvent.VK_SPACE && attractModeActive == true) {
                attractModeActive = false;
               // modeButtons();
            }
            if (e.getKeyCode() == KeyEvent.VK_1 && attractModeActive == true) {
                attractModeActive = false;

                mode = new Mode(10, 5, "DEFAULT");
            }
            if (e.getKeyCode() == KeyEvent.VK_2 && attractModeActive == true) {
                attractModeActive = false;

                mode = new Mode(5, 3, "FRENZY");
            }
        }


        //stopping paddle after releasing key - resetting deltaX
        public void keyReleased(KeyEvent e) {

            if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) & attractModeActive == false) {
                paddle1.setDeltaX(0);
            }

            if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && attractModeActive == false) {
                paddle1.setDeltaX(0);
            }
        }

    }

    public void checkIfLost(int lives) {
        int remainingLives = lives;

        if (remainingLives < 1) {
            beginAttractMode();
        }
    }

    public void beginAttractMode() {
        attractModePaddles();
        newWelcome();

        attractModeActive = true;
    }

    public void attractModePaddles() {
        paddle1 = new Paddle(0, GAME_HEIGHT - (PADDLE_HEIGHT - DISTANCE / 2) - 50, GAME_WIDTH, PADDLE_HEIGHT);
    }

    public void beginGame() {
        newPaddles();
        newBall();
        newBricks();
        destroyWelcome();
        score = 0;

        ballColour = Color.white;
    }}

//    public static JPanel modeButtons() {
//        JPanel panel = new JPanel();
//        optionsPanel.setLayout(new FlowLayout());
//
//        JButton mode1 = new JButton("Atari");
//        modeButtonsOptionsPanel.add(mode1);
//
//        JButton mode2 = new JButton("Frenzy");
//        optionsPanel.add(mode2);
//
//        JButton mode3 = new JButton("#*#^!&#!?");
//        optionsPanel.add(mode3);
//
//        return optionsPanel;
//    }
//}






