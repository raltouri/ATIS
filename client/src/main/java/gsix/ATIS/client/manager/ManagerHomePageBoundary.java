/**
 * Sample Skeleton for 'ManagerHomePage.fxml' Controller Class
 */

package gsix.ATIS.client.manager;

import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ManagerHomePageBoundary {
    private Stage stage;
    private User loggedInUser;

    @FXML // fx:id="approvedRequests"
    private Button approvedRequests; // Value injected by FXMLLoader

    @FXML // fx:id="msgToUser"
    private Button msgToUser; // Value injected by FXMLLoader

    @FXML // fx:id="RequestedTask"
    private Button RequestedTask; // Value injected by FXMLLoader

    @FXML // fx:id="viewMsges"
    private Button viewMsges; // Value injected by FXMLLoader

    @FXML // fx:id="viewReports"
    private Button viewReports; // Value injected by FXMLLoader

    @FXML // fx:id="logOut"
    private Button logOut; // Value injected by FXMLLoader

    public void setLoggedInUser(User loggedUser) {
        this.loggedInUser = loggedUser;
    }

    @FXML
    void MessageToUser(ActionEvent event) {

    }

    @FXML
    void ViewApprovedRequests(ActionEvent event) {

    }

    @FXML
    void ViewCommunityReports(ActionEvent event) {

    }

    @FXML
    void ViewMessages(ActionEvent event) {

    }

    @FXML
    void ViewRequestedTasks(ActionEvent event) {
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        RequestedTasks pendingRequests1 = (RequestedTasks) guiCommon.displayNextScreen("RequestedTasks.fxml",
                "Community's Requested Tasks Page", stage, true);  // Example for opening new screen

        pendingRequests1.setLoggedInUser(loggedInUser);

    }

    @FXML
    void logOut(ActionEvent event) {
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        /*LoginFrameBoundary loginFrameBoundary = (LoginFrameBoundary)*/ guiCommon.displayNextScreen("LoginForm.fxml",
                "Login Screen", stage, true);
    }

}
