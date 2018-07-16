package Model;

import java.io.*;
import java.util.ArrayList;
/**
 * The Employee class. An employee have an unique name, and have access to the restaurant's inventory. Any employee,
 * regardless of being a server, a cook or a manager can take over the receiving job. An employee can find an order
 * from a collection of orders by its name and table number.
 */

public class Employee implements Serializable, Observable{
  private ArrayList<Observer> observers = new ArrayList<>();
  String name;
  Inventory2 inventory;

  /**
   * The constructor for an employee.
   *
   * @param name the name of the chef.
   * @param inventory the inventory that stores the ingredients.
   */
  Employee(String name, Inventory2 inventory) {
    this.name = name;
    this.inventory = inventory;
  }

  /**
   * Get the inventory for all Employee.
   * @return the Inventory
   */
  public Inventory2 getInventory() {
    return inventory;
  }

  /**
   *Receive a list of ingredients delivered based on the Request.txt that are currently present in the Request.txt file.
   * Update the ingredients in the inventory for each item received. Clear the all the requests in the file when
   * finished receiving.
   * @param receiveName
   * @param receiveQuantity
   */
  public void receive(String receiveName, int receiveQuantity){
    for (String ingredientName: inventory.getRequestIngredients().keySet()){
      int requestQuantity = inventory.getRequestIngredients().get(ingredientName);
      if (ingredientName.equals(receiveName)){
          inventory.getRequestIngredients().put(ingredientName, requestQuantity - receiveQuantity);
        inventory.addItems(ingredientName, receiveQuantity);
      }
    }
    try {
      inventory.writeRequest();
    } catch (IOException e) {
      e.printStackTrace();
    }
    notifyObservers();
  }

  /**
   * Find an return order from a collection of orders based on its name and table number.
   * @param order_name the name of the order to find.
   * @param table_num the table number of the order need to be finded
   * @param order_list the collection of orders to find from.
   * @return the Order that need to be finded
   */
  Order findOrder(String order_name, int table_num, ArrayList<Order> order_list) {
    for (Order order: order_list) {
      if (order.equals(order_name, table_num)) {
        return order;
      }
    }
    return null;
  }

  @Override
  public void register(Observer observer) {
    observers.add(observer);
  }

  @Override
  public void notifyObservers() {
    for (Observer observer : observers) {
      observer.update();
    }
  }
}
