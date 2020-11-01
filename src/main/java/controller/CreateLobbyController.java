import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * This class controls the create lobby window.
 */
public class CreateLobbyController {

    private UIController parent;
    @FXML private PasswordField password;
    @FXML private TextField lobbyName;
    @FXML private Spinner spinner;

    /**
     * createButtonPressed method creates the lobby.
     */
    public void createButtonPressed() {
        if (password.getText() == ""){
            System.out.println("Password cannot be empty");
            return;
        }

        System.out.println("Lobby name: " + lobbyName.getText() + "\nPassword: " + password.getText() + "\nNumber of players: " +  spinner.getValue());

        Lobby lobby = new Lobby(0, lobbyName.getText(), (int)spinner.getValue(), 1, "");
        if (!password.getText().isEmpty())
            lobby.setPassword(password.getText());

        if (parent.joinLobby(lobby, true)) {
            System.out.println("Lobby created");
            parent.changeScene(7);
        }
        else{
            System.out.println("Failed to create lobby");
        }
    }

    /**
     * backButtonPressed method changes window to play window.
     */
    public void backButtonPressed(){
        parent.changeScene(4);
    }

    /**
     * privateChecked method changes between public and private lobby.
     */
    public void privateChecked(){
        if(password.isEditable()){
            password.clear();
            password.setEditable(false);
            password.setDisable(true);
        }
        else{
            password.setEditable(true);
            password.setDisable(false);
        }
    }

    /**
     * init method contains the logic behind the game screen.
     * @param parent reference to UIController.
     * @param loader the loader object.
     * @param stage the stage object.
     */
    public void init(UIController parent, FXMLLoader loader, Stage stage) {
        this.parent = parent;
        try {
            loader = new FXMLLoader();
            loader.setController(this);
            loader.setLocation(parent.getClass().getResource("createLobby.fxml"));
            AnchorPane anchor = loader.load();
            stage.setTitle("Lario Kart");
            stage.setScene(new Scene(anchor));
            stage.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
