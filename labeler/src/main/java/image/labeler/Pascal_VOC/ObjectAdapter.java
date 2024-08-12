package image.labeler.Pascal_VOC;

import image.labeler.Polygon;
import image.labeler.Point;
import java.util.ArrayList;
import java.util.List;

public class ObjectAdapter {

    public static List<XmlObjects> adapter(List<Polygon> Polygons){
        List<XmlObjects> objects = new ArrayList<XmlObjects>();
        for (Polygon polygon: Polygons){
            XmlObjects object = new XmlObjects(polygon);
            objects.add(object);
        }
        return objects;
    }

}