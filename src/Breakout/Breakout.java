package Breakout;

import java.awt.*;
import javax.swing.*;

//main method

public class Breakout {
    private Welcome welcomeComponent;
    public static void main(String[] args) {
        JFrame frame = new JFrame(  "Brumble Breakout : Breaking Brumble");
        GamePanel window = new GamePanel(950, 665);

        frame.getContentPane().add(window);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.repaint();

    }
}
