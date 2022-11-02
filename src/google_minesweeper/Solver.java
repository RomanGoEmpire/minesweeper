package google_minesweeper;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

import static java.lang.Thread.sleep;

public class Solver {

    Cell[][] cells;
    int rows;
    int columns;
    int startX;
    int startY;
    int cellSize;
    Dimension screenSize;
    Rectangle area;
    BufferedImage screenshot;
    Converter converter;
    Robot r;


//    screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//    area = new Rectangle(0, 0, screenSize.width, screenSize.height);
//    screenshot = r.createScreenCapture(area);

    public Solver(int rows, int columns, int startX, int startY, int cellSize) {
        this.rows = rows;
        this.columns = columns;
        this.cellSize = cellSize;
        this.startX = startX;
        this.startY = startY;
        cells = new Cell[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cells[i][j] = new Cell(Color.WHITE);
            }
        }
        converter = new Converter();
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
                cells[i][j].setColor(color);
            }
        }
    }

    public void update_neighbors(int row, int column) {
        screenshot = r.createScreenCapture(area);
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (row + i >= 0 && row + i < rows && column + j >= 0 && column + j < columns) {
                    int[] xy = converter.convertRowColumnToXY(startX, startY, row + i, column + j, cellSize);
                    Color color = new Color(screenshot.getRGB(xy[0], xy[1]));
                    cells[row + i][column + j].setColor(color);
                }
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
                    if (converter.isHidden(cells[row + i][column + j].getColor())) {
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
                    if (converter.isFlagged(cells[row + i][column + j].getColor())) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public void revealHidden(int row, int column) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (row + i >= 0 && row + i < rows && column + j >= 0 && column + j < columns) {
                    if (converter.isHidden(cells[row + i][column + j].getColor())) {
                        int[] xy = converter.convertRowColumnToXY(startX, startY, row + i, column + j, cellSize);
                        r.mouseMove(xy[0], xy[1]);
                        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                        r.mouseMove(200, 200);
                    }
                }
            }
            update_neighbors(row, column);
        }
    }

    private boolean isMine(int row, int column) {
        return converter.isMine(cells[row][column].getColor());

    }

    public void flagHidden(int row, int column) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (row + i >= 0 && row + i < rows && column + j >= 0 && column + j < columns && !cells[row + i][column + j].isExplored()) {
                    if (converter.isHidden(cells[row + i][column + j].getColor())) {
                        int[] xy = converter.convertRowColumnToXY(startX, startY, row + i, column + j, cellSize);
                        r.mouseMove(xy[0], xy[1]);
                        r.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                        r.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                        setCellToExplored(row + i, column + j);
                        r.mouseMove(200, 200);
                    }
                }
            }
        }
    }

    private void sleepAbit() {
        try {
            sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void solve() {
        boolean changed = true;
        while (changed) {
            update_all_cells();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (!cells[i][j].isExplored() && converter.isNumber(cells[i][j].getColor())) {
                        int hiddenNeighbors = countHiddenNeighbors(i, j);
                        int flaggedNeighbors = countFlaggedNeighbors(i, j);
                        int number = converter.convertColorToNumber(cells[i][j].getColor());
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

    private boolean clickedOnMine() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (isMine(i, j)) {
                    System.out.println("Mine found at " + i + ", " + j);
                    System.out.println("Mine color: " + cells[i][j].getColor());
                    return true;
                }
            }
        }
        return false;
    }

    private void clickOnRandomCell() {
        // only click on cells that is hidden
        int row = (int) (Math.random() * rows);
        int column = (int) (Math.random() * columns);
        while (converter.isHidden(cells[row][column].getColor())) {
            row = (int) (Math.random() * rows);
            column = (int) (Math.random() * columns);
        }
        int[] xy = converter.convertRowColumnToXY(startX, startY, row, column, cellSize);
        r.mouseMove(xy[0], xy[1]);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        r.mouseMove(200, 200);
    }

    public static void main(String[] args) {
        int rows = 20;
        int columns = 24;
        int startX = 673;
        int startY = 330;
        int cellSize = 25;
        Solver solver = new Solver(rows, columns, startX, startY, cellSize);
        solver.solve();
    }
}