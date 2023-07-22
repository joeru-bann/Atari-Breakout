package Breakout;

import java.awt.*;

public class Paddle extends Rectangle {
    int dy;
    int dx;
    static int paddleSpeed = 9;

    int paddleWidth;
    public int getPaddleWidth() {

        return paddleWidth;
    }

    public void setPaddleWidth(int aWidth) {
        paddleWidth = aWidth;
    }
    Paddle(int x, int y, int paddleWidth, int PADDLE_HEIGHT) {

        super(x, y, paddleWidth, PADDLE_HEIGHT); //make the rectangle

    }
    public void setDeltaY(int yDirection) {
        dy = yDirection*paddleSpeed;
    }
    public void setDeltaX(int xDirection) {
        dx = xDirection*paddleSpeed;
    }

    public void move() {
        y = y + dy;
        x = x + dx;
    }

    public void draw(Graphics g) {
        g.setColor(new Color(160, 160, 255));
        g.fillRect(x, y, width, height);
    }

    public static void setPaddleSpeed(int newPaddleSpeed){
        paddleSpeed = newPaddleSpeed;

    }


}
