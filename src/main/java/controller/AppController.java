import java.util.ArrayList;
import java.util.concurrent.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import java.util.Random;
import java.util.Arrays;

/**
 * Main Controller object for the Lario Kart application.
 */
public class AppController extends Application {

    private UIController ui;

    public static final String PROPERTIES_URL = ("DatabaseLogin.properties");

    private ScheduledExecutorService scheduler;
    private ProfileModule profileModule;
    private RoomModule roomModule;
    private GameModule gameModule;

    /**
     *
     * start method starts the game.
     * @param primaryStage the stage object.
     */
    public void start(Stage primaryStage) {
        scheduler = Executors.newScheduledThreadPool(1, new ThreadFactory() {
                                                                        public Thread newThread(Runnable r) {
                                                                            Thread t = new Thread(r);
                                                                            t.setDaemon(true);
                                                                            return t;
                                                                        }});
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!scheduler.isShutdown())
                scheduler.shutdownNow();

            System.out.println("Shutting down scheduler");
        }, "Shutdown-thread"));

        profileModule = new ProfileModule();
        ui = new UIController(primaryStage, this);
    }

    /**
     * getUserID method gives the current user's ID
     * @return the userID from the current user
     */
    public int getUserID() {
        return profileModule.getUserID();
    }

    /**
     * checkAccess method checks username and password and performs a login if they are correct.
     * @param username player username.
     * @param password player password.
     * @param newUser if the user is new or not. True = new. False = old.
     * @return true if login is completes without failure. False if login fails.
     */
    public boolean checkAccess(String username, String password, boolean newUser){
        return newUser ? profileModule.newUser(username, password) : profileModule.login(username, password);
    }

    /**
     * init method launches the application.
     * @param args launch arguments
     */
    public void init(String[] args){ launch(args); }

    /**
     * getHighscores method fetches the top five best highscores.
     * @return array with highscores.
     */
    public Score[] getHighscores(){
        return profileModule.checkHighscores();
    }

    /**
     * getPersonalStats method fetches a player's personal statistics.
     * @return array with personal stats
     */
    public int[] getPersonalStats(){
        return profileModule.checkStats();
    }

    /**
     * findLobbies method let's you see the available lobbies.
     * @param prv If the lobby is private or not. True = private. False = public.
     * @return an ArrayList over available lobbies.
     */
    public ArrayList<Lobby> findLobbies(boolean prv){
        return ClientRoomModule.findLobbies(prv);
    }

    /**
     * updatePlayerList method updates the player list inside the lobby.
     * @param hasBecomeHost if the player has become the host or not. True = now host. False = not host.
     */
    public void updatePlayerList(boolean hasBecomeHost){
        if (hasBecomeHost)
            becomeHost(roomModule.getLobbyID());

        ui.updatePlayerList(profileModule.getUsername(roomModule.getPlayerOrder()));
    }

    /**
     * becomeHost method makes a new player host if the current host leaves the game.
     * @param lobbyID the lobbyID
     */
    private void becomeHost(int lobbyID){
        endScheduledTask();
        roomModule = new HostRoomModule(lobbyID);
        scheduler.scheduleAtFixedRate(()->getLobbyUpdate(), 1, 2, TimeUnit.SECONDS);
        ui.becomeHost();
    }

    /**
     * endScheduledTask method resets the schedule object.
     */

    public void endScheduledTask(){
        scheduler.shutdownNow();
        scheduler = Executors.newScheduledThreadPool(1, new ThreadFactory() {
                                                                        public Thread newThread(Runnable r) {
                                                                            Thread t = new Thread(r);
                                                                            t.setDaemon(true);
                                                                            return t;
                                                                        }});
    }

    /**
     * joinLobby method connects an user to a lobby.
     * @param lobby Lobby object of the lobby the user wants to join
     * @param isHost if the host user is the host or not. True = is host. False = not host.
     * @return true if connection success. False if connection failure.
     */

    public boolean joinLobby(Lobby lobby, boolean isHost) {
        if (isHost) {
            roomModule = new HostRoomModule();
        }
        else {
            roomModule = new ClientRoomModule();
        }

        if (roomModule.joinRoom(lobby, profileModule.getUserID())) {
            scheduler.scheduleAtFixedRate(()->getLobbyUpdate(), 1, 2, TimeUnit.SECONDS);
        }
        else{
            roomModule = null;
            return false;
        }
        return true;
    }

    /**
     * getLobbyUpdate method updates the lobby
     */

    public void getLobbyUpdate(){
        roomModule.getUpdate(this);
    }

    /**
     * hasOrderChanged method checks if the order of player have changed and updates the order if it has.
     * @return true if order has changed. False if order hasn't changed.
     */

    public boolean hasOrderChanged(){
        return roomModule.hasOrderChanged();
    }

    /**
     * getPlayerOrder method gives an array with the player order.
     * @return Integer array with player order.
     */

    public Integer[] getPlayerOrder(){
        return roomModule.getPlayerOrder();
    }

    /**
     * removePlayer method removes a player if they timeout during the game.
     * @param userID the user's ID
     * @param order in which order the player is in (player 1-4).
     */

    public void removePlayer(int userID, int order){
        ui.removePlayer(profileModule.getUsername(new Integer[]{ userID})[0], order);
    }

    /**
     * gameStart method starts the game and changes the window to the game window.
     */

    public void gameStart(){

        endScheduledTask();

        if (roomModule instanceof HostRoomModule){
            ((HostRoomModule) roomModule).gameStart();
        }
        else{
            ui.changeScene(8);
        }

        gameModule = new GameModule(roomModule.getLobbyID(), profileModule.getUserID());
        if (gameModule.isMyTurn(roomModule.getPlayerOrder())) {
            Random rand = new Random();
            roll(rand.nextInt(6) + 1);
        }
        scheduler.scheduleAtFixedRate(()->getGameUpdate(), 1, 2, TimeUnit.SECONDS);

    }

    /**
     * disconnect method disconnects a player from the lobby.
     */

    public void disconnect(){
        endScheduledTask();
        roomModule.disconnect(profileModule.getUserID());
        gameModule = null;
        roomModule = null;
    }

    /**
     * roll method rolls the dice.
     * @param diceRoll this number shown on the "dice".
     */

    public void roll(int diceRoll){
        PlayerUpdate update = gameModule.roll(diceRoll, profileModule.getUsername(new Integer[]{ profileModule.getUserID()})[0]);
        if (update == null){
            disconnect();
            ui.changeScene(3);
        }
        update.setUsername(profileModule.getUsername(new Integer[]{ update.getUser()})[0]);
        ui.roll(update);
    }

    /**
     * onDiceRolled method runs an update when a player rolls a dice.
     * @param update updates player.
     */
    public void onDiceRolled(PlayerUpdate update){
        movePlayer(update);
        if (!gameModule.sendPlayerUpdate(update)){
            disconnect();
            ui.changeScene(3);
        }
    }

    /**
     * movePlayer method moves the player to the new tile.
     * @param update updates the player.
     */
    public void movePlayer(PlayerUpdate update){
        update.setUsername(profileModule.getUsername(new Integer[]{ update.getUser()})[0]);
        update.setUser(Arrays.asList(roomModule.getPlayerOrder()).indexOf(update.getUser()));
        ui.movePlayer(update);
    }

    /**
     * getGameUpdate method updates the game and checks if there are any changes in the player order.
     */

    public void getGameUpdate() {
        gameModule.getUpdate(this);
    }

    /**
     * gameOver method shows the end screen with the game statistics.
     * @param players array of the players in the game with stats.
     */

    public void gameOver(Integer[] players){
        if (players == null){
            endScheduledTask();
            ui.changeScene(3);
        }
        else {
            Platform.runLater(() -> ui.gameOver(profileModule.getUsername(players)));
            endScheduledTask();
        }
    }
}
