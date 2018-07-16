package Model;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * The Server class, which is a subclass of Employee. A server have access to the restaurant's OrderManager and
 * TableManager. A server is responsible for taking orders from the costumer, deliver prepared orders to tables
 *  and get bills from tables.
 */

public class Server extends Employee implements Serializable, Observable {
    private TableManager tableManager;
    private OrderManager orderManager;

    /**
     * The constructor for a server.
     *
     * @param name the name of the server.
     * @param inventory the inventory that stores the ingredients.
     * @param orderManager the orderManager that stores the collections of orders.
     */
    public Server(String name, Inventory2 inventory, TableManager tableManager, OrderManager orderManager) {
        super(name, inventory);
        this.tableManager = tableManager;
        this.orderManager = orderManager;
    }
    public boolean isValidOrder(Order order) throws IOException {
        HashMap<String, Integer> needIngredients = order.getIngredients();
        for(String name : needIngredients.keySet()){
            if(inventory.getQuantity(name) < needIngredients.get(name)){
                return false;
            }
        }
        return true;
    }

    /**
     * Given the order name and the table number, create the order and add to the order system for the chef to confirm.
     * If the order is valid, i.e., exists in the menu, the order will be created and add to the orderManager.
     * Otherwise a denial message will be printed.
     *
     * @param
     * @throws IOException the path for file Menu.txt is incorrect
     */
    public void createOrder(Order order, int table_num) throws IOException{
        if (isValidOrder(order)) {
            order.setTable_num(table_num);
            orderManager.addOrder_cook(order);
            Phase2Log.getInstance().putLog(Level.FINE, name + ": " +order.getName() + " has been placed");
            updateInventory(order);
            notifyObservers();
        } else {
            Phase2Log.getInstance().putLog(Level.INFO, "Sorry, we cannot order " + order.getName() + ".");
        }
    }

    public void cancelOrder(Order order){
        for (String ingredient: order.getIngredients().keySet()){
          inventory.addItems(ingredient, order.getIngredients().get(ingredient));
          Phase2Log.getInstance().putLog(Level.FINE, name + " has cancel the " + order.getName());
          Phase2Log.getInstance().putLog(Level.FINE, order.getIngredients().get(ingredient) + " units of " +ingredient + " has added back to inventory");
        }
        orderManager.cancelOrder(order);
        notifyObservers();
    }

    /**
     * Given the order name and the table number, deliver the order to the corresponding table. If there is such order
     * to deliver, i.e., the order is present in the list of prepared orders in the OrderManager system, the order
     * will be delivered to the corresponding table. otherwise, a denial message will be printed.
     *
     * @param order_name the name of the order to deliver.
     * @param table_num the table number of the order to deliver.
     */
    public void deliverOrder(String order_name, int table_num) {
        Table deliver_to = tableManager.getTable(table_num);
        Order order = findOrder(order_name, table_num, orderManager.getOrders_deliver());
        if (order != null) {
            deliver_to.addOrder(order);
            orderManager.getOrders_deliver().remove(order);
            notifyObservers();
            Phase2Log.getInstance().putLog(Level.FINE, name + ": " + order + " is delivered.");
        } else {
            Phase2Log.getInstance().putLog(Level.INFO, name + ": Sorry, no such order to deliver.");
        }
    }

    /**
     * if consumer reject the order with reason not taste good,
     * this order will not be charged.
     * @param order_name the name of reject order
     * @param table_num the reject table number
     */
    public void freeOrder(String order_name, int table_num) {
        Order reject_order = findOrder(order_name, table_num, orderManager.getOrders_deliver());
        if (reject_order != null) {
            orderManager.getOrders_deliver().remove(reject_order);
            Phase2Log.getInstance().putLog(Level.INFO, name + ": " + "We are sorry that this dish is not to your taste, " +
                    "and we are not going to charge you for this dish. Enjoy your meal.");
        } else {
            Phase2Log.getInstance().putLog(Level.INFO, name + ": Sorry, no such order to reject.");
        }
        notifyObservers();
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }

    public TableManager getTableManager() {
        return tableManager;
    }

    /** if consumer reject his order with "NoReason",
     * the order will be put into the refrigerator.
     * @param order_name reject order name
     * @param table_num reject table number
     */
    public void refrigerator(String order_name, int table_num) {
        Order reject_order = findOrder(order_name, table_num, orderManager.getOrders_deliver());
        if (reject_order != null) {
            orderManager.addOrder_refrigerator(reject_order);
            orderManager.getOrders_deliver().remove(reject_order);
            Phase2Log.getInstance().putLog(Level.INFO, name + ": " + order_name + " has been put into the refrigerator.");
        } else {
            Phase2Log.getInstance().putLog(Level.INFO, name + ": Sorry, no such order to reject.");
        }
        notifyObservers();
    }

    /** if consumer reject his order with "reheat",
     * the order will be reheated, then put back into the getOrders_deliver,
     * and waiting for delivery.
     * @param order_name reject order name
     * @param table_num reject table number
     */
    public void reheat(String order_name, int table_num) {
        Order reheat_order = findOrder(order_name, table_num, orderManager.getOrders_deliver());
        if (reheat_order != null) {
            orderManager.getOrders_deliver().remove(reheat_order);
            Phase2Log.getInstance().putLog(Level.FINE, name +": " + order_name + " for Table " + table_num + " has already been reheated.");
            orderManager.addOrder_deliver(reheat_order);
        } else {
            Phase2Log.getInstance().putLog(Level.INFO, name + ": Sorry, no such order to reject.");
        }
        notifyObservers();
    }

    /**
     * take the bill and write the bill detail into income.txt file
     * @param orderName the name of order need to be payed
     * @param tableNum the table number of order
     * @throws IOException wrong path
     */
    public void writeDetails(String orderName, int tableNum) throws IOException {
        Table tb = tableManager.getTable(tableNum);
        Order od = findOrder(orderName,tableNum,tb.getOrders());
        tb.getOrders().remove(od); //remove this order
        Calendar cd = Calendar.getInstance();
        // get Year, Month and Date
        Integer year = cd.get(Calendar.YEAR);
        Integer month = cd.get(Calendar.MONTH);
        Integer date = cd.get(Calendar.DATE);
        String newString = year.toString() + month.toString()+ date.toString() + " | " +orderName +": " + od.getPrice();
        updateIncome(newString);
        Phase2Log.getInstance().putLog(Level.FINE, name + ": table " + tableNum + "has take the bill for " + orderName);
        notifyObservers();
    }

    /**
     * write payment details to income.txt
     * @param details the payment detail
     * @throws IOException wrong path
     */
    void updateIncome(String details) throws IOException {
        File file = new File("phase2/income.txt" );
        if(file.exists()){
            FileWriter fw = new FileWriter(file,true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(details + System.lineSeparator());
            bw.close();
            fw.close();
        }
    }

    /**
     * use ingredient each time the order is created
     * @param order the order created
     * @throws IOException wrong path
     */
    private void updateInventory(Order order) throws IOException {
        HashMap<String, Integer> order_ingredients = order.getIngredients();
        for (String ingredientsNames: order_ingredients.keySet()){
            inventory.useItems(ingredientsNames, order_ingredients.get(ingredientsNames));
        }
        inventory.selfCheck();
        notifyObservers();
    }


}