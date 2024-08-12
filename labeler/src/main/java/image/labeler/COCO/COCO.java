package image.labeler.COCO;

import image.labeler.Img;
import image.labeler.Polygon;
import image.labeler.Point;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * This class is responsible for creating a COCO-style JSON representation
 * of images, annotations, and categories.
 */
public class COCO {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
    public void addCategory(String id, String name) {
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
    private String getCategoryID(String categoryName) {
        for (Map<String, Object> category : categories) {
            if (category.get("name").equals(categoryName)) {
                return category.get("id").toString(); // Return the ID if found
            }
        }
        return "-1"; // Return -1 if the category is not found
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

    public List<Map<String, Object>> getImages() {
        return images;
    }

    public List<Map<String, Object>> getAnnotations() {
        return annotations;
    }

    public List<Map<String, Object>> getCategories() {
        return categories;
    }

    public void exportToJson(String filePath) {

        if (!filePath.toLowerCase().endsWith(".json")) {
            filePath += ".json";
        }
        
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n");

        // Add images
        jsonBuilder.append("  \"images\": [\n");
        for (int i = 0; i < images.size(); i++) {
            Map<String, Object> image = images.get(i);
            jsonBuilder.append("    {");
            appendJsonMap(jsonBuilder, image);
            jsonBuilder.append("}");
            if (i < images.size() - 1) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\n");
        }
        jsonBuilder.append("  ],\n");

        // Add annotations
        jsonBuilder.append("  \"annotations\": [\n");
        for (int i = 0; i < annotations.size(); i++) {
            Map<String, Object> annotation = annotations.get(i);
            jsonBuilder.append("    {");
            appendJsonMap(jsonBuilder, annotation);
            jsonBuilder.append("}");
            if (i < annotations.size() - 1) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\n");
        }
        jsonBuilder.append("  ],\n");


        ArrayList<Map<String, Object>> existingCategories = new ArrayList<>();
        // Add categories
        jsonBuilder.append("  \"categories\": [\n");
        for (int i = 0; i < categories.size(); i++) {
            Map<String, Object> category = categories.get(i);
            
            if(existingCategories.contains(category)){
                continue;
            }

            jsonBuilder.append("    {");
            appendJsonMap(jsonBuilder, category);
            jsonBuilder.append("}");
            if (i < categories.size() - 1) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\n");
            existingCategories.add(category);
        }
        jsonBuilder.append("  ]\n");

        jsonBuilder.append("}");

        // Write JSON string to file
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void appendJsonMap(StringBuilder jsonBuilder, Map<String, Object> map) {
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                jsonBuilder.append(", ");
            }
            first = false;
            jsonBuilder.append("\"").append(entry.getKey()).append("\": ");
            Object value = entry.getValue();
            if (value instanceof String) {
                jsonBuilder.append("\"").append(value).append("\"");
            } else if (value instanceof Number || value instanceof Boolean) {
                jsonBuilder.append(value);
            } else if (value instanceof List) {
                appendJsonArray(jsonBuilder, (List<?>) value);
            } else {
                // Handle nested objects
                jsonBuilder.append("{");
                appendJsonMap(jsonBuilder, (Map<String, Object>) value);
                jsonBuilder.append("}");
            }
        }
    }

    private void appendJsonArray(StringBuilder jsonBuilder, List<?> list) {
        jsonBuilder.append("[");
        for (int i = 0; i < list.size(); i++) {
            Object item = list.get(i);
            if (item instanceof String) {
                jsonBuilder.append("\"").append(item).append("\"");
            } else if (item instanceof Number || item instanceof Boolean) {
                jsonBuilder.append(item);
            } else if (item instanceof Map) {
                jsonBuilder.append("{");
                appendJsonMap(jsonBuilder, (Map<String, Object>) item);
                jsonBuilder.append("}");
            } else if (item instanceof List) {
                appendJsonArray(jsonBuilder, (List<?>) item);
            }
            if (i < list.size() - 1) {
                jsonBuilder.append(", ");
            }
        }
        jsonBuilder.append("]");
    }
    
    public ArrayList<Img> fromCOCO(String jsonFilePath, ArrayList<Img> imgsFolder) {
        Type imgListType = new TypeToken<ArrayList<Img>>() {}.getType();
        
        ArrayList<Img> imgs = new ArrayList<>();
        String jsonContent = null;

        try {
            jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        ArrayList<String> mainSegments = new ArrayList<>(List.of("\"images\": ","\"annotations\": ","\"categories\": "));
        ArrayList<String> segments = splitByWords(jsonContent, mainSegments, false);

        //ANNOTATIONS
        ArrayList<String> campos = new ArrayList<>(List.of("category_id","bbox","segmentation","id","image_id"));
        String[] annotations = segments.get(1).split("},",-1);

        for(int i = 0; i < annotations.length; i++){
            String annotation = annotations[i];
            annotation = annotation.replace(":","");
            annotation = annotation.replace("\"","");
            annotation = annotation.substring(1,annotation.length()-1);

            ArrayList<String> values = splitByWords(annotation, campos, true);
            for(int j = 1; j < 10; j+=2){
                String value = values.get(i);
                if(value.endsWith(",")){
                    value = value.substring(0, value.length()-1);
                }
            }
        }

        // CATEGORIES
        campos = new ArrayList<>(List.of("name","id"));
        String[] categories = segments.get(2).split("},",-1);

        for(int i = 0; i < categories.length; i++){
            String category = categories[i];
            category = category.replace(":","");
            category = category.replace("\"","");
            category = category.substring(1,category.length()-1);

            ArrayList<String> values = splitByWords(category, campos, true);
            for(int j = 1; j < 10; j+=2){
                String value = values.get(i);
                if(value.endsWith(",")){
                    value = value.substring(0, value.length()-1);
                }
            }
        }
        return imgs;    
    }

    /**
     * Le hace split a una línea según las palabras dadas.
     * @param line la linea a splittear
     * @param words las palabras que van a dividir a la linea
     * @return Un ArrayList<String> con las palabras divididas sin contener a los divisores
     * @throws SQLSyntaxException
     */
    public static ArrayList<String> splitByWords(String line, ArrayList<String> words, Boolean includeSplittingWords) {
        ArrayList<String> tokens = new ArrayList<>();
        String token;

        // Crear un regex pattern a partir de las palabras divisoras
        StringBuilder patternBuilder = new StringBuilder();
        for (String word : words) {
            if (patternBuilder.length() > 0) {
                patternBuilder.append("|");
            }
            patternBuilder.append(Pattern.quote(word));
        }

        Pattern pattern = Pattern.compile(patternBuilder.toString());
        Matcher matcher = pattern.matcher(line);
        int lastEnd = 0;

        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                token = line.substring(lastEnd, matcher.start()).trim();
                if (!token.isEmpty()) {
                    tokens.add(token);
                }
            }

            // agregar la palabra divisora al array de tokens si includeSplittingWords es verdadero
            if (includeSplittingWords) {
                tokens.add(matcher.group().trim());
            }

            lastEnd = matcher.end();
        }

        if (lastEnd < line.length()) {
            tokens.add(line.substring(lastEnd).trim());
        }

        return tokens;
    }            
}
