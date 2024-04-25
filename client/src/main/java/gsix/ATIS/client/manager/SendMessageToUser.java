package gsix.ATIS.client.manager;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.mysql.cj.ServerPreparedQuery;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SendMessageToUser {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="BtnBack"
    private Button BtnBack; // Value injected by FXMLLoader

    @FXML // fx:id="messageTB"
    private TextField messageTB; // Value injected by FXMLLoader

    @FXML // fx:id="BtnSend"
    private Button BtnSend; // Value injected by FXMLLoader
    @FXML // fx:id="taskIdTV"
    private Text taskIdTV; // Value injected by FXMLLoader

    private Stage stage;
    private User loggedInManager;
    private String requesterID;
    private int taskID;
    public void setTaskID(int taskID) {
        this.taskID = taskID;
        taskIdTV.setText(taskID+"");
    }



    public void setLoggedInManager(User loggedInUser) {
        loggedInManager = loggedInUser;
    }

    public void setRequesterID(String requesterID) {
        this.requesterID = requesterID;
    }

    @FXML
    void BackToRequestedTasksScreen(ActionEvent event) {
       /* stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        RequestedTasks requestedTasks = (RequestedTasks) guiCommon.displayNextScreen("RequestedTasks.fxml",
                "Manager Home Page", stage, true);  // Example for opening new screen
        requestedTasks.setLoggedInUser(loggedInManager);*/
    }

    private void showEmptyMessageBoxAlert() {
        // Handle the case when no task is selected
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fill in a message");
        alert.setHeaderText(null);
        alert.setContentText("can't send empty message!");

        // Show the alert dialog
        alert.showAndWait();
    }
    @FXML
    void SendMessageForUser(ActionEvent event) {
        if(messageTB.getText().isEmpty()){
            showEmptyMessageBoxAlert();
        }
    }
    @Subscribe
    public void handleTasksEvent(MessageEvent event){
        Message handledMessage=event.getMessage();

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert BtnBack != null : "fx:id=\"back_Btn\" was not injected: check your FXML file 'SendMsgToUser.fxml'.";
        assert messageTB != null : "fx:id=\"message_TF\" was not injected: check your FXML file 'SendMsgToUser.fxml'.";
        assert BtnSend != null : "fx:id=\"send_Btn\" was not injected: check your FXML file 'SendMsgToUser.fxml'.";
        assert taskIdTV != null : "fx:id=\"taskIdTV\" was not injected: check your FXML file 'SendMsgToUser.fxml'.";


        //System.out.println(taskID+" inside send msg to user initialize" );
        EventBus.getDefault().register(this);
    }

}
