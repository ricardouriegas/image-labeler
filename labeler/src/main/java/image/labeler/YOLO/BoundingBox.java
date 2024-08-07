package image.labeler.YOLO;

import java.util.List;
import image.labeler.Polygon;
import image.labeler.Point;

/**
 * This class is here bc is needed for the YOLO format
 * we record polygons (a list of points) but YOLO needs rectangles
 * so we need to transform the polygon into a rectangle using this class
 */
public class BoundingBox {
    private Polygon polygon;

    /**
     * Creates a bounding box with the given polygon
     * @param polygon polygon
     */
    public BoundingBox(Polygon polygon) {
        // Transform the polygon into a rectangle
        List<Point> points = polygon.getPoints();

        if (points == null || points.isEmpty()) {
            throw new IllegalArgumentException("La lista de puntos no puede estar vacía");
        }

        double minX = points.get(0).getX();
        double minY = points.get(0).getY();
        double maxX = points.get(0).getX();
        double maxY = points.get(0).getY();

        for (Point point : points) {
            if (point.getX() < minX) {
                minX = point.getX();
            }
            if (point.getX() > maxX) {
                maxX = point.getX();
            }
            if (point.getY() < minY) {
                minY = point.getY();
            }
            if (point.getY() > maxY) {
                maxY = point.getY();
            }
        }

        // Create a polygin as the bounding box (rectangle)
        Polygon rectangle = new Polygon();
        
        rectangle.addPoint(new Point(minX, minY));
        rectangle.addPoint(new Point(maxX, minY));
        rectangle.addPoint(new Point(maxX, maxY));
        rectangle.addPoint(new Point(minX, maxY));

        this.polygon = rectangle;
    }

    // ********** Getters **********
    public Polygon getRectanglePolygon() {
        return polygon;
    }

    // getX() 
    public double getX() {
        return polygon.getPoints().get(0).getX();
    }

    // getY()
    public double getY() {
        return polygon.getPoints().get(0).getY();
    }

    // getWidth()
    public double getWidth() {
        return polygon.getPoints().get(2).getX() - polygon.getPoints().get(0).getX();
    }

    // getHeight()
    public double getHeight() {
        return polygon.getPoints().get(2).getY() - polygon.getPoints().get(0).getY();
    }

    // ********** Setters **********
    public void setRectanglePolygon(Polygon polygon) {
        this.polygon = polygon;
    }
}
