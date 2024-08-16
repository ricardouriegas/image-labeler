package image.labeler.COCO;

import java.util.ArrayList;
import java.util.List;
import image.labeler.COCO.coco_objects.*;

/**
 *  Class to represent a COCO object
  */
public class CocoObj {
    private List<ImgCoco> images = new ArrayList<>();
    private List<Annotation> annotations = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();

    public List<ImgCoco> getImages() {
        return images;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public List<Category> getCategories() {
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

    public void setImages(List<ImgCoco> images) {
        this.images = images;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
