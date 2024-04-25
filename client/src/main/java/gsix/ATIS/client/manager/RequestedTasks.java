package gsix.ATIS.client.manager;

import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.client.TasksController;
import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.client.user.UserHomePageBoundary;
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.Task;
import gsix.ATIS.entities.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
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

    @FXML // fx:id="requestedLV"
    private ListView<String> requestedLV; // Value injected by FXMLLoader

    @FXML // fx:id="showPending"
    private Button showRequested; // Value injected by FXMLLoader

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInManager=loggedInUser;
    }


    @FXML
    void ShowRequestedTasks(ActionEvent event) {
        if(loggedInManager != null) {
            int communityID = this.loggedInManager.getCommunityId();
            //System.out.println("im inside showTasks after pressing the button and loggedInUsere isnt null");
            // Initialize the ListView with available tasks
            getRequestedTasksForCommunity(communityID); //
        }
        else{
            //System.out.println("LoggedInUser is null in Volunteer Do task line 81");
        }
    }
    public void getRequestedTasksForCommunity(int communityID) {

        Message message = new Message(1, LocalDateTime.now(), "get pending tasks", communityID);
        System.out.println("requestedTasks:before send to server");
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @FXML
    void ApproveTask(ActionEvent event) {
        String selectedTaskInfo = requestedLV.getSelectionModel().getSelectedItem();
        if (selectedTaskInfo != null) {
            // Parse the selected task information to get the task ID
            int taskId = extractTaskId(selectedTaskInfo);
            System.out.println("task id is : "+taskId);
            // Update the task status in the database
            updateTaskStatus(taskId, "Pending");
            //updateTaskVolunteer(taskId,loggedInUser.getUser_id());

            // Show a pop-up message to indicate success
            showSuccessMessage();
            // Assuming tasksList is the ObservableList backing the ListView
            ObservableList<String> tasksList = requestedLV.getItems();

            // Remove the pending task from the data model
            tasksList.remove(selectedTaskInfo);

            // Refresh the ListView to reflect the changes
            requestedLV.refresh();

            // Implement logic to navigate back to the user's home page
            //open a new page
            /*stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
            GuiCommon guiCommon = GuiCommon.getInstance();
            UserHomePageBoundary userHomePage = (UserHomePageBoundary) guiCommon.displayNextScreen("UserHomePage.fxml",
                    "Community User Home Page", stage, true);  // Example for opening new screen
            userHomePage.setLoggedInUser(loggedInUser);*/
        } else {
            // Handle the case when no task is selected
            showNoSelectedItemMessage();
        }
    }

    private int extractTaskId(String taskInfo) {
        // Split the taskInfo string based on comma and colon to extract the task ID
        String[] parts = taskInfo.split(","); // Split by comma
        for (String part : parts) {
            // Further split each part based on colon to check for "Task ID"
            String[] keyValue = part.trim().split(":");
            if (keyValue.length == 2 && keyValue[0].trim().equals("Task ID")) {
                // Parse and return the task ID
                return Integer.parseInt(keyValue[1].trim());
            }
        }
        // Return 0 if task ID is not found (or handle error appropriately)
        return 0;
    }

    private void updateTaskStatus(int taskId, String newStatus) {
        // Implement the logic to update the task status in the database
        // Example: You may use JDBC or an ORM framework to execute an update query
        // Example query: UPDATE tasks SET status = 'pending' WHERE id = taskId;
        String info = taskId +","+ newStatus;
        Message message = new Message(1, LocalDateTime.now(), "update task status",info);
        System.out.println("task id="+taskId+"new status="+newStatus);
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteTask(int taskID) {
        // Implement the logic to update the task status in the database
        // Example: You may use JDBC or an ORM framework to execute an update query
        // Example query: UPDATE tasks SET status = 'pending' WHERE id = taskId;

        Message message = new Message(1, LocalDateTime.now(), "delete task",taskID);
        //System.out.println("task id="+taskId+"new status="+newStatus);
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getTaskByID(int taskId) {
        // Implement the logic to update the task status in the database
        // Example: You may use JDBC or an ORM framework to execute an update query
        // Example query: UPDATE tasks SET status = 'pending' WHERE id = taskId;
        Message message = new Message(1, LocalDateTime.now(), "get task for decline",taskId);
        //System.out.println("task id="+taskId+"new status=");
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showSuccessMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Task Approved");
        alert.setHeaderText(null);
        alert.setContentText("You have Approved this task!");

        // Show the alert dialog
        alert.showAndWait();
    }
    private void showNoSelectedItemMessage() {
        // Handle the case when no task is selected
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Select a task");
        alert.setHeaderText(null);
        alert.setContentText("You need to select a task");

        // Show the alert dialog
        alert.showAndWait();
    }
    @FXML
    void DeclineTask(ActionEvent event) {
        String selectedTaskInfo = requestedLV.getSelectionModel().getSelectedItem();
        if (selectedTaskInfo != null) {
            // Parse the selected task information to get the task ID
            int taskId = extractTaskId(selectedTaskInfo);
            getTaskByID(taskId);
            System.out.println("declined task id is : "+taskId);

            // Assuming tasksList is the ObservableList backing the ListView
            ObservableList<String> tasksList = requestedLV.getItems();

            // Remove the pending task from the data model
            tasksList.remove(selectedTaskInfo);

            // Refresh the ListView to reflect the changes
            requestedLV.refresh();

            //deleteTask(taskId);
            // Implement logic to navigate back to the user's home page
            //open a new page
            stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
            //userHomePage.setLoggedInUser(loggedInUser);
        } else {
            // Handle the case when no task is selected
            showNoSelectedItemMessage();
        }

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
        if(event.getMessage().getMessage().equals("get pending tasks: Done")){
            List<Task> communityTasks=(List<Task>) event.getMessage().getData();
            //System.out.println("I am handling the tasks for community being brought back from eventbus in Volunteer class");
            List<String> tasks_info = TasksController.getTasksInfo(communityTasks);
            //System.out.println(tasks_info);
            // Update ListView with received tasks
            Platform.runLater(() -> {
                requestedLV.getItems().clear(); // Clear existing items
                ObservableList<String> observableTasks = FXCollections.observableArrayList(tasks_info);
                requestedLV.setItems(observableTasks); // Add received tasks
            });
        }
        if(event.getMessage().getMessage().equals("get task for decline: Done")) {
            Task declinedTask = (Task) event.getMessage().getData();
            String requesterID = declinedTask.getRequester_id();
            System.out.println(declinedTask.getTask_id()+" taskId before msg to user");
            Platform.runLater(() -> {
                GuiCommon guiCommon = GuiCommon.getInstance();
                SendMessageToUser sendMessageToUser = (SendMessageToUser) guiCommon.displayNextScreen("SendMsgToUser.fxml",
                        "Send Decline Message", stage, false);
                sendMessageToUser.setRequesterID(requesterID);
                sendMessageToUser.setLoggedInManager(loggedInManager);
                sendMessageToUser.setTaskID(declinedTask.getTask_id());
                System.out.println(declinedTask.getTask_id());
            });
            System.out.println("before calling delete task in handle");
            deleteTask(declinedTask.getTask_id());  /*
            //call function to refresh list view
            int communityID = this.loggedInManager.getCommunityId();
            // refresh tasks
            getRequestedTasksForCommunity(communityID);*/

        }

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert BtnApprove != null : "fx:id=\"BtnApprove\" was not injected: check your FXML file 'RequestedTasks.fxml'.";
        assert BtnBack != null : "fx:id=\"BtnBack\" was not injected: check your FXML file 'RequestedTasks.fxml'.";
        assert BtnDecline != null : "fx:id=\"BtnDecline\" was not injected: check your FXML file 'RequestedTasks.fxml'.";
        assert requestedLV != null : "fx:id=\"pendingLV\" was not injected: check your FXML file 'RequestedTasks.fxml'.";
        assert showRequested != null : "fx:id=\"showPending\" was not injected: check your FXML file 'RequestedTasks.fxml'.";

        EventBus.getDefault().register(this);
    }
}