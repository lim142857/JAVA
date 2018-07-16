package Controller;

import Model.*;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The server scene controller that handles event inputs from the user in ServerScene.fxml and display the relevant
 * data to the user. The class observers the Employee class and captures any actions that modifies the data in the
 * model and update the current display.
 */

public class ServerController implements Initializable, Observer {

    static Server user; //the server user.
    static EmployeeManager employeeManager; //the EmployeeManager that stores all the Employees.
    private static Table currentTable; //the current table being selected.
    @FXML
    ListView<String> ingredientList; //The ListView that shows the list of ingredients to receive.

    @FXML
    TextField ingredientQuantity; //The TextField that asks are the actual quantity of a selected ingredient to receive.

    @FXML
    TextField numPeople; //The TextField to set the number of people in a selected table.

    @FXML
    ListView<String> ordersOnTable; //The ListView that shows the orders delivered on a selected table.

    @FXML
    Label currentSelectedTable; //The Label that shows the current selected table.

    @FXML
    Label billsLeft; //The Label that shows the bill to pay for a selected table;

    @FXML
    Label tipsAdded; //The Label that shows 15% tips being added when the table has 8 or more people.

    @FXML
    Label numOfPeople; //The label that shows the number of people in a selected table.

    @FXML
    Label stillOrderToDeliver;  //The label that shows there are still orders to deliver when the user tries to
    //add an order before finished delivering all orders.

    @FXML
    ListView<Order> orderPlaced; //The ListView that shows all orders that are placed but not seen by the Chef.

    @FXML
    ListView<Order> orderToDeliver; //The ListView that shows all orders that are ready to deliver.

    /**
     * The handler for the "Add Order" button. A new menu window will open only if all orders that are ready to
     * deliver are delivered.
     *
     * @throws IOException the exception when the fxml file is not found.
     */
    @FXML
    void addOrder() throws IOException {
        Stage stage = new Stage();
        AlertWindow window = new AlertWindow(stage);
        if (user.getOrderManager().getOrders_deliver().isEmpty()) {
            Pane menu = FXMLLoader.load(getClass().getResource("/GUI/Menu.fxml"));
            MenuController.primaryStage = stage;
            MenuController.user = user;
            CartController.user = user;
            CartController.orderManager = user.getOrderManager();
            window.display("Menu", new Scene(menu));
        } else {
            stillOrderToDeliver.setText("There are still orders\nto be deliver!");
        }
    }

    /**
     * The handler for the "Reject" button under the orders to deliver ListView. A new reject window will open for the
     * user to enter reason.
     *
     * @throws IOException the exception when the fxml file is not found.
     */
    @FXML
    void reject() throws IOException {
        Stage stage = new Stage();
        AlertWindow window = new AlertWindow(stage);
        Pane reject = FXMLLoader.load(getClass().getResource("/GUI/RejectScene.fxml"));
        RejectController.order = orderToDeliver.getSelectionModel().getSelectedItem();
        RejectController.user = user;
        window.display("Reject", new Scene(reject));
    }

