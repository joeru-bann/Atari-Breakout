package Breakout;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class Welcome extends Rectangle {
    Welcome(int x, int y, int welcomeWidth, int welcomeHeight) {
        super(x, y, welcomeWidth, welcomeHeight);
    }


    public void draw(Graphics g, Font atari, int GAME_WIDTH, int GAME_HEIGHT, String welcomeMessage, String modeMessage) {
        FontMetrics fm = g.getFontMetrics();
        String messageToDisplay = welcomeMessage;
        String modeMessageToDisplay = modeMessage;

        g.setFont(atari);
        g.setColor(Color.white);
        //welcome to brumbly
        g.drawString(messageToDisplay, (GAME_WIDTH / 2) - fm.stringWidth(messageToDisplay) - 20, (GAME_HEIGHT / 2) - fm.getHeight());
       //mode message
       // g.drawString(modeMessageToDisplay, (GAME_WIDTH / 2) - fm.stringWidth(modeMessageToDisplay) - 20, (GAME_HEIGHT / 2 + 50) - fm.getHeight());


    }

}