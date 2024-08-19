package image.labeler.COCO.coco_objects;

import java.util.ArrayList;

/**
 *  Class to represent a COCO annotation
  */
public class Annotation {
    private String category_id;
    private Double[] bbox = new Double[4];
    private String image_id;
    private int id;
    private ArrayList<ArrayList<Double>> segmentation;

    public Annotation(String category_id, Double[] bbox, String image_id, int id, ArrayList<ArrayList<Double>> segmentation) {
        this.category_id = category_id;
        this.bbox = bbox;
        this.image_id = image_id;
        this.id = id;
        this.segmentation = segmentation;
    }

    public Double[] getBbox() {
        return bbox;
    }

    public String getCategory_id() {
        return category_id;
    }
    
    public int getId() {
        return id;
    }

    public String getImage_id() {
        return image_id;
    }

    public ArrayList<ArrayList<Double>> getSegmentation() {
        return segmentation;
    }
}
