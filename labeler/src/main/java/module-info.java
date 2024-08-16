module image.labeler {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires thumbnailator;
    requires javafx.swing;
    requires com.google.gson;

    opens image.labeler to javafx.fxml, com.google.gson; // Abrir el paquete a Gson y JavaFX
    opens image.labeler.JSON to com.google.gson;
    opens image.labeler.COCO to com.google.gson;
    opens image.labeler.COCO.coco_objects to com.google.gson;
    exports image.labeler;
}
