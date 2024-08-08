module image.labeler {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires thumbnailator;
    requires javafx.swing;
    requires com.google.gson;

    opens image.labeler to javafx.fxml;
    exports image.labeler;
}
