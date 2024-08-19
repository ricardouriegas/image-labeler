package image.labeler.JSON;

import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import image.labeler.Img;
import javafx.stage.FileChooser;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * JSON class to save the images in a JSON file
 * @date 08/agosto/2024
 * @author Joshua Arrazola
  */
public class JSON {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Saves the labels in a JSON file
     * @param imgs ArrayList of Img objects
      */
    public void toJson(ArrayList<Img> imgs){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save JSON File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            if(!file.getName().endsWith(".json"))
                file = new File(file.getAbsolutePath() + ".json");
            
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                gson.toJson(imgs, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Img> fromJson(String jsonFilePath, ArrayList<Img> imgsFolder) {
        Type imgListType = new TypeToken<ArrayList<Img>>() {}.getType();
        
        ArrayList<Img> imgs = new ArrayList<>();
    
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            imgs = gson.fromJson(jsonContent, imgListType);
            if (imgs == null) {
                System.out.println("No images found in JSON.");
                return new ArrayList<>();
            }
        } catch (IOException e) {
            System.out.println("Failed to read JSON file: " + e.getMessage());
            return new ArrayList<>();
        } catch (JsonSyntaxException e) {
            System.out.println("Failed to parse JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    
        // for (Img img : imgs) {
        //     System.out.println(img.toString());
        // }
    
        return imgs;
    }
}
