package google_minesweeper;

import java.awt.*;

public class Converter {
    final Color hidden;
     final Color hidden2;
    private final Color one;
    private final Color two;
    private final Color three;
    private final Color four;
    private final Color five;
    private final Color flag;
    private final Color blank;
    private final Color blank2;


    public Converter() {
        hidden = new Color(170, 215, 81);
        hidden2 = new Color(162, 209, 73);
        one = new Color(25, 118, 210);
        two = new Color(56, 142, 60);
        three = new Color(211, 47, 47);
        four = new Color(123, 31, 162);
        five = new Color(240, 174, 92);
        flag = new Color(242, 54, 7);
        blank = new Color(215,184,153);
        blank2 = new Color(229, 194, 159);

    }

    public int convertColorToNumber(Color color) {
        if (color.equals(one)) {
            return 1;
        } else if (color.equals(two)) {
            return 2;
        } else if (color.equals(three)) {
            return 3;
        } else if (color.equals(four)) {
            return 4;
        } else if (color.equals(five)) {
            return 5;
        } else {
            System.out.println(color);
            System.out.println("Color not found");
               throw new RuntimeException("Color not found");
        }
    }


    // convert row and column to x and y
    public int[] convertRowColumnToXY(int startX, int StartY, int row, int column, int cellSize) {
        int[] xy = new int[2];
        xy[0] = startX + column * cellSize;
        xy[1] = StartY + row * cellSize;
        return xy;
    }

    public boolean isHidden(Color color) {
        return color.equals(hidden) || color.equals(hidden2);
    }

    public boolean isFlagged(Color color) {
        return color.equals(flag);
    }

    public boolean isNumber(Color color) {
        return color.equals(one) || color.equals(two) || color.equals(three) || color.equals(four) || color.equals(five);
    }

    public boolean isMine(Color color) {
        return !isHidden(color) && !isFlagged(color) && !isNumber(color) && !isBlank(color);
    }

    private boolean isBlank(Color color) {
        return color.equals(blank) || color.equals(blank2);
    }
}
