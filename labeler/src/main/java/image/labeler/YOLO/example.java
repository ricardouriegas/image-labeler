package image.labeler.YOLO;

import java.util.*;
import image.labeler.Polygon;
import image.labeler.Point;

/**
 * This class is just a class to show an example of how to use the YOLO classes
 * Im gonna create a list of polygons and convert it to a list of YOLO objects
 * then im gonna convert the list of YOLO objects to a list of polygons
 */
public class example {
    public static void main(String[] args) {
        // Create a list of polygons
        List<Polygon> polygons = new ArrayList<>();

        // triangle
        Polygon polygon = new Polygon();
        polygon.setId(1);
        polygon.addPoint(new Point(0, 0));
        polygon.addPoint(new Point(1, 0));
        polygon.addPoint(new Point(0.5, 1));
        polygons.add(polygon);

        // irregular quadrilateral
        polygon = new Polygon();
        polygon.setId(2);
        polygon.addPoint(new Point(0, 0));
        polygon.addPoint(new Point(1.2, 0));
        polygon.addPoint(new Point(1, 1.8));
        polygon.addPoint(new Point(0, 8));
        polygons.add(polygon);

        // pentagon
        polygon = new Polygon();
        polygon.setId(3);
        polygon.addPoint(new Point(0.5, 0));
        polygon.addPoint(new Point(1, 0.5));
        polygon.addPoint(new Point(0.75, 1));
        polygon.addPoint(new Point(0.25, 1));
        polygon.addPoint(new Point(0, 0.5));
        polygons.add(polygon);

        // Convert the list of polygons to a list of YOLO objects
        List<YOLO> yoloList = YOLOManager.toYolo(polygons, 100, 100);

        System.out.println("===YOLO objects===");
        for (YOLO yolo : yoloList) {
            System.out.println(yolo.toYOLOFormat());
        }
        
        // Convert the list of YOLO string formats to a list of polygons
        polygons = YOLOManager.toPolygon(yoloList, 100, 100);
        System.out.println("===Polygons===");
        for (Polygon p : polygons) {
            System.out.println(p);
        }
    }

}
