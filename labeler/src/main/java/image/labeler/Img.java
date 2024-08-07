package image.labeler;

import java.util.ArrayList;

public class Img {
    private ArrayList<Polygon> polygons;
    private String fileName;
    private int width;
    private int height;

    public Img(String fileName, int width, int height) {
        this.fileName = fileName;
        this.width = width;
        this.height = height;
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
}
