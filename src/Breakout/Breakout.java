package Breakout;

import javax.swing.*;

//main method

public class Breakout {
    public static void main(String[] args) {
        JFrame frame = new JFrame(  "Brumble Breakout : Breaking Brumble");
        GamePanel window = new GamePanel(GamePanel.GAME_WIDTH, GamePanel.GAME_HEIGHT);

//        Thread gameThread = new Thread(window);
//        gameThread.start();

        frame.getContentPane().add(window);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.repaint();
        frame.setResizable(false);
    }
}