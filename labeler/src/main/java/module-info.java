module image.labeler {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires thumbnailator;
    requires javafx.swing;
<<<<<<< HEAD
    requires com.google.gson;

    opens image.labeler to javafx.fxml, com.google.gson; // Abrir el paquete a Gson y JavaFX
    opens image.labeler.JSON to com.google.gson;
=======
    requires org.eclipse.persistence.moxy;

    opens image.labeler to javafx.fxml, jakarta.xml.bind;
>>>>>>> origin/Cristov
    exports image.labeler;
    requires jakarta.xml.bind;
    opens image.labeler.Pascal_VOC to jakarta.xml.bind;
}

