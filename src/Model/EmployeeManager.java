package Model;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class EmployeeManager {
  private HashMap<String, Employee> employeeHashMap;
  private Server[] servers;
  private Chef[] chefs;
  private Inventory2 inventory;
  private OrderManager orderManager;
  private TableManager tableManager;
  private int i = 0, j = 0;

  /**
   * The EmployeeManager Class, stores all Employees' name, position. Allowed to create new employee and
   * remove any employee(server or chef)
   * @param orderManager the orderManager of all employees use
   * @param tableManager the tableManager of all employees use
   * @param inventory the inventory of all employees use
   */
  public EmployeeManager(OrderManager orderManager, TableManager tableManager, Inventory2 inventory){
    servers = new Server[20000];
    chefs = new Chef[20000];
    employeeHashMap = new HashMap<>();
    this.orderManager = orderManager;
    this.inventory = inventory;
    this.tableManager = tableManager;

  }

  /**
   * get the employee HashMap
   * @return the HashMap of of employeeManager
   */
  public HashMap<String, Employee> getEmployeeHashMap() {
    return employeeHashMap;
  }

  /**
   * read employee.txt file and create new server or chef depends on the the given information in txt.
   * @throws IOException the path or format of employee.txt is wrong
   */
  public void readEmployeeFile() throws IOException {
      try (BufferedReader fileReader = new BufferedReader(new FileReader("phase2/employees.txt"))) {
        String line = fileReader.readLine();
        while (line != null) {
          String[] employeeDetail = line.split("\\s");
          String employeeName = employeeDetail[0];
          String employeePosition = employeeDetail[1];

          if (employeePosition.equals("Server")){
              servers[i] = new Server(employeeName, inventory, tableManager, orderManager);
              employeeHashMap.put(employeeName, servers[i]);
              i++;
          }
            if (employeePosition.equals("Chef")){
              chefs[j] = new Chef(employeeName, inventory, orderManager);
              employeeHashMap.put(employeeName, chefs[j]);
              j++;
          }
          line = fileReader.readLine();

        }

      }
    }

  /**
   * add new employee(server or chef) to employee's HashMap and write the information to employee.txt
   * @param employeeName the new employee's name
   * @param employeePosition the new employee's position
   * @throws IOException the path of file writer is wrong
   */
    public void addEmployee(String employeeName, String employeePosition) throws IOException{
      readEmployeeFile();
      if (employeePosition.equals("Server")){
        servers[i] = new Server(employeeName, inventory, tableManager, orderManager);
        employeeHashMap.put(employeeName, servers[i]);
        i++;
      }
      if (employeePosition.equals("Chef")){
        chefs[j] = new Chef(employeeName, inventory, orderManager);
        employeeHashMap.put(employeeName, chefs[j]);
        j++;
      }
      writeEmployeeFile();
    }

  /**
   * add new manager to employee's HashMap and write the information to employee.txt
   * @param name the name of new manager
   * @param manager the Manager need to be added.
   */
    public void addEmployee(String name, Manager manager){
    employeeHashMap.put(name,manager);
    }

  /**
   * remove the Employee(server or chef) by given this employee's name
   * @param employeeName the name of employee that need to be removed
   * @throws IOException wrong path to emplyee.txt
   */
    void remove(String employeeName) throws IOException {
      readEmployeeFile();
      employeeHashMap.remove(employeeName);
      writeEmployeeFile();
    }

  /**
   * write the Employees' HashMap to emplyee.txt
   * @throws IOException wrong path to employee.txt
   */
  private void writeEmployeeFile() throws IOException {
      File file = new File("phase2/employees.txt" );
      if(file.exists()){
        FileWriter fw = new FileWriter(file,false);
        BufferedWriter bw = new BufferedWriter(fw);
        for(String employeeName: employeeHashMap.keySet()){
          if (employeeHashMap.get(employeeName) instanceof Server) bw.write( employeeName + " " + "Server"+ System.lineSeparator());
          else if (employeeHashMap.get(employeeName) instanceof Chef) bw.write( employeeName + " " + "Chef"+ System.lineSeparator());
        }
        bw.close();
        fw.close();
    }
  }

}