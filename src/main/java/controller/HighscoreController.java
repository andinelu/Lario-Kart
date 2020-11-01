import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * This class controls the highscore window.
 */
public class HighscoreController {

    private UIController parent;
    private final ObservableList<Score> data = FXCollections.observableArrayList();
    private SortedList<Score> sortedData;
    @FXML private TableView<Score> tableView;
    @FXML private TableColumn<Score, String> username;
    @FXML private TableColumn<Score, String> gameswon;

    /**
     * initTable method sets the table with highscores.
     */
    private void initTable() {
        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        gameswon.setCellValueFactory(new PropertyValueFactory<>("gamesWon"));
        data.addAll(parent.getHighscores());
        sortedData = new SortedList<>(data);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
        tableView.getSortOrder().add(gameswon);
        gameswon.setSortable(false);
        username.setSortable(false);
    }

    /**
     * backButtonPressed method changes window to main menu window.
     */
    public void backButtonPressed() {
        parent.changeScene(3);
    }

    /**
     * Init method contains the logic behind the highscore screen.
     * @param parent reference to UIController.
     * @param loader the loader object.
     * @param stage the stage object.
     */
    public void init(UIController parent, FXMLLoader loader, Stage stage) {
        this.parent = parent;

        try {
            loader = new FXMLLoader();
            loader.setController(this);
            loader.setLocation(parent.getClass().getResource("highscore.fxml"));
            AnchorPane anchor = loader.load();
            stage.setTitle("Lario Kart");
            stage.setScene(new Scene(anchor));
            stage.show();
            initTable();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
