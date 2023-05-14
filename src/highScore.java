import java.awt.*;

public class highScore extends Rectangle {

    highScore(int x, int y, int width, int height, int highScore) {
        super(x, y, width, height);
    }



    public void draw(Graphics g, Font atari, int GAME_WIDTH, int GAME_HEIGHT, int highScore) {
        int currentHighScore = highScore;
        String hScoreDisplay = Integer.toString(currentHighScore);

        g.setFont(atari);
        g.setColor(Color.green);
        g.drawString("HIGHSCORE: " + hScoreDisplay, 140, (GAME_HEIGHT) - 15);
    }
}

