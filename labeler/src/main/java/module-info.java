module image.labeler {
    requires javafx.controls;
    requires javafx.fxml;

    opens image.labeler to javafx.fxml;
    exports image.labeler;
}
