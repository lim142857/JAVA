package Controller;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The AlertWindow class. The new window popped when a new Stage. The AlertWindow must be closed before actions on any
 * other window.
 */

public class AlertWindow {
    private Stage stage; // the stage of the alert window.

    /**
     * The constructor of the AlertWindow.
     *
     * @param window the stage
     */
    AlertWindow(Stage window) {
        stage = window;
    }

    /**
     * Given a scene and a title, show the alert window.
     *
     * @param title the title of the alert window.
     * @param scene the scene to set the stage to.
     */
    void display(String title, Scene scene) {
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.showAndWait();
    }
}
