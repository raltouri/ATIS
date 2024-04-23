package gsix.ATIS.client.manager;

import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.client.TasksController;
import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.Task;
import gsix.ATIS.entities.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class RequestedTasks {

    private User loggedInManager = null;
    private Stage stage;

    @FXML // fx:id="BtnApprove"
    private Button BtnApprove; // Value injected by FXMLLoader

    @FXML // fx:id="BtnBack"
    private Button BtnBack; // Value injected by FXMLLoader

    @FXML // fx:id="BtnDecline"
    private Button BtnDecline; // Value injected by FXMLLoader

    @FXML // fx:id="pendingLV"
    private ListView<String> pendingLV; // Value injected by FXMLLoader

    @FXML // fx:id="showPending"
    private Button showRequested; // Value injected by FXMLLoader

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInManager=loggedInUser;
    }


    @FXML
    void ShowRequestedTasks(ActionEvent event) {
        if(loggedInManager != null) {
            int communityID = this.loggedInManager.getCommunityId();
            System.out.println("im inside showTasks after pressing the button and loggedInUsere isnt null");
            // Initialize the ListView with available tasks
            getPendingTasksForCommunity(communityID); //
        }
        else{
            System.out.println("LoggedInUser is null in Volunteer Do task line 81");
        }
    }
    public void getPendingTasksForCommunity(int communityID) {

        Message message = new Message(1, LocalDateTime.now(), "get pending tasks", communityID);
        System.out.println("Volunteer:before send to server *Get Tasks for Community* command");
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @FXML
    void ApproveTask(ActionEvent event) {

    }


    @FXML
    void DeclineTask(ActionEvent event) {

    }

    @FXML
    void BackToManagerHome(ActionEvent event) {
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        ManagerHomePageBoundary managerHomePageBoundary = (ManagerHomePageBoundary) guiCommon.displayNextScreen("ManagerHomePage.fxml",
                "Manager Home Page", stage, true);  // Example for opening new screen
        managerHomePageBoundary.setLoggedInUser(loggedInManager);
    }

    @Subscribe
    public void handleTasksEvent(MessageEvent event){
        Message handledMessage=event.getMessage();
        //ADDED BY AYAL
        if(event.getMessage().getMessage().equals("get pending tasks: Done")){
            List<Task> communityTasks=(List<Task>) event.getMessage().getData();
            //System.out.println("I am handling the tasks for community being brought back from eventbus in Volunteer class");
            List<String> tasks_info = TasksController.getTasksInfo(communityTasks);
            //System.out.println(tasks_info);
            // Update ListView with received tasks
            Platform.runLater(() -> {
                pendingLV.getItems().clear(); // Clear existing items
                ObservableList<String> observableTasks = FXCollections.observableArrayList(tasks_info);
                pendingLV.setItems(observableTasks); // Add received tasks
            });
        }

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert BtnApprove != null : "fx:id=\"BtnApprove\" was not injected: check your FXML file 'RequestedTasks.fxml'.";
        assert BtnBack != null : "fx:id=\"BtnBack\" was not injected: check your FXML file 'RequestedTasks.fxml'.";
        assert BtnDecline != null : "fx:id=\"BtnDecline\" was not injected: check your FXML file 'RequestedTasks.fxml'.";
        assert pendingLV != null : "fx:id=\"pendingLV\" was not injected: check your FXML file 'RequestedTasks.fxml'.";
        assert showRequested != null : "fx:id=\"showPending\" was not injected: check your FXML file 'RequestedTasks.fxml'.";

        EventBus.getDefault().register(this);
    }
}