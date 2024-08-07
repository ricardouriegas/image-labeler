package image.labeler;

import java.util.ArrayList;

public class Img {
    private ArrayList<Polygon> polygons;
    private String fileName;
    private int width;
    private int height;
    private int id;

    public Img(String fileName, int width, int height, int id) {
        this.fileName = fileName;
        this.width = width;
        this.height = height;
        this.id = id;
        polygons = new ArrayList<>();
    }

    public void addPolygon(Polygon polygon) {
        polygons.add(polygon);
    }

    public ArrayList<Polygon> getPolygons() {
        return polygons;
    }

    public String getFileName() {
        return fileName;
    }

    public int getHeight() {
        return height;
    }
    
    public int getWidth() {
        return width;
    }

    public int getId() {
        return id;
    }
}
