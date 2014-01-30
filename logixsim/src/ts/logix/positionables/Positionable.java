package ts.logix.positionables;

public abstract class Positionable {
    public int x, y;
    private boolean vis = false;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean blocks(int cx, int cy) {
        return cx == x && cy == y;
    }

    public void setVisible(boolean visible) {
        vis = visible;
    }

    public boolean isVisible() {
        return vis;
    }

    @Override
    public String toString() {
        return "[(" + x + ", " + y + "), " + (vis ? "visible" : "not visible")
                + "]";
    }
}
