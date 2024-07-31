package image.labeler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.coobird.thumbnailator.Thumbnails;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

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
    private File tempPngFile; // temporary PNG file

    private ArrayList<Polygon> polygons; // List of polygons
    private Polygon currentPolygon; // Current polygon being drawn
    private Point initialPoint; // Initial point

    private static final double CLOSE_DISTANCE = 10.0; // Distance to detect close points
    private Color[] colors = { Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.PURPLE }; // Array of colors
    private int colorIndex = 0; // Index to track current color

    /**
     * Initializes the controller
     */
    @FXML
    public void initialize() {
        polygons = new ArrayList<>();
        currentPolygon = new Polygon();

        // Bind the canvas size to the pane size
        mainCanvas.widthProperty().bind(canvasPane.widthProperty());
        mainCanvas.heightProperty().bind(canvasPane.heightProperty());

        // Redraw the image when the canvas size changes
        mainCanvas.widthProperty().addListener((observable, oldValue, newValue) -> drawImageOnCanvas());
        mainCanvas.heightProperty().addListener((observable, oldValue, newValue) -> drawImageOnCanvas());

        // Add click event handler
        mainCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleMouseClick);
    }

    /**
     * Handles the load image button
     */
    @FXML
    private void handleLoadImage() {
        FileChooser fileChooser = new FileChooser(); // create a file chooser
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(stage); // show the file chooser

        // Load the image
        if (file != null) {
            String filePath = file.toURI().toString();

            // Check if the file is a jpg file
            if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
                tempPngFile = convertJpgToPng(file); // convert the jpg file to a png file

                if (tempPngFile != null)
                    currentImage = new Image(tempPngFile.toURI().toString()); // load the image

            } else
                currentImage = new Image(file.toURI().toString()); // load the image

            drawImageOnCanvas(); // draw the image on the canvas
        }
    }

    /**
     * Function to draw the image on the canvas
     */
    private void drawImageOnCanvas() {
        GraphicsContext gc = mainCanvas.getGraphicsContext2D(); // get the graphics context
        gc.clearRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight()); // clear the canvas
        if (currentImage != null) {
            gc.drawImage(currentImage, 0, 0, mainCanvas.getWidth(), mainCanvas.getHeight()); // draw the image
        }
        drawAllPolygons(gc); // draw all the polygons

        // Delete the temporary PNG file if it exists
        if (tempPngFile != null && tempPngFile.exists()) {
            tempPngFile.delete();
            tempPngFile = null;
        }
    }

    /**
     * Function to draw all polygons on the canvas
     */
    private void drawAllPolygons(GraphicsContext gc) {
        gc.setLineWidth(2);

        for (int i = 0; i < polygons.size(); i++) {
            Polygon polygon = polygons.get(i);
            Color color = colors[i % colors.length];
            gc.setFill(color);
            gc.setStroke(color);

            Point prevPoint = null;
            for (Point point : polygon.getPoints()) {
                gc.fillOval(point.getX() - 2.5, point.getY() - 2.5, 5, 5); // draw a small circle at each point
                if (prevPoint != null) {
                    gc.strokeLine(prevPoint.getX(), prevPoint.getY(), point.getX(), point.getY()); // draw line between points
                }
                prevPoint = point;
            }
            if (polygon.getPoints().size() > 2) {
                Point firstPoint = polygon.getPoints().get(0);
                Point lastPoint = polygon.getPoints().get(polygon.getPoints().size() - 1);
                gc.strokeLine(lastPoint.getX(), lastPoint.getY(), firstPoint.getX(), firstPoint.getY()); // close the polygon
            }
        }

        // Draw the current polygon being created
        gc.setFill(colors[colorIndex]);
        gc.setStroke(colors[colorIndex]);
        Point prevPoint = null;
        for (Point point : currentPolygon.getPoints()) {
            gc.fillOval(point.getX() - 2.5, point.getY() - 2.5, 5, 5); // draw a small circle at each point
            if (prevPoint != null) {
                gc.strokeLine(prevPoint.getX(), prevPoint.getY(), point.getX(), point.getY()); // draw line between points
            }
            prevPoint = point;
        }
    }

    /**
     * Handles mouse click events
     */
    private void handleMouseClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (currentImage == null) return; // return if no image is loaded

            double x = event.getX();
            double y = event.getY();
            Point newPoint = new Point(x, y);

            if (currentPolygon.getPoints().isEmpty()) {
                initialPoint = newPoint; // save the initial point
            }

            if (isClose(initialPoint, newPoint) && currentPolygon.getPoints().size() > 1) {
                // Close the polygon by drawing a line to the initial point
                currentPolygon.addPoint(initialPoint);
                polygons.add(currentPolygon); // Save the current polygon
                currentPolygon = new Polygon(); // Reset for the next polygon
                initialPoint = null;
                colorIndex = (colorIndex + 1) % colors.length; // Move to the next color
            } else {
                currentPolygon.addPoint(newPoint);
            }

            drawImageOnCanvas(); // redraw the canvas to include the new point and lines
        }
    }

    /**
     * Checks if two points are close to each other
     */
    private boolean isClose(Point p1, Point p2) {
        double distance = Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
        return distance < CLOSE_DISTANCE;
    }

    /**
     * Converts a jpg file to a png file
     * @param jpgFile
     * @return pngFile
     */
    private File convertJpgToPng(File jpgFile) {
        try {
            // Convert the jpg file to a buffered image
            BufferedImage bufferedImage = Thumbnails.of(jpgFile)
                    .size(1920, 1080)
                    .asBufferedImage();

            // Save the buffered image to a temporary PNG file
            File pngFile = new File(jpgFile.getParent(),
                    jpgFile.getName().replace(".jpg", ".png").replace(".jpeg", ".png"));
            Thumbnails.of(bufferedImage)
                    .scale(1)
                    .outputFormat("png")
                    .toFile(pngFile);
            return pngFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null if conversion fails
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
