import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This class controls the popup windows in the game.
 */
public class PopupController {

    @FXML private PasswordField password;
    @FXML private Button connectBtn;
    @FXML private Text message;
    private Stage stage = null;
    private Lobby lobby = null;
    private JoinLobbyController parent;

    /**
     * passwordEntered method registers the typed in password.
     */
    public void passwordEntered(){
        if (!password.getText().isEmpty())
            lobby.setPassword(password.getText());
        closeStage();
    }

    /**
     * setting the stage of this view
     * @param stage Stage object.
     * @param parent JoinLobbyController object.
     * @param lobby Lobby object.
     */
    public void setInitValues(JoinLobbyController parent, Stage stage, Lobby lobby) {
        this.parent = parent;
        this.stage = stage;
        this.lobby = lobby;
    }

    /**
     * Closes the stage of this view
     */
    public void closeStage() {
        if(stage!=null) {
            parent.connect(lobby);
            stage.close();
        }
    }
}
