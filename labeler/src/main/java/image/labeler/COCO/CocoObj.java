package image.labeler.COCO;

import java.util.ArrayList;
import image.labeler.COCO.coco_objects.*;

/**
 *  Class to represent a COCO object
  */
public class CocoObj {
    private ArrayList<ImgCoco> images = new ArrayList<>();
    private ArrayList<Annotation> annotations = new ArrayList<>();
    private ArrayList<Category> categories = new ArrayList<>();

    public ArrayList<ImgCoco> getImages() {
        return images;
    }

    public ArrayList<Annotation> getAnnotations() {
        return annotations;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void addImage(ImgCoco img) {
        images.add(img);
    }

    public void addAnnotation(Annotation annotation) {
        annotations.add(annotation);
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public void setImages(ArrayList<ImgCoco> images) {
        this.images = images;
    }

    public void setAnnotations(ArrayList<Annotation> annotations) {
        this.annotations = annotations;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    @Override   
    public String toString() {
        return "CocoObj{" +
                "images=" + images +
                ", annotations=" + annotations +
                ", categories=" + categories +
                '}';
    }
}
