package image.labeler.COCO;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.HashMap;
import image.labeler.Img;
import image.labeler.Point;
import image.labeler.Polygon;
import image.labeler.COCO.coco_objects.ImgCoco;
import image.labeler.COCO.coco_objects.Annotation;
import image.labeler.COCO.coco_objects.Category;

public class CocoParser {
    private final Gson gson = new Gson().newBuilder().setPrettyPrinting().create();

    /**
     * MD5 hash function to generate unique id for image
     * @param input - file name
     * @return - unique id
     * @author Joshua Arrazola
     */
    private String generateId(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); // MD5 hash function
            byte[] messageDigest = md.digest(input.getBytes()); // generate hash
            BigInteger no = new BigInteger(1, messageDigest); // convert byte array to signum representation
            String hashText = no.toString(16); // convert message digest to hex value
            while (hashText.length() < 32) // add leading zeros to make it 32 bit
                hashText = "0" + hashText;
            return hashText;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parse the images to a COCO object
     * @param images
      */
    public void parse(ArrayList<Img> images, File file) {
        CocoObj cocoObj = new CocoObj();

        for (Img img : images) 
            cocoObj.addImage(new ImgCoco(img.getFileName(), img.getWidth(), img.getId(), img.getHeight()));
        
        ArrayList<Annotation> annotations = getAnnotations(images);
        cocoObj.setAnnotations(annotations);

        ArrayList<Category> categories = getCategories(images);
        cocoObj.setCategories(categories);

        String json = gson.toJson(cocoObj);

        if(!file.getName().endsWith(".json"))
            file = new File(file.getAbsolutePath() + ".json");
        
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Annotation> getAnnotations(ArrayList<Img> images) {
        ArrayList<Annotation> annotations = new ArrayList<>();

        for(Img img : images){
            for(Polygon polygon : img.getPolygons()){
                Double[] bbox = new Double[4]; // Here i create the bbox
                generateBbox(bbox, polygon);

                String category_id = generateId(polygon.getCategory() != null ? polygon.getCategory() : ""); // Here i create the category_id

                ArrayList<ArrayList<Double>> segmentation = new ArrayList<>();
                ArrayList<Double> segment = new ArrayList<>();
                for(Point point : polygon.getPoints()){ // Here i create the segmentation
                    segment.add(point.getX());
                    segment.add(point.getY());
                }
                segmentation.add(segment); // Here i add the segment to the segmentation

                String image_id = img.getId(); // Here i get the image_id
                int polygon_id = polygon.getId(); // Here i get the polygon_id

                annotations.add(new Annotation(category_id, bbox, image_id, polygon_id, segmentation)); // Here i add the annotation
            }
        }

        return annotations;
    }

    private void generateBbox(Double[] bbox, Polygon polygon) {
        // bbox[0] = vertice superior izquierdo x
        // bbox[1] = vertice superior izquierdo y
        // bbox[2] = ancho
        // bbox[3] = alto

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for(Point point : polygon.getPoints()){
            if(point.getX() < minX)
                minX = point.getX();
            if(point.getY() < minY)
                minY = point.getY();
            if(point.getX() > maxX)
                maxX = point.getX();
            if(point.getY() > maxY)
                maxY = point.getY();
        }

        bbox[0] = minX;
        bbox[1] = minY;
        bbox[2] = maxX - minX;
        bbox[3] = maxY - minY;
    }

    private ArrayList<Category> getCategories(ArrayList<Img> images) {
        ArrayList<Category> categories = new ArrayList<>();
        HashSet<String> category_names = new HashSet<>();

        for(Img img : images){
            for(Polygon polygon : img.getPolygons()){
                String category_name = polygon.getCategory();
                if(category_name == null)
                    continue;
                String category_id = generateId(category_name);

                if(!category_names.contains(category_name))
                    category_names.add(category_name);
                else 
                    continue;

                categories.add(new Category(category_name, category_id));
            }
        }

        return categories;
    }

    // ---- IMPORT FUNCTION ----

    public ArrayList<Img> importCoco(File file) {
        ArrayList<Img> images = new ArrayList<>();
        
        String path = file.getAbsolutePath();
        CocoObj cocoObj = gson.fromJson(path, CocoObj.class);

        HashMap<String, String> categories = new HashMap<>(); // category_id -> category_name
        for(Category category : cocoObj.getCategories())
            categories.put(category.getId(), category.getName());

        ArrayList<ImgCoco> imgCocos = cocoObj.getImages();
        HashMap<String, Img> img_map = new HashMap<>(); // image_id -> Img

        for(ImgCoco imgCoco : imgCocos){
            Img img = new Img(imgCoco.getFile_name(), imgCoco.getWidth(), imgCoco.getId(), imgCoco.getHeight());
            img_map.put(img.getId(), img);
            images.add(img);
        }

        for(Annotation annotation : cocoObj.getAnnotations()){
            String category = "";
            if(categories.containsKey(annotation.getCategory_id()))
                category = categories.get(annotation.getCategory_id());
            else
                category = "";
            
            
        }

        return images;
    }

}
