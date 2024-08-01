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

    /**
     * Returns the bounding box in YOLO format has a String
     * @param classId is the identifier of the class, a class 
     * @param imageWidth is the width of the image
     * @param imageHeight is the height of the image
     * @return
     */
    public String toYOLOFormat(int classId, int imageWidth, int imageHeight) {
        List<Point> points = polygon.getPoints();

        double minX = points.get(0).getX();
        double minY = points.get(0).getY();
        double maxX = points.get(2).getX();
        double maxY = points.get(2).getY();

        double xCenter = (minX + maxX) / 2.0;
        double yCenter = (minY + maxY) / 2.0;
        double width = maxX - minX;
        double height = maxY - minY;

        double normXCenter = xCenter / imageWidth;
        double normYCenter = yCenter / imageHeight;
        double normWidth = width / imageWidth;
        double normHeight = height / imageHeight;

        return String.format("%d %.6f %.6f %.6f %.6f", classId, normXCenter, normYCenter, normWidth, normHeight);
    }

    // ********** Getters **********
    public Polygon getRectanglePolygon() {
        return polygon;
    }

    // ********** Setters **********
    public void setRectanglePolygon(Polygon polygon) {
        this.polygon = polygon;
    }
}
