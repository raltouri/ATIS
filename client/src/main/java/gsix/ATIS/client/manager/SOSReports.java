package gsix.ATIS.client.manager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import org.greenrobot.eventbus.EventBus;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SOSReports implements Initializable {

    @FXML // fx:id="barChart"
    private BarChart<?, ?> barChart; // Value injected by FXMLLoader

    @FXML // fx:id="checkBoxCommunity"
    private ComboBox<String> comboBoxCommunity; // Value injected by FXMLLoader

    @FXML // fx:id="endDate"
    private DatePicker endDate; // Value injected by FXMLLoader

    @FXML // fx:id="startDate"
    private DatePicker startDate; // Value injected by FXMLLoader

    private final String[] pickCommunity = {"My Community", "All Communities"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboBoxCommunity.getItems().addAll(pickCommunity);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert barChart != null : "fx:id=\"barChart\" was not injected: check your FXML file 'SOSReports.fxml'.";
        assert comboBoxCommunity != null : "fx:id=\"checkBoxCommunity\" was not injected: check your FXML file 'SOSReports.fxml'.";
        assert endDate != null : "fx:id=\"endDate\" was not injected: check your FXML file 'SOSReports.fxml'.";
        assert startDate != null : "fx:id=\"startDate\" was not injected: check your FXML file 'SOSReports.fxml'.";


        // Register with EventBus
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }
}
