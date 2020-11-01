import javafx.stage.Stage;

/**
 * Class to control the dice rolls.
 */
public class DiceController {

    private GameController parent;
    private Stage stage;

    /**
     * onDiceRolled method runs an update on the player update after dice thrown.
     */
    public void onDiceRolled(){
        if(stage!=null) {
            parent.onDiceRolled();
            stage.close();
        }
    }

    /**
     * setInitValues sets the stage of this view
     * @param parent reference to GameController object.
     * @param stage the stage object.
     */
    public void setInitValues(GameController parent, Stage stage) {
        this.parent = parent;
        this.stage = stage;
    }
}
