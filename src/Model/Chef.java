package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * The Chef class, which is a subclass of employee that have access to the orderManager. Each chef stores a list
 * of order toPrepare, which is a collection of Order that the chef have seen. A chef can confirm that a placed order
 * is seen, and can prepare a seen order given the corresponding event input.
 */

public class Chef extends Employee implements Serializable, Observable {
    private OrderManager orderManager;
    private ArrayList<Order> toPrepare = new ArrayList<>();


    /**
     * The constructor for a chef.
     *
     * @param name         the name of the chef.
     * @param inventory    the inventory that stores the ingredients.
     * @param orderManager the orderManager that stores the collections of orders.
     */
    public Chef(String name, Inventory2 inventory, OrderManager orderManager) {
        super(name, inventory);
        this.orderManager = orderManager;
    }

    /**
     * get the OrderManager of this chef
     * @return this chef's OrderManager
     */
    public OrderManager getOrderManager() {
        return orderManager;
    }

    /**
     * The chef confirms that he sees the order corresponding to the given order name and table number. If the order
     * exists, the chef confirms that he sees the order. Otherwise, the chef would print a message saying that the
     * order was actually not placed.
     *
     * @param order_name the name of the order to confirm.
     * @param table_num  the table number of the order to confirm.
     */
    public void seeOrder(String order_name, int table_num) {
        Order order = findOrder(order_name, table_num, orderManager.getOrders_cook());
        if (order != null) {
            orderManager.getOrders_cook().remove(order);
            toPrepare.add(order);
            notifyObservers();
            Phase2Log.getInstance().putLog(Level.FINE, name + ": " + order + " is seen.");
        } else {
            Phase2Log.getInstance().putLog(Level.FINE, name + ": Sorry, Order " + order_name + " (table " + table_num + ") was never placed.");
        }
    }

    /**
     * get the Orders need to be prepared
     * @return ArrayList of Orders need to be prepared
     */
    public ArrayList<Order> getToPrepare() {
        return toPrepare;
    }

    /**
     * The chef prepare the order corresponding to the given order name and table number. If the order
     * exists, the chef prepares that order and upgrade the inventory. Otherwise, the chef would print a
     * message saying that the order was not seen. Before cooking, the chef will check the refrigerator. If
     * an order with the same name and Ingredient is found, the cook will use this order rather than cooking.
     *
     * @param order_name the name of the order to prepare.
     * @param table_num  the table number of the order to prepare.
     */
    public void fillOrder(String order_name, int table_num){
        Order order = findOrder(order_name, table_num, toPrepare);
        ArrayList<Order> refrigerator = orderManager.getRefrigerator();
        boolean in_refrigerator = false;
        if (order != null) {
            for (Order reject_order : refrigerator) {
                if (reject_order.getName().equals(order_name) && reject_order.getIngredients().equals(order.getIngredients())) {
                    refrigerator.remove(reject_order);
                    reject_order.setTable_num(table_num);
                    Phase2Log.getInstance().putLog(Level.FINE, order_name + " is reheated, plz be nice to that consumer.");
                    orderManager.addOrder_deliver(reject_order);
                    toPrepare.remove(order);
                    in_refrigerator = true;
                    break;
                }
            }
            if (!in_refrigerator) {
                orderManager.addOrder_deliver(order);
                toPrepare.remove(order);
            }
            Phase2Log.getInstance().putLog(Level.FINE,  name + ": " + order_name +" has been cooked");
            notifyObservers();
        } else {
           Phase2Log.getInstance().putLog(Level.FINE, name + ": Sorry, I didn't see Order " + order_name + " (table " + table_num + ").");

        }
    }

    /**
     * the chef can cancel the order because some reasons
     * @param order the order need to be canceled.
     */
    public void cancelOrder(Order order) {
        for (String ingredient: order.getIngredients().keySet()){
            inventory.addItems(ingredient, order.getIngredients().get(ingredient));
            Phase2Log.getInstance().putLog(Level.FINE, order.getIngredients().get(ingredient) + " units of " +ingredient + " has added back to inventory");
        }
        orderManager.cancelOrder(order);
        notifyObservers();
    }
}