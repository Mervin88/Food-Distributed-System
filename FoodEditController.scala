package ch.makery.address.view

import ch.makery.address.model.Food
import javafx.fxml.FXML
import javafx.scene.control.{ComboBox, TextField} // https://openjfx.io/javadoc/17/javafx.controls/javafx/scene/control
import scalafx.stage.Stage
import scalafx.Includes.*
import ch.makery.address.util.DateUtil.*
import javafx.event.ActionEvent
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

@FXML
class FoodEditController ():
  @FXML
  private var nameField: TextField = _
  @FXML
  private var quantityField: TextField = _
  @FXML
  private var categoryBox: ComboBox[String] = _
  @FXML
  private var expiryDate: TextField = _
  @FXML
  private var statusBox: ComboBox[String] = _

  var dialogStage: Stage = null
  private var __food : Food = null
  var confirmClicked = false

  def food = __food
  def food_= (x : Food): Unit =
    __food = x

    categoryBox.getItems.setAll("Vegetable", "Fruit", "Meat", "Beverage", "Other")
    statusBox.getItems.setAll("Available", "Expired", "Distributed", "Other")

    nameField.text = __food.name.value
    quantityField.text = __food.quantity.value
    categoryBox.value = (__food.category.value)
    expiryDate.text = __food.expiryDate.value.asString
    statusBox.value = (__food.status.value)
    expiryDate.setPromptText("yyyy-MM-dd")

  @FXML
  def handleConfirm(action: ActionEvent): Unit = {
    if isInputValid() then
      __food.name.value = nameField.text.value
      __food.quantity.value = quantityField.text.value
      __food.category.value = Option(categoryBox.getValue).get
      __food.status.value = Option(statusBox.getValue).get

      // Parse expiry date
      val parsedDate = expiryDate.text.value.parseLocalDate
      if parsedDate.isDefined then
        __food.expiryDate.value = parsedDate.get

      confirmClicked = true
      dialogStage.close()
  }

  @FXML
  def handleCancel(action: ActionEvent): Unit = {
    dialogStage.close()
  }

  private def nullChecking (x: String): Boolean = x == null || x.trim.isEmpty

  def isInputValid(): Boolean =
    var errorMessage = ""

    if nullChecking(nameField.text.value) then
      errorMessage += "No Valid Food Name!\n"

    if nullChecking(quantityField.text.value) then
      errorMessage += "No Valid Quantity!\n"

    if categoryBox.getValue == null || nullChecking(categoryBox.getValue) then
      errorMessage += "No Valid Category!:\n"

    if nullChecking(expiryDate.text.value) || !expiryDate.text.value.isValidDate then
      errorMessage += "No valid expiry date! Use format yyyy-MM-dd\n"

    if statusBox.value == null || nullChecking(statusBox.getValue)then
      errorMessage += "No Valid Status!\n"

    if errorMessage.isEmpty then
      true

    else
      val alert = new Alert(AlertType.Error):
        initOwner(dialogStage)
        title = "Invalid Food"
        headerText = "Please Correct Invalid Food"
        contentText = errorMessage
      alert.showAndWait()
      false











