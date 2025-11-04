package ch.makery.address.view

import ch.makery.address.MainApp
import javafx.fxml.FXML
import javafx.scene.control.{TextField, PasswordField}
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import java.util.regex.Pattern


class LoginController:

  @FXML
  private var usernameField: TextField = _
  @FXML
  private var passwordField: PasswordField = _

  private val emailRegax = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"

  @FXML
  def handleLogin(): Unit =
    val username = usernameField.getText.trim
    val password = passwordField.getText

    if !Pattern.matches (emailRegax, username) then
      val alert = new Alert(AlertType.Warning):
        title = "Invalid Email"
        headerText = "Email Format is Incorrect"
        contentText = "Please enter a valid email address."
      alert.showAndWait()
      return

    if password.length < 8 then
      val alert = new Alert(AlertType.Warning):
        title = "Invalid Password"
        headerText = "Password too short"
        contentText = "Password must be at least 8 characters long."
      alert.showAndWait()
      return

    if username == "admin@gmail.com" && password == "12345678" then {
      MainApp.initRootLayout()
      MainApp.showDashboard()

    } else
      val alert = new Alert(AlertType.Error):
        title = "Login Failed"
        headerText = "Invalid Credentials"
        contentText = "Please Try Again"
      alert.showAndWait()
