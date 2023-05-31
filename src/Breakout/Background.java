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

    public void setBackgroundColor(Color color) {
        backgroundColor = color;
    }

    public void draw(Graphics g,int r,int gr,int b) {
        g.setColor(new Color(r, gr, b));
        g.fillRect(0, 30, 1000, 585);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background color or image
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Add any additional code for drawing other elements or images on top of the background
    }
}
