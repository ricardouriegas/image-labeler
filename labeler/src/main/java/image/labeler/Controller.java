package image.labeler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.coobird.thumbnailator.Thumbnails;

public class Controller {

    @FXML private Canvas mainCanvas;
    @FXML private ListView<String> tagListView;
    @FXML private Button loadImageButton;
    @FXML private Pane canvasPane;

    private Stage stage;
    private Image currentImage;
    private File tempPngFile;

    private ArrayList<Polygon> polygons;
    private Polygon currentPolygon;
    private Point initialPoint;

    private static final double CLOSE_DISTANCE = 10.0;
    private Color[] colors = { 
        Color.RED, Color.BLUE, 
        Color.GREEN, Color.ORANGE, 
        Color.PURPLE, Color.YELLOW,
        Color.CYAN, Color.MAGENTA,
        Color.BROWN, Color.PINK
    };
    private int colorIndex = 0;
    private int polygonCounter = 1; // Contador para nombres predeterminados

    private ContextMenu currentContextMenu;

    @FXML
    public void initialize() {
        polygons = new ArrayList<>();
        currentPolygon = new Polygon();

        mainCanvas.widthProperty().bind(canvasPane.widthProperty());
        mainCanvas.heightProperty().bind(canvasPane.heightProperty());
        mainCanvas.widthProperty().addListener((observable, oldValue, newValue) -> drawImageOnCanvas());
        mainCanvas.heightProperty().addListener((observable, oldValue, newValue) -> drawImageOnCanvas());
        mainCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleMouseClick);
    }

    @FXML
    private void handleLoadImage() {
        if (!polygons.isEmpty() || !currentPolygon.getPoints().isEmpty()) {
            Alert alert = new Alert(AlertType.CONFIRMATION, "You have unsaved work. Do you want to load a new image and lose the current work?", ButtonType.YES, ButtonType.NO);
            alert.setTitle("Unsaved Work Warning");
            alert.setHeaderText(null);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    loadImage();
                }
            });
        } else {
            loadImage();
        }
    }

    private void loadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            String filePath = file.toURI().toString();
            if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
                tempPngFile = convertJpgToPng(file);
                if (tempPngFile != null)
                    currentImage = new Image(tempPngFile.toURI().toString());
            } else {
                currentImage = new Image(file.toURI().toString());
            }
            polygons.clear();
            currentPolygon = new Polygon();
            initialPoint = null;
            colorIndex = 0;
            polygonCounter = 1; // Reset the counter when a new image is loaded
            drawImageOnCanvas();
        }
    }

    private void drawImageOnCanvas() {
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());
        if (currentImage != null) {
            gc.drawImage(currentImage, 0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());
        }
        drawAllPolygons(gc);
        if (tempPngFile != null && tempPngFile.exists()) {
            tempPngFile.delete();
            tempPngFile = null;
        }
    }

    private void drawAllPolygons(GraphicsContext gc) {
        gc.setLineWidth(2);
        for (int i = 0; i < polygons.size(); i++) {
            Polygon polygon = polygons.get(i);
            Color color = colors[i % colors.length];
            gc.setFill(color.deriveColor(0, 1, 1, 0.3));
            gc.setStroke(color);
            Point prevPoint = null;
            double[] xPoints = new double[polygon.getPoints().size()];
            double[] yPoints = new double[polygon.getPoints().size()];
            for (int j = 0; j < polygon.getPoints().size(); j++) {
                Point point = polygon.getPoints().get(j);
                xPoints[j] = point.getX();
                yPoints[j] = point.getY();
                gc.fillOval(point.getX() - 2.5, point.getY() - 2.5, 5, 5);
                if (prevPoint != null) {
                    gc.strokeLine(prevPoint.getX(), prevPoint.getY(), point.getX(), point.getY());
                }
                prevPoint = point;
            }
            if (polygon.getPoints().size() > 2) {
                gc.fillPolygon(xPoints, yPoints, polygon.getPoints().size());
                Point firstPoint = polygon.getPoints().get(0);
                Point lastPoint = polygon.getPoints().get(polygon.getPoints().size() - 1);
                gc.strokeLine(lastPoint.getX(), lastPoint.getY(), firstPoint.getX(), firstPoint.getY());
            }

            // Draw the polygon name in the center
            drawPolygonName(gc, polygon);
        }

        gc.setFill(colors[colorIndex].deriveColor(0, 1, 1, 0.3));
        gc.setStroke(colors[colorIndex]);
        Point prevPoint = null;
        double[] xPoints = new double[currentPolygon.getPoints().size()];
        double[] yPoints = new double[currentPolygon.getPoints().size()];
        for (int i = 0; i < currentPolygon.getPoints().size(); i++) {
            Point point = currentPolygon.getPoints().get(i);
            xPoints[i] = point.getX();
            yPoints[i] = point.getY();
            gc.fillOval(point.getX() - 2.5, point.getY() - 2.5, 5, 5);
            if (prevPoint != null) {
                gc.strokeLine(prevPoint.getX(), prevPoint.getY(), point.getX(), point.getY());
            }
            prevPoint = point;
        }
    }

    private void drawPolygonName(GraphicsContext gc, Polygon polygon) {
        double centerX = 0;
        double centerY = 0;
        for (Point point : polygon.getPoints()) {
            centerX += point.getX();
            centerY += point.getY();
        }
        centerX /= polygon.getPoints().size();
        centerY /= polygon.getPoints().size();
        gc.setFill(Color.BLACK);
        gc.fillText(polygon.getName(), centerX, centerY);
    }

    private void handleMouseClick(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (currentImage == null) return;

            double x = event.getX();
            double y = event.getY();
            Point newPoint = new Point(x, y);

            if (currentPolygon.getPoints().isEmpty()) {
                initialPoint = newPoint;
            }

            if (isClose(initialPoint, newPoint) && currentPolygon.getPoints().size() > 1) {
                currentPolygon.addPoint(initialPoint);
                currentPolygon.setName("Polygon " + polygonCounter++); // Set a default name
                polygons.add(currentPolygon);
                currentPolygon = new Polygon();
                initialPoint = null;
                colorIndex = (colorIndex + 1) % colors.length;
                updatePolygonList(); // Update the list view with the new polygon
            } else {
                currentPolygon.addPoint(newPoint);
            }

            drawImageOnCanvas();
        } else if (event.getButton() == MouseButton.SECONDARY) {
            if (currentContextMenu != null && currentContextMenu.isShowing()) {
                currentContextMenu.hide();
            }
            Polygon polygon = findPolygonAt(event.getX(), event.getY());
            if (polygon != null) {
                currentContextMenu = createPolygonContextMenu(polygon);
                currentContextMenu.show(mainCanvas, event.getScreenX(), event.getScreenY());
            }
        }
    }

    private Polygon findPolygonAt(double x, double y) {
        for (Polygon polygon : polygons) {
            if (isPointInPolygon(x, y, polygon.getPoints())) {
                return polygon;
            }
        }
        return null;
    }

    private boolean isPointInPolygon(double x, double y, ArrayList<Point> points) {
        int intersectCount = 0;
        for (int i = 0; i < points.size(); i++) {
            Point p1 = points.get(i);
            Point p2 = points.get((i + 1) % points.size());

            if (((p1.getY() > y) != (p2.getY() > y)) &&
                (x < (p2.getX() - p1.getX()) * (y - p1.getY()) / (p2.getY() - p1.getY()) + p1.getX())) {
                intersectCount++;
            }
        }
        return (intersectCount % 2) == 1;
    }

    private ContextMenu createPolygonContextMenu(Polygon polygon) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem setNameItem = new MenuItem("Set Name");
        setNameItem.setOnAction(e -> {
            TextInputDialog nameDialog = new TextInputDialog(polygon.getName());
            nameDialog.setTitle("Polygon Name");
            nameDialog.setHeaderText("Enter the name for this polygon:");
            nameDialog.setContentText("Name:");
            Optional<String> result = nameDialog.showAndWait();
            result.ifPresent(name -> {
                polygon.setName(name);
                updatePolygonList();
                drawImageOnCanvas();
            });
        });

        MenuItem deleteItem = new MenuItem("Delete Polygon");
        deleteItem.setOnAction(e -> {
            polygons.remove(polygon);
            colorIndex = polygons.size() % colors.length;
            updatePolygonList();
            drawImageOnCanvas();
        });

        contextMenu.getItems().addAll(setNameItem, deleteItem);
        return contextMenu;
    }

    private void updatePolygonList() {
        tagListView.getItems().clear();
        polygons.forEach(p -> tagListView.getItems().add(p.getName()));
    }

    private boolean isClose(Point p1, Point p2) {
        double distance = Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
        return distance < CLOSE_DISTANCE;
    }

    private File convertJpgToPng(File jpgFile) {
        try {
            BufferedImage bufferedImage = Thumbnails.of(jpgFile).size(1920, 1080).asBufferedImage();
            File pngFile = new File(jpgFile.getParent(), jpgFile.getName().replace(".jpg", ".png").replace(".jpeg", ".png"));
            Thumbnails.of(bufferedImage).scale(1).outputFormat("png").toFile(pngFile);
            return pngFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
