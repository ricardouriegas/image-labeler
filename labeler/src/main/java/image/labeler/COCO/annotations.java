package image.labeler.COCO;

import java.util.*;
import image.labeler.Point;

public class annotations {
    int image_id;
    int category_id;
    List<Point> segmentation;
    List<Integer> bbox;

    public annotations(int image_id, int category_id, List<Point> segmentation, List<Integer> bbox) {
        this.image_id = image_id;
        this.category_id = category_id;
        this.segmentation = segmentation;
        this.bbox = bbox;
    }

    public List<Integer> getBbox() {
        return bbox;
    }

    public int getCategory_id() {
        return category_id;
    }

    public int getImage_id() {
        return image_id;
    }

    public List<Point> getSegmentation() {
        return segmentation;
    }

    public void setBbox(List<Integer> bbox) {
        this.bbox = bbox;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public void setSegmentation(List<Point> segmentation) {
        this.segmentation = segmentation;
    }
}