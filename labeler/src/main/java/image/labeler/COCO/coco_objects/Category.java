package image.labeler.COCO.coco_objects;

/**
 * Class to represent a COCO category
  */
public class Category {
    private final String name;
    private final String id;

    public Category(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
