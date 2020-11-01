import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * This class controls the login window.
 */
public class LoginController {

    private UIController parent;

    @FXML TextField username;
    @FXML PasswordField password;
    @FXML Text message;

    /**
     * loginButtonPressed method changes window to main menu window if login is successful.
     */
    public void loginButtonPressed(){

        String trimmedUsername = username.getText().trim();

        if (trimmedUsername == "" || password.getText() == "") {
            System.out.println("Username or password cannot be empty");
            return;
        }

        if (parent.login(trimmedUsername, password.getText())) {
            System.out.println("Login successful");
            parent.changeScene(3);
        }
        else{
            System.out.println("Login failed");
        }
    }

    /**
     * backButtonPressed method change window to start up window.
     */
    public void backButtonPressed()
    {
        parent.changeScene(0);
    }

    /**
     * init method contains the logic behind the login screen.
     * @param parent reference to the UIController.
     * @param loader the loader object.
     * @param stage the stage object.
     */
    public void init(UIController parent, FXMLLoader loader, Stage stage) {
        this.parent = parent;

        try {
            loader = new FXMLLoader();
            loader.setController(this);
            loader.setLocation(parent.getClass().getResource("login.fxml"));
            AnchorPane anchor = loader.load();
            stage.setTitle("Lario Kart");
            stage.setScene(new Scene(anchor));
            stage.show();
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }
}
