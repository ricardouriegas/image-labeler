package image.labeler;

import java.util.Objects;

/**
 * Represents a point in 2D space
  */
public class Point {
    private final double x; // x-coordinate
    private final double y; // y-coordinate

    /**
     * Creates a point with the given coordinates
     * @param x x-coordinate
     * @param y y-coordinate
      */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // ********** Getters **********

    /**
     * Returns the x-coordinate
     * @return x-coordinate
      */
    public double getX() {
        return x;
    }

    /**
     * Returns the y-coordinate
     * @return y-coordinate
      */
    public double getY() {
        return y;
    }

    /**
     * Returns true if the point is equal to the given object
     * @param obj object to compare
      */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point point = (Point) obj;
        return Double.compare(point.x, x) == 0 &&
               Double.compare(point.y, y) == 0;
    }

    /**
     * Returns the hash code of the point
     * @return hash code
      */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

}
