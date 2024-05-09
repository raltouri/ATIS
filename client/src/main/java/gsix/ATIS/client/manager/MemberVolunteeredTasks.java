package gsix.ATIS.client.manager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

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
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MemberVolunteeredTasks {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnBack"
    private Button btnBack; // Value injected by FXMLLoader

    @FXML // fx:id="volunteeredLV"
    private ListView<String> volunteeredLV; // Value injected by FXMLLoader

    private Stage stage;
    private User loggedInManager = null;
    private String communityMemberID = null;
    ArrayList<Task> memberVolunteeredTasksArrayList; // from DB
    ArrayList<String> memberVolunteeredTasksInfoString; // for listView

    public void setLoggedInUser(User loggedInManager) {
        this.loggedInManager = loggedInManager;
    }

    public void setCommunityMember(String communityMemberID) {
        this.communityMemberID = communityMemberID;
        getVolunteeredTasks(communityMemberID);
    }

    private void showNoTasksToViewAlert() {
        // Handle the case when no task is selected
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("No Available Tasks");
        alert.setHeaderText(null);
        alert.setContentText("There Are No Tasks To View");

        // Show the alert dialog
        alert.showAndWait();
    }

    @FXML
    public void BackToCommunityMembersScreen(ActionEvent event) {
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        /*CommunityMembers communityMembers = (CommunityMembers) guiCommon.displayNextScreen("CommunityMembers.fxml",
                "Community Members", stage, true);  // Example for opening new screen
        communityMembers.setLoggedInUser(loggedInManager);
        EventBus.getDefault().unregister(this);*/
        stage.close();
    }

    private void getVolunteeredTasks(String userId) {
        Message message = new Message(1, LocalDateTime.now(), "get volunteered tasks", userId);
        //System.out.println("MyTasksPage:before send to server *Get Volunteered Tasks* command");
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Subscribe
    public void handleTasksEvent(MessageEvent event){
        Message handledMessage=event.getMessage();
        if(event.getMessage().getMessage().equals("get volunteered tasks: Done")){
           /* List<Task> communityTasks=(List<Task>) event.getMessage().getData();
            //System.out.println("I am handling the tasks for community being brought back from eventbus in Volunteer class");
            List<String> tasks_info = TasksController.getTasksInfo(communityTasks);
            //System.out.println(tasks_info);
            // Update ListView with received tasks
            Platform.runLater(() -> {
                requestedLV.getItems().clear(); // Clear existing items
                ObservableList<String> observableTasks = FXCollections.observableArrayList(tasks_info);
                requestedLV.setItems(observableTasks); // Add received tasks
            });*/
            memberVolunteeredTasksArrayList = (ArrayList<Task>) event.getMessage().getData();
            if(!memberVolunteeredTasksArrayList.isEmpty()){
                Platform.runLater(() -> {
                    memberVolunteeredTasksInfoString = (ArrayList<String>) TasksController.getTasksInfo(memberVolunteeredTasksArrayList);
                    volunteeredLV.getItems().addAll(memberVolunteeredTasksInfoString);
                });
            }else{
                Platform.runLater(this::showNoTasksToViewAlert);
            }

        }

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnBack != null : "fx:id=\"btnBack\" was not injected: check your FXML file 'MemberVolunteeredTasks.fxml'.";
        assert volunteeredLV != null : "fx:id=\"volunteeredLV\" was not injected: check your FXML file 'MemberVolunteeredTasks.fxml'.";

        EventBus.getDefault().register(this);

    }


/*    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }*/

}

