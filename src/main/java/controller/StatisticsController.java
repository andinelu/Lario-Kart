import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * This class controls the statistics window.
 */
public class StatisticsController {

    private UIController parent;
    @FXML private Text gamesPlayed;
    @FXML private Text gamesWon;
    @FXML private Text winp;

    /**
     * initStatistics method sets the statistics.
     */
    private void initStatistics(){
        int[] statistics = parent.getPersonalStats();
        gamesPlayed.setText(Integer.toString(statistics[0]));
        gamesWon.setText(Integer.toString(statistics[1]));
        winp.setText(statistics[2] + "%");
    }

    /**
     * backButtonPressed method changes window to main menu.
     */
    public void backButtonPressed(){
        parent.changeScene(3);
    }

    /**
     * init method is the logic behind the statistics window.
     * @param parent reference to UI controller.
     * @param loader the loader object.
     * @param stage the stage object.
     */
    public void init(UIController parent, FXMLLoader loader, Stage stage) {
        this.parent = parent;

        try {
            loader = new FXMLLoader();
            loader.setController(this);
            loader.setLocation(parent.getClass().getResource("statistics.fxml"));
            AnchorPane anchor = loader.load();
            stage.setTitle("Lario Kart");
            stage.setScene(new Scene(anchor));
            stage.show();
            initStatistics();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
