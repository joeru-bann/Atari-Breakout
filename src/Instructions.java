import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class Instructions extends Rectangle {
    Instructions(int x, int y, int instructionWidth, int instructionHeight) {
        super(x, y, instructionWidth, instructionHeight);
    }


    public void draw(Graphics g, Font atari, int GAME_WIDTH, int GAME_HEIGHT, String instructionMessage, String modeMessage) {
        FontMetrics fm = g.getFontMetrics();
        String messageToDisplay = instructionMessage;
        String modeMessageToDisplay = modeMessage;

        g.setFont(atari);
        g.setColor(Color.white);
        //welcome to brumbly
        g.drawString(messageToDisplay, (GAME_WIDTH / 2 + 30) - fm.stringWidth(messageToDisplay) - 20, (GAME_HEIGHT / 2) - fm.getHeight());
       //mode message
        g.drawString(modeMessageToDisplay, (GAME_WIDTH / 2 + 15) - fm.stringWidth(modeMessageToDisplay) - 20, (GAME_HEIGHT / 2 + 50) - fm.getHeight());

    }

}
