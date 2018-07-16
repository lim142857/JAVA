package Model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;

public class OrderManager{
    private ArrayList<Order> orders ;
    // the list of Orders need to be cooked
    private ArrayList<Order> orders_cook = new ArrayList<>();

    // the list of Orders need to be delivered
    private ArrayList<Order> orders_deliver = new ArrayList<>();

    // the list of Orders rejected and stores in fridge
    private ArrayList<Order> refrigerator = new ArrayList<>();

    public OrderManager(){
      try {
        FileInputStream file = new FileInputStream("phase2/orders.ser");
        ObjectInputStream in = new ObjectInputStream(file);
        orders =  (ArrayList<Order>) in.readObject();

        in.close();
        file.close();

      } catch (ClassNotFoundException | IOException e) {
        orders = new ArrayList<>();
      }
    }

    public Order getOrder(String orderName){
      for (Order order: orders){
        if (order.getName().equals(orderName)) return order;
      }
      return null;
    }

    /**
     * get the list of Orders need to be cooked
     * @return list of Orders need to be cooked
     */
    public ArrayList<Order> getOrders_cook() {
        return orders_cook;
    }

    /**
     * get the list of Orders need to be delivered
     * @return list of Orders need to be delivered
     */
    public ArrayList<Order> getOrders_deliver() {
        return orders_deliver;
    }

    /**
     * get the list of Orders rejected and stores in firdge
     * @return list of Orders rejected and stores in firdge
     */
    public ArrayList<Order> getRefrigerator() {
        return refrigerator;
    }

    public void cancelOrder(Order order){
        orders_cook.remove(order);
    }

    /**
     * add Order to list of Orders need to be cooked
     * @param order the Order added to list of Orders need to be cooked
     */
    public void addOrder_refrigerator(Order order){
        refrigerator.add(order);
    }

    /**
     * add Order to list of Orders need to be delivered
     * @param order the Order added to list of Orders need to be delivered
     */
    public void addOrder_cook(Order order){
        orders_cook.add(order);
//        Phase2Log.getInstance().putLog(Level.FINE, order + " is placed.");
    }

    /**
     * add Order to list of Orders rejected and stores in firdge
     * @param order the Order added to firdge
     */
    public void addOrder_deliver(Order order){
        orders_deliver.add(order);
//        Phase2Log.getInstance().putLog(Level.FINE, order + " is ready to deliver.");
    }

//    private void writeFile(){
//        try {
//            FileOutputStream file = new FileOutputStream("phase1/orders.ser");
//            ObjectOutputStream out = new ObjectOutputStream(file);
//            out.writeObject(orders);
//
//            file.close();
//            out.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    void addItem(Order order){
//
//      for (Order order1: orders){
//        if (order1.getName().equals(order.getName())) hasOrder = false;
//      }
//      if (hasOrder) orders.add(order);
//
//      // Serialization
//      writeFile();
//    }

//
//    public void printItems(){
//      for(Order i: orders){
//        System.out.println(i.getName() + " " + i.getIngredients());
//      }
//    }
//
//    void getIngredients() throws IOException {
//      try (BufferedReader fileReader = new BufferedReader(new FileReader("phase1/Menu.txt"))) {
//        String line = fileReader.readLine();
//        int i = 0;
//        while (line != null) {
//          String[] line1 = line.split("\\s" +"\\|"+ "\\s");
//          item[i] = new Order(line1[0], 0);
//          this.addItem(item[i]);
//          line = fileReader.readLine();
//          i += 1;
//        }
//      }
//      writeFile();
//    }

}
