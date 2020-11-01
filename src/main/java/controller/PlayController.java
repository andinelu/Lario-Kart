import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * This class controls the play window.
 */
public class PlayController {

    UIController parent;

    /**
     * joinButtonPressed changes window to join lobby window.
     */
    public void joinButtonPressed(){
        parent.changeScene(5);
    }

    /**
     * createButtonPressed changes window to create lobby window.
     */
    public void createButtonPressed(){
        parent.changeScene(6);
    }

    /**
     * backButtonPressed changes window to main menu window.
     */
    public void backButtonPressed(){ parent.changeScene(3);
    }

    /**
     * init method contains the logic behind the play screen.
     * @param contr reference to UIController.
     * @param loader the loader object.
     * @param stage the stage object.
     */
    public void init(UIController contr, FXMLLoader loader, Stage stage){
        parent = contr;

        try {
            loader = new FXMLLoader();
            loader.setController(this);
            loader.setLocation(parent.getClass().getResource("play.fxml"));
            AnchorPane anchor = loader.load();
            stage.setTitle("Lario Kart");
            stage.setScene(new Scene(anchor));
            stage.show();
        }
        catch(IOException e){
            System.out.println(e);
        }
    }
}
