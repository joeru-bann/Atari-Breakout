import java.awt.*;
import javax.swing.*;

public class GameFrame extends JFrame {

    GamePanel panel;

    GameFrame() {

        panel = new GamePanel();

        JPanel modeButtonsPanel = new JPanel();
        // add mode buttons to modeButtonsPanel here

        // set layout manager to stack components vertically
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // add modeButtonsPanel above the panel
        add(modeButtonsPanel);
        add(panel);

        setTitle("Brumble Breakout : Breaking Brumble");
        setResizable(false);
        setBackground(Color.black);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
    }
}


