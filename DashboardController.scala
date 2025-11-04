package ch.makery.address.view

import ch.makery.address.MainApp
import ch.makery.address.model.Food
import javafx.event.ActionEvent
import javafx.scene.control.{TableColumn, TableView, TextField, ComboBox, TableRow}
import javafx.fxml.{FXML, FXMLLoader}
import scalafx.stage.{Modality, Stage}
import scalafx.Includes.*
import scalafx.beans.property.StringProperty
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import javafx.collections.transformation.{FilteredList, SortedList}
import ch.makery.address.util.DateUtil.*
import scalafx.beans.binding.Bindings


class DashboardController:

  @FXML private var foodTable: TableView[Food] = _
  @FXML private var nameColumn: TableColumn[Food, String] = _
  @FXML private var quantityColumn: TableColumn[Food, String] = _
  @FXML private var categoryColumn: TableColumn[Food, String] = _
  @FXML private var expiryDateColumn: TableColumn[Food, String] = _
  @FXML private var statusColumn: TableColumn[Food, String] = _
  @FXML private var searchField: TextField = _
  @FXML private var filterBox: ComboBox[String] = _
  @FXML private var filteredData: FilteredList[Food] = _


  @FXML def initialize(): Unit =
    foodTable.items = MainApp.foodDetails
    // Bind columns to Food properties
    nameColumn.setCellValueFactory(x => x.value.name)
    quantityColumn.setCellValueFactory(x => x.value.quantity)
    categoryColumn.setCellValueFactory(x => x.value.category)
    expiryDateColumn.setCellValueFactory { cd =>
        Bindings.createStringBinding(
          () => cd.value.expiryDate.value.asString, // recompute text
          cd.value.expiryDate // observe this property
        )
    }
    statusColumn.setCellValueFactory(x => x.value.status)
    filterBox.getItems.addAll("All", "Available", "Expired", "Distributed")
    filterBox.setValue("All")
    filteredData = new FilteredList(MainApp.foodDetails.delegate, _ => true)
    val sortedData = new SortedList(filteredData)
    sortedData.comparatorProperty().bind(foodTable.comparatorProperty())
    foodTable.setItems(sortedData)

    //Custom row styling
    foodTable.setRowFactory { (_: TableView[Food]) =>
      new TableRow[Food]() {
        override def updateItem(item: Food, empty: Boolean): Unit = {
          super.updateItem(item, empty)
          getStyleClass.removeAll("expired-row", "soon-expire") // reset

          if (!empty && item != null) {
            if (item.isExpired) {
              getStyleClass.add("expired-row")
            } else if (item.isExpiringSoon(5)) {
              getStyleClass.add("soon-expire")
            }
          }
        }
      }
    }

  @FXML
  def handleDeleteFood(action: ActionEvent): Unit =
    val selectedIndex = foodTable.selectionModel().selectedIndex.value
    if (selectedIndex >= 0) then
      MainApp.foodDetails.remove(selectedIndex).delete()
    else
      val alert = new Alert(AlertType.Warning):
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Food Selected"
        contentText = "Please select a food in the table"
      alert.showAndWait()

  @FXML
  def handleNewFood(action: ActionEvent) =
    val food = new Food("", "", "", "", "")
    val confirmClicked = MainApp.showFoodEdit(food)
    if (confirmClicked) then {
      MainApp.foodDetails += food
      food.save()
    }

  @FXML
  def handleEditFood(actionEvent: ActionEvent) =
    val selectedFood = foodTable.selectionModel().selectedItem.value
    if (selectedFood != null) then
      val confirmClicked = MainApp.showFoodEdit(selectedFood)
      selectedFood.save()

    else
      val alert = new Alert(AlertType.Warning):
        initOwner(MainApp.stage)
        title = "No Selection"
        headerText = "No Food Selected"
        contentText = "Please select a food in the table"
      alert.showAndWait()

  @FXML
  def handleDistributeFood(actionEvent: ActionEvent): Unit =
    val selectedFood = foodTable.selectionModel().selectedItem.value

    if selectedFood != null then
      val confirmClicked = MainApp.showDistributeForm()

      confirmClicked match
        case Some((quantityStr, recipient, notes)) =>
          quantityStr.trim.toIntOption match
            case Some(amount) =>
              selectedFood.distribute(amount) match
                case Left(errorMsg) =>
                  val alert = new Alert(AlertType.Error):
                    initOwner(MainApp.stage)
                    title = "Distribution Error"
                    headerText = "Failed to Distribute"
                    contentText = errorMsg
                  alert.showAndWait()

                case Right(successMsg) =>
                  selectedFood.save()
                  println(successMsg)
                  println(s"Recipient: $recipient")
                  println(s"Notes: $notes")
                  foodTable.refresh()

            case None =>
              val alert = new Alert(AlertType.Error):
                initOwner(MainApp.stage)
                title = "Invalid Input"
                headerText = "Invalid Quantity Entered"
                contentText = "Quantity must be a valid number."
              alert.showAndWait()

        case None =>
          println("Distribution cancelled by user.")
    else
      val alert = new Alert(AlertType.Warning):
        initOwner(MainApp.stage)
        title = "No Food Selected"
        headerText = "No Food Selected"
        contentText = "Please select a food item from the table to distribute."
      alert.showAndWait()

  @FXML
  def handleSearch(): Unit =
    val query = searchField.getText.toLowerCase
    filteredData.setPredicate { food =>
      if (query == null || query.isEmpty) true
      else
        food.name.value.toLowerCase.contains(query) ||
          food.category.value.toLowerCase.contains(query)
    }

  @FXML
  def handleFilter(): Unit =
    val choice = filterBox.getValue
    filteredData.setPredicate { food =>
      if (choice == null || choice == "All") true
      else food.status.value == choice
    }
