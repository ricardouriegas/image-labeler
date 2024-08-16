package image.labeler.COCO.coco_objects;

public class ImgCoco {
    private final String file_name;
    private final int width;
    private final String id;
    private final int height;

    public ImgCoco(String file_name, int width, String id, int height) {
        this.file_name = file_name;
        this.width = width;
        this.id = id;
        this.height = height;
    }

    public String getFile_name() {
        return file_name;
    }

    public int getWidth() {
        return width;
    }

    public String getId() {
        return id;
    }

    public int getHeight() {
        return height;
    }
}
