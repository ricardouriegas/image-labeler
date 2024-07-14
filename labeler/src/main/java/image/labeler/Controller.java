package image.labeler;

import java.io.File;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
        // Add some tags to the list
        tagListView.getItems().addAll("Etiqueta 1", "Etiqueta 2", "Etiqueta 3");

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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.gif")); // set the file extension filters
        File file = fileChooser.showOpenDialog(stage); // show the file chooser

        // Load the image
        if (file != null) {
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
     * Sets the main stage
     * @param stage
      */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
