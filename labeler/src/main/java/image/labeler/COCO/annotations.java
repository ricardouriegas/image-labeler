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
}