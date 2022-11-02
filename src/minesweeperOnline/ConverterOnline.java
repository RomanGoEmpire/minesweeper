package minesweeperOnline;

import java.awt.*;

public class ConverterOnline {
    private final Color blank;
    private final Color white;
    private final Color one;
    private final Color two;
    private final Color three;
    private final Color four;
    private final Color five;
    private final Color six;
    private final Color flag;




    public ConverterOnline() {
        blank = new Color(198, 198, 198);
        flag = new Color(140,140,140);
        white = new Color(255, 255, 255);
        one = new Color(0, 0, 255);
        two = new Color(0, 128, 0);
        three = new Color(255, 0, 0);
        four = new Color(0, 0, 128);
        five = new Color(128, 0, 0);
        six = new Color(49, 145, 145);

    }

    public String convertColorToContent(Color color, Color colorOffset) {
        if (color.equals(one)) {
            return "one";
        } else if (color.equals(two)) {
            return "two";
        } else if (color.equals(three)) {
            return "three";
        } else if (color.equals(four)) {
            return "four";
        } else if (color.equals(five)) {
            return "five";
        } else if (color.equals(six)) {
            return "six";
        } else if (color.equals(white)) {
            return "bomb";
        } else if (color.equals(flag)) {
            return "flag";
        } else if (color.equals(blank)) {
            if (colorOffset.equals(white)) {
                return "hidden";
            } else {
                return "blank";
            }
        } else {
            return "unknown";
        }
    }


    // convert row and column to x and y
    public int[] convertRowColumnToXY(int startX, int StartY, int row, int column, int cellSize) {
        int[] xy = new int[2];
        xy[0] = startX + column * cellSize;
        xy[1] = StartY + row * cellSize;
        return xy;
    }

    public boolean isHidden(String content) {
        return content.equals("hidden");
    }

    public boolean isFlagged(String content) {
        return content.equals("flag");
    }

    public boolean isNumber(String content) {
        return content.equals("one") || content.equals("two") || content.equals("three") || content.equals("four") || content.equals("five") || content.equals("six");
    }

    public boolean isMine(String content) {
        return content.equals("bomb");
    }

    private boolean isBlank(String content) {
        return content.equals("blank");
    }

    public int convertContentToNumber(String content) {
        if (content.equals("one")) {
            return 1;
        } else if (content.equals("two")) {
            return 2;
        } else if (content.equals("three")) {
            return 3;
        } else if (content.equals("four")) {
            return 4;
        } else if (content.equals("five")) {
            return 5;
        } else if (content.equals("six")) {
            return 6;
        } else {
            throw new IllegalArgumentException("Content is not a number");
        }
    }
}
