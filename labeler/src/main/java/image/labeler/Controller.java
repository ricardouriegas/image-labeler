package image.labeler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.coobird.thumbnailator.Thumbnails;
import javafx.util.Callback;
import javafx.scene.input.ScrollEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import image.labeler.JSON.JSON;

import java.util.Map;
import javafx.scene.control.Alert.AlertType;
import javafx.embed.swing.SwingFXUtils;

// YOLO import
import image.labeler.YOLO.*;

public class Controller {

    @FXML private Canvas mainCanvas;
    @FXML private TreeView<String> tagTreeView;
    @FXML private Button loadImageButton;
    @FXML private Pane canvasPane;
    @FXML private ListView<File> imageListView;

    private Stage stage;
    private Image currentImage;
    private File tempPngFile;

    private ArrayList<Img> images;
    private Img currentImg;
    private ObservableList<String> categories;
    private Polygon currentPolygon;
    private Polygon selectedPolygon;
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

    private double dragStartX, dragStartY;
    private double canvasStartTranslateX, canvasStartTranslateY;

    private boolean isDragging = false;
    private boolean mousePressed = false;

    private static final Color HIGHLIGHT_COLOR = Color.LIME;

    @FXML
    public void initialize() {
        images = new ArrayList<>();
        categories = FXCollections.observableArrayList();
        currentPolygon = new Polygon();

        // Handlers for mouse events
        mainCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleMouseClick);
        mainCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        mainCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        mainCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);
        mainCanvas.addEventHandler(ScrollEvent.SCROLL, this::handleScroll);

        TreeItem<String> rootItem = new TreeItem<>("Polygons");
        rootItem.setExpanded(true);
        tagTreeView.setRoot(rootItem);

        // Handlers for UI controls
        tagTreeView.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleTreeItemClick);
        tagTreeView.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleTreeItemClick);

        canvasPane.addEventHandler(KeyEvent.KEY_PRESSED, this::handleKeyPress);
        canvasPane.setFocusTraversable(true);
        canvasPane.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleCanvasClick);

        // Add ContextMenu to TreeView for editing polygons
        ContextMenu treeContextMenu = createTreeContextMenu();
        tagTreeView.setContextMenu(treeContextMenu);

        // Custom cell factory to show image previews
        imageListView.setCellFactory(new Callback<ListView<File>, ListCell<File>>() {
            @Override
            public ListCell<File> call(ListView<File> listView) {
                return new ListCell<File>() {
                    private ImageView imageView = new ImageView();

                    @Override
                    protected void updateItem(File item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            try {
                                BufferedImage bufferedImage = Thumbnails.of(item).size(50, 50).asBufferedImage();
                                imageView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
                                setText(item.getName());
                                setGraphic(imageView);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
            }
        });

        // Load selected image when clicked
        imageListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadImage(newValue);
            }
        });
    }

    @FXML
    private void handleLoadImage() {
        openDirectory();
    }

    private void openDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            File[] files = selectedDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".jpeg"));
            if (files != null) {
                imageListView.getItems().setAll(files);
                for (File file : files) {
                    loadImageToList(file);
                }
            }
        }
    }

    private void loadImageToList(File file) {
        try {
            BufferedImage bufferedImage = Thumbnails.of(file).size(1920, 1080).asBufferedImage();
            Image img = SwingFXUtils.toFXImage(bufferedImage, null);

            Img newImg = new Img(file.getName(), (int) img.getWidth(), (int) img.getHeight());
            images.add(newImg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadImage(File file) {
        try {
            currentImg = images.stream().filter(image -> image.getFileName().equals(file.getName())).findFirst().orElse(null);

            // Convert JPEG to PNG if necessary
            if (file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".jpeg")) {
                tempPngFile = convertJpgToPng(file);
                if (tempPngFile != null)
                    currentImage = new Image(tempPngFile.toURI().toString());
            } else {
                currentImage = new Image(file.toURI().toString());
            }

            currentPolygon = new Polygon();
            initialPoint = null;
            colorIndex = 0;
            polygonCounter = findHighestPolygonId(currentImg) + 1; // Adjust the counter based on the highest ID
            selectedPolygon = null;

            adjustCanvasSizeToImage();
            drawImageOnCanvas();
            updatePolygonList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int findHighestPolygonId(Img img) {
        int maxId = 0;
        for (Polygon polygon : img.getPolygons()) {
            if (polygon.getId() > maxId) {
                maxId = polygon.getId();
            }
        }
        return maxId;
    }

    private void adjustCanvasSizeToImage() {
        if (currentImage != null) {
            mainCanvas.setWidth(currentImage.getWidth());
            mainCanvas.setHeight(currentImage.getHeight());
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
        for (int i = 0; i < currentImg.getPolygons().size(); i++) {
            Polygon polygon = currentImg.getPolygons().get(i);
            Color color = (polygon == selectedPolygon) ? HIGHLIGHT_COLOR : colors[i % colors.length];
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
        if (event.getButton() == MouseButton.PRIMARY && !isDragging) {
            if (currentImage == null) return;

            double x = event.getX();
            double y = event.getY();
            Point newPoint = new Point(x, y);

            if (currentPolygon.getPoints().isEmpty()) {
                initialPoint = newPoint;
            }

            if (isClose(initialPoint, newPoint) && currentPolygon.getPoints().size() > 1) {
                currentPolygon.setId(polygonCounter);
                currentPolygon.addPoint(initialPoint);
                currentPolygon.setName("Polygon " + polygonCounter++); // Set a default name
                currentImg.addPolygon(currentPolygon);
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

    private void handleMousePressed(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            isDragging = false;
            mousePressed = true;
            dragStartX = event.getSceneX();
            dragStartY = event.getSceneY();
            canvasStartTranslateX = mainCanvas.getTranslateX();
            canvasStartTranslateY = mainCanvas.getTranslateY();
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && mousePressed) {
            isDragging = true;
            double offsetX = event.getSceneX() - dragStartX;
            double offsetY = event.getSceneY() - dragStartY;
            mainCanvas.setTranslateX(canvasStartTranslateX + offsetX);
            mainCanvas.setTranslateY(canvasStartTranslateY + offsetY);
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            mousePressed = false;
        }
    }

    private void handleScroll(ScrollEvent event) {
        double delta = event.getDeltaY();
        double scale = mainCanvas.getScaleX() + delta / 100;
        scale = Math.max(0.1, Math.min(scale, 10));
        mainCanvas.setScaleX(scale);
        mainCanvas.setScaleY(scale);
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            selectedPolygon = null;
            drawImageOnCanvas();
        }
    }

    private void handleCanvasClick(MouseEvent event) {
        if (selectedPolygon != null && event.getButton() == MouseButton.PRIMARY) {
            selectedPolygon = null;
            drawImageOnCanvas();
        }
    }

    private void handleTreeItemClick(MouseEvent event) {
        TreeItem<String> selectedItem = tagTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && !selectedItem.getValue().startsWith("Category: ")) {
            String polygonName = selectedItem.getValue();
            for (Polygon polygon : currentImg.getPolygons()) {
                if (polygon.getName().equals(polygonName)) {
                    selectedPolygon = polygon;
                    drawImageOnCanvas();
                    break;
                }
            }
        } else {
            selectedPolygon = null;
            drawImageOnCanvas();
        }
    }

    private Polygon findPolygonAt(double x, double y) {
        for (Polygon polygon : currentImg.getPolygons()) {
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

        MenuItem setCategoryItem = new MenuItem("Set Category");
        setCategoryItem.setOnAction(e -> {
            ComboBox<String> comboBox = new ComboBox<>(categories);
            comboBox.setEditable(true);
            if (polygon.getCategory() != null) {
                comboBox.setValue(polygon.getCategory());
            }

            Alert categoryDialog = new Alert(AlertType.CONFIRMATION);
            categoryDialog.setTitle("Polygon Category");
            categoryDialog.setHeaderText("Select or enter a category:");
            categoryDialog.getDialogPane().setContent(comboBox);

            ButtonType confirmButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            categoryDialog.getButtonTypes().setAll(confirmButtonType, cancelButtonType);

            categoryDialog.setOnCloseRequest(dialogEvent -> {
                String category = comboBox.getEditor().getText();
                if (category != null) {
                    if (category.isEmpty()) {
                        polygon.setCategory(null); // Set category to null if the input is empty
                    } else {
                        if (!categories.contains(category)) {
                            categories.add(category);
                        }
                        polygon.setCategory(category);
                    }
                    updatePolygonList();
                    drawImageOnCanvas();
                }
            });

            categoryDialog.showAndWait();
        });

        MenuItem deleteItem = new MenuItem("Delete Polygon");
        deleteItem.setOnAction(e -> {
            currentImg.getPolygons().remove(polygon);
            colorIndex = currentImg.getPolygons().size() % colors.length;
            updatePolygonList();
            drawImageOnCanvas();
        });

        contextMenu.getItems().addAll(setNameItem, setCategoryItem, deleteItem);
        return contextMenu;
    }

    private ContextMenu createTreeContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem renameItem = new MenuItem("Rename Polygon");
        renameItem.setOnAction(e -> {
            TreeItem<String> selectedItem = tagTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && !selectedItem.getValue().startsWith("Category: ")) {
                Polygon polygonToRename = findPolygonByName(selectedItem.getValue());
                if (polygonToRename != null) {
                    TextInputDialog nameDialog = new TextInputDialog(polygonToRename.getName());
                    nameDialog.setTitle("Rename Polygon");
                    nameDialog.setHeaderText("Enter new name for the polygon:");
                    nameDialog.setContentText("Name:");
                    Optional<String> result = nameDialog.showAndWait();
                    result.ifPresent(name -> {
                        polygonToRename.setName(name);
                        updatePolygonList();
                        drawImageOnCanvas();
                    });
                }
            }
        });

        MenuItem changeCategoryItem = new MenuItem("Change Category");
        changeCategoryItem.setOnAction(e -> {
            TreeItem<String> selectedItem = tagTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && !selectedItem.getValue().startsWith("Category: ")) {
                Polygon polygonToChangeCategory = findPolygonByName(selectedItem.getValue());
                if (polygonToChangeCategory != null) {
                    ComboBox<String> comboBox = new ComboBox<>(categories);
                    comboBox.setEditable(true);
                    if (polygonToChangeCategory.getCategory() != null) {
                        comboBox.setValue(polygonToChangeCategory.getCategory()); 
                    }

                    Alert categoryDialog = new Alert(AlertType.CONFIRMATION);
                    categoryDialog.setTitle("Change Polygon Category");
                    categoryDialog.setHeaderText("Select or enter a new category:");
                    categoryDialog.getDialogPane().setContent(comboBox);

                    ButtonType confirmButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                    ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                    categoryDialog.getButtonTypes().setAll(confirmButtonType, cancelButtonType);

                    categoryDialog.setOnCloseRequest(dialogEvent -> {
                        String category = comboBox.getEditor().getText();
                        if (category != null) {
                            if (category.isEmpty()) {
                                polygonToChangeCategory.setCategory(null); 
                            } else {
                                if (!categories.contains(category)) {
                                    categories.add(category);
                                }
                                polygonToChangeCategory.setCategory(category);
                            }
                            updatePolygonList();
                            drawImageOnCanvas();
                        }
                    });

                    categoryDialog.showAndWait();
                }
            }
        });

        MenuItem deleteItem = new MenuItem("Delete Polygon");
        deleteItem.setOnAction(e -> {
            TreeItem<String> selectedItem = tagTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && !selectedItem.getValue().startsWith("Category: ")) {
                Polygon polygonToDelete = findPolygonByName(selectedItem.getValue());
                if (polygonToDelete != null) {
                    currentImg.getPolygons().remove(polygonToDelete);
                    colorIndex = currentImg.getPolygons().size() % colors.length;
                    updatePolygonList();
                    drawImageOnCanvas();
                }
            }
        });

        contextMenu.getItems().addAll(renameItem, changeCategoryItem, deleteItem);
        return contextMenu;
    }

    private Polygon findPolygonByName(String name) {
        for (Polygon polygon : currentImg.getPolygons()) {
            if (polygon.getName().equals(name)) {
                return polygon;
            }
        }
        return null;
    }

    private void updatePolygonList() {
        Map<String, Boolean> categoryExpansionState = new HashMap<>();
        for (TreeItem<String> categoryItem : tagTreeView.getRoot().getChildren()) {
            categoryExpansionState.put(categoryItem.getValue(), categoryItem.isExpanded());
        }

        TreeItem<String> rootItem = new TreeItem<>("Polygons");
        rootItem.setExpanded(true);

        for (String category : categories) {
            TreeItem<String> categoryItem = new TreeItem<>("Category: " + category);
            categoryItem.setExpanded(categoryExpansionState.getOrDefault("Category: " + category, true));
            rootItem.getChildren().add(categoryItem);

            for (Polygon polygon : currentImg.getPolygons()) {
                if (category.equals(polygon.getCategory())) {
                    TreeItem<String> polygonItem = new TreeItem<>(polygon.getName());
                    categoryItem.getChildren().add(polygonItem);
                }
            }
        }

        for (Polygon polygon : currentImg.getPolygons()) {
            if (polygon.getCategory() == null) {
                TreeItem<String> polygonItem = new TreeItem<>(polygon.getName());
                rootItem.getChildren().add(polygonItem);
            }
        }

        tagTreeView.setRoot(rootItem);
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
        stage.setMaximized(true); // Make the stage full screen
    }

    // ** Export functions **

    /**
     * Ok, una vez en este punto, tengan en cuenta que lo que van a tener
     * que exportar es el ArrayList<Img> images, que contiene todas las imágenes
     * con sus respectivos polígonos.
     * 
     * De esta clase Img pueden obtener su nombre, ancho, alto, id y polígonos.
     * A su vez, de cada polígono pueden obtener su nombre (del polígono), categoría y puntos.
     * Cada punto a su vez tiene las coordenadas x e y.
     * 
     * Usen estos datos para exportar a los formatos que les toque.
     * Piensen también en el proceso inverso, recuerden que también debemos poder cargar estos archivos 
     * para recuperar la información. Voy a hacer un método para reconstruir el canva a partir de un arraylist 
     * de imágenes, pero de ustedes depende darme ese array. 
      */

    @FXML
    private void handleExportToCoco() {
        // TODO: Implement the export to COCO format
        // TODO: Aquí ustedes se van a encargar de usar el setter para asignarle exportDate a cada imagen antes de exportar
        // Wichoboy
        // Alan
        // Guijarro
    }

    
    @FXML
    private void handleExportToYolo() {
        // TODO: Implement the export to YOLO format
        // Uriegas
        if (currentImg == null) {
            Alert alert = new Alert(AlertType.ERROR, "No image loaded", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // Convert the polygons of the current image to YOLO format
        List<YOLO> yoloList = YOLOManager.toYolo(currentImg.getPolygons(), (int) currentImage.getWidth(), (int) currentImage.getHeight());
        
        // Save the YOLO objects to a file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save YOLO File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("YOLO Files", "*.txt"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            YOLOManager.saveYolo(file, yoloList);
        }

    }

    @FXML
    private void handleExportToPascalVOC() {
        // TODO: Implement the export to Pascal VOC format
        // Cristobal
        // Aris
    }

    @FXML
    private void handleExportToJson() {
        JSON json = new JSON();
        json.toJson(images);
        // Joshua
    }
}
