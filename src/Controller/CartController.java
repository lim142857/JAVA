package Controller;

import Model.Order;
import Model.OrderManager;
import Model.Server;
import Model.Table;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.image.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

/** CartController, which is the controller of Cart.fxml
 * Server can see the scene of cart if he press the button of cart in menu or order scene.
 */

public class CartController implements Initializable {

    @FXML private ListView<String> cartView;
    @FXML ImageView thankYou;
    static OrderManager orderManager;
    static HashMap<String, ArrayList> order = new HashMap<>();
    static Table currentTable;
    static Server user;

    /** if server press Menu button,
     *  the scene will change to menu scene.
     * @throws IOException the path for Menu.fxml is incorrect.
     */
    @FXML
    void toMenu() throws IOException {
        thankYou.setVisible(false);
        Parent MenuScene = FXMLLoader.load(getClass().getResource("/GUI/Menu.fxml"));
        MenuController.primaryStage.setScene(new Scene(MenuScene));
    }

    /** show the orders that consumer orders in the listview
     *  it includes "Name", the order's name
     *  "Add" the add ingredients and
     *  "Subtract" the subtract ingredients
     */
    @FXML
    void showCart() {
        for (Object name : order.keySet()) {
            int count = 0;
            while (count < order.get(name).size()) {
                cartView.getItems().add("Name: " + name + "\n" + toString((HashMap) order.get(name).get(count)));
                count += 1;
            }
        }
    }

    /** if server press the Remove button, the selected order will be removed
     *  and disappeared in the list view.
     */

    @FXML
    void removeOrder() {
        thankYou.setVisible(false);
        ObservableList<Integer> remove = cartView.getSelectionModel().getSelectedIndices();
        if (!remove.isEmpty()) {
            int remove_index = remove.get(0);
            int count = 0;
            Object key = new Object();
            for (Object k : order.keySet()) {
                count += order.get(k).size();
                if (!(count <= remove_index)) {
                    key = k;
                    break;
                }
            }
            count -= remove_index;
            if (order.get(key).size() == 1) {
                order.remove(key);
            } else {
                order.get(key).remove((order.get(key).size() - count));
            }
            cartView.getItems().clear();
            showCart();
        }
    }

    /** if the server press the Submit button,
     *  the order will be created. The chef will get ready to cook them.
     * @throws IOException
     */
    @FXML
    void createOrder() throws IOException {
        thankYou.setVisible(true);
        for (Object key : order.keySet()) {
            if (currentTable != null) {
                Order currentOrder = user.getOrderManager().getOrder((String) key);
                int count = 0;
                while (count < order.get(key).size()) {
                    Object ingredient = order.get(key).get(count);
                    if (currentOrder != null && ((HashMap) ingredient).containsKey("Add")) {
                        for (Object add : ((ArrayList) ((HashMap) ingredient).get("Add"))) {
                            currentOrder.addIngredient(((String) add), 1);
                        }
                    }
                    if (currentOrder != null && ((HashMap) ingredient).containsKey("Subtract")) {
                        for (Object sub : ((ArrayList) ((HashMap) ingredient).get("Subtract"))) {
                            currentOrder.subtractIngredient(((String) sub), 1);
                        }
                    }
                    count += 1;
                    user.createOrder(currentOrder, currentTable.getTableNumber());
                }
            }
        }
        order.clear();
        cartView.getItems().clear();
    }

    /** make string of the added ingredients and subtracted ingredients.
     * @param order Hashmap order, which included added ingredients and subtracted ingredients.
     * @return string, ie. Add: + ingredients + \n + Subtract + ingredients
     */
    private String toString(HashMap order) {
        String result = "";
        if (order.containsKey("Add")) {
            result += "Add: " + order.get("Add") + "\n";
        }
        if (order.containsKey("Subtract")) {
            result += "Subtract: " + order.get("Subtract");
        }
        return result;

    }

    /** the initialization, it will showCart() initialized.
     * @param location The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showCart();
    }
}
