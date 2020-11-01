import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * This class controls the lobby window.
 */
public class LobbyController {

    private UIController parent;
    private ObservableList<String> items;

    @FXML private ListView playerList;
    @FXML private Button startGameButton;

    /**
     * updatePlayerList method updates the players window on screen.
     * @param players array with players showing on window.
     */
    public void updatePlayerList(String[] players){
        if (players.length == 0) return;
        Platform.runLater(()->{
            playerList.getItems().clear();
            items = playerList.getItems();
            items.addAll(players);
        });

    }

    /**
     * becomeHost method makes the start game button available for the host.
     */
    public void becomeHost(){
        startGameButton.setDisable(false);
    }

    /**
     * leaveButtonPressed method disconnects the player.
     */
    public void leaveLobbyButtonPressed(){
        parent.disconnect();
    }

    /**
     * startGameButtonPressed method starts the game if there are more than one player.
     */
    public void startGameButtonPressed(){
        if (playerList.getItems().size() <= 1)
            return;
        parent.gameStart();
    }

    /**
     * init method contains the logic behind the lobby screen.
     * @param parent reference to UIController.
     * @param loader the loader object.
     * @param stage the stage object.
     * @param isHost boolean if host. True = is host. False = not host.
     */
    public void init(UIController parent, FXMLLoader loader, Stage stage, boolean isHost) {
        this.parent = parent;
        items = FXCollections.observableArrayList();
        try {
            loader = new FXMLLoader();
            loader.setController(this);
            loader.setLocation(parent.getClass().getResource("lobby.fxml"));
            AnchorPane anchor = loader.load();
            stage.setTitle("Lario Kart");
            stage.setScene(new Scene(anchor));
            startGameButton.setDisable(!isHost);
            stage.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
