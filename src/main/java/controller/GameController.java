import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.Parent;
import javafx.stage.StageStyle;

/**
 * Class that controls the main game functions
 */
public class GameController {

    UIController parent;
    Stage stage;
    @FXML private GridPane gridPane;
    @FXML private ListView updateWindow;
    @FXML private ObservableList<Text> updateMessages;
    private Node[] players;
    private PlayerUpdate currentTurn;
    Timer timer;

    /**
     * init method contains the logic behind the game screen.
     * @param parent reference to UIController.
     * @param loader the loader object.
     * @param stage the stage object.
     * @param playerCount amount of player in the game.
     */
    public void init(UIController parent, FXMLLoader loader, Stage stage, int playerCount) {
        this.parent = parent;
        this.stage = stage;

        try {
            loader = new FXMLLoader();
            loader.setController(this);
            loader.setLocation(parent.getClass().getResource("game.fxml"));
            AnchorPane anchor = loader.load();
            stage.setTitle("Lario Kart");
            stage.setScene(new Scene(anchor));
            stage.show();
            stage.centerOnScreen();
        } catch (IOException e) {
            System.out.println(e);
        }

        initBoard(playerCount);
    }

    /**
     * initBoard method sets the gaming board.
     * @param playerCount is the amount of players in the game.
     */
    private void initBoard(int playerCount){
        Image defaultImage = new Image("defaultTile.png");
        Image startImage = new Image("startTile.jpg");
        Image powerupImage = new Image("powerupTile.png");
        Image deadImage = new Image("deadTile.jpg");

        initTiles(defaultImage, BoardDefaults.getBoardTiles());
        initTiles(startImage, new Point[] {BoardDefaults.getStartLocation()});
        initTiles(powerupImage, BoardDefaults.getPowerupTiles());
        initTiles(deadImage, BoardDefaults.getDeadTiles());

        initPlayers(playerCount);
    }

    /**
     * initTiles method sets the different tiles on the board.
     * @param tileImage image of the tile used.
     * @param locations which spot on the board in the tile is at.
     */
    private void initTiles(Image tileImage, Point[] locations){
        ImageView node;
        for (Point location : locations){
            node = new ImageView();
            node.setImage(tileImage);
            node.setFitWidth(50);
            node.setFitHeight(50);
            gridPane.add(node, location.getX(), location.getY());
        }
    }

    /**
     * initPlayers method sets the visuals of each player.
     * @param count amount of players in the game.
     */
    private void initPlayers(int count){
        players = new Node[4];
        Rectangle player;
        Color[] colours = new Color[]{Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE};
        for (int i = 0; i < count; i++){
            player = new Rectangle();
            player.setWidth(25);
            player.setHeight(25);
            player.setTranslateX((i/2)*25);
            player.setTranslateY((i%2 == 0) ? -12.5f : 12.5f);
            player.toFront();
            player.setFill(colours[i]);
            players[i] = player;
            gridPane.add(player, BoardDefaults.getStartLocation().getX(), BoardDefaults.getStartLocation().getY());
        }
    }

    /**
     * roll method controls the roll window giving the players chance to roll themselves.
     * @param update an player update to the players turn after thrown dice.
     */
    public void roll(PlayerUpdate update){
        currentTurn = update;

        DiceController diceController = new DiceController();
        Parent layout;
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("dice.fxml"));
            loader.setController(diceController);
            layout = loader.load();
            Scene scene = new Scene(layout);
            Stage diceStage = new Stage();
            diceController.setInitValues(this, diceStage);
            if(this.parent!= null){
                diceStage.initOwner(stage);
            }
            diceStage.initModality(Modality.WINDOW_MODAL);
            diceStage.initStyle(StageStyle.UNDECORATED);
            diceStage.setScene(scene);

            timer = new Timer();
            timer.schedule(new TimerTask() {
                                   public void run(){
                                       System.out.println("Dice timeout");
                                       Platform.runLater(()-> closeDiceOnTimeout(diceStage));
                                   }}, 10000);


            diceStage.showAndWait();
        }
        catch (IOException e){
            System.out.println(e);
        }
    }

    /**
     * closeDiceOnTimeout method closes the window when enough time has passed.
     * @param diceStage window giving the player the chance to throw.
     */
    public void closeDiceOnTimeout(Stage diceStage){
        diceStage.close();
        onDiceRolled();
    }

    /**
     * onDiceRolled method cancels the timer, and updates the player.
     */
    public void onDiceRolled(){
        timer.cancel();
        parent.onDiceRolled(currentTurn);
    }

    /**
     * movePlayer method moves the player after thrown dice.
     * @param update playerUpdate object.
     */
    public void movePlayer(PlayerUpdate update){
        GridPane.setColumnIndex(players[update.getUser()], BoardDefaults.getBoardTiles()[update.getLocation()].getX());
        GridPane.setRowIndex(players[update.getUser()], BoardDefaults.getBoardTiles()[update.getLocation()].getY());

        displayMessage(update.getUsername() + " rolled " + update.getDiceRoll());
        displayMessage(update.getMsg());
    }

    /**
     * removePlayer method removes a player from the running game.
     * @param username username of the player who's getting removed.
     * @param order which spot in the order the player had.
     */
    public void removePlayer(String username, int order){
        gridPane.getChildren().remove(players[order]);

        Node[] temp = new Node[players.length - 1];
        boolean playerFound = false;
        for (int i = 0; i < players.length; i++) {
            if (i == order) {
                playerFound = true;
                continue;
            }
            if (playerFound) temp[i - 1] = players[i];
            else temp[i] = players[i];
        }

        players = temp;

        displayMessage(username + " has disconnected from the game!");
    }

    /**
     * displayMessage method writes a message in the chat window.
     * @param message message that will be written in the chat window.
     */
    public void displayMessage(String message){
        if (message == "" || message == null) return;

        Text text = new Text(message);
        text.wrappingWidthProperty().bind(updateWindow.widthProperty().subtract(15));
        updateMessages = updateWindow.getItems();
        updateMessages.add(text);

        updateWindow.setItems(updateMessages);

        updateWindow.scrollTo(updateMessages.size()-1);
    }

    /**
     * gameOver method ends the game.
     * @param users array of users in the game.
     */
    public void gameOver(String[] users){
        GameOverController gameOverController = new GameOverController();
        Parent layout;
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("gameOver.fxml"));
            loader.setController(gameOverController);
            layout = loader.load();
            Scene scene = new Scene(layout);
            Stage gameOverStage = new Stage();
            gameOverController.setInitValues(this, gameOverStage, users);
            if(this.parent!= null){
                gameOverStage.initOwner(stage);
            }
            gameOverStage.initModality(Modality.WINDOW_MODAL);
            gameOverStage.initStyle(StageStyle.UNDECORATED);
            gameOverStage.setScene(scene);

            Timer timer = new Timer();
            Platform.runLater(new Thread(()->timer.schedule(new TimerTask() {
                public void run(){
                    closeDiceOnTimeout(gameOverStage);
                }}, 10000)));


            gameOverStage.showAndWait();
        }
        catch (IOException e){
            System.out.println(e);
        }
    }

    /**
     * returnToMain method changes the window to main menu window.
     */
    public void returnToMain(){
        parent.changeScene(3);
    }
}
