import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.util.*;

/**
 * This class manages the program's user interface.
 */
public class UIController {

    Stage stage;
    AppController parent;
    FXMLLoader loader;
    Object controller;

    /**
     * Class constructor.
     * @param primaryStage the stage object.
     * @param parent reference to AppController.
     */
    public UIController(Stage primaryStage, AppController parent){
        start(primaryStage, parent);
    }

    /**
     * start method starts the game.
     * @param primaryStage the stage object.
     * @param parent reference to Appcontroller.
     */
    public void start(Stage primaryStage, AppController parent){
        stage = primaryStage;

        controller = new StartupController();
        ((StartupController)controller).init(this, loader, stage);

        stage.setResizable(false);

        this.parent = parent;
    }

    /**
     * changeScene method changes the window based on what the user clicks on.
     * @param scene integer value of sceneEnum.
     */
    public void changeScene(Integer scene){
        switch(SceneEnum.values()[scene]){
            case STARTUP:
                controller = new StartupController();
                ((StartupController) controller).init(this, loader, stage);
                break;
            case LOGIN:
                controller = new LoginController();
                ((LoginController) controller).init(this, loader, stage);
                break;
            case REGISTER:
                controller = new RegisterController();
                ((RegisterController) controller).init(this, loader, stage);
                break;
            case MAINMENU:
                controller = new MainMenuController();
                ((MainMenuController) controller).init(this, loader, stage);
                break;
            case PLAY:
                controller = new PlayController();
                ((PlayController) controller).init(this, loader, stage);
                break;
            case JOIN:
                controller = new JoinLobbyController();
                ((JoinLobbyController) controller).init(this, loader, stage);
                break;
            case CREATE:
                controller = new CreateLobbyController();
                ((CreateLobbyController) controller).init(this, loader, stage);
                break;
            case LOBBY:
                boolean isHost = (controller instanceof CreateLobbyController);
                controller = new LobbyController();
                ((LobbyController) controller).init(this, loader, stage, isHost);
                parent.updatePlayerList(false);
                break;
            case GAME:
                controller = new GameController();
                ((GameController) controller).init(this, loader, stage, parent.getPlayerOrder().length);
                break;
            case STATISTICS:
                controller = new StatisticsController();
                ((StatisticsController) controller).init(this, loader, stage);
                break;
            case HIGHSCORE:
                controller = new HighscoreController();
                ((HighscoreController) controller).init(this, loader, stage);
                break;
        }
    }

    /**
     * login method checks if the login credentials are true or false.
     * @param username player username.
     * @param password player password.
     * @return true if login was successful. False if login was denied.
     */
    public boolean login(String username, String password){
        return parent.checkAccess(username, password, false);
    }

    /**
     * register method checks if the new user created is valid or not.
     * @param username new user name.
     * @param password new password.
     * @return true if the username is available and password fits the requirements.
     * False if username is taken and/or password meets the requirements.
     */
    public boolean register(String username, String password){
        return parent.checkAccess(username, password, true);
    }

    /**
     * getHighscores method fetches the top five best highscores.
     * @return highscore.
     */
    public Score[] getHighscores(){
        return parent.getHighscores();
    }

    /**
     * getPersonalStats method fetches a player's personal statistics.
     * @return array with personal stats
     */
    public int[] getPersonalStats(){
        return parent.getPersonalStats();
    }

    /**
     * joinLobby method connects an user to a lobby.
     * @param lobby Lobby object of the lobby the user wants to join
     * @param isHost if the host user is the host or not. True = is host. False = not host.
     * @return true if connection success. False if connection failure.
     */
    public boolean joinLobby(Lobby lobby, boolean isHost){
        return parent.joinLobby(lobby, isHost);
    }

    /**
     * becomeHost method makes a new player host if the current host leaves the game.
     */
    public void becomeHost(){
        if (controller instanceof LobbyController){
            ((LobbyController)controller).becomeHost();
        }
    }

    /**
     * findLobbies method let's you see the available lobbies.
     * @param prv if the lobby is private or not. True = private. False = public.
     * @return an ArrayList of available lobbies.
     */
    public ArrayList<Lobby> findLobbies(boolean prv){
        return parent.findLobbies(prv);
    }

    /**
     * updatePlayerList method updates the player list inside the lobby.
     * @param players players in the lobby.
     */
    public void updatePlayerList(String[] players){
        if (controller instanceof LobbyController) {
            ((LobbyController)controller).updatePlayerList(players);
        }
    }

    /**
     * gameStart method starts the game and changes the window to the game window.
     */
    public void gameStart(){
        changeScene(8);
        parent.gameStart();
    }

    /**
     * disconnect method disconnects the player and changes window to main menu window.
     */
    public void disconnect(){
        parent.disconnect();
        changeScene(3);
    }

    /**
     * removePlayer method removes a player if they timeout during the game.
     * @param username the player.
     * @param order in which order the player is in (player 1-4).
     */
    public void removePlayer(String username, int order){
        if (controller instanceof GameController){
            ((GameController)controller).removePlayer(username, order);
        }
    }

    /**
     * roll method rolls the dice.
     * @param update updates the player after the roll.
     */
    public void roll(PlayerUpdate update){
        ((GameController)controller).roll(update);
    }

    /**
     * onDiceRolled method runs an update when a player rolls a dice.
     * @param update updates player.
     */
    public void onDiceRolled(PlayerUpdate update){
        parent.onDiceRolled(update);
    }

    /**
     * movePlayer method moves the player to the new tile.
     * @param update updates the player.
     */
    public void movePlayer(PlayerUpdate update){
        ((GameController)controller).movePlayer(update);
    }

    /**
     * gameOver method shows the end screen with the game statistics.
     * @param players array of the players in the game with stats.
     */
    public void gameOver(String[] players){ // Game over stats
        ((GameController)controller).gameOver(players);
    }
}
