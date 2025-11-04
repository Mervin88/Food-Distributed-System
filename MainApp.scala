package ch.makery.address

import ch.makery.address.MainApp.stage
import ch.makery.address.model.Food
import ch.makery.address.util.Database
import ch.makery.address.view.{DashboardController, DistributeFormController, FoodEditController, RootLayoutController}
import javafx.fxml.FXMLLoader
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import javafx.scene as jfxs
import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
import scalafx.scene.image.Image
import scalafx.scene.layout.BorderPane
import scalafx.stage.{Modality, Stage}

object MainApp extends JFXApp3 {

  Database.setupDB()
  var roots: Option[scalafx.scene.layout.BorderPane] = None

  var dashboardController: Option[DashboardController] = None

  val foodDetails = new ObservableBuffer[Food]()
  foodDetails ++= Food.getAllFoods


  override def start(): Unit = {
    showLogin()
  }

  def showLogin(): Unit = {
    val rootResource = getClass.getResource("/ch.makery.address.view/Login.fxml")
    val loader = new FXMLLoader(rootResource)
    loader.load()

    val loginRoot = loader.getRoot[jfxs.Parent]

    stage = new PrimaryStage {
      title = "Login - Food Distribution Management"
      icons += new Image(getClass.getResource("/Images/LoginLogo.png").toExternalForm)
      scene = new Scene {
        root = loginRoot
      }
    }

    stage.show()
  }

  def initRootLayout(): Unit =
    val resource = getClass.getResource("/ch.makery.address.view/RootLayout.fxml")
    val loader = new FXMLLoader(resource)
    loader.load()

    val rootLayout = loader.getRoot[jfxs.layout.BorderPane]
    this.roots = Some(rootLayout)

    stage.scene = new Scene(rootLayout)
    stage.title = "Dashboard - Food Distribution Management"
    stage.show()

  def showDashboard(): Unit =
    val resource = getClass.getResource("/ch.makery.address.view/Dashboard.fxml")
    var cssResource = getClass.getResource("view/Dashboard.css")
    val loader = new FXMLLoader(resource)
    loader.load()
    val dashboardRoot = loader.getRoot[jfxs.layout.AnchorPane]
    val controller = loader.getController[DashboardController]
    dashboardController = Some(controller)
    Food.normalizeAll(foodDetails)

    roots match
      case Some(layout) => layout.setCenter(dashboardRoot)
      case None         => println("Root layout not initialized. Call initRootLayout() first.")

  def showFoodEdit(food: Food): Boolean =
    val resource = getClass.getResource("/ch.makery.address.view/FoodEdit.fxml")
    val loader = new FXMLLoader(resource)
    loader.load();
    val root2 = loader.getRoot[jfxs.Parent]
    val control = loader.getController[FoodEditController]

    val dialog = new Stage():
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      title = "Add / Edit Food Details"
      icons += new Image(getClass.getResource("/Images/addfood.png").toExternalForm)
      scene = new Scene:
        root = root2

    control.dialogStage = dialog
    control.food = food
    dialog.showAndWait()
    control.confirmClicked

  def showDistributeForm(): Option[(String, String, String)] =
    val resource = getClass.getResource("/ch.makery.address.view/DistributeForm.fxml")
    val loader = new FXMLLoader(resource)
    loader.load();
    val root2 = loader.getRoot[jfxs.Parent]
    val control = loader.getController[DistributeFormController]

    val dialog = new Stage():
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      title = "Distribute Food"
      icons += new Image(getClass.getResource("/Images/distributedfood.png").toExternalForm)
      scene = new Scene:
        root = root2

    control.dialogStage = dialog
    dialog.showAndWait()

    if control.confirmed then
      Some((control.getQuantity, control.getRecipient, control.getNotes))
    else
      None
}