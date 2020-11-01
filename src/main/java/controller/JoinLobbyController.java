import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.*;

/**
 * This class controls the join lobby window.
 */
public class JoinLobbyController {

    private UIController parent;
    private Stage primaryStage;
    private ArrayList<Lobby> lobbies;
    private final ObservableList<Lobby> data =
            FXCollections.observableArrayList();
    @FXML private ToggleButton toggle;
    @FXML private TableView<Lobby> tableView;
    @FXML private TableColumn<Lobby, String> lobbyName;
    @FXML private TableColumn<Lobby, String> connected;

    /**
     * initTable method sets the table with lobbies.
     */
    private void initTable(){
        lobbyName.setCellValueFactory(new PropertyValueFactory<>("name"));
        connected.setCellValueFactory(new PropertyValueFactory<>("connected"));
        data.addAll(parent.findLobbies(false));
        tableView.setItems(data);
    }

    /**
     * toggleButtonPressed method changes the lobbies shown from public to private, and from private to public.
     */
    public void toggleButtonPressed(){
        if (toggle.getText().equals("Public")){
            toggle.setText("Private");
            updateLobbyList();
        }
        else if (toggle.getText().equals("Private")){
            toggle.setText("Public");
            updateLobbyList();
        }
    }

    /**
     * updateLobbyList method updates the shown lobbies.
     */
    public void updateLobbyList(){
        ArrayList<Lobby> lobbies = null;

        tableView.getItems().clear();
        tableView.refresh();
        if(toggle.getText().equals("Public")){
            lobbies = parent.findLobbies(true);
        }
        else if(toggle.getText().equals("Private")){
            lobbies = parent.findLobbies(false);
        }

        if (lobbies == null) return;

        data.addAll(lobbies);
        tableView.setItems(data);
    }

    /**
     * joinButtonPressed method connects lobby if public. Shows popup window if private.
     */
    public void joinButtonPressed(){
        if(tableView.getSelectionModel().getSelectedItem() != null) {
            Lobby lobby = tableView.getSelectionModel().getSelectedItem();
            if(!lobby.isPublic()){
                showPopupWindow(lobby);
            }
            else
                connect(lobby);
        }
    }

    /**
     * connect method connects an user to a lobby.
     * @param lobby the lobby object the user is connecting.
     */
    public void connect(Lobby lobby){
        if (parent.joinLobby(lobby, false)) {
            System.out.println("Joined lobby");
            parent.changeScene(7);
        }
        else{
            System.out.println("Couldn't connect");
            //Message saying wrong password or connection error
        }
    }

    /**
     * backButtonPressed method changes window to play.
     */
    public void backButtonPressed(){
        parent.changeScene(4);
    }

    /**
     * showPopupWindow method shows popup window allowing the user to type password.
     * @param lobby the lobby object the user tries to connect.
     */
    private void showPopupWindow(Lobby lobby) {
        PopupController popupController = new PopupController();
        Parent layout;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("popup.fxml"));
            loader.setController(popupController);
            layout = loader.load();
            Scene scene = new Scene(layout);
            Stage popupStage = new Stage();
            popupController.setInitValues(this, popupStage, lobby);
            if(this.parent!=null) {
                popupStage.initOwner(primaryStage);
            }
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.setScene(scene);
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * init method contains the logic behind the join lobby screen.
     * @param contr reference to UIController.
     * @param loader the loader object.
     * @param stage the stage object.
     */
    public void init(UIController contr, FXMLLoader loader, Stage stage) {
        parent = contr;
        primaryStage = stage;

        try {
            loader = new FXMLLoader();
            loader.setController(this);
            loader.setLocation(parent.getClass().getResource("joinLobby.fxml"));
            AnchorPane anchor = loader.load();
            stage.setTitle("Lario Kart");
            stage.setScene(new Scene(anchor));
            initTable();
            stage.show();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
