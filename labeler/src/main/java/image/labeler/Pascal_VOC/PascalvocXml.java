package image.labeler.Pascal_VOC;

import image.labeler.Point;
import jakarta.xml.bind.annotation.*;
import image.labeler.Polygon;
import image.labeler.Img;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Singel Tone Class creada para proporcionar para serializar
 */
@XmlRootElement(name = "annotation")
@XmlAccessorType(XmlAccessType.FIELD)
public final class PascalvocXml {

    @XmlTransient
    private static PascalvocXml instance;
    @XmlElement
    private int id;
    @XmlElement
    private String name;
    @XmlElement
    private int width;
    @XmlElement
    private int height;
    @XmlElement(name = "Objects")
    private List<XmlObjects> Objects;

    private PascalvocXml(Img img) {
        id = img.getId();
        name = img.getFileName();
        width = img.getWidth();
        height = img.getHeight();
        Objects = ObjectAdapter.adapter(img.getPolygons());
    }

    public static PascalvocXml instance(Img img){
        instance = instance == null ? new PascalvocXml(img) : instance;
        return instance;
    }

}

@XmlRootElement(name = "Object")
final class XmlObjects{

    @XmlTransient
    private static int num;
    @XmlElement
    private int id;
    @XmlElement
    private String name;
    @XmlElement
    private String category;
    @XmlElement(name = "Points")
    private List<XmlPoint> points;

    XmlObjects(Polygon polygon){
        id = polygon.getId();
        name = polygon.getName();
        category = polygon.getCategory();
        points = PointsAdapter.adapter(polygon.getPoints());
    }

}

@XmlRootElement(name = "Points")
final class XmlPoint{

    @XmlElement(name = "X")
    private double x;
    @XmlElement(name = "Y")
    private double y;

    XmlPoint(Point point){
        x = point.getX();
        y = point.getY();
    }

}
