package Breakout;

import java.awt.*;
import java.util.*;


public class PowerUpBall extends Ball
{
    GamePanel brickLocation;
    int powerVal;
    int DirY;
    int powerUpSpeed = 1;


    public PowerUpBall(int x, int y, int width, int height, double speed)
    {   
        super(x,y,width,height,speed);
        
    }
    // need to use brick height and width calculations from array to U.I in PowerUpBall function for x and y coordinates

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
