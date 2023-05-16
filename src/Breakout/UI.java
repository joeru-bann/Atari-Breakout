package Breakout;

import javax.swing.*;
import java.awt.*;

public class UI extends JPanel {
    private int x;
    private int y;
    private int width;
    private int height;
    private Color color;
    private String message;

    private Font atari;


    public UI(int x, int y, Color color, String message, Font atari) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.message = message;
        this.atari = atari;

    }

    public void draw(Graphics2D g2d, int width, int height, int value) {
        g2d.setFont(atari);
        g2d.setColor(color);
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int textWidth = fontMetrics.stringWidth(message + value);
        int textHeight = fontMetrics.getHeight();
        int textX = x - textWidth / 2; // Center horizontally
        int textY = y + textHeight / 2; // Center vertically

        //g2d.fillRect(x, y, width, height);
        g2d.drawString(message + value, textX, textY);    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        draw(g2d, 0,0,0);
        g2d.dispose();
    }
}


