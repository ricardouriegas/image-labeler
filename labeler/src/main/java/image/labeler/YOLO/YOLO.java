package image.labeler.YOLO;

// import <!-- com.github.bjoern2 -->
import image.labeler.Polygon;
import image.labeler.Point;

/**
 * This class should be able to export the polygons to the YOLO format
 * and also should be able to import the YOLO format to polygons
 */
public class YOLO {
    int classId;
    double x, y, width, height;

    /**
     * This constructor should be able to create a YOLO object from the parameters
     * @param classId
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public YOLO(int classId, double x, double y, double width, double height) {
        this.classId = classId;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * This constructor should be able to parse a string in YOLO format
     * @param yoloString
     */
    public YOLO(String yoloString) {
        String[] parts = yoloString.split(" ");
        this.classId = Integer.parseInt(parts[0]);
        this.x = Double.parseDouble(parts[1]);
        this.y = Double.parseDouble(parts[2]);
        this.width = Double.parseDouble(parts[3]);
        this.height = Double.parseDouble(parts[4]);
    }

    /**
     * This method should be able to convert the YOLO object to a Polygon object
     * @param imageWidth
     * @param imageHeight
     * @return
     */
    public Polygon toPolygon(int imageWidth, int imageHeight) {
        Polygon polygon = new Polygon();
        polygon.addPoint(new Point(x * imageWidth - width * imageWidth / 2, y * imageHeight - height * imageHeight / 2));
        polygon.addPoint(new Point(x * imageWidth + width * imageWidth / 2, y * imageHeight - height * imageHeight / 2));
        polygon.addPoint(new Point(x * imageWidth + width * imageWidth / 2, y * imageHeight + height * imageHeight / 2));
        polygon.addPoint(new Point(x * imageWidth - width * imageWidth / 2, y * imageHeight + height * imageHeight / 2));
        return polygon;
    }

    /**
     * This method should be able to convert the YOLO object to a string in YOLO format
     * @return
     */
    public String toYOLOFormat() {
        return String.format("%d %.6f %.6f %.6f %.6f", classId, x, y, width, height);
    }

    @Override
    public String toString() {
        return "YoloLabel{" +
                "classId=" + classId +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
