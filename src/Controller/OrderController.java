package Controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Pane;
import javafx.scene.image.*;
import javafx.scene.text.Text;
import java.lang.Math;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

/** OrderController, the controller of OrderView.fxml
 *  it shows the details of orders, and server can select the order, add or subtract or clear ingredients,
 *  and after that, he can submit the order to the cart.
 */
public class OrderController implements Initializable {
  @FXML CheckBox upperSelection;
  @FXML CheckBox lowerSelection;
  @FXML Button Add;

  @FXML Button Sub;
  @FXML Button Clear;
  @FXML Button Submit;
  @FXML Button Cart;
  @FXML Button Back;
  @FXML Button Next;
  @FXML ListView upperList;
  @FXML ListView lowerList;
  @FXML ImageView upperImage;
  @FXML ImageView lowerImage;
  @FXML Text Title;
  @FXML Text upperPrice;
  @FXML Text lowerPrice;
  @FXML Text Notation;
  @FXML ImageView cat;

  private HashMap<String, ArrayList<String>> ingredient = new HashMap<>();
  static int click;
  static int scene;
  static ArrayList<String> Scene = new ArrayList();
  static HashMap OrderPicture = new HashMap();
  static String name;

  /** there are two orders in one order view scene.
   * only one Checkbox of orders can be selected. It will switch to another one, if there is already one Checkbox
   * be selected.
   */
  void UniqueChoice() {
    if (lowerSelection.isVisible()) {
      upperSelection
          .selectedProperty()
          .addListener((observable, oldValue, newValue) -> lowerSelection.setSelected(!newValue));
      lowerSelection
          .selectedProperty()
          .addListener((observable, oldValue, newValue) -> upperSelection.setSelected(!newValue));
    } else {
      if (!upperSelection.isSelected()) {
        upperList.getItems().clear();
      }
    }
  }

  /** display the ingredients of selected order in the list view.
   * Only the order with selected Checkbox can be displayed its ingredients.
   * @throws IOException
   */
  @FXML
  private void displayIngredients() throws IOException {
    cat.setVisible(false);
    Notation.setText("");
    if (getSelected() != null && getList().isVisible()) {
      HashMap ingredient = getIngredient(getSelected().getText());
      if (getSelected().isSelected()) {
        getList().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        for (Object name : ingredient.keySet()) {
          getList().getItems().add(name);
        }
      } else {
        getList().getItems().clear();
      }
    }
    UniqueChoice();
    if (getunList() != null) {
      getunList().getItems().clear();
    }
  }

  /** get which list view should display the ingredients
   * @return the list view which order's Checkbox is selected.
   */
  ListView getList() {
    if (upperSelection.isSelected()) {
      return upperList;
    }
    if (lowerSelection.isSelected()) {
      return lowerList;
    }
    return null;
  }

  /**get which list view should not display the ingredients.
   * @return the list view which order's Checkbox is not selected.
   */
  ListView getunList() {
    if (upperSelection.isSelected()) {
      return lowerList;
    }
    if (lowerSelection.isSelected()) {
      return upperList;
    }
    return null;
  }

  /** make string of the ingredients which has successfully added or subtracted
   * @param ingredients ingredient wants to be added or subtracted
   * @param type add or subtract
   * @return
   */
  String toString(ObservableList<String> ingredients, String type) {
    String result = "";
    for (String name : ingredients) {
      result += "You have already " + type + name + "\n";
    }
    return result;
  }

  /** for the order which Checkbox is selected, if sever press Add, Subtract, the ingredients he selected
   * will be added into the Hashmap ingredient. if sever press Clear, all the ingredients he used to add or
   * subtract will be cleared.
   * There will display a notation on the right bottom of the scene, if the ingredients has successfully added
   * subtracted or cleared.
   */
  @FXML
  void BoxSelect() {
    cat.setVisible(false);
    if (getList() != null) {
      ObservableList<String> selectIngredient = getList().getSelectionModel().getSelectedItems();
      ArrayList<String> list;
      if (Add.isPressed()) {
        Notation.setText(toString(selectIngredient, "added "));
        if (ingredient.containsKey("Add")) {
          list = ingredient.get("Add");
          list.addAll(selectIngredient);
        } else {
          ArrayList<String> hasSelect = new ArrayList<>(selectIngredient);
          ingredient.put("Add", hasSelect);
        }
      }
      if (Sub.isPressed()) {
        if (ingredient.containsKey("Subtract")) {
          Notation.setText(toString(selectIngredient, "subtracted "));
          list = ingredient.get("Subtract");
          list.addAll(selectIngredient);
        } else {
          ArrayList<String> hasSelect = new ArrayList<>(selectIngredient);
          ingredient.put("Subtract", hasSelect);
        }
      }
      if (getSelected().isSelected() && Clear.isPressed()) {
        Notation.setText("All items has been cleared");
        ingredient.clear();
      }
    }
  }

