package Breakout;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class Background extends JPanel {

    private Color backgroundColor;

    public Background() {
        super();
        backgroundColor = Color.BLUE; // Set a default background color
    }
    public void draw(Graphics g,int r,int gr,int b, int width, int height) {
        g.setColor(new Color(r, gr, b));
        g.fillRect(0, 30, width, height);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background color or image
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());

    }

}