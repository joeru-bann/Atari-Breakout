package Breakout;

import java.awt.*;
import java.util.*;


public class PowerUpBall extends Rectangle
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

    public PowerUpBall(int column, int row, int width, int height, int brickWidth, int brickHeight)
    {
        System.out.println("row="+row + "col="+column+"brickheight="+brickHeight+"brickwidth="+brickWidth);
        this.y = (brickHeight * 3) + ((row * brickHeight));
        this.x = (column * brickWidth + 25);
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
        //System.out.println("row "+x+", column"+y);
    }
}