  /** if server press Submit button. The order he selected with the added and subtracted ingredients will be
   * added to cart.
   * if the order has successfully submitted, there will display a cat with a notation on the bottom of the scene.
   */
  @FXML
  void PressSubmit() {
    ArrayList list = new ArrayList();
    if (getSelected() != null) {
      cat.setVisible(true);
      Notation.setText("Congratulations! Your Order has been added to cart ");
      if (CartController.order.containsKey(getSelected().getText())) {
        CartController.order.get(getSelected().getText()).add((ingredient.clone()));
        ingredient.clear();
      } else {
        list.add(ingredient.clone());
        CartController.order.put(getSelected().getText(), list);
        ingredient.clear();
      }
    }
  }

  /** get which Checkbox is selected.
   * @return the Checkbox is selected.
   */
  CheckBox getSelected() {
    if (upperSelection.isSelected()) {
      return upperSelection;
    } else if (lowerSelection.isSelected()) {
      return lowerSelection;
    }
    return null;
  }

  /** get the ingredients of order.
   * @param order the name of order
   * @return the ingredients of order
   * @throws IOException path of Menu.txt is not corrected
   */
  private HashMap<String, Integer> getIngredient(String order) throws IOException {
    try (BufferedReader fileReader = new BufferedReader(new FileReader("phase2/Menu.txt"))) {
      HashMap<String, Integer> ingredients = new HashMap<>();
      String line = fileReader.readLine();

      while (line != null) {
        String[] line1 = line.split("\\s" + "\\|" + "\\s");
        int ingredientNum = line1.length;
        if (order.equals(line1[0])) {
          for (int i = 2; i <= ingredientNum - 1; i++) {
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

  /** if server press main button, it will switch to menu scene
   * @throws IOException path of Menu.fxml is not corrected
   */
  @FXML
  void toMenu() throws IOException {
    cat.setVisible(false);
    Parent MenuScene = FXMLLoader.load(getClass().getResource("/GUI/Menu.fxml"));
    MenuController.primaryStage.setScene(new Scene(MenuScene));
  }

  /** find the index of the order in the Arraylist OrderPicture.get(type)
   * @param type the type of order.
   * @param name the name of order
   * @return the index of order in the OrderPicture.get(type)
   */
  int FindIndex(String type, String name) {
    int count = 0;
    int num = 0;
    if (OrderPicture.containsKey(type)) {
      while (num < ((ArrayList) OrderPicture.get(type)).size()) {
        if (((ArrayList) OrderPicture.get(type))
            .get(num)
            .equals("GUI/picture/" + name.replace(" ", "_") + ".jpg")) {
          count += 0;
          break;
        } else {
          count += 1;
        }
        num += 1;
      }
    }
    return count;
  }

  /** find the index of type in the Arraylist Scene.
   * @param type type of order
   * @return the index of type in the Arraylist Scene
   */
  int FindType(String type) {
    if (Scene.contains(type)) {
      return Scene.indexOf(type);
    }
    return 0;
  }

  /** change the title, pictures, Checkboxs' text and price if server press Back, or Next.
   * it may only display one order, if there only left one order of this type of order to show.
   * @throws IOException path of picture's URL is not correct.
   */
  @FXML
  public void changeImage() throws IOException {
    String currentTitle = Title.getText();
    cat.setVisible(false);
    if (!OrderPicture.isEmpty()) {
      if (lowerSelection.isVisible()) {
        String LName = lowerSelection.getText();
        String UName = upperSelection.getText();
        int Lcount = FindIndex(currentTitle, LName);
        int Ucount = FindIndex(currentTitle, UName);
        click = Math.max(Lcount, Ucount);
      } else {
        FindIndex(currentTitle, upperSelection.getText());
      }
      scene = FindType(currentTitle);
    }
    if (scene < Scene.size()) {
      if (click == ((ArrayList) OrderPicture.get(currentTitle)).size() - 1) {
        click = -1;
        scene += 1;
        if (scene == Scene.size()) {
          scene = 0;
        }
        Title.setText(Scene.get(scene));
        currentTitle = Title.getText();
      }
      click += 1;
      String upperUrl = (String) ((ArrayList) OrderPicture.get(currentTitle)).get(click);
      Image upperPhoto = new Image(upperUrl, 700, 700, false, true);
      upperImage.setImage(upperPhoto);
      String upperName =
          upperUrl
              .substring(upperUrl.lastIndexOf("/") + 1, upperUrl.length() - 4)
              .replace("_", " ");
      upperSelection.setText(upperName);
      upperPrice.setText("$"+String.valueOf(getPrice(upperName)));
      if (click == ((ArrayList) OrderPicture.get(currentTitle)).size() - 1) {
        lowerImage.setImage(null);
        lowerSelection.setText(null);
        lowerSelection.setVisible(false);
        lowerList.setVisible(false);
        lowerPrice.setText(null);
      }
      if (click < ((ArrayList) OrderPicture.get(currentTitle)).size() - 1) {
        click += 1;
        String lowerUrl = (String) ((ArrayList) OrderPicture.get(currentTitle)).get(click);
        Image lowerPhoto = new Image(lowerUrl, 700, 700, false, true);
        String lowerName = lowerUrl.substring(lowerUrl.lastIndexOf("/") + 1, lowerUrl.length() - 4).replace("_", " ");
        lowerImage.setImage(lowerPhoto);
        lowerSelection.setVisible(true);
        lowerList.setVisible(true);
        lowerSelection.setText(lowerName);
        lowerPrice.setText("$" + String.valueOf(getPrice(lowerName)));
        click += 1;
      }
    }
    upperList.getItems().clear();
    if (lowerList.isVisible()) {
      lowerList.getItems().clear();
    }
    displayIngredients();
  }

  /** if server press cart button, the scene will switch to cart scene
   * @throws IOException path Cart.fxml is not correct.
   */
  @FXML
  void toCart() throws IOException {
    cat.setVisible(false);
    Pane CartScene = FXMLLoader.load(getClass().getResource("/GUI/Cart.fxml"));
    MenuController.primaryStage.setScene(new Scene(CartScene));
  }

  /** if back button is pressed the order will be displayed in reversed order.
   *  if next button is pressed the order will be displayed in normal order.
   * @throws IOException
   */
  @FXML
  void addPicture() throws IOException {
    Scene.clear();
    OrderPicture.clear();

    String[] OrderType = {
      "Chef Recommendation | GUI/picture/Pork_Roast.jpg | GUI/picture/Temari_Sushi.jpg",
      "Salad | GUI/picture/Vinegared_Cucumber_Salad.jpg | GUI/picture/Watercress_Salad.jpg",
      "Rice | GUI/picture/Gyudon_Beef_Bowl.jpg | GUI/picture/Natto_Curry.jpg | GUI/picture/Ochazuke_Green_Tea_And_Rice.jpg | GUI/picture/Tamagoyaki_Japanese_Omelette.jpg",
      "Noodles | GUI/picture/Mushroom_and_Cream_Pasta_with_Dashi.jpg | GUI/picture/Yakisoba_Fried_Noodle.jpg",
      "Sushi | GUI/picture/Avocado_Sushi.jpg | GUI/picture/Inari_Sushi.jpg | GUI/picture/Turkey_Sushi.jpg",
      "Snacks and Sides | GUI/picture/Cheesy_Tempura_Soy_Parcels.jpg | GUI/picture/Dashi_Bruschetta.jpg | GUI/picture/Mabo_Tofu.jpg | GUI/picture/Marinated_Tofu_Soboro_Mince.jpg | GUI/picture/Shio_Koji_Vegetable_Saute.jpg | GUI/picture/Tempting_Tofu.jpg | GUI/picture/Yaki_Nasu_Grilled_Aubergine.jpg",
      "Dessert | GUI/picture/Cheesy_Hanpen_Fish_Cakes.jpg | GUI/picture/Milk_Tea_Pudding.jpg | GUI/picture/Oshiruko_Sweet_Red_Bean_Soup.jpg",
      "Soup | GUI/picture/Seafood_Soup.jpg | GUI/picture/Soba_Noodle_Soup.jpg"
    };

    String[] ROrderType = {
      "Soup | GUI/picture/Soba_Noodle_Soup.jpg | GUI/picture/Seafood_Soup.jpg",
      "Dessert | GUI/picture/Oshiruko_Sweet_Red_Bean_Soup.jpg | GUI/picture/Milk_Tea_Pudding.jpg | GUI/picture/Cheesy_Hanpen_Fish_Cakes.jpg",
      "Snacks and Sides | GUI/picture/Yaki_Nasu_Grilled_Aubergine.jpg | GUI/picture/Tempting_Tofu.jpg | GUI/picture/Shio_Koji_Vegetable_Saute.jpg | GUI/picture/Marinated_Tofu_Soboro_Mince.jpg | GUI/picture/Mabo_Tofu.jpg | GUI/picture/Dashi_Bruschetta.jpg | GUI/picture/Cheesy_Tempura_Soy_Parcels.jpg",
      "Sushi | GUI/picture/Turkey_Sushi.jpg | GUI/picture/Inari_Sushi.jpg | GUI/picture/Avocado_Sushi.jpg",
      "Noodles | GUI/picture/Yakisoba_Fried_Noodle.jpg | GUI/picture/Mushroom_and_Cream_Pasta_with_Dashi.jpg",
      "Rice | GUI/picture/Tamagoyaki_Japanese_Omelette.jpg | GUI/picture/Ochazuke_Green_Tea_And_Rice.jpg | GUI/picture/Natto_Curry.jpg | GUI/picture/Gyudon_Beef_Bowl.jpg",
      "Salad | GUI/picture/Watercress_Salad.jpg | GUI/picture/Vinegared_Cucumber_Salad.jpg",
      "Chef Recommendation | GUI/picture/Pork_Roast.jpg | GUI/picture/Temari_Sushi.jpg"
    };
    helper(OrderPicture, OrderType);
    if (Back != null && Back.isPressed()) {
      helper(OrderPicture, ROrderType);
      changeImage();
    } else if (Next != null && Next.isPressed()) {
      helper(OrderPicture, OrderType);
      changeImage();
    }
  }

  /** helper function. To put the order's type and all this type's orders name in a hashmap
   * @param map Hashmap OrderPicture
   * @param list String[] of order's name
   */
  void helper(HashMap<String, ArrayList> map, String[] list) {
    Scene.clear();
    OrderPicture.clear();
    for (String pathList : list) {
      String[] path = pathList.split(" \\| ");
      int count = 1;
      while (count < path.length) {
        if (map.containsKey(path[0])) {
          map.get(path[0]).add(path[count]);
        } else {
          ArrayList newList = new ArrayList();
          newList.add(path[count]);
          map.put(path[0], newList);
        }
        count += 1;
      }
      Scene.add(path[0]);
    }
  }

  /** get the price of the order
   * @param order order name
   * @return the price of this order
   * @throws IOException path Menu.txt is not correct
   */
  private double getPrice(String order) throws IOException {
    try (BufferedReader fileReader = new BufferedReader(new FileReader("phase2/Menu.txt"))) {
      double price = 0.0;
      String line = fileReader.readLine();

      while (line != null) {
        String[] line1 = line.split("\\s" + "\\|" + "\\s");
        if (order.equals(line1[0])) {
          price = Double.parseDouble(line1[1]);
          break;
        }
        line = fileReader.readLine();
      }
      return price;
    }
  }

  /** initialization of OrderController. It will display the orders based on the selection of order's type in the
   * menu or next menu scene.
   * @param location The location used to resolve relative paths for the root object, or
   *                  <tt>null</tt> if the location is not known.
   * @param resources The resources used to localize the root object, or <tt>null</tt> if
   *                  the root object was not localized.
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Title.setText(name);
    click = 0;
    scene = 0;
    try {
      addPicture();
    } catch (IOException e) {
      e.printStackTrace();
    }
    String upperUrl = (String) ((ArrayList) OrderPicture.get(name)).get(0);
    Image upperPhoto = new Image(upperUrl, 700, 700, false, true);
    upperImage.setImage(upperPhoto);
    String upperName = upperUrl.substring(upperUrl.lastIndexOf("/") + 1, upperUrl.length() - 4).replace("_", " ");
    upperSelection.setText(upperName);
    try {
      upperPrice.setText("$" + String.valueOf(getPrice(upperName)));
    } catch (IOException e) {
      e.printStackTrace();
    }

    String lowerUrl = (String) ((ArrayList) OrderPicture.get(name)).get(1);
    Image lowerPhoto = new Image(lowerUrl, 700, 700, false, true);
    lowerImage.setImage(lowerPhoto);
    String lowerName = lowerUrl.substring(lowerUrl.lastIndexOf("/") + 1, lowerUrl.length() - 4).replace("_", " ");
    lowerSelection.setText(lowerName);
    try {
      lowerPrice.setText("$" + String.valueOf(getPrice(lowerName)));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
