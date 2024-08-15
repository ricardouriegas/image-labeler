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

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "annotation") public final class PascalvocXml {

    @XmlTransient
    private static PascalvocXml instance;
    @XmlElement
    private String filepath;
    @XmlElement
    private String id;
    @XmlElement
    private String name = "null";
    @XmlElement
    private int width;
    @XmlElement
    private int height;
    @XmlElementWrapper(name = "Objects")
    @XmlElement(name = "Object")
    private List<XmlObjects> Objects;

    public PascalvocXml() {}

    private PascalvocXml(Img img, String filepath) {
        this.filepath = filepath;
        id = img.getId();
        name = img.getFileName();
        width = img.getWidth();
        height = img.getHeight();
        Objects = ObjectAdapter.adapter(img.getPolygons());
    }

    public static PascalvocXml instance(Img img){
        instance = instance != null ? new PascalvocXml(img,instance.filepath) : new PascalvocXml(img,null);
        return instance;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<XmlObjects> getObjects() {
        return Objects;
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
final class XmlObjects{

    @XmlElement
    private int id;
    @XmlElement
    private String name = "null";
    @XmlElement
    private String category = "null";
    @XmlElementWrapper(name = "Polygon")
    @XmlElement(name = "Points")
    private List<XmlPoints> points;

    public XmlObjects() {}

    XmlObjects(Polygon polygon){
        id = polygon.getId();
        name = polygon.getName();
        category = polygon.getCategory();
        points = PointsAdapter.adapter(polygon.getPoints());
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public List<XmlPoints> getPoints() {
        return points;
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
final class XmlPoints{

    @XmlElement(name = "X")
    private double x;
    @XmlElement(name = "Y")
    private double y;

    public XmlPoints() {}

    XmlPoints(Point point){
        x = point.getX();
        y = point.getY();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
