package Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * instantiate the order by setting its orderName and tableNumber.
 * sever can make addition and subtraction of ingredients when order is created.
 */

public class Order implements Serializable{
    private String name; //name of the order
    private int table_num; // table number of the order
    private HashMap<String, Integer> ingredients; //the needed ingredients and quantity for the order
    private double price; //the price for the order.
//    private boolean validOrder = false; // whether there is enough ingredients for this order
    private boolean freeOrder = false; // whether order is going to be free
    private static final int LIMIT = 5; //maximum quantity to add

    /**
     * constructor for Order, set order by entering the orderName and tableNumber, set the ingredients
     * needed for this order by searching the menu.txt file.
     * @param dishName the name of the order.
     * @param tableNumber the tableNumber make this order.
     * @throws IOException the Menu.txt file is not in the prorate format.
     */
    public Order(String dishName, int tableNumber) throws IOException{
        this.name = dishName;
        this.table_num = tableNumber;
        this.price = getPrice(this);
        this.ingredients = getIngredient(this);
    }

    public boolean isFreeOrder() {
        return freeOrder;
    }

    public int getTable_num() {
    return table_num;
  }

  /**
     * use the file reader to get the ingredients needed to cook from menu.txt.
     * @param order the order needed to search.
     * @return the hashMap of all ingredients needed to cook the dish.
     * @throws IOException the Menu.txt file is not in the prorate format.
     */
    private HashMap<String, Integer> getIngredient(Order order) throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new FileReader("phase2/Menu.txt"))) {
            HashMap<String, Integer> ingredients = new HashMap<>();
            String line = fileReader.readLine();

            while (line != null) {
                String[] line1 = line.split("\\s" +"\\|"+ "\\s");
                int ingredientNum = line1.length;
                if (order.getName().equals(line1[0])){
                    for (int i = 2; i <= ingredientNum - 1; i++){
                        String[] ingre = line1[i].split("," + "\\s");
                        ingredients.put(ingre[0], Integer.parseInt(ingre[1]));
                    }
                    break;
                }
                line = fileReader.readLine();
            }
            return ingredients;
        }
    }

    /**
     *
     * use the file reader to get the price of the dish from menu.txt.
     * @param order the order needed to search.
     * @return the price in double of the dish.
     * @throws IOException the Menu.txt file is not in the prorate format.
     */
    private double getPrice(Order order) throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new FileReader("phase2/Menu.txt"))) {
            double price = 0.0;
            String line = fileReader.readLine();

            while (line != null) {
                String[] line1 = line.split("\\s" +"\\|"+ "\\s");
                if (order.getName().equals(line1[0])){
                    price = Double.parseDouble(line1[1]);
                    break;
                }
                line = fileReader.readLine();
            }
            return price;
        }
    }

    /**
     * get the name of the Order.
     * @return  name of the order.
     */
    public String getName(){ return this.name; }

    /**
     * set the table number for this order.
     * @param newNumber the new table number of the order.
     */
    public void setTable_num(int newNumber){ this.table_num = newNumber; }

    /**
     * get the price of the order.
     * @return price of the order.
     */
    public double getPrice(){ return this.price; }

    /**
     * get the HashMap of the ingredients needed for this order.
     * @return HashMap of the ingredients needed for this order.
     */
    public HashMap<String, Integer> getIngredients() {
        return ingredients;
    }

    /**
     * make addition to the order.
     * @param ingredientName the name of the Ingredient needed to add.
     * @param ingredientNumber the quantity of the ingredients needed to add.
     */
    public void addIngredient(String ingredientName, int ingredientNumber){
        if (ingredients.containsKey(ingredientName))
          if (ingredientNumber < LIMIT){
            ingredients.put(ingredientName, ingredients.get(ingredientName) + ingredientNumber);
            Phase2Log.getInstance().putLog(Level.FINE, ingredientNumber + " units of " +ingredientName + " has been added to " + name);
        }
          else Phase2Log.getInstance().putLog(Level.INFO, "Sorry, too many " + ingredientName +" added for " + name);
        else Phase2Log.getInstance().putLog(Level.FINE, name + " can not add " + ingredientName);
    }

    /**
     * make subtraction to the order.
     * @param ingredientName the name of the Ingredient needed to subtract.
     * @param ingredientNumber the quantity of the ingredients needed to subtract.
     */
    public void subtractIngredient(String ingredientName, int ingredientNumber) {
        if (ingredients.containsKey(ingredientName)) {
            if (ingredients.get(ingredientName) >= ingredientNumber) {
                ingredients.put(ingredientName, ingredients.get(ingredientName) - ingredientNumber);
                Phase2Log.getInstance().putLog(Level.FINE, ingredientNumber + " units of " +ingredientName + " has been subtract from " + name);
            }
            else
                ingredients.put(ingredientName, 0);
            Phase2Log.getInstance().putLog(Level.FINE, "all " +ingredientName + " has been subtract from " + name);
        } else
        Phase2Log.getInstance().putLog(Level.INFO, name + " does not have " + ingredientName);
    }

    /**
     * check whether the input order name matches the table number.
     * @param orderName name of the order.
     * @param table_num table number of the order.
     * @return true if and only if the order name matches the table number.
     */
    public boolean equals(String orderName, int table_num){
        return this.name.equals(orderName) && this.table_num == table_num;
    }

    /**
     * The String representation of the Order
     * @return The String representation of the Order
     */
    @Override
    public String toString() {
        return "Order " + name + " (table " + table_num + ")";
    }
}