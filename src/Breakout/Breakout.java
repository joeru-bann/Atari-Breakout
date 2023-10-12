package Breakout;

import javax.swing.*;
//main method

public class Breakout {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Brumble Breakout : Breaking Brumble");
            GameHandler window = new GameHandler(GameHandler.GAME_WIDTH, GameHandler.GAME_HEIGHT);
            frame.add(window);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
            frame.repaint();
            frame.setResizable(false);
            window.addKeyListener(window);

        });
    }
}