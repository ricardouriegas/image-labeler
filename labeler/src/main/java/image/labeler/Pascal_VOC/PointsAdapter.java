package image.labeler.Pascal_VOC;

import image.labeler.Point;
import java.util.ArrayList;
import java.util.List;

public class PointsAdapter{

    public static List<XmlPoint> adapter(List<Point> Points){
        List<XmlPoint> xmlpoints = new ArrayList<>();
        for (Point point: Points){
            XmlPoint xmlPoint = new XmlPoint(point);
            xmlpoints.add(xmlPoint);
        }
        return xmlpoints;
    }

}