package src.Breakout;

import java.awt.*;
import java.util.*;


public class PowerUpBall 
{
    Random random;
    GamePanel game;
    int powerVal;
    int DirY;
    int powerUpSpeed = 2;
    int x;
    int y;
    int width;
    int height;

    public PowerUpBall(int row, int column, int width, int height)
    {   
        this.x = (row * width) + 1 * (row + 1);
        this.y = (height * 3) + ((column * height) + 1 * (column + 1));
        this.width = width;
        this.height = height;
        
    }
    // need to use brick hieght and width calculations from array to U.I in PowerUpBall function for x and y coordinates 


    public void setDY(double vectorY) {
        DirY = (int)(vectorY * powerUpSpeed);
    }
    public void move() {
        y = y + DirY;
    }
    public void draw(Graphics g, Color color) {
        g.setColor(color);
        g.fillOval(x, y, width, height);
        System.out.println("row "+x+", column"+y);
    }
}
