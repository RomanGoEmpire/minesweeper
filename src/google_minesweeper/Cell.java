package google_minesweeper;

import java.awt.*;

public class Cell {
    private Color color;
    private boolean explored;

    public Cell(Color color) {
        this.color = color;
        explored = false;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isExplored() {
        return explored;
    }

    public void setExploredToTrue() {
        explored = true;
    }
}
