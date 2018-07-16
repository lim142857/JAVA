package Controller;

import Model.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The manager scene controller that handles event inputs from the user in ManagerScene.fxml and display the relevant
 * data to the user. The class observers the Employee class and captures any actions that modifies the data in the
 * model and update the current display.
 */

public class ManagerController implements Initializable, Observer {

    static Manager user; //the Manager user of the controller.

    static EmployeeManager employeeManager; //the EmployeeManager.

    @FXML
    ListView<String> Ingredients; // the ListView of ingredients in inventory.
    @FXML
    ListView<String> Request; // the ListView of Requests.
    @FXML
    ListView<String> Records; // the ListView of Records(income).
    @FXML
    ListView<String> EmployeeNames; // the ListView of Employees' names.
    @FXML
    ListView<String> requestList; // The ListView of ingredients based on the requests to receive.
    @FXML
    TextField dates; // the TextField to enter the date to get the record.
    @FXML
    ChoiceBox<String> newEmployeePosition; // the ChoiceBox of choosing a role to hire.
    @FXML
    TextField ingredientQuantity; // the TextField to enter the receiving quantity to for a selected ingredient.
    @FXML
    TextField setQuantity; // the TextField to change the quantity of ingredient in the request.
    @FXML
    TextField newEmployeeName; // the TextField to enter a new Employee's name.
    @FXML
    Label totalIncome; // the Label to display the total income for a given date.

    /**
     * The handler for the "Receive" button in the Receive tab. Read the ingredient being selected in the List View,
     * the quantity entered in the text field and call the Receive method in the Employee class to do the receive.
     */
    @FXML
    void receiveIngredient() {
        ObservableList<String> ingredients = requestList.getSelectionModel().getSelectedItems();
        String receiveDetail = ingredients.get(0);
        if (receiveDetail != null) {
            String receiveDetails[] = receiveDetail.split(" ");
            String ingredientName = receiveDetails[0];
            int receiveAmount = Integer.parseInt(ingredientQuantity.getText());
            user.receive(ingredientName, receiveAmount);
        }
    }

    /**
     * The handler for the "Fire" button in the Employee tab. The selected Employee gets fired by using the fire
     * method in Manager class.
     */
    @FXML
    void fire() {
        String name = EmployeeNames.getSelectionModel().getSelectedItem();
        if (name != null)
            user.fire(name);
    }

    /**
     * The handler for the "Hire" button in the Employee tab. The new employee's name is read from the TextField
     * newEmployeeName and the role is read from the newEmployeePosition ChoiceBox. The new Employee is hired using
     * the hire method in the Manager class.
     */
    @FXML
    void hire() {
        String name = newEmployeeName.getText();
        String position = newEmployeePosition.getValue();
        if (name != null && position != null)  user.hire(name, position);
    }

    /**
     * Display the ingredients and quantity of the inventory. The ingredients' name and quantity are read from the
     * inventory, which stores this information.
     */
    private void displayIngredients() {
        Ingredients.getItems().clear();
        for (Ingredient ingredient : user.getInventory().getItems()) {
            Ingredients.getItems().add(ingredient.getIngredientName() + "  " + ingredient.getQuantity());
        }
    }

    /**
     * Display the requests on the ListView in the Receive tab. The requests are read from the inventory, in which
     * all the requests are stored.
     */
    private void displayRequest() {
        Request.getItems().clear();
        requestList.getItems().clear();
        user.getInventory().readRequest();
        for (String requestIngredient : user.getInventory().getRequestIngredients().keySet()) {
            Request.getItems().add(requestIngredient + "  " + user.getInventory().getRequestIngredients().get(requestIngredient));
            requestList.getItems().add(requestIngredient + "  " + user.getInventory().getRequestIngredients().get(requestIngredient));
        }
    }

    /**
     * Display the Employees on the ListView in the Employee tab. The employees' information are read from the
     * EmployeeManager.
     */
    private void displayEmployees() {
        EmployeeNames.getItems().clear();
        for (String name : user.getEmployeeManager().getEmployeeHashMap().keySet()) {
            if (!name.equals("Zed"))
                EmployeeNames.getItems().add(name);
        }
    }

    /**
     * the handler for the "Set Request" button in the Inventory&Request tab. The selected request is modified by using
     * the setRequest method in the Manager class.
     */
    @FXML
    void setRequest() {
        String ingredients = Request.getSelectionModel().getSelectedItem();
        if (ingredients != null) {
            String[] ingredients1 = ingredients.split("\\s");
            String ingredientName = ingredients1[0];
            int quantity = Integer.parseInt(setQuantity.getText());
            try {
                user.setRequest(ingredientName, quantity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The handler for the "Log Off" button. The scene will be switched to the main scene.
     *
     * @throws IOException the exception when the fxml file is not found.
     */
    @FXML
    protected void logOff() throws IOException {
        Parent MenuScene = FXMLLoader.load(getClass().getResource("/GUI/MainScene.fxml"));
        Main.getPrimaryStage().setScene(new Scene(MenuScene));
    }

    /**
     * The handler for the "Check" button in the Records tab. The date will be read from the dates TextField and the
     * records of the day will be read using the getIncomes method in the manager class and be displayed.
     *
     * @throws IOException the exception raised by getIncomes method.
     */
    @FXML
    protected void check() throws IOException {
        Records.getItems().clear();
        String date = dates.getText();
        user.getIncomes();
        if (user.validDate(date)) {
            for (String payment : user.getPayments(date)) {
                Records.getItems().add(payment);
            }
            double total = user.getTotal(date);
            totalIncome.setText(String.valueOf(total));
        }
    }

    /**
     * Initialize the Manager scene. Display the orders and all other data. Let the controller observe all the Employees
     * that are read from the EmployeeManager.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayIngredients();
        displayRequest();
        displayEmployees();
        newEmployeePosition.getItems().add("Server");
        newEmployeePosition.getItems().add("Chef");
        for (Employee employee : employeeManager.getEmployeeHashMap().values()) {
            employee.register(this);
        }
    }

    /**
     * Update all the orders and other information when capturing a movement that modifies the Model data by an
     * Employee.
     */
    @Override
    public void update() {
        displayRequest();
        displayEmployees();
        displayIngredients();
    }

}
