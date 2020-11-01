/**
 * This class represents a location on the game board.
 */

public class Point {
    private int x;
    private int y;

    /**
     * The class constructor.
     * @param x x-coordinate on the game board.
     * @param y y-coordinate on the game board.
     */
    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     * getX method fetches the X position.
     * @return X position.
     */
    public int getX(){
        return x;
    }

    /**
     * getY method fetches the Y position.
     * @return Y position.
     */
    public int getY(){
        return y;
    }

    /**
     * equals method compares two points in the board.
     * @param other new point.
     * @return true if two points are the same.
     */
    public boolean equals(Point other){
        return (x == other.getX() && y == other.getY());
    }
}
