package gsix.ATIS.client.login;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/*import client.CEMSClient;
import client.ClientUI;
import control_common.LoginController;*/
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;


import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.client.manager.ManagerHomePageBoundary;
import gsix.ATIS.client.user.UserHomePageBoundary;
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.User;
import gsix.ATIS.entities.UserType;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/*import entity.LoggedInUser;
import entity.Profession;
import logic.Msg;
import logic.MsgType;
import entity.User;
import entity.UserType;
import entity.Course;
import entity.Lecturer;*/

public class LoginFrameBoundary implements Initializable{

	private Stage stage;
	
	@FXML
	private AnchorPane window;

    @FXML
    private TextField UsernameField;

    @FXML
    private TextField PasswordField;
    
    @FXML
    private Label msgArea;

    @FXML
    private Button loginButton;
    private String username;
    private String password;
	private String selectedRole="User";



	@FXML
	private ComboBox<String> roleComboBox;
    ArrayList<String> userDetails = new ArrayList<String>();
    
    @FXML 
	public void Login( ActionEvent event) throws IOException {

		stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value

		username = UsernameField.getText();
    	password = PasswordField.getText();
    	if (username.trim().isEmpty()) {
    		loginMsg( "Username is a required field.");
    		return;
    	} 
		if (password.trim().isEmpty()) {
			 loginMsg("Password is a required field.");
			 return;
		}

		User userDetails = new User(username,password, selectedRole);
		LoginController.loginUser(userDetails);
		System.out.println("Hey");


		

	}
	@Subscribe
	public void handleTasksEvent(MessageEvent event) {
		Message loginMessage = event.getMessage();
		if (event.getMessage().getMessage().equals("login request: Done")) {
			User loggedInUser = (User) loginMessage.getData();
			System.out.println("I am in handle tasks event before opening userHomepage");
			System.out.println("Selected Role is : "+selectedRole);
			//check if user is manager or normal user
			if(selectedRole.equals("Manager")){

				if(loggedInUser.getUser_type().equals("community user")){
					//ADD APPROPRIATE CODE FOR A POP UP MESSAGE THAT TELLS USER THAT HE DOESNT HAVE THE RIGHT PERMISSIONS
					// User tried to log in as a manager but doesn't have the correct permissions
					System.out.println("it is a USER trying to enter as a MANAGER");
					Platform.runLater(() -> {
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setTitle("Access Denied");
						alert.setHeaderText("Permission Error");
						alert.setContentText("You do not have the correct permissions to access the manager's area.");
						alert.showAndWait();
					});
				}
				else {
					//else it means manager is logging in as manager
					Platform.runLater(() -> {
						GuiCommon guiCommon = GuiCommon.getInstance();
						System.out.println("Manager entering as Manager");
						ManagerHomePageBoundary managerHomePageBoundary = (ManagerHomePageBoundary) guiCommon.
								displayNextScreen("ManagerHomePage.fxml", "Manager Home Page", stage, true);
						managerHomePageBoundary.setLoggedInUser(loggedInUser);
					});
				}

			}


			else {
				//GuiCommon.popUp(loggedInUser.toString());
				Platform.runLater(() -> {
					//GuiCommon.popUp(loggedInUser.toString());

					GuiCommon guiCommon = GuiCommon.getInstance();
					if (loggedInUser.getUser_type().equals("community user")) {
						System.out.println("Community user entering");
						UserHomePageBoundary userHomePageBoundary = (UserHomePageBoundary) guiCommon.displayNextScreen("UserHomePage.fxml",
								"Community User Home Page", stage, true);  // Example for opening new screen
						userHomePageBoundary.setLoggedInUser(loggedInUser);
					} else if (loggedInUser.getUser_type().equals("manager") && selectedRole.equals("Manager")) {
						System.out.println("Manager entering as Manager");
						ManagerHomePageBoundary managerHomePageBoundary = (ManagerHomePageBoundary) guiCommon.
								displayNextScreen("ManagerHomePage.fxml", "Manager Home Page", stage, true);
						managerHomePageBoundary.setLoggedInUser(loggedInUser);
					}
					else{
						//this handles case where Manager is trying to enter as a user
						UserHomePageBoundary userHomePageBoundary = (UserHomePageBoundary) guiCommon.displayNextScreen("UserHomePage.fxml",
								"Community User Home Page", stage, true);  // Example for opening new screen
						userHomePageBoundary.setLoggedInUser(loggedInUser);
					}

				});
			}



	}
		if (event.getMessage().getMessage().equals("login request: Failed")) {
			String errMsg = (String) loginMessage.getData();
			Platform.runLater(() -> {
				msgArea.setText(errMsg);
			});
		}
	}
    
    public void loginMsg( String msg) {
    	msgArea.setText(msg);
    }
	// Method to handle ComboBox selection changes
	@FXML
	private void onRoleSelected() {
		selectedRole = roleComboBox.getValue();
		System.out.println("Selected Role: " + selectedRole);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {


		// Set default value and listen for changes
		roleComboBox.setValue("User");
		roleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			selectedRole = newValue; // Update the selected role
			System.out.println("Selected role changed to: " + selectedRole);
		});



		EventBus.getDefault().register(this);
		
	}
    
}
