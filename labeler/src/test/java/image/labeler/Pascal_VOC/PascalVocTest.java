package image.labeler.Pascal_VOC;

import image.labeler.Img;
import image.labeler.Point;
import image.labeler.Polygon;
import junit.framework.TestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PascalVocTest extends TestCase {

    public void testOpenPascalvoc() {
        Img img = PascalVoc.openPascalvoc(new File("src/main/java/image/labeler/Pascal_VOC/Figure.xml"));
    }

    public void testPascalvocParser() {
        Img img = new Img("sonic",800,600,12);
        List<Polygon> polygons = new ArrayList<Polygon>();
        Point point1 = new Point(0,0);
        Point point2 = new Point(12,14);
        Point point3 = new Point(24,35);
        Polygon polygon1 = new Polygon();
        polygon1.addPoint(point1);
        polygon1.setName("Mario");
        polygon1.setCategory("Admario");
        polygon1.addPoint(point2);
        polygon1.addPoint(point3);
        Point point4 = new Point(14,13);
        Point point5 = new Point(24,35);
        Point point6 = new Point(0,9);
        Polygon polygon2 = new Polygon();
        polygon2.addPoint(point4);
        polygon2.addPoint(point5);
        polygon2.addPoint(point6);
        polygons.add(polygon1);
        polygons.add(polygon2);
        img.addPolygon(polygon1);
        img.addPolygon(polygon2);
        PascalVoc.pascalvocParser(img,"src/main/java/image/labeler/Pascal_VOC/Figure.xml");
    }
}