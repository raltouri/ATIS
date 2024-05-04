package gsix.ATIS.client.manager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.client.TasksController;
import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.Task;
import gsix.ATIS.entities.User;
import javafx.application.Platform;
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

public class MemberOpenedTasks {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnBack"
    private Button btnBack; // Value injected by FXMLLoader

    @FXML // fx:id="openedLV"
    private ListView<String> openedLV; // Value injected by FXMLLoader

    private Stage stage;
    private User loggedInManager = null;
    private User communityMember = null;
    ArrayList<Task> memberOpenedTasksArrayList; // from DB
    ArrayList<String> memberOpenedTasksInfoString; // for listView

    public void setLoggedInUser(User loggedInManager) {
        this.loggedInManager = loggedInManager;
    }
    public void setCommunityMember(User communityMember) {
        this.communityMember = communityMember;
        getRequestedTasks(communityMember.getUser_id());
        if(openedLV.getItems().isEmpty()){
            showNoTasksToViewAlert();
        }
    }

    private void showNoTasksToViewAlert() {
        // Handle the case when no task is selected
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Select a task");
        alert.setHeaderText(null);
        alert.setContentText("You need to select a task");

        // Show the alert dialog
        alert.showAndWait();
    }

    @FXML
    void BackToCommunityMembersScreen(ActionEvent event) {
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        CommunityMembers communityMembers = (CommunityMembers) guiCommon.displayNextScreen("CommunityMembers.fxml",
                "Community Members", stage, true);  // Example for opening new screen
        communityMembers.setLoggedInUser(loggedInManager);
    }

    private void getRequestedTasks(String userId) {
        Message message = new Message(1, LocalDateTime.now(), "get requested tasks", userId);
        //System.out.println("MyTasksPage:before send to server *Get Requested Tasks* command");
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ArrayList<String> getOpenedTasksInfo(List<Task> tasks) {
        ArrayList<String> tasks_info = new ArrayList<>();
        for (Task task : tasks) {
            tasks_info.add("Task ID:" + task.getTask_id() + ", Help Request Description: " +
                    task.getRequested_operation() + ", Task Status: " + task.getStatus());
        }
        return tasks_info;
    }

    @Subscribe
    public void handleTasksEvent(MessageEvent event){
        Message handledMessage=event.getMessage();

        if(event.getMessage().getMessage().equals("get requested tasks: Done")) {
          /*  Task declinedTask = (Task) event.getMessage().getData();
            String requesterID = declinedTask.getRequester_id();
            System.out.println(declinedTask.getTask_id()+" taskId before msg to user");
            Platform.runLater(() -> {
                GuiCommon guiCommon = GuiCommon.getInstance();
                SendMessageToUser sendMessageToUser = (SendMessageToUser) guiCommon.displayNextScreen("SendMsgToUser.fxml",
                        "Send Decline Message", stage, false);
                sendMessageToUser.setRequesterID(requesterID);
                sendMessageToUser.setLoggedInManager(loggedInManager);
                sendMessageToUser.setTaskID(declinedTask.getTask_id());
                sendMessageToUser.setRequestedTasks(this);
                //System.out.println(declinedTask.getTask_id());

            });*/
            Platform.runLater(() -> {
                memberOpenedTasksArrayList = (ArrayList<Task>) event.getMessage().getData();
                memberOpenedTasksInfoString = (ArrayList<String>) getOpenedTasksInfo(memberOpenedTasksArrayList);
                openedLV.getItems().addAll(memberOpenedTasksInfoString);
            });

        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnBack != null : "fx:id=\"btnBack\" was not injected: check your FXML file 'MemberOpenedTasks.fxml'.";
        assert openedLV != null : "fx:id=\"openedLV\" was not injected: check your FXML file 'MemberOpenedTasks.fxml'.";

        EventBus.getDefault().register(this);

    }

 /*   @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }*/

}