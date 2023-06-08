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

    public PowerUpBall(int x, int y, int width, int height)
    {
        super(x, y, width, height);
        Random rand = new Random();
        int vectorY = rand.nextInt(2);
        if (vectorY == 0){
            vectorY = -1;
            setDirY(vectorY);
        }
    }
    public void locateBrokenBrick(){    
        
    
    
    }

    public void setDirY(double vectorY) {
        DirY = (int)(vectorY * powerUpSpeed);
    }
    public void move() {
        y = y + DirY;
    }
    public void draw(Graphics g, Color color) {
        g.setColor(color);
        g.fillOval(x, y, width, height);
    }
}
