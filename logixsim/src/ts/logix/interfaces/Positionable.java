package ts.logix.interfaces;

public abstract class Positionable {
    public int x, y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean blocks(int cx, int cy) {
        return cx == x && cy == y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
