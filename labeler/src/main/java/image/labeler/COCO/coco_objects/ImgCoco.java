package image.labeler.COCO.coco_objects;

public class ImgCoco {
    private final String file_name;
    private final double width;
    private final String id;
    private final double height;

    public ImgCoco(String file_name, double width, String id, double height) {
        this.file_name = file_name;
        this.width = width;
        this.id = id;
        this.height = height;
    }

    public String getFile_name() {
        return file_name;
    }

    public double getWidth() {
        return width;
    }

    public String getId() {
        return id;
    }

    public double getHeight() {
        return height;
    }
}
