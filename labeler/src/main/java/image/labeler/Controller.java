package image.labeler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.coobird.thumbnailator.Thumbnails;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

public class Controller {

    // Canvas to display the image
    @FXML
    private Canvas mainCanvas;

    // List of tags
    @FXML
    private ListView<String> tagListView;

    // Button to load an image
    @FXML
    private Button loadImageButton;

    // Pane to hold the canvas
    @FXML
    private Pane canvasPane;

    private Stage stage; // main stage
    private Image currentImage; // current image

    /**
     * Initializes the controller
     */
    @FXML
    public void initialize() {
        // ? We will use this to add tags to the list view when we implement the tagging 
        // ? functionality.
        // ? Use the following line to add items to the list view
        // tagListView.getItems().addAll("Etiqueta 1", "Etiqueta 2", "Etiqueta 3");

        // Bind the canvas size to the pane size
        mainCanvas.widthProperty().bind(canvasPane.widthProperty());
        mainCanvas.heightProperty().bind(canvasPane.heightProperty());

        // Redraw the image when the canvas size changes
        mainCanvas.widthProperty().addListener((observable, oldValue, newValue) -> drawImageOnCanvas());
        mainCanvas.heightProperty().addListener((observable, oldValue, newValue) -> drawImageOnCanvas());
    }

    /**
     * Handles the load image button
     */
    @FXML
    private void handleLoadImage() {
        FileChooser fileChooser = new FileChooser(); // create a file chooser
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(stage); // show the file chooser

        // Load the image
        if (file != null) {
            String filePath = file.toURI().toString();
            if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg"))
                file = convertJpgToPng(file);

            currentImage = new Image(file.toURI().toString()); // load the image
            drawImageOnCanvas(); // draw the image on the canvas
        }
    }

    /**
     * Function to draw the image on the canvas
     */
    private void drawImageOnCanvas() {
        if (currentImage != null) {
            GraphicsContext gc = mainCanvas.getGraphicsContext2D(); // get the graphics context
            gc.clearRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight()); // clear the canvas
            gc.drawImage(currentImage, 0, 0, mainCanvas.getWidth(), mainCanvas.getHeight()); // draw the image
        }
    }

    /**
     * Converts a jpg file to a png file
     * 
     * @param jpgFile
     * @return pngFile
     */
    private File convertJpgToPng(File jpgFile) {
        try {
            // Convert the jpg file to a png file
            BufferedImage bufferedImage = Thumbnails.of(jpgFile)
                    .size(1920, 1080)
                    .asBufferedImage();

            // Save the png file
            File pngFile = new File(jpgFile.getParent(),
                    jpgFile.getName().replace(".jpg", ".png").replace(".jpeg", ".png"));

            // Save the png file
            Thumbnails.of(bufferedImage)
                    .scale(1)
                    .outputFormat("png")
                    .toFile(pngFile);
            return pngFile;
        } catch (IOException e) {
            e.printStackTrace();
            return jpgFile; // Return original file if conversion fails
        }
    }

    /**
     * Sets the main stage
     * 
     * @param stage
     */
    public void setStage(@SuppressWarnings("exports") Stage stage) {
        this.stage = stage;
    }
}
