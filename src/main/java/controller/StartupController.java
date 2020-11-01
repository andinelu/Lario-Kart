import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * This class controls the start up scene window.
 */
public class StartupController {

    UIController parent;

    /**
     * loginButtonPressed method changes window to login window.
     */
    public void loginButtonPressed(){
        parent.changeScene(1);
    }

    /**
     * registerButtonPressed method changes window to register window.
     */
    public void registerButtonPressed(){
        parent.changeScene(2);
    }

    /**
     * closeButtonAction method closes the application.
     */
    public void closeButtonAction(){
        Platform.exit();
    }

    /**
     * init method contains the logic behind the startup screen.
     * @param contr reference to UIController.
     * @param loader the loader object.
     * @param stage the stage object.
     */
    public void init(UIController contr, FXMLLoader loader, Stage stage){
        parent = contr;
        try {
            loader = new FXMLLoader();
            loader.setController(this);
            loader.setLocation(parent.getClass().getResource("startup.fxml"));
            AnchorPane anchor = loader.load();
            stage.setTitle("Lario Kart");
            stage.setScene(new Scene(anchor));
            stage.show();
        }
        catch(IOException e){
        }
    }
}
