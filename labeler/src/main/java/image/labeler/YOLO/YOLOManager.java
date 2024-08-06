package image.labeler.YOLO;

import image.labeler.Polygon;
import image.labeler.Point;
import java.util.*;

/**
 * This class should manage the YOLO object using the BoundingBox object
 * It should be able of take a list of polygons and convert them to a list of YOLO objects
 * and also should be able to take a list of YOLO objects and convert them to a list of polygons
 */
public class YOLOManager {

    /**
     * This method should be able to convert a list of polygons to a list of YOLO objects
     * @param polygons
     * @param imageWidth
     * @param imageHeight
     * @return
     */
    public static List<YOLO> toYolo(List<Polygon> polygons, int imageWidth, int imageHeight) {
        List<YOLO> yoloList = new ArrayList<>();
        for (Polygon polygon : polygons) {
            // create a bounding box from the polygon
            BoundingBox boundingBox = new BoundingBox(polygon);

            // obtain the list of points of the rectangle
            List<Point> points = boundingBox.getRectanglePolygon().getPoints();
            // calculate the x, y, width and height of the rectangle
            double x = (points.get(0).getX() + points.get(2).getX()) / 2 / imageWidth;
            double y = (points.get(0).getY() + points.get(2).getY()) / 2 / imageHeight;
            double width = (points.get(2).getX() - points.get(0).getX()) / imageWidth;
            double height = (points.get(2).getY() - points.get(0).getY()) / imageHeight;
            int classId = polygon.getId();
            
            // add the YOLO object to the list
            yoloList.add(new YOLO(classId, x, y, width, height));
        }
        return yoloList;
    }

    /**
     * This method should be able to convert a list of YOLO objects to a list of polygons
     * @param yoloList
     * @param imageWidth
     * @param imageHeight
     * @return
     */
    public static List<Polygon> toPolygon(List<YOLO> yoloList, int imageWidth, int imageHeight) {
        List<Polygon> polygonList = new ArrayList<>();
        for (YOLO yolo : yoloList) {
            polygonList.add(yolo.toPolygon(imageWidth, imageHeight));
        }
        return polygonList;
    }
    
}
