package Breakout;

import javax.swing.*;

//main method

public class Breakout {
    public static void main(String[] args) {
        JFrame frame = new JFrame(  "Brumble Breakout : Breaking Brumble");
        GamePanel window = new GamePanel();

        //frame.getContentPane().add(window);
        //frame.pack();
        frame.add(window);
        frame.setSize(window.newWidth, window.newHeight);
        //frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocation(window.horizontalMargin, window.verticalMargin);
        frame.setVisible(true);

    }
}