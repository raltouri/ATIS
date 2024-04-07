package gsix.ATIS.client.login;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/*import client.CEMSClient;
import client.ClientUI;
import control_common.LoginController;*/


import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.Task;
import gsix.ATIS.entities.User;
import gsix.ATIS.entities.UserType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    ArrayList<String> userDetails = new ArrayList<String>();
    
    @FXML 
	public void Login( ActionEvent event) throws IOException {
    	
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
		

		User userDetails = new User(username,password, UserType.COMMUNITYUSER.toString());
		LoginController.loginUser(userDetails);

		/*String loginCase = LoginController.loginUser(userDetails);
//    	Msg msg1 = new Msg("login",MsgType.FROM_CLIENT,userDetails);
//		ClientUI.chat.accept(msg1);
		if (loginCase.equals("UserNotFound")) {
			loginMsg("No such user and password found");
			return;
		}
		if (loginCase.equals("AlreadyLoggedIn")) {
			loginMsg("User already logged in.");
			return;
		}
		else {
			switch (UserType.getType(loginCase)) {
			case Lecturer: {
				GuiCommon.getInstance().displayNextScreen("/gui_lecturer/lecturerMenu.fxml", "Hello Lecturer", event,true);
				break;
			}
			case Student: {
				GuiCommon.getInstance().displayNextScreen("/gui_student/studentMenu.fxml", "Hello Student", event,true);
				break;
			}
			case HeadDep: {
				GuiCommon.getInstance().displayNextScreen("/gui_headDep/headDepMenu.fxml", "Hello Head Department", event,true);
				break;
			}

			}
		}*/
		

	}
	@Subscribe
	public void handleTasksEvent(MessageEvent event) {
		Message loginMessage = event.getMessage();
		if (event.getMessage().getMessage().equals("login request: Done")) {
			User loggedInUser = (User) loginMessage.getData();

			//GuiCommon.popUp(loggedInUser.toString());
			Platform.runLater(() -> {
				GuiCommon.popUp(loggedInUser.toString());
			});
			/*List<Task> tasks = (List<Task>) event.getMessage().getData();
			List<String> tasks_info = getTasksInfo(tasks);
			System.out.println("handleTasksEvent");
			// Update ListView with received tasks
			Platform.runLater(() -> {
				tasks_Lv.getItems().clear(); // Clear existing items
				ObservableList<String> observableTasks = FXCollections.observableArrayList(tasks_info);
				tasks_Lv.setItems(observableTasks); // Add received tasks
			});
*/		}
	}
    
    public void loginMsg( String msg) {
    	msgArea.setText(msg);
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Platform.runLater(() -> {
			Stage stage = (Stage) window.getScene().getWindow();
			stage.setOnCloseRequest(event -> {
				/*event.consume(); // Consume the event to prevent automatic window closing
				LoginController.disconnect();
				stage.close();*/
			});
		});
		EventBus.getDefault().register(this);
		
	}
    
}
