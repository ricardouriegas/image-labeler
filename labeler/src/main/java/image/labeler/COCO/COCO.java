package image.labeler.COCO;

import image.labeler.Img;
import image.labeler.Polygon;
import image.labeler.Point;
import java.util.*;

/**
 * This class is responsible for creating a COCO-style JSON representation
 * of images, annotations, and categories.
 */
public class COCO {
    private List<Map<String, Object>> images;
    private List<Map<String, Object>> annotations;
    private List<Map<String, Object>> categories;

    /**
     * Constructor initializes the lists for images, annotations, and categories.
     */
    public COCO() {
        images = new ArrayList<>();
        annotations = new ArrayList<>();
        categories = new ArrayList<>();
    }

    /**
     * Adds an image and its associated annotations to the COCO representation.
     *
     * @param img The image to be added.
     */
    public void addImage(Img img) {
        // Create a map to represent the image
        Map<String, Object> image = new HashMap<>();
        image.put("id", img.getId()); // Unique ID for the image
        image.put("file_name", img.getFileName()); // File name of the image
        image.put("width", img.getWidth()); // Width of the image
        image.put("height", img.getHeight()); // Height of the image
        images.add(image); // Add the image map to the list

        // Add annotations for each polygon in the image
        for (Polygon polygon : img.getPolygons()) {
            addAnnotation(polygon, img.getId());
        }
    }

    /**
     * Adds an annotation for a given polygon to the COCO representation.
     *
     * @param polygon The polygon to be annotated.
     * @param imageId The ID of the image the polygon is associated with.
     */
    public void addAnnotation(Polygon polygon, String imageId) {
        // Create a map to represent the annotation
        Map<String, Object> annotation = new HashMap<>();
        annotation.put("id", polygon.getId()); // Unique ID for the annotation
        annotation.put("image_id", imageId); // ID of the associated image
        annotation.put("category_id", getCategoryID(polygon.getCategory())); // ID of the category

        // Convert the polygon's points to a list of coordinates for segmentation
        List<Double> segmentation = new ArrayList<>();
        for (Point point : polygon.getPoints()) {
            segmentation.add(point.getX());
            segmentation.add(point.getY());
        }
        annotation.put("segmentation", Arrays.asList(segmentation)); // Add segmentation data
        annotation.put("bbox", calculateBbox(polygon.getPoints())); // Add bounding box data

        annotations.add(annotation); // Add the annotation map to the list
    }

    /**
     * Adds a category to the COCO representation.
     *
     * @param id The unique ID for the category.
     * @param name The name of the category.
     */
    public void addCategory(int id, String name) {
        // Create a map to represent the category
        Map<String, Object> category = new HashMap<>();
        category.put("id", id); // Unique ID for the category
        category.put("name", name); // Name of the category
        categories.add(category); // Add the category map to the list
    }

    /**
     * Retrieves the ID for a category based on its name.
     *
     * @param categoryName The name of the category.
     * @return The ID of the category, or -1 if not found.
     */
    private int getCategoryID(String categoryName) {
        for (Map<String, Object> category : categories) {
            if (category.get("name").equals(categoryName)) {
                return (int) category.get("id"); // Return the ID if found
            }
        }
        return -1; // Return -1 if the category is not found
    }

    /**
     * Calculates the bounding box for a list of points.
     *
     * @param points The list of points defining the polygon.
     * @return A list containing the bounding box coordinates [x, y, width, height].
     */
    private List<Integer> calculateBbox(List<Point> points) {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

        // Find the minimum and maximum x and y coordinates
        for (Point point : points) {
            if (point.getX() < minX) minX = point.getX();
            if (point.getY() < minY) minY = point.getY();
            if (point.getX() > maxX) maxX = point.getX();
            if (point.getY() > maxY) maxY = point.getY();
        }

        // Create a list for the bounding box coordinates
        return Arrays.asList((int) minX, (int) minY, (int) (maxX - minX), (int) (maxY - minY));
    }
}
