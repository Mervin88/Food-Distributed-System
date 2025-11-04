package ch.makery.address.view

import ch.makery.address.MainApp
import javafx.event.ActionEvent
import javafx.fxml.FXML


@FXML
class RootLayoutController ():

  def handleclose(action: ActionEvent): Unit =
    MainApp.stage.close()