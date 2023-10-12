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
    }
    public double getDY() {
        return dy;
    }

    public double getDX() {
        return dx;
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
}