package Controller;

import Model.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The Main class, where the program runs. It contains a primaryStage, which is the stage where the user is operating
 * on.
 */

public class Main extends Application {
    private static Stage primaryStage; //the stage that the user is operating on.

    /**
     * The getter for the primaryStage.
     *
     * @return the primaryStage of the class.
     */
    static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * The setter for the primaryStage.
     *
     * @param primaryStage the stage to set the primaryStage to.
     */
    private void setPrimaryStage(Stage primaryStage) {
        Main.primaryStage = primaryStage;
    }

    public static void main(String[] args) throws IOException {
        Inventory2 inventory = new Inventory2();
        TableManager tableManager = new TableManager();
        OrderManager orderManager = new OrderManager();
        EmployeeManager employeeManager = new EmployeeManager(orderManager, tableManager, inventory);
        employeeManager.readEmployeeFile();

        Manager zed = new Manager("Zed", inventory, employeeManager);
        employeeManager.addEmployee("Zed", zed);
        MainController.employeeManager = employeeManager;
        ServerController.employeeManager = employeeManager;
        ChefController.employeeManager = employeeManager;
        ManagerController.employeeManager = employeeManager;

        for (int i = 1; i < 10; i++) {
            Table table = new Table(i);
            tableManager.addTable(table);
        }

        launch(args);
    }

    /**
     * Record the primaryStage in the Class primaryStage field using the setter. load the Main scene and
     * show the window.
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        setPrimaryStage(primaryStage);
        Parent root = FXMLLoader.load(getClass().getResource("/GUI/MainScene.fxml"));
        primaryStage.setTitle("Restaurant GUI");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}