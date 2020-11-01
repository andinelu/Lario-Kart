import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This class controls the game over window.
 */
public class GameOverController {

    private GameController parent;
    private Stage stage;
    @FXML private Text first;
    @FXML private Text second;
    @FXML private Text third;
    @FXML private Text fourth;


    /**
     * setInitValues sets the global values.
     * @param stage window in the application.
     * @param parent Gamecontroller object
     * @param players array with players in the game.
     */
    public void setInitValues(GameController parent, Stage stage, String[] players) {
        this.parent = parent;
        this.stage = stage;

        populateTable(players);
    }

    /**
     * populateTable method fills a scoreboard with which players are ranked where.
     * @param players array with player in the game.
     */
    public void populateTable(String[] players){
        first.setText("1. " + players[0]);
        second.setText("2. " + players[1]);
        if (players.length > 2) {
            third.setText("3. " + players[2]);
            if (players.length == 4) {
                fourth.setText("4. " + players[3]);
            }
        }
    }

    /**
     * onOkButtonClicked method closes the game after button pressed.
     */
    public void onOKButtonClicked(){
        if(stage!=null) {
            parent.returnToMain();
            stage.close();
        }
    }
}
