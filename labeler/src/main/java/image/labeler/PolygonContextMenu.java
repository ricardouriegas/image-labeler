package image.labeler;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PolygonContextMenu {

    private String polygonName;
    private boolean isDeleted;

    public String getPolygonName() {
        return polygonName;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void display() {
        Platform.runLater(() -> {
            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Polygon Options");
            window.setMinWidth(300);

            Label label = new Label();
            label.setText("Enter the name for the polygon:");

            TextField nameInput = new TextField();
            nameInput.setPromptText("Polygon Name");

            Button addButton = new Button("Add Name");
            addButton.setOnAction(e -> {
                polygonName = nameInput.getText();
                isDeleted = false;
                window.close();
            });

            Button deleteButton = new Button("Delete Polygon");
            deleteButton.setOnAction(e -> {
                polygonName = null;
                isDeleted = true;
                window.close();
            });

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(20, 20, 20, 20));
            layout.getChildren().addAll(label, nameInput, addButton, deleteButton);

            Scene scene = new Scene(layout);
            window.setScene(scene);
            window.showAndWait();
        });
    }

    public boolean isShowing() {
        // Esta es una implementación simple y podría ser diferente según el uso
        return false;
    }
}
