package image.labeler.Pascal_VOC;

import image.labeler.Point;
import image.labeler.Polygon;
import java.util.ArrayList;
import java.util.List;

public class ObjectAdapter {

    private ObjectAdapter(){}

    public static List<XmlObjects> adapter(List<Polygon> Polygons){
        List<XmlObjects> objects = new ArrayList<XmlObjects>();
        for (Polygon polygon: Polygons){
            XmlObjects object = new XmlObjects(polygon);
            objects.add(object);
        }
        return objects;
    }

    public static List<Polygon> toPolygon(List<XmlObjects> Objects){
        List<Polygon> polygons = new ArrayList<Polygon>();
        for (XmlObjects object : Objects){
            int id = object.getId();
            String name = object.getName();
            String category = object.getCategory();
            ArrayList<Point> polygon = (ArrayList<Point>) PointsAdapter.toPoints(object.getPoints());
            polygons.add(new Polygon(id,name,category,polygon));
        }
        return polygons;
    }

}