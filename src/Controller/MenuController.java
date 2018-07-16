package Controller;

import Model.Server;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/** MenuController, the controller of Menu.fxml and nextMenu.fxml.
 *  sever can see menu scene and next menu scene.
 */
public class MenuController implements Initializable {

  static Stage primaryStage;
  static Server user;
  @FXML Button Snacks;
  @FXML Button Chef_R;
  @FXML Button Salad;
  @FXML Button Noddles;
  @FXML Button Sushi;
  @FXML Button Rice;
  @FXML Button Dessert;
  @FXML Button Soup;

  /** initialization of MenuController.
   * @param location The location used to resolve relative paths for the root object, or
   *                  <tt>null</tt> if the location is not known.
   * @param resources The resources used to localize the root object, or <tt>null</tt> if
   *                  the root object was not localized.
   */
  public void initialize(URL location, ResourceBundle resources) {
  }

  /** if the sever press page2, the scene will switch change to next menu scene
   * @throws IOException path of nextMenu.fxml is not correct
   */
  @FXML
  void toNextMenu() throws IOException {
    Parent nextMenuScene = FXMLLoader.load(getClass().getResource("/GUI/nextMenu.fxml"));
    primaryStage.setScene(new Scene(nextMenuScene));
  }

  /** if the sever press page1, main , or menu it will switch to menu scene.
   * @throws IOException path of Menu.fxml is not corrected
   */
  @FXML
  void toMenu() throws IOException {
    Parent MenuScene = FXMLLoader.load(getClass().getResource("/GUI/Menu.fxml"));
    primaryStage.setScene(new Scene(MenuScene));
  }

  /** in menu scene, there are many order types selection.
   *  it will switch to different order's scene based on the press which button.
   * @throws IOException
   */
  @FXML
  void toChefRecommendation() throws IOException {
    String name = "";
    if (Chef_R != null && Chef_R.isPressed()) {
      name = Chef_R.getText();
    }
    if (Salad != null && Salad.isPressed()) {
      name =  Salad.getText();
    }
    if (Rice != null && Rice.isPressed()) {
      name =  Rice.getText();
    }
    if (Noddles != null && Noddles.isPressed()) {
      name = Noddles.getText();
    }
    if (Sushi != null && Sushi.isPressed()) {
      name = Sushi.getText();
    }
    if (Snacks != null && Snacks.isPressed()) {
      name =  Snacks.getText();
    }
    if (Dessert != null && Dessert.isPressed()) {
      name = Dessert.getText();
    }
    if (Soup != null && Soup.isPressed()) {
      name =Soup.getText();
    }
    if (! name.isEmpty()) {
      OrderController.name = name;
      Parent chef_recommendationScene =
              FXMLLoader.load(getClass().getResource("/GUI/OrderView.fxml"));
      primaryStage.setScene(new Scene(chef_recommendationScene));
    }
  }

}
