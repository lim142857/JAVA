package Model;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;


public class Inventory2 {

  private ArrayList<Ingredient> items;
  private HashMap<String, Integer> requestIngredients = new HashMap<>(); // The ingredients requested for adding.
  /**
   * the Class of Inventory, stores all the ingredient and can check or set each ingredient's quantity.
   */
  public Inventory2(){
    try {
      FileInputStream file = new FileInputStream("phase2/items.ser");
      ObjectInputStream en = new ObjectInputStream(file);

      items =  (ArrayList<Ingredient>) en.readObject();

      en.close();
      file.close();

    } catch (ClassNotFoundException | IOException e) {
      items = new ArrayList<>();
    }
  }

  /**
   * get the quantity of the given ingredient name
   * @param ingredientName the ingredient name
   * @return the quantity of this ingredient
   */
  public int getQuantity(String ingredientName) {
    for (Ingredient ingredient: items){
      if (ingredient.getIngredientName().equals(ingredientName)) return ingredient.getQuantity();
    }
    return 0;
  }

  /**
   * get the ArrayList of all Ingredients
   * @return the ArrayList of all Ingredients
   */
  public ArrayList<Ingredient> getItems() {
    return items;
  }

  /**
   * get the HashMap of the ingredient's name and quantity that need to be shown on th request.txt
   * @return the HashMap of the ingredient's name and quantity that need to be shown on th request.txt
   */
  public HashMap<String, Integer> getRequestIngredients() {
    return requestIngredients;
  }

  /**
   * check whether the given ingredient name is valid
   * @param ingredientName the name of given ingredient
   * @return true iff ingredient is valid
   */
   boolean contains(String ingredientName){
    boolean contain = false;
    for (Ingredient ingredient: items){
      if (ingredient.getIngredientName().equals(ingredientName)) contain = true;
    }
    return contain;
  }

  /**
   * write the current ArrayList of Ingredient to items.ser by using serialization
   */
  private void writeFile(){
    try {
      FileOutputStream file = new FileOutputStream("phase2/items.ser");
      ObjectOutputStream out = new ObjectOutputStream(file);
      out.writeObject(items);

      file.close();
      out.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * add ingredient to the inventory
   * @param ingredient the name of the ingredient added
   * @param quantity the quantity of the ingredient added
   */
  void addItems(String ingredient, int quantity){
    for (Ingredient ingredient1: items){
      if (ingredient1.getIngredientName().equals(ingredient)) ingredient1.setQuantity(ingredient1.getQuantity() + quantity);
    }
    writeFile();
  }

  /**
   * remove ingredient to the inventory
   * @param ingredient the name of the ingredient removed
   * @param quantity the quantity of the ingredient removed
   * @throws IOException the path is wrong
   */
  void useItems(String ingredient, int quantity) throws IOException {
    for (Ingredient ingredient1: items){
      if (ingredient1.getIngredientName().equals(ingredient)) {
        ingredient1.setQuantity(ingredient1.getQuantity() - quantity);
        Phase2Log.getInstance().putLog(Level.FINE, ingredient + " has used " + quantity + " units.");
      }
    }
    writeFile();
    selfCheck();
  }

  /**
   * check whether there is any ingredient's quantity is less than 10, if it is then update request
   * @throws IOException wrong path
   */
  void selfCheck() throws IOException {
    for (Ingredient ingredient: items) {
      if (ingredient.getQuantity() < 10 && !requestIngredients.containsKey(ingredient.getIngredientName())) {
        requestIngredients.put(ingredient.getIngredientName(),20);
        // when there is less than 10 units, call Request.txt
      }
    }
    writeRequest();
  }


  /**
   * write the request.txt by read Request HashMap
   * @throws IOException wrong path
   */
  void writeRequest() throws IOException {
    File file = new File("phase2/Request.txt" );
    if(file.exists()){
      FileWriter fw = new FileWriter(file,false);
      BufferedWriter bw = new BufferedWriter(fw);
      for(String requestName: requestIngredients.keySet()){
        if (requestIngredients.get(requestName) <= 0) requestIngredients.remove(requestName);
      }
      for(String requestName: requestIngredients.keySet()){
        bw.write( requestName + " needs "+ String.valueOf(requestIngredients.get(requestName)) +
          " units" + System.lineSeparator());
      }
      bw.close();
      fw.close();
    }
  }

  /**
   * read the information in th Request.txt and store in the HashMap
   */
  public void readRequest(){
    try (BufferedReader fileReader = new BufferedReader(new FileReader("phase2/Request.txt"))) {
      String line = fileReader.readLine();
      while (line != null) {
        String[] line1 = line.split(" needs ");
        String ingredientName = line1[0];
        String[] units = line1[1].split(" units");
        requestIngredients.put(ingredientName,Integer.parseInt(units[0]));
        line = fileReader.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

//  public void addItem(Ingredient i){
//
//    for (Ingredient ingredient: items){
//      if (ingredient.getIngredientName().equals(i.getIngredientName())) hasIngredient = false;
//    }
//    if (hasIngredient) items.add(i);
//
//    // Serialization
//    writeFile();
//  }
//

//    public void printItems(){
//      for(Ingredient i:items){
//        System.out.println(i.getIngredientName() + " " + i.getQuantity());
//      }
//    }
//
//    void getIngredients() throws IOException {
//      try (BufferedReader fileReader = new BufferedReader(new FileReader("phase1/Ingredient.txt"))) {
//        String line = fileReader.readLine();
//        int i = 0;
//        while (line != null) {
//          String[] line1 = line.split("\\s" +"\\|"+ "\\s");
//          ingredients[i] = new Ingredient(line1[0],Integer.parseInt(line1[1]));
//          this.addItem(ingredients[i]);
//          line = fileReader.readLine();
//          i += 1;
//        }
//      }
//      writeFile();
//    }
//
//  public static void main(String[] args) throws IOException{
//    Inventory2 inventory2 = new Inventory2();
//    inventory2.readRequest();
//    System.out.println(inventory2.requestIngredients);
//  }
}

