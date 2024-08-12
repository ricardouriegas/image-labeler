package image.labeler.COCO;

import java.util.List;
import java.util.ArrayList;
import image.labeler.Polygon;
import image.labeler.Point;
import image.labeler.Img;

public class example {
    public static void main(String[] args) {
        // Create a COCO object
        COCO coco = new COCO();

        // Define categories
        //coco.addCategory(1, "Triangle");
        //coco.addCategory(2, "Quadrilateral");
        //coco.addCategory(3, "Pentagon");    

        // Create an image and add polygons to it
        Img img = new Img("imagen.jpg", 640, 640);

        // Triangle polygon
        Polygon polygon = new Polygon();
        polygon.setId(1);
        polygon.setCategory("Triangle");
        polygon.addPoint(new Point(0, 0));
        polygon.addPoint(new Point(1, 0));
        polygon.addPoint(new Point(0.5, 1));
        img.addPolygon(polygon);

        // Irregular quadrilateral polygon
        polygon = new Polygon();
        polygon.setId(2);
        polygon.setCategory("Quadrilateral");
        polygon.addPoint(new Point(0, 0));
        polygon.addPoint(new Point(1.2, 0));
        polygon.addPoint(new Point(1, 1.8));
        polygon.addPoint(new Point(0, 8));
        img.addPolygon(polygon);

        // Pentagon polygon
        polygon = new Polygon();
        polygon.setId(3);
        polygon.setCategory("Pentagon");
        polygon.addPoint(new Point(0.5, 0));
        polygon.addPoint(new Point(1, 0.5));
        polygon.addPoint(new Point(0.75, 1));
        polygon.addPoint(new Point(0.25, 1));
        polygon.addPoint(new Point(0, 0.5));
        img.addPolygon(polygon);

        // Add the image with polygons to COCO
        coco.addImage(img);

        // Print out the COCO structure (this would typically be converted to JSON)
        System.out.println("===COCO Images===");
        System.out.println(coco.getImages());

        System.out.println("===COCO Annotations===");
        System.out.println(coco.getAnnotations());

        System.out.println("===COCO Categories===");
        System.out.println(coco.getCategories());

        coco.exportToJson("labeler/src/main/java/image/labeler/COCO/prueba");
    }
}

