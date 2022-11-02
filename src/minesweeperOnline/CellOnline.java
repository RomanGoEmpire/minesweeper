package minesweeperOnline;

public class CellOnline {
    private String content;
    private boolean explored;

    public CellOnline(String content) {
        this.content = content;
        explored = false;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content =content;
    }

    public boolean isExplored() {
        return explored;
    }

    public void setExploredToTrue() {
        explored = true;
    }
}
