package minesweeperOnline;


import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

import static java.lang.Thread.sleep;

public class Solver {

    private final int offset;
    CellOnline[][] cells;
    int rows;
    int columns;
    int startX;
    int startY;
    int cellSize;
    Dimension screenSize;
    Rectangle area;
    BufferedImage screenshot;
    ConverterOnline converter;
    Robot r;
    private int flagCount;


//    screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//    area = new Rectangle(0, 0, screenSize.width, screenSize.height);
//    screenshot = r.createScreenCapture(area);

    public Solver(int rows, int columns, int startX, int startY, int cellSize, int offset) {
        this.rows = rows;
        this.columns = columns;
        this.cellSize = cellSize;
        this.offset = offset;
        this.startX = startX;
        this.startY = startY;
        cells = new CellOnline[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cells[i][j] = new CellOnline("unknown");
            }
        }
        converter = new ConverterOnline();
        try {
            r = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        area = new Rectangle(0, 0, screenSize.width, screenSize.height);
    }

    public void update_all_cells() {
        screenshot = r.createScreenCapture(area);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int[] xy = converter.convertRowColumnToXY(startX, startY, i, j, cellSize);
                Color color = new Color(screenshot.getRGB(xy[0], xy[1]));
                Color colorOffset = new Color(screenshot.getRGB(xy[0] - offset, xy[1] - offset));
                String content = converter.convertColorToContent(color, colorOffset);
                cells[i][j].setContent(content);
            }
        }
    }

    public void setCellToExplored(int row, int column) {
        cells[row][column].setExploredToTrue();
    }

    public int countHiddenNeighbors(int row, int column) {
        int count = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (row + i >= 0 && row + i < rows && column + j >= 0 && column + j < columns) {
                    if (converter.isHidden(cells[row + i][column + j].getContent())) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public int countFlaggedNeighbors(int row, int column) {
        int count = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (row + i >= 0 && row + i < rows && column + j >= 0 && column + j < columns) {
                    if (converter.isFlagged(cells[row + i][column + j].getContent())) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public void revealHidden(int row, int column) {
        if (flagCount == 98){
            return;
        }
        int[] xy = converter.convertRowColumnToXY(startX, startY, row, column, cellSize);
        r.mouseMove(xy[0], xy[1]);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        setCellToExplored(row, column);
    }

    public void flagHidden(int row, int column) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (flagCount == 98){
                    return;
                }
                if (row + i >= 0 && row + i < rows && column + j >= 0 && column + j < columns && !cells[row + i][column + j].isExplored()) {
                    if (converter.isHidden(cells[row + i][column + j].getContent())) {
                        int[] xy = converter.convertRowColumnToXY(startX, startY, row + i, column + j, cellSize);
                        r.mouseMove(xy[0], xy[1]);
                        r.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                        r.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                        setCellToExplored(row + i, column + j);
                        cells[row + i][column + j].setContent("flag");
                        flagCount++;
                    }
                }
            }
        }
    }


    public void solve(int count) {
        if (count == 10) {
            return;
        }
        boolean changed = true;
        while (changed) {
            update_all_cells();
            //System.out.println("update");
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (!cells[i][j].isExplored() && converter.isNumber(cells[i][j].getContent())) {
                        int hiddenNeighbors = countHiddenNeighbors(i, j);
                        int flaggedNeighbors = countFlaggedNeighbors(i, j);
                        int number = converter.convertContentToNumber(cells[i][j].getContent());
                        if (hiddenNeighbors == 0) {
                            setCellToExplored(i, j);
                        } else if (hiddenNeighbors + flaggedNeighbors == number) {
                            //System.out.println("flagging   " + "hidden: " + hiddenNeighbors + " flagged: " + flaggedNeighbors + " number: " + number + " initialized by " + i + ", " + j);
                            flagHidden(i, j);
                            setCellToExplored(i, j);
                            changed = false;
                            update_all_cells();
                            sleepAbit();
                        } else if (flaggedNeighbors == number) {
                            //System.out.println("revealing   " + "hidden: " + hiddenNeighbors + " flagged: " + flaggedNeighbors + " number: " + number + " initialized by " + i + ", " + j);
                            revealHidden(i, j);
                            setCellToExplored(i, j);
                            changed = false;
                            update_all_cells();
                            sleepAbit();
                        }
                    }
                }
            }
            changed = !changed;
        }
    }

    private void sleepAbit() {
        try {
            //int time = (int) (Math.random() * 100);
            sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean checkForMine(int row, int column) {
        return converter.isMine(cells[row][column].getContent());
    }

    public static void main(String[] args) throws AWTException {
        int rows = 16;
        int columns = 30;
        int startX = 511;
        int startY = 376;
        int cellSize = 38;
        int offset = 18;
        Solver solver = new Solver(rows, columns, startX, startY, cellSize, offset);
        solver.solve(0);
    }
}