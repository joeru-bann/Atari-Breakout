package Breakout;

import javax.swing.*;
import java.awt.*;

public class Welcome extends JPanel {
    public Welcome(int x, int y, int welcomeWidth, int welcomeHeight) {
        setBounds(x, y, welcomeWidth, welcomeHeight);
        setLayout(new BorderLayout());
    }

    public void draw(Graphics g, Font atariFont, int GAME_WIDTH, int GAME_HEIGHT, String welcomeMessage, String modeMessage, String instructions, String leaderBoard) {
        FontMetrics fm = g.getFontMetrics(atariFont);

        g.setFont(atariFont);
        g.setColor(Color.white);

        // Calculate horizontal center
        int messageX = (GAME_WIDTH - fm.stringWidth(welcomeMessage)) / 2;

        // Position vertically centered
        int messageY = (GAME_HEIGHT - fm.getHeight()) / 2;

        g.drawString(welcomeMessage, messageX, messageY);
        g.drawString(modeMessage, messageX, messageY + 40);
        //g.drawString(leaderBoard, messageX, messageY + 100);

        // Draw instructions with line breaks
        String[] instructionLines = instructions.split("\n");
        for (int i = 0; i < instructionLines.length; i++) {
            String line = instructionLines[i];
            int lineX = (GAME_WIDTH - fm.stringWidth(line)) / 2; // Calculate lineX based on the line width
            int lineY = messageY + 100 + (i + 1) * fm.getHeight();
            g.drawString(line, lineX, lineY);
        }


        // Draw Leader board with breaks

        String[] leaderBoardLines = leaderBoard.split("\n");
        for (int i = 0; i < leaderBoardLines.length; i++) {
            String line = leaderBoardLines[i];
            int lineX = (GAME_WIDTH - fm.stringWidth(line)) / 2; // Calculate lineX based on the line width
            int lineY = messageY + 60 + (i + 1) * fm.getHeight();
            g.drawString(line, lineX, lineY);
        }
    }

}
