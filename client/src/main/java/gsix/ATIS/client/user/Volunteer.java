
/**
 * Sample Skeleton for 'Untitled' Controller Class
 */

package gsix.ATIS.client.user;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.client.TasksController;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.Task;
import gsix.ATIS.entities.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class Volunteer {

    private User loggedInUser=null;


    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="availableTaskListView"
    private ListView<String> availableTaskListView; // Value injected by FXMLLoader

    @FXML // fx:id="back_Btn"
    private Button back_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="choose_Btn"
    private Button choose_Btn; // Value injected by FXMLLoader


    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert availableTaskListView != null : "fx:id=\"availableTaskListView\" was not injected: check your FXML file 'Untitled'.";
        assert back_Btn != null : "fx:id=\"back_Btn\" was not injected: check your FXML file 'Untitled'.";
        assert choose_Btn != null : "fx:id=\"choose_Btn\" was not injected: check your FXML file 'Untitled'.";

        EventBus.getDefault().register(this);

    }
    @Subscribe
    public void handleTasksEvent(MessageEvent event){
        Message handledMessage=event.getMessage();
        //ADDED BY AYAL
        if(event.getMessage().getMessage().equals("get tasks for community: Done")){
            List<Task> communityTasks=(List<Task>) event.getMessage().getData();
            System.out.println("I am handling the tasks for community being brought back from eventbus in Volunteer class");
            List<String> tasks_info = TasksController.getTasksInfo(communityTasks);
            System.out.println(tasks_info);
            // Update ListView with received tasks
            Platform.runLater(() -> {
                availableTaskListView.getItems().clear(); // Clear existing items
                ObservableList<String> observableTasks = FXCollections.observableArrayList(tasks_info);
                availableTaskListView.setItems(observableTasks); // Add received tasks
            });
        }

    }
    @FXML
    private void backToUserHome() {
        // Implement logic to navigate back to the user's home page
    }
    @FXML
    private void doTask() {
        if(loggedInUser!=null) {
            String userId = this.loggedInUser.getUser_id();
            System.out.println("im inside doTask after pressing the button and loggedInUsere isnt null");
            // Initialize the ListView with available tasks
            getTasksForCommunity(userId); //
        }
        else{
            System.out.println("LoggedInUser is null in Volunteer Do task line 81");
        }
    }
    public void getTasksForCommunity(String userId) {

        Message message = new Message(1, LocalDateTime.now(), "get tasks for community", userId);
        System.out.println("Volunteer:before send to server *Get Tasks for Community* command");
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("I HATE THIS COURSE");

    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser=loggedInUser;
    }
}