    /**
     * Initialize the server scene. Display the orders and all other data. Let the controller observe all the Employees
     * that are read from the EmployeeManager.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayRequest();
        displayOrderPlaced();
        displayOrderToDeliver();
        for (Employee employee : employeeManager.getEmployeeHashMap().values()) {
            employee.register(this);
        }
    }

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
     * The handler for the "Table i" button in the Table View tab. The currentTable field is assigned and all orders,
     * bill and other corresponding information are displayed(updated).
     */
    @FXML
    public void specifyTable(Event event) {
        Button a = (Button) event.getSource();
        String tableName = a.getText();
        int tableNumber = Integer.parseInt(tableName.substring(6));
        currentTable = user.getTableManager().getTable(tableNumber);
        CartController.currentTable = currentTable;
        currentSelectedTable.setText("Currently Selected: " + tableName);
        numOfPeople.setText("Number of People: " + currentTable.getNumOfPeople());
        displayOrdersOnTable();
        displayBill();
        if (currentTable.overEight()) {
            tipsAdded.setText("15% Tips Added");
        } else {
            tipsAdded.setText("");
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
     * Display the (delivered)orders on the currentTable. The delivered orders are read from the currentTable, which
     * is a Table instance that stores all orders that are delivered to it.
     */
    private void displayOrdersOnTable() {
        ordersOnTable.getItems().clear();
        if (currentTable != null) {
            for (Order order : currentTable.getOrders()) {
                ordersOnTable.getItems().add(order.getName() + ",         Price: " + order.getPrice());
            }
        }
        ordersOnTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Display the orders that are placed but not seen by the chef. The placed orders are read from the OrderManager,
     * which stores a list of placed orders.
     */
    private void displayOrderPlaced() {
        orderPlaced.getItems().clear();
        for (Order order : user.getOrderManager().getOrders_cook()) {
            orderPlaced.getItems().add(order);
        }
    }

    /**
     * Display the orders that are ready to deliver. The orders are read from the OrderManager, which stores
     * a list of orders that are ready to deliver.
     */
    private void displayOrderToDeliver() {
        orderToDeliver.getItems().clear();
        for (Order order : user.getOrderManager().getOrders_deliver()) {
            orderToDeliver.getItems().add(order);
        }
    }

    /**
     * Display the bill for the selected table. The bill is read from currentTable, which is a Table instance that
     * have a method to calculate its bill.
     */
    private void displayBill() {
        if (currentTable != null) {
            double bill = currentTable.calculateBill();
            billsLeft.setText("Bills Left: " + bill);
        }
    }

    /**
     * Cancel an order that is placed but not seen by the chef. The selected order will be canceled using the
     * cancelOrder method in the Server class.
     */
    @FXML
    void cancelOrder() {
        ObservableList<Order> orders = orderPlaced.getSelectionModel().getSelectedItems();
        for (Order order : orders) {
            user.cancelOrder(order);
        }

    }

    /**
     * The handler for the "Pay for selected Orders"  button in the Table View tab. The selected orders are obtained
     * from the "Orders Delivered" ListView and then processed using the writeDetails method in the Server class,
     * which records the payment.
     *
     * @throws IOException the exception thrown by the writeDetail method.
     */
    @FXML
    void payOrders() throws IOException {
        ObservableList<String> orderNamesAndPrices = ordersOnTable.getSelectionModel().getSelectedItems();
        String[] orderNamesAndPricesArray = new String[orderNamesAndPrices.size()];
        orderNamesAndPrices.toArray(orderNamesAndPricesArray);
        for (String orderNameAndPrice : orderNamesAndPricesArray) {
            String[] splitNameAndPrice = orderNameAndPrice.split(",");
            user.writeDetails(splitNameAndPrice[0], currentTable.getTableNumber());
        }
    }

    /**
     * The handler for the "Deliver Order" button in the Order View tab. The order selected from the "Order To Deliver"
     * ListView is processed using the deliverOrder method in the Server class.
     */
    @FXML
    void deliverOrder() {
        ObservableList<Order> ordersToDeliver = orderToDeliver.getSelectionModel().getSelectedItems();
        for (Order order : ordersToDeliver) {
            user.deliverOrder(order.getName(), order.getTable_num());
        }
    }

    /**
     * The handler for the "Set" button in the Table View tab. The number of people is read from the numPeople TextField
     * and set it to the currentTable.
     */
    @FXML
    void setNumPeople() {
        if (!numPeople.getText().isEmpty()) {
            int numberOfPeople = Integer.parseInt(numPeople.getText());
            if (currentTable != null) {
                currentTable.setNumOfPeople(numberOfPeople);
                numOfPeople.setText("Number of People: " + currentTable.getNumOfPeople());
                displayBill();
            }
        }
    }

    /**
     * Update all the orders and other information when capturing a movement that modifies the Model data by an
     * Employee.
     */
    @Override
    public void update() {
        displayOrderPlaced();
        displayOrderToDeliver();
        displayRequest();
        displayOrdersOnTable();
        displayBill();
        stillOrderToDeliver.setText("");
    }
}






