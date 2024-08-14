package image.labeler.COCO;

import java.util.*;
import image.labeler.Point;

public class annotations {
    String image_id;
    String category_id;
    List<Point> segmentation;
    List<Integer> bbox;

    public annotations(String image_id, String category_id, List<Point> segmentation, List<Integer> bbox) {
        this.image_id = image_id;
        this.category_id = category_id;
        this.segmentation = segmentation;
        this.bbox = bbox;
    }

    public List<Integer> getBbox() {
        return bbox;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getImage_id() {
        return image_id;
    }

    public List<Point> getSegmentation() {
        return segmentation;
    }

    public void setBbox(List<Integer> bbox) {
        this.bbox = bbox;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public void setSegmentation(List<Point> segmentation) {
        this.segmentation = segmentation;
    }

    @Override
    public String toString() {
        return "annotations{" +
                "image_id='" + image_id + '\'' +
                ", category_id='" + category_id + '\'' +
                ", segmentation=" + segmentation +
                ", bbox=" + bbox +
                '}';
    }
}
