package image.labeler;

import java.util.ArrayList;
import java.io.Serializable;

public class Polygon implements Serializable {
    private int id;
    private String name;
    private String category;
    private ArrayList<Point> points;

    public Polygon() {
        points = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    @Override
    public String toString() {
        String content = "";
        for (Point point : points) {
            content += point.toString() + "\n";
        }

        return "Polygon{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", points=" + content +
                '}';
    }
}
