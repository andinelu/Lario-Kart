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
 * This class contols the register window.
 */
public class RegisterController {

    private UIController parent;

    @FXML TextField username;
    @FXML PasswordField initial;
    @FXML PasswordField repeat;
    @FXML Text message;

    /**
     * ConfirmButtonPressed method contains the logic behind when pressing the register button.
     * It checks if the username and password is valid.
     */
    public void confirmButtonPressed(){
        String trimmedUsername = username.getText().trim();

        if (!initial.getText().equals(repeat.getText())) {
            System.out.println("Passwords do not match");
            return;
        }
        else if (trimmedUsername == "" || initial.getText() == "") {
            System.out.println("Username or password cannot be empty");
            return;
        }

        if (parent.register(trimmedUsername, initial.getText())) {
            System.out.println("Register successful");
            parent.changeScene(1);
        }
        else{
            System.out.println("Register failed");
        }
    }

    /**
     * backButtonPressed method changes window to start up window.
     */
    public void backButtonPressed(){
        parent.changeScene(0);
    }

    /**
     * init method contains the logic behind the register screen.
     * @param parent reference to UIController.
     * @param loader the loader object.
     * @param stage Stage object.
     */
    public void init(UIController parent, FXMLLoader loader, Stage stage) {
        this.parent = parent;

        try {
            loader = new FXMLLoader();
            loader.setController(this);
            loader.setLocation(parent.getClass().getResource("register.fxml"));
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
