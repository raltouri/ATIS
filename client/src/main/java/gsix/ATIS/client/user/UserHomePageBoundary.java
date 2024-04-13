package gsix.ATIS.client.user;


import java.net.URL;
import java.util.ResourceBundle;

import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class UserHomePageBoundary {
    private User loggedInUser;
    private Stage stage;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="message_to_manager_Btn"
    private Button message_to_manager_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="messages_inbox_Btn"
    private Button messages_inbox_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="my_tasks_Btn"
    private Button my_tasks_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="open_request_Btn"
    private Button open_request_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="volunteer_Btn"
    private Button volunteer_Btn; // Value injected by FXMLLoader

    @FXML
    void MessageToManager(ActionEvent event) {

    }

    @FXML
    void MessagesInbox(ActionEvent event) {

    }

    @FXML
    void MyTasks(ActionEvent event) {
        //open a new page
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        MyTasksPage myTasksPage = (MyTasksPage) guiCommon.displayNextScreen("MyTasksPage.fxml",
                "Community User My Tasks Page", stage, true);  // Example for opening new screen

        myTasksPage.setLoggedInUser(loggedInUser);

    }

    @FXML
    void OpenRequest(ActionEvent event) {

    }

    @FXML
    void Volunteer(ActionEvent event) {
        //open a new page
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        Volunteer volunteerPage = (Volunteer) guiCommon.displayNextScreen("Volunteer.fxml",
                "Community User Volunteer Page", stage, true);  // Example for opening new screen

        volunteerPage.setLoggedInUser(loggedInUser);
    }

    @Subscribe
    public void handleSomeEvent(MessageEvent event) {
    }

    public void setLoggedInUser(User loggedUser){
        this.loggedInUser = loggedUser;
    }
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert message_to_manager_Btn != null : "fx:id=\"message_to_manager_Btn\" was not injected: check your FXML file 'UserHomePage.fxml'.";
        assert messages_inbox_Btn != null : "fx:id=\"messages_inbox_Btn\" was not injected: check your FXML file 'UserHomePage.fxml'.";
        assert my_tasks_Btn != null : "fx:id=\"my_tasks_Btn\" was not injected: check your FXML file 'UserHomePage.fxml'.";
        assert open_request_Btn != null : "fx:id=\"open_request_Btn\" was not injected: check your FXML file 'UserHomePage.fxml'.";
        assert volunteer_Btn != null : "fx:id=\"volunteer_Btn\" was not injected: check your FXML file 'UserHomePage.fxml'.";



        EventBus.getDefault().register(this);
    }

}
