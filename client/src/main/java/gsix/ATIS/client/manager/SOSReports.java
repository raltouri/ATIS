package gsix.ATIS.client.manager;

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
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SOSReports /*implements Initializable*/ {

    @FXML // fx:id="btnBack"
    private Button btnBack; // Value injected by FXMLLoader

    @FXML // fx:id="barChart"
    private BarChart<?, ?> barChart; // Value injected by FXMLLoader

    @FXML // fx:id="checkBoxCommunity"
    private ComboBox<String> comboBoxCommunity; // Value injected by FXMLLoader

    @FXML // fx:id="endDate"
    private DatePicker endDate; // Value injected by FXMLLoader

    @FXML // fx:id="startDate"
    private DatePicker startDate; // Value injected by FXMLLoader

    private final String[] pickCommunity = {"My Community", "All Communities"};

    private Stage stage;

    private User loggedInManager = null;

    public void setLoggedInUser(User loggedInManager) {
        this.loggedInManager = loggedInManager;
    }
    @FXML
    void backToManagerHomePage(ActionEvent event) {
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        ManagerHomePageBoundary managerHomePageBoundary = (ManagerHomePageBoundary) guiCommon.displayNextScreen("ManagerHomePage.fxml",
                "Manager Home Page", stage, true);  // Example for opening new screen
        managerHomePageBoundary.setLoggedInUser(loggedInManager);
    }

    /*@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }*/


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

        }

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert barChart != null : "fx:id=\"barChart\" was not injected: check your FXML file 'SOSReports.fxml'.";
        assert btnBack != null : "fx:id=\"btnBack\" was not injected: check your FXML file 'SOSReports.fxml'.";
        assert comboBoxCommunity != null : "fx:id=\"checkBoxCommunity\" was not injected: check your FXML file 'SOSReports.fxml'.";
        assert endDate != null : "fx:id=\"endDate\" was not injected: check your FXML file 'SOSReports.fxml'.";
        assert startDate != null : "fx:id=\"startDate\" was not injected: check your FXML file 'SOSReports.fxml'.";

        comboBoxCommunity.getItems().addAll(pickCommunity);

        // Register with EventBus
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }
}
