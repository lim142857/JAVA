package Model;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * One kind of Employee such that he/she can
 * check inventory, change and send Request.txt
 * who also can receive ingredients like employee.
 */


public class Manager extends Employee{

  public EmployeeManager employeeManager;
  private HashMap<String, ArrayList<String>> payments;

  /**
   * Constructor a Manger class as a kind of Employee
   *  which has his/her own name and connects the same inventory.
   * @param name the name of the manager
   * @param inventory the inventory which the manager
   */
  public Manager(String name, Inventory2 inventory, EmployeeManager employeeManager1){
    super(name,inventory);
    employeeManager = employeeManager1;
    payments = new HashMap<>();
  }

  /**
   * Set the quantity for the specified Ingredient name
   * in Request.txt if the name is in Request.txt file and Request.txt is called.
   * @param name the name of ingredients I want to set quantity in Request.txt
   * @param quantity the quantity of ingredients I want to set
   * @throws IOException the path for file Request.txt is incorrect
   */
// loop and modify requestIngredients, then rewrite the Request.txt
  public void setRequest(String name, Integer quantity) throws IOException {
    if(inventory.getRequestIngredients().containsKey(name)){
      for(String requestName: inventory.getRequestIngredients().keySet()){
        if(requestName.equals(name)){
          inventory.getRequestIngredients().put(name,quantity);
//          setName.add(name);
        }
      }
      super.inventory.writeRequest();
      Phase2Log.getInstance().putLog(Level.FINE, "Manager sets " + name +" for " + quantity + " units.");
    }
    else if(super.inventory.contains(name)){
      Phase2Log.getInstance().putLog(Level.FINE, "This Ingredient does not call Request.txt.");
    }
    else {
      Phase2Log.getInstance().putLog(Level.FINE, "This name of Ingredient is wrong.");
    }
    notifyObservers();
  }

  /**
   * the manager can hire new employee
   * @param name the new employee's name
   * @param position the new employee's position
   */
  public void hire(String name, String position){
    try {
      employeeManager.addEmployee(name, position);
    } catch (IOException e) {
      e.printStackTrace();
    }
    notifyObservers();
  }

  /**
   * the manager can fire a employee
   * @param name the name of the employee need to be fired
   */
  public void fire(String name){
    try {
      employeeManager.remove(name);
    } catch (IOException e) {
      e.printStackTrace();
    }
    notifyObservers();
  }

  /**
   * get manager's employee's manager
   * @return manager's employee's manager
   */
  public EmployeeManager getEmployeeManager() {
    try {
      employeeManager.readEmployeeFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return employeeManager;
  }

  /**
   * store all the income to the HashMap
   * @throws IOException wrong path
   */
  public void getIncomes() throws IOException{
    payments.clear();
    try (BufferedReader fileReader = new BufferedReader(new FileReader("phase2/income.txt"))) {

      String line = fileReader.readLine();

      while (line != null) {
        String[] incomeDetail = line.split("\\s" + "\\|" +"\\s");
        String date = incomeDetail[0];
        String detail = incomeDetail[1];
        if (validDate(date))
          payments.get(date).add(detail);
        else {
          ArrayList<String> payOnDay = new ArrayList<>();
          payOnDay.add(detail);
          payments.put(date, payOnDay);
        }

        line = fileReader.readLine();

      }

    }
  }

  /**
   * get all payments on the given day
   * @param date the day need to be checked
   * @return the ArrayList of payments' information
   */
  public ArrayList<String> getPayments(String date) {
//    ArrayList<String> pays= new ArrayList<>();
    for (String day: payments.keySet()){
      if (day.equals(date)) return payments.get(date);
    }
    return null;
  }

  /**
   * get manager's inventory
   * @return manager's inventory
   */
  public Inventory2 getInventory() {
    return super.inventory;
  }

  /**
   * check whether this date has any income
   * @param date the given date
   * @return true iff there is at least on payment
   */
  public boolean validDate(String date){
    return  (payments.keySet().contains(date));
  }

  /**
   * get the total income on the given day
   * @param date the date need to be checked
   * @return the total income
   */
  public double getTotal(String date){
    double total = 0;
    if (payments.keySet().contains(date)) {
      ArrayList<String> pays = payments.get(date);
      for (String eachPayment: pays){
        String[] detail = eachPayment.split(": ");
        String price = detail[1];
        total += Double.parseDouble(price);
      }
    }

    BigDecimal bd = new BigDecimal(total);

    return bd.setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
  }

}
