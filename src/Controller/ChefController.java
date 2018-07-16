package Controller;

import Model.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * The Chef scene controller that handles event inputs from the user in ChefScene.fxml and display the relevant
 * data to the user. The class observers the Employee class and captures any actions that modifies the data in the
 * model and update the current display.
 */

public class ChefController implements Initializable, Observer {

    static Chef user; // the Chef user of the controller.
    static EmployeeManager employeeManager; // the EmployeeManager.
    @FXML
    ListView<Order> orders_cook; //the ListView of orders that are placed but not seen.
    @FXML
    ListView<Order> orderSeen; //the ListView of orders that are seen bt this chef(user).
    @FXML
    ListView<String> ingredientList; //the ListView of ingredients to receive.
    @FXML
    TextField ingredientQuantity; //the TextField to enter the amount of a selected ingredient to receive.

    /**
     * The handler for the "Receive" button in the Receive tab. Read the ingredient being selected in the List View,
     * the quantity entered in the text field and call the Receive method in the Employee class to do the receive.
     */
    @FXML
    void receiveIngredient() {
        ObservableList<String> ingredients = ingredientList.getSelectionModel().getSelectedItems();
        String receiveDetail = ingredients.get(0);
        if (receiveDetail != null) {
            String receiveDetails[] = receiveDetail.split(" ");
            String ingredientName = receiveDetails[0];
            int receiveAmount = Integer.parseInt(ingredientQuantity.getText());
            user.receive(ingredientName, receiveAmount);
        }
    }

    /**
     * Display the requests on the ListView in the Receive tab. The requests are read from the inventory, in which
     * all the requests are stored.
     */
    @FXML
    private void displayRequest() {
        ingredientList.getItems().clear();
        user.getInventory().readRequest();
        for (String requestIngredient : user.getInventory().getRequestIngredients().keySet()) {
            ingredientList.getItems().add(requestIngredient + "  " + user.getInventory().getRequestIngredients().get(requestIngredient));
        }
    }

    /**
     * The handler for the "Confirm Order" button in the Order View tab. The order selected in the List View get
     * confirmed by using the seeOrder method in the Cef class. Also, only the order at the top of the List View can
     * be confirmed so that the Chef don't "forget" the order that was placed first.
     */
    @FXML
    protected void confirmOrder() {
        Order dish = orders_cook.getSelectionModel().getSelectedItems().get(0);
        if (dish != null) {
            if (dish == orders_cook.getItems().get(0)) {
                user.seeOrder(dish.getName(), dish.getTable_num());
            } else Phase2Log.getInstance().putLog(Level.INFO, "You have forget the previous order");
        }
    }

    /**
     * The handler for the "Finish" button in the Order View tab. The order selected in the List View get
     * finished by using the fillOrder method in the Chef class.
     */
    @FXML
    protected void finishOrder() {
        Order dish = orderSeen.getSelectionModel().getSelectedItems().get(0);
        if (dish != null) {
            user.fillOrder(dish.getName(), dish.getTable_num());
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
     * Cancel an order that is placed but not seen by the chef. The selected order will be canceled using the
     * cancelOrder method in the Chef class.
     */
    @FXML
    protected void cancelOrder() {
        Order dish = orders_cook.getSelectionModel().getSelectedItems().get(0);
        if (dish != null) {
            user.cancelOrder(dish);
        }
    }

    /**
     * Display the orders that are placed but not seen by the (any)chef. The placed orders are read from the
     * OrderManager, which stores a list of placed orders.
     */
    @FXML
    private void displayOrders_cook() {
        orders_cook.getItems().clear();
        for (Order order : user.getOrderManager().getOrders_cook()) {
            orders_cook.getItems().add(order);
        }
    }

    /**
     * Display the orders that seen by the chef(user). The seen orders are read from the user, which is a Chef instance
     * that stores a list of orders he have seen.
     */
    @FXML
    private void displayOrders_seen() {
        orderSeen.getItems().clear();
        for (Order order : user.getToPrepare()) {
            orderSeen.getItems().add(order);
        }
        orderSeen.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Initialize the Chef scene. Display the orders and all other data. Let the controller observe all the Employees
     * that are read from the EmployeeManager.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayOrders_cook();
        displayOrders_seen();
        displayRequest();
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
        displayOrders_cook();
        displayOrders_seen();
    }
}
