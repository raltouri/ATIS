/**
 * Sample Skeleton for 'MessagesViewerPage.fxml' Controller Class
 */


package gsix.ATIS.client.manager;

import gsix.ATIS.client.CommunityMessageController;
import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.client.user.UserHomePageBoundary;
import gsix.ATIS.entities.CommunityMessage;
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class ShowMessagesViewer {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="Mback_Btn"
    private Button Mback_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="Mreceived_Btn"
    private Button Mreceived_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="Mreceived_LV"
    private ListView<String> Mreceived_LV; // Value injected by FXMLLoader

    @FXML // fx:id="Msent_Btn"
    private Button Msent_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="Msent_LV"
    private ListView<String> Msent_LV; // Value injected by FXMLLoader

    private User loggedInUser=null;
    private Stage stage;

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser=loggedInUser;
    }


    ///------------------------------------btns------------------------------------
    @FXML
    void MShowReceivedMessages(ActionEvent event) {
        if(loggedInUser!=null) {
            String userId = this.loggedInUser.getUser_id();
            CommunityMessageController.getMessageBox(userId);
        }
        else{
            System.out.println("LoggedInUser is null in ShowMessages showReceivedMesages");
        }
    }

    @FXML
    void MgoBack(ActionEvent event) {
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();

        ManagerHomePageBoundary managerHomePage =
                (ManagerHomePageBoundary) guiCommon.displayNextScreen("ManagerHomePage.fxml",
                "Manager Home Page", stage, true);  // Example for opening new screen

        managerHomePage.setLoggedInUser(loggedInUser);
    }

    @FXML
    void MshowSentMessages(ActionEvent event) {
        if(loggedInUser!=null) {
            String userId = this.loggedInUser.getUser_id();
            // Initialize the ListView with available tasks
            getSentMessages(userId); //
        }
        else{
            System.out.println("LoggedInUser is null in ShowMessages showSentMesages");
        }
    }
    ///-----------------------------helper functions-------------------------------------------
    private void getSentMessages(String userId) {
        Message message = new Message(1, LocalDateTime.now(), "get sent messages", userId);
        System.out.println("ShowMessages: Sent Messages");
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleTasksEvent(MessageEvent event){
        Message handledMessage=event.getMessage();

        if(handledMessage.getMessage().equals("get sent messages: Done")){
            List<CommunityMessage> sentMessages=(List<CommunityMessage>) handledMessage.getData();
            System.out.println("I am handling the sent messages being brought back from eventbus in ShowMessageBox class");
            List<String> messages_info ;
            //Code to get message sent
            messages_info=getSentMessagesInfo(sentMessages);

            System.out.println(messages_info);
            // Update ListView with received tasks
            Platform.runLater(() -> {
                Msent_LV.getItems().clear(); // Clear existing items
                ObservableList<String> observableTasks = FXCollections.observableArrayList(messages_info);
                Msent_LV.setItems(observableTasks); // Add received tasks
            });
        }
        if(handledMessage.getMessage().equals("get received messages: Done")){
            List<CommunityMessage> receivedMessages=(List<CommunityMessage>) handledMessage.getData();
            System.out.println("I am handling the received messages being brought back from eventbus in showMessageBox class");
            List<String> messages_info ;
            //Code to get message sent
            messages_info=getReceivedMessagesInfo(receivedMessages);
            System.out.println(messages_info);
            // Update ListView with received tasks
            Platform.runLater(() -> {
                Mreceived_LV.getItems().clear(); // Clear existing items
                ObservableList<String> observableTasks = FXCollections.observableArrayList(messages_info);
                Mreceived_LV.setItems(observableTasks); // Add received tasks
            });
        }

    }

    public static List<String> getSentMessagesInfo(List<CommunityMessage> messages) {
        List<String> messages_info = new ArrayList<>();
        for (CommunityMessage message : messages) {
            messages_info.add("Message ID:" + message.getMessage_id() +  ", Receiver: " + message.getReceiver_id()+ ", Content: " + message.getContent());
        }
        return messages_info;
    }

    public static List<String> getReceivedMessagesInfo(List<CommunityMessage> messages) {
        List<String> messages_info = new ArrayList<>();
        for (CommunityMessage message : messages) {
            messages_info.add("Message ID:" + message.getMessage_id() +  ", Sender: " + message.getSender_id()+ ", Content: " + message.getContent());
        }
        return messages_info;
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert Mback_Btn != null : "fx:id=\"Mback_Btn\" was not injected: check your FXML file 'MessagesViewerPage.fxml'.";
        assert Mreceived_Btn != null : "fx:id=\"Mreceived_Btn\" was not injected: check your FXML file 'MessagesViewerPage.fxml'.";
        assert Mreceived_LV != null : "fx:id=\"Mreceived_LV\" was not injected: check your FXML file 'MessagesViewerPage.fxml'.";
        assert Msent_Btn != null : "fx:id=\"Msent_Btn\" was not injected: check your FXML file 'MessagesViewerPage.fxml'.";
        assert Msent_LV != null : "fx:id=\"Msent_LV\" was not injected: check your FXML file 'MessagesViewerPage.fxml'.";

    }

}