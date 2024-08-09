package image.labeler.JSON;

import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import image.labeler.Img;
import javafx.stage.FileChooser;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

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
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                gson.toJson(imgs, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
