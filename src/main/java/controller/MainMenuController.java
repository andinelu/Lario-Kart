import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * This class controls the main menu window.
 */
public class MainMenuController {

    private UIController parent;

    /**
     * playButtonPressed changes method window to play window.
     */
    public void playButtonPressed() {
        parent.changeScene(4);
    }

    /**
     * statisticsButtonPressed method changes window to statistics window.
     */
    public void statisticsButtonPressed() {
        parent.changeScene(9);
    }

    /**
     * highscoreButtonPressed method changes window to highscore window.
     */
    public void highscoreButtonPressed() {
        parent.changeScene(10);
    }

    /**
     * logoutButtonPressed method changes window to start up window.
     */
    public void logoutButtonPressed() {
        parent.changeScene(0);
    }

    /**
     * closeButtonAction method closes the application.
     */
    public void closeButtonAction(){
        Platform.exit();
    }

    /**
     * init method contains the logic behind the main menu screen.
     * @param contr reference to UIController.
     * @param loader the loader object.
     * @param stage the stage object.
     */
    public void init(UIController contr, FXMLLoader loader, Stage stage) {
        parent = contr;

        try {
            loader = new FXMLLoader();
            loader.setController(this);
            loader.setLocation(parent.getClass().getResource("mainMenu.fxml"));
            AnchorPane anchor = loader.load();
            stage.setTitle("Lario Kart");
            stage.setScene(new Scene(anchor));
            stage.show();
            stage.centerOnScreen();
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }
}
