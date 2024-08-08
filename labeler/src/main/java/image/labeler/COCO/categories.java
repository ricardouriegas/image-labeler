package image.labeler.COCO;

public class categories {
    int id;
    String name;

    /**
     * This constructor should be able to create a categorie object from the parameters
     * @param id
     * @param name
     */
    public categories(int id, String name) {
        this.id = id;
        this.name = name;
    }
}