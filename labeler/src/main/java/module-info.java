module image.labeler {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires thumbnailator;
    requires javafx.swing;
    requires org.eclipse.persistence.moxy;

    opens image.labeler to javafx.fxml, jakarta.xml.bind;
    exports image.labeler;
    requires jakarta.xml.bind;
    opens image.labeler.Pascal_VOC to jakarta.xml.bind;
}

