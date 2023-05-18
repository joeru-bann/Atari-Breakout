package src.Breakout;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class Welcome extends Rectangle {
    Welcome(int x, int y, int welcomeWidth, int welcomeHeight) {
        super(x, y, welcomeWidth, welcomeHeight);
    }

    public void draw(Graphics g, Font atari, int GAME_WIDTH, int GAME_HEIGHT, String welcomeMessage, String modeMessage, String instructions) {
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

        g.drawString(modeMessage, messageX, (messageY)+50);

        g.drawString(instructions, (messageX)-100, (messageY)-100);
    }

}
