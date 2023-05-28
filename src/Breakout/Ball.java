package Breakout;

import java.awt.*;
import java.util.*;

public class Ball extends Rectangle {

    private Random random;
    double dx;
    double dy;
    private double speed;

    Ball(int x, int y, int width, int height, double speed) {
        super(x, y, width, height);
        this.speed = speed;

        random = new Random();
        setRandomDirection();
    }

    private void setRandomDirection() {
        double angle = Math.toRadians(random.nextDouble() * 360);
        dx = Math.cos(angle);
        dy = Math.sin(angle);
    }

    public void setDX(double vectorX) {
        dx = vectorX;
    }

    public void setDY(double vectorY) {
        dy = vectorY;
    }

    public void move() {
        x += (int) (dx * speed);
        y += (int) (dy * speed);
    }

    public void draw(Graphics g, Color color) {
        g.setColor(color);
        g.fillOval(x, y, width, height);
    }

    // Adjust the ball's direction based on collision position
    public void adjustDirection(Rectangle collisionObject) {
        double collisionX = x + width / 2.0;
        double collisionY = y + height / 2.0;

        double objectCenterX = collisionObject.x + collisionObject.width / 2.0;
        double objectCenterY = collisionObject.y + collisionObject.height / 2.0;

        double deltaX = collisionX - objectCenterX;
        double deltaY = collisionY - objectCenterY;

        // Calculate the new direction based on the collision position
        dx = -deltaX / (Math.abs(deltaX) + Math.abs(deltaY));
        dy = -deltaY / (Math.abs(deltaX) + Math.abs(deltaY));
    }
}
