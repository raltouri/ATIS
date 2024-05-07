package gsix.ATIS.client.manager;

import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.entities.CommunityMessage;
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.SosRequest;
import gsix.ATIS.entities.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class SOSReports implements Initializable {

    @FXML // fx:id="btnBack"
    private Button btnBack; // Value injected by FXMLLoader

    @FXML // fx:id="barChart"
    private BarChart<String, Number> barChart; // Value injected by FXMLLoader

    @FXML // fx:id="checkBoxCommunity"
    private ComboBox<String> comboBoxCommunity; // Value injected by FXMLLoader

    @FXML // fx:id="endDate"
    private DatePicker endDate; // Value injected by FXMLLoader

    @FXML // fx:id="startDate"
    private DatePicker startDate; // Value injected by FXMLLoader

    private final String[] pickCommunity = {"My Community", "All Communities"};

    private Stage stage;

    private User loggedInManager = null;
    @FXML
    private Button show_Btn;

    @FXML
    private void onGenerateReport() {
        LocalDate start = startDate.getValue();
        LocalDate end = endDate.getValue();
        LocalDate today = LocalDate.now(); // Get today's date

        // Validate dates
        if (start == null || end == null) {
            showInvalidDateAlert("Please select both start and end dates.");
            return;
        }

        if (start.isAfter(end)) {
            showInvalidDateAlert("Start date cannot be after end date.");
            return;
        }
        // Check if start date is in the future
        if (start.isAfter(today)) {
            showInvalidDateAlert("Start date cannot be in the future.");
            return;
        }

        // Check if end date is in the future
        if (end.isAfter(today)) {
            showInvalidDateAlert("End date cannot be in the future.");
            return;
        }
        String selectedCommunity = comboBoxCommunity.getValue();

        Message message;

        if ("My Community".equals(selectedCommunity)) {
            // Fetch reports only for the manager's community
            message = new Message(
                    1,
                    LocalDateTime.now(),
                    "get sos requests for community between dates",
                    new Object[]{loggedInManager.getCommunityId(), start, end}
            );
        } else {
            // Fetch reports for all communities
            message = new Message(
                    1,
                    LocalDateTime.now(),
                    "get sos requests for all communities between dates",
                    new LocalDate[]{start, end}
            );
        }

        try {
            SimpleClient.getClient("", 0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // Function to show an alert for invalid date selections
    private void showInvalidDateAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Date Selection");
        alert.setHeaderText("Incorrect Dates");
        alert.setContentText(message);

        alert.showAndWait(); // Displays the alert and waits for user acknowledgment
    }

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboBoxCommunity.getItems().addAll(pickCommunity);
        assert barChart != null : "fx:id=\"barChart\" was not injected: check your FXML file 'SOSReports.fxml'.";
        assert btnBack != null : "fx:id=\"btnBack\" was not injected: check your FXML file 'SOSReports.fxml'.";
        assert comboBoxCommunity != null : "fx:id=\"checkBoxCommunity\" was not injected: check your FXML file 'SOSReports.fxml'.";
        assert endDate != null : "fx:id=\"endDate\" was not injected: check your FXML file 'SOSReports.fxml'.";
        assert startDate != null : "fx:id=\"startDate\" was not injected: check your FXML file 'SOSReports.fxml'.";


        // Register with EventBus
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
//        assert barChart != null : "fx:id=\"barChart\" was not injected: check your FXML file 'SOSReports.fxml'.";
//        assert btnBack != null : "fx:id=\"btnBack\" was not injected: check your FXML file 'SOSReports.fxml'.";
//        assert comboBoxCommunity != null : "fx:id=\"checkBoxCommunity\" was not injected: check your FXML file 'SOSReports.fxml'.";
//        assert endDate != null : "fx:id=\"endDate\" was not injected: check your FXML file 'SOSReports.fxml'.";
//        assert startDate != null : "fx:id=\"startDate\" was not injected: check your FXML file 'SOSReports.fxml'.";
//
//
//        // Register with EventBus
//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }

    }
    public void onSOSDataReceived(List<SosRequest> sosRequests) {
        // Wrap the update in Platform.runLater() to ensure it runs on the FX application thread
        Platform.runLater(() -> {
            // Process the received SOS requests
            Map<LocalDate, Integer> dailySOSCount = new HashMap<>();

            for (SosRequest sos : sosRequests) {
                LocalDate date = sos.getTime().toLocalDate(); // Convert to LocalDate
                dailySOSCount.put(date, dailySOSCount.getOrDefault(date, 0) + 1);
            }

            // Update the BarChart
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("SOS Requests");

            List<String> categories = new ArrayList<>(); // Collect all dates as strings
            for (Map.Entry<LocalDate, Integer> entry : dailySOSCount.entrySet()) {
                System.out.println("Date is : "+entry.getKey().toString()+" Values is: "+entry.getValue());
                series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
                categories.add(entry.getKey().toString());
            }
            barChart.setPrefWidth(800);
            barChart.setPrefHeight(350);
            CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();


            xAxis.setTickLength(1); // Ensure there's a tick for every category (date)
            //Force showing the labels
            xAxis.setAutoRanging(false);
            xAxis.setCategories(FXCollections.observableArrayList(categories)); // Set the X-axis categories explicitly
            // Clear existing data and add the new series
            barChart.getData().clear();
            barChart.getData().add(series);
        });
    }
    @Subscribe
    public void handleTasksEvent(MessageEvent event) {
        Message handledMessage = event.getMessage();

        if(handledMessage.getMessage().equals("SOS data retrieval for all communities: Done")){
            System.out.println("SOS DATA is here");
            List<SosRequest> sosRequests=(List<SosRequest>)handledMessage.getData();
            onSOSDataReceived(sosRequests);
        }
        if(handledMessage.getMessage().equals("SOS data retrieval for community: Done")){
            List<SosRequest> sosRequests=(List<SosRequest>)handledMessage.getData();
            onSOSDataReceived(sosRequests);
        }
    }

}
