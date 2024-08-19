package image.labeler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {
    private static Scene scene; // main scene

    public static void main(String[] args) {
        launch();
    }

    @SuppressWarnings("exports")
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("window.fxml")); // load the FXML file
        Parent root = fxmlLoader.load(); // load the FXML file
        scene = new Scene(root, 1920, 1080);  // create the scene
        stage.setScene(scene); // set the scene

        Controller controller = fxmlLoader.getController(); // get the controller
        controller.setStage(stage); // set the stage

        stage.show(); // show the stage
    }

    /* private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    } */

    // // function to switch to a secondary window
    // static void setRoot(String fxml) throws IOException {
    //     scene.setRoot(loadFXML(fxml));
    // }
}
    