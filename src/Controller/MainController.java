package Controller;

import Model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * The main scene controller that handles event inputs from the user in MainScene.fxml. The controller switches the
 * scene to either the server scene, the chef scene or the manager scene based on the username input.
 */

public class MainController {

    static EmployeeManager employeeManager; // the EmployeeManager.
    @FXML

    Label logInStatus; // The login status label.
    @FXML
    private TextField userName; // the username that the user typed.

    /**
     * The "Log In" button handler, switch the scene to the Server, Chef or the Manager scene based on the user's
     * input in the TextField. If the input is a valid employee name, the corresponding Employee instance will be found
     * from the EmployeeManager. The user of the next scene will be set by using this Employee instance.
     *
     * @throws Exception the exception when the fxml file is not found.
     */
    @FXML
    void logIn() throws Exception {
        String name = userName.getText();
//        Employee employee = (Employee) employeeManager.getEmployeeHashMap().values().toArray()[0];
        Stage primaryStage = Main.getPrimaryStage();
        if (!employeeManager.getEmployeeHashMap().keySet().contains(name)) {
            logInStatus.setText("Try again");
        } else {
            Employee user = employeeManager.getEmployeeHashMap().get(name);

            if (user instanceof Server) {
                ServerController.user = (Server) user;
                TabPane serverScene = FXMLLoader.load(getClass().getResource("/GUI/ServerScene.fxml"));
                primaryStage.setScene(new Scene(serverScene));
            } else if (user instanceof Chef) {
                ChefController.user = (Chef) user;
                TabPane serverScene = FXMLLoader.load(getClass().getResource("/GUI/ChefScene.fxml"));
                primaryStage.setScene(new Scene(serverScene));
            } else if (user instanceof Manager) {
                ManagerController.user = (Manager) user;
                ManagerController.user.employeeManager = employeeManager;
                TabPane serverScene = FXMLLoader.load(getClass().getResource("/GUI/ManagerScene.fxml"));
                primaryStage.setScene(new Scene(serverScene));
            }
        }
    }

}
