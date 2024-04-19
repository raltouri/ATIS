package gsix.ATIS.client.user;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import gsix.ATIS.client.CommunityMessageController;
import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SendMessageToManager {
    private User loggedInUser=null;
    private int communityID=0;
    private String managerID=null;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    @FXML // fx:id="message_TF"
    private TextField message_TF; // Value injected by FXMLLoader

    @FXML // fx:id="back_Btn"
    private Button back_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="send_Btn"
    private Button send_Btn; // Value injected by FXMLLoader
    private Stage stage;

    @FXML
    void BackToUserHomePage(ActionEvent event) {
        // Implement logic to navigate back to the user's home page
        //open a new page
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        UserHomePageBoundary userHomePage = (UserHomePageBoundary) guiCommon.displayNextScreen("UserHomePage.fxml",
                "Community User Home Page", stage, true);  // Example for opening new screen
        userHomePage.setLoggedInUser(loggedInUser);

    }

    @FXML
    void SendMessageForManager(ActionEvent event) {
        //get the message from the text field
        String givenMessage=message_TF.getText();

        if (givenMessage.isEmpty()) {
            // If the message is empty, show an alert to the user
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Empty Message");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a message before sending.");
            alert.showAndWait();
            return; // Exit the method, don't proceed further
        }
        communityID= loggedInUser.getCommunityId();

        //get manager id from community table using comunity id
        Message message2 = new Message(1, LocalDateTime.now(), "get manager id", communityID);
        System.out.println("Send to Manager getting Manager id");
        try {
            SimpleClient.getClient("",0).sendToServer(message2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert back_Btn != null : "fx:id=\"back_Btn\" was not injected: check your FXML file 'Untitled'.";
        assert send_Btn != null : "fx:id=\"send_Btn\" was not injected: check your FXML file 'Untitled'.";
        assert message_TF != null : "fx:id=\"message_TF\" was not injected: check your FXML file 'Untitled'.";

        EventBus.getDefault().register(this);
    }
    @Subscribe
    public void handleTasksEvent(MessageEvent event){
        Message handledMessage=event.getMessage();
        //ADDED BY AYAL
        if(handledMessage.getMessage().equals("manager id is here")){
            //get the manager id from the handled message
            managerID=(String)handledMessage.getData();

            //get the message from the text field
            String message=message_TF.getText();
            //sending messages via CommunityMessageController
            CommunityMessageController.send(loggedInUser.getUser_id(),managerID,message);


        }
    }
    public  void setLoggedInUser(User loggedUser){
        this.loggedInUser = loggedUser;
    }

}
