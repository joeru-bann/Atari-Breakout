package Breakout;

import java.awt.*;
import java.util.*;


public class PowerUpBall 
{
    Random random;
    GamePanel brickLocation;
    int powerVal;
    int DirY;
    int powerUpSpeed = 2;
    int x;
    int y;
    int width;
    int height;

    public PowerUpBall(int row, int column, int width, int height)
    {   
        this.x = row;
        this.y = column;
        this.width = width;
        this.height = height;
        
    }
    // need to use brick height and width calculations from array to U.I in PowerUpBall function for x and y coordinates


    public void setDY(double vectorY) {
        DirY = (int)(vectorY * powerUpSpeed);
    }
    public void move() {
        y = y + DirY;
    }
    public void draw(Graphics g, Color color) {
        g.setColor(color);
        g.fillOval(x, y, width, height);
    }

    private Color color;

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
