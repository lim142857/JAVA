package Model;

import java.io.Serializable;

public class Ingredient implements Serializable{

  private String ingredientName;
  private int quantity;

  /**
   * the Ingredient class, store each ingredient's name and quantity
   * @param name the ame of ingredient
   * @param amount the quantity this ingredient has
   */
  public Ingredient(String name, int amount){
    ingredientName = name;
    quantity = amount;
  }

  /**
   * get the ingredient's name
   * @return the name of this ingredient
   */
  public String getIngredientName() {
    return ingredientName;
  }

  /**
   * get the quantity of this ingredient
   * @return the quantity of this ingredient
   */
  public int getQuantity() {
    return quantity;
  }

  /**
   * set the quantity of this ingredient
   * @param quantity the new quantity of this ingredient has
   */
  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
}
