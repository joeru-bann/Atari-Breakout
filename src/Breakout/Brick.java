package Breakout;
import java.awt.*;

public class Brick extends Rectangle {
    int row, column;

    Color purple = new Color(160, 32, 240);
    // Adjust the parameters as needed for positioning and spacing
    Brick(int row, int column, int brickWidth, int brickHeight) {
        super(((row * brickWidth) + 1 * (row + 1)), (brickHeight * 3) + ((column * brickHeight) + 1 * (column + 1)), brickWidth, brickHeight); //create rectangle
        this.row = row;
        this.column = column;
    }
    public void draw(Graphics g) {

        // decide which color has to be applied to each row of Bricks.
        if (this.column > -1 && this.column < 2) {
            g.setColor(Color.red);
        }

        if (this.column > 1 && this.column < 4) {
            g.setColor(Color.orange);
        }

        if (this.column > 3 && this.column < 5) {
            g.setColor(Color.green);
        }

        if (this.column > 5 && this.column < 8) {
            g.setColor(Color.blue);
        }
        g.fillRect(x, y, width, height);
    }

}
