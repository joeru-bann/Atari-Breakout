package src.Breakout;
import java.awt.*;
import java.util.*;
/**
 * Write a description of class PowerUpBall here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class PowerUpBall extends Rectangle
{
    
    Random random;
    int powerVal;
    int DirY;
    int powerUpSpeed = 2; 

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
  
  public void setDirY(double vectorY) {
        DirY = (int)(vectorY * powerUpSpeed);
    }
    public void move() {
        y = y + DirY;
    }
    public void draw(Graphics g, Color color) {
        g.setColor(color);
        g.fillOval(x, y, height, width);
    }
}
