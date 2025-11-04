package ch.makery.address.view

import javafx.scene.control.{TextArea, TextField}
import javafx.fxml.FXML
import scalafx.stage.Stage
import javafx.event.ActionEvent
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

class DistributeFormController:

  @FXML
  private var quantityField: TextField = _
  @FXML
  private var recipientField: TextField = _
  @FXML
  private var notesArea: TextArea = _

  var dialogStage: Stage = _
  var confirmed: Boolean = false

  def getQuantity: String = quantityField.getText
  def getRecipient: String = recipientField.getText
  def getNotes: String = notesArea.getText


  @FXML
  def handleConfirm(event: ActionEvent): Unit =
    if isInputValue() then
      confirmed = true
      dialogStage.close()

  @FXML
  def handleCancel(event: ActionEvent): Unit =
    dialogStage.close()

  private def nullChecking(value: String): Boolean =
    value == null || value.trim.isEmpty

  // Validate input values, show error alert if invalid
  def isInputValue(): Boolean =
    var errorMessage = ""

    if nullChecking(quantityField.getText) then
      errorMessage += "No Valid Quantity Entered!\n"

    if nullChecking(recipientField.getText) then
      errorMessage += "No Valid Recipient Entered!\n"

    if errorMessage.isEmpty then
      true

    else
      val alert = new Alert(AlertType.Error):
        initOwner(dialogStage)
        title = "Invalid Input"
        headerText = "Please correct the following errors"
        contentText = errorMessage
      alert.showAndWait()
      false

