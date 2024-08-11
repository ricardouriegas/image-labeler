package image.labeler.Pascal_VOC;

import image.labeler.Point;
import java.util.ArrayList;
import java.util.List;

public class PointsAdapter{

    private PointsAdapter(){}

    public static List<XmlPoints> adapter(List<Point> Points){
        List<XmlPoints> xmlpoints = new ArrayList<>();
        for (Point point: Points){
            XmlPoints xmlPoint = new XmlPoints(point);
            xmlpoints.add(xmlPoint);
        }
        return xmlpoints;
    }

    public static List<Point> toPoints(List<XmlPoints> xmlPoints){
        List <Point> points = new ArrayList<>();
        for (XmlPoints xmlPoint : xmlPoints){
            double x = xmlPoint.getX();
            double y = xmlPoint.getY();
            points.add(new Point(x, y));
        }
        return points;
    }

}