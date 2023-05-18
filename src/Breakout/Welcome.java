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

        // Calculate horizontal centre
        int messageX = (GAME_WIDTH - fm.stringWidth(welcomeMessage)) / 2;

        // position vertically centered
        int messageY = (GAME_HEIGHT - fm.getHeight()) / 2;

        g.drawString(welcomeMessage, messageX, messageY);

        int modeX = (GAME_WIDTH - fm.stringWidth(modeMessage)) / 2;

        // drw mode message below welcome
        int modeY = messageY + fm.getHeight() + 50;

        g.drawString(modeMessage, modeX, modeY);

    }

}