package Breakout;

import javax.swing.*;
import java.awt.event.MouseMotionListener;

import static com.sun.java.accessibility.util.AWTEventMonitor.addMouseMotionListener;

//main method

public class Breakout {
    public static void main(String[] args) {
        JFrame frame = new JFrame(  "Brumble Breakout : Breaking Brumble");
        GamePanel window = new GamePanel(GamePanel.GAME_WIDTH, GamePanel.GAME_HEIGHT);
        frame.add(window);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.repaint();
        frame.setResizable(false);
    }
}