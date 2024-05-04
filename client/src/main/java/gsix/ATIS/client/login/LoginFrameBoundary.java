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


import gsix.ATIS.client.common.DeviceIdentifier;
import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.client.common.SosBoundary;
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
	private Button SoS_Btn;
  
    @FXML
    private Button loginButton;
    private String username;
    private String password;
	private String selectedRole="User";



	@FXML
	private ComboBox<String> roleComboBox;


	@FXML
	void OpenSosCall(ActionEvent event) {
		stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
		GuiCommon guiCommon = GuiCommon.getInstance();
		SosBoundary sosBoundary = (SosBoundary) guiCommon.displayNextScreen("SosWindow.fxml",
				"SoS Call", stage, false);  // Example for opening new screen
		String macID = DeviceIdentifier.getMACAddress();
		System.out.println("SOS Call button was clicked. Your device MAC Addess: "+macID);
		User unKnown = new User(macID, "community user","MAC Addess", "MAC Addess"
				, "MAC Addess", "MAC Addess", 0);
		sosBoundary.setRequester(unKnown);
	}

    @FXML 
	public void Login( ActionEvent event) throws IOException {

		stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
		// Set an event handler for the stage's close request
		stage.setOnCloseRequest(e -> {
			// Custom logic before the stage closes
			System.out.println("Stage is closing. Unregistering from EventBus.");

			// Unregister from EventBus to avoid memory leaks
			if (EventBus.getDefault().isRegistered(this)) {
				EventBus.getDefault().unregister(this);
			}
		});
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

			System.out.println("I am in handle tasks event before opening ANY PAGE WHATSOEVER");
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
								displayNextScreen("ManagerHomePage.fxml", "Manager Home Page", null, false);//reminder to return stage normal
						managerHomePageBoundary.setLoggedInUser(loggedInUser);
						stage.close();
						EventBus.getDefault().unregister(this);
					});

				}

			}


			else {
				//GuiCommon.popUp(loggedInUser.toString());
				Platform.runLater(() -> {
					//GuiCommon.popUp(loggedInUser.toString());

					GuiCommon guiCommon = GuiCommon.getInstance();

					//user entering as a user
					if (loggedInUser.getUser_type().equals("community user")) {
						System.out.println("Community user entering");
						UserHomePageBoundary userHomePageBoundary = (UserHomePageBoundary) guiCommon.displayNextScreen("UserHomePage.fxml",
								"Community User Home Page", null, false);  // Example for opening new screen
						userHomePageBoundary.setLoggedInUser(loggedInUser);
						stage.close();
						EventBus.getDefault().unregister(this);
					}
					else{
						//this handles case where Manager is trying to enter as a user
						UserHomePageBoundary userHomePageBoundary = (UserHomePageBoundary) guiCommon.displayNextScreen("UserHomePage.fxml",
								"Community User Home Page", null, false);  // Example for opening new screen
						userHomePageBoundary.setLoggedInUser(loggedInUser);
						stage.close();
						EventBus.getDefault().unregister(this);
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
		if (event.getMessage().getMessage().equals("login request: User already logged in")){
			//Show a pop up that tells user he is already logged in
			Platform.runLater(() -> {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Log in Error");
				alert.setHeaderText("Already Logged In");
				alert.setContentText("You are already logged in, make sure to close prior windows to log in again");
				alert.showAndWait();
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
		ObservableList<String> roles = FXCollections.observableArrayList("User", "Manager");
		roleComboBox.setItems(roles);

		// Set default value
		roleComboBox.setValue("User");

		// Listener to update selectedRole when ComboBox value changes
		roleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			selectedRole = newValue;
			System.out.println("Selected role changed to: " + selectedRole);
		});
		selectedRole="User";

		// Register with EventBus if not already registered
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}



//		// Set default value and listen for changes
//		roleComboBox.setValue("User");
//		roleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
//			selectedRole = newValue; // Update the selected role
//			System.out.println("Selected role changed to: " + selectedRole);
//		});
//		EventBus.getDefault().register(this);
		
	}

	public LoginFrameBoundary() {
		selectedRole="User";

	}

}
