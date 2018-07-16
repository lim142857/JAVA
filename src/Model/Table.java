package Model;

import java.util.ArrayList;
import java.util.logging.Level;
import java.math.BigDecimal;
/**
 * The table where customs sit, stores the dishes ordered.
 */
public class Table {
    private ArrayList<Order> orders ; // the list of orders ordered by this table
    private int tableNumber;    // the table number of this table
    private int numOfPeople;

    /**
     * construct an table with the table number.
     * @param tableNum the table number of this table
     */

    public Table(int tableNum){
        this.tableNumber = tableNum;
        this.orders = new ArrayList<>();
    }

    /**
     * get the table number for this table.
     * @return table number for the table
     */
    public int getTableNumber() {
        return tableNumber;
    }

    public int getNumOfPeople() {
        return numOfPeople;
    }

    public void setNumOfPeople(int numOfPeople) {
        this.numOfPeople = numOfPeople;
    }

    public boolean overEight() {
        return numOfPeople >= 8;

    }

    public ArrayList<Order> getOrders() { return orders; }

    /**
     * add Order to the table
     * @param order the order added to the table
     */
    public void addOrder(Order order){ orders.add(order); }

    /**
     * calculate the total amount of the price on this table and print details
     * @return orderName and price of all the dishes and total price
     */
    public double calculateBill(){
        double totalPrice = 0;
        for (Order order : orders) {
            if (order.isFreeOrder()) {
                Phase2Log.getInstance().putLog(Level.FINE, order.getName() + "    " + "0.00");
            } else {
                totalPrice += order.getPrice();
                Phase2Log.getInstance().putLog(Level.FINE, order.getName() + "    " + order.getPrice()+".");
            }
        }
        if(overEight()){
            BigDecimal bd=new BigDecimal(totalPrice * 1.13 * 1.15);
            return bd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();

        }
        else{
            BigDecimal bd=new BigDecimal(totalPrice *1.13 );
            return bd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        }
    }


    /***
     * clean all the orders on the table.
     */
    public void clean() {
        orders.clear();
    }
}
