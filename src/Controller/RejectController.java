package Controller;

import Model.Order;
import Model.Phase2Log;
import Model.Server;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * The reject scene controller that handles event inputs from the user in RejectScene.fxml and process the order being
 * rejected.
 */


public class RejectController implements Initializable {

    static Server user; //the user of this controller.

    static Order order; //the order being rejected.

    @FXML
    TextField otherReason; //TextField to enter other reasons.

    @FXML
    ChoiceBox<String> reasons; //ChoiceBox of reasons to reject.

    /**
     * The handler for the "Reject" button. The rejected order is processed by methods in the Server class that rejects
     * the order based on various reasons.
     */
    @FXML
    void rejectOrder() {
        if (order != null && reasons.getSelectionModel().getSelectedItem() != null){
            if (reasons.getSelectionModel().getSelectedItem().equals("Dish is Cold")) {
                user.reheat(order.getName(), order.getTable_num());
                Phase2Log.getInstance().putLog(Level.INFO, "The order is reject because dish is cold");
            } else if (reasons.getSelectionModel().getSelectedItem().equals("Food is not good and this dish will be free")) {
                user.freeOrder(order.getName(), order.getTable_num());
                Phase2Log.getInstance().putLog(Level.INFO, "The order is reject because food is not good");
            } else if (reasons.getSelectionModel().getSelectedItem().equals("others")) {
                user.refrigerator(order.getName(), order.getTable_num());
                Phase2Log.getInstance().putLog(Level.INFO, "The order is reject because " + otherReason.getText());
            }
        }
    }


    /**
     * Initialize the reject scene. Display the choice box of reason to reject the order.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reasons.getItems().add("Dish is Cold");
        reasons.getItems().add("Food is not good and this dish will be free");
        reasons.getItems().add("others");
    }
}
