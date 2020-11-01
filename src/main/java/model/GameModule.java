import javafx.application.Platform;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * This class handles the game logic.
 */
public class GameModule {
    private final int userID;
    private final int lobbyID;
    private int previousID;
    private static boolean isRolling;
    private static int timeoutCounter;

    /**
     * This is the class constructor.
     * @param lobbyID the lobby's ID.
     * @param userID the player's ID.
     */
    public GameModule(int lobbyID, int userID){
        this.lobbyID = lobbyID;
        this.userID = userID;
        previousID = 0;
        isRolling = false;
    }
    /**
     * isMyTurn method checks if it's the user's turn.
     * @param order array with player orders.
     * @return true if it's the user's turn. False if it isn't.
     */
    public boolean isMyTurn(Integer[] order) {
        int nextIndex;
        if (order[order.length - 1] == previousID)
            nextIndex = 0;
        else
            nextIndex = Arrays.asList(order).indexOf(previousID) + 1;
        return userID == order[nextIndex];
    }
    /**
     * playerUpdate method updates the player's location.
     * @param diceRoll Integer values of a dice.
     * @param username username of user doing the dice roll.
     * @return player update with new location.
     */
    public PlayerUpdate roll(int diceRoll, String username){
        String query = "SELECT position FROM connected WHERE userID = ? AND lobbyID = ?";

        int location;
        Connection conn = DBConnection.getConnection();
        try{
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userID);
            stmt.setInt(2, lobbyID);
            ResultSet res = stmt.executeQuery();
            if (res.next())
                location = res.getInt("position");
            else {
                res.close();
                stmt.close();
                conn.close();
                return null;
            }

            res.close();
            stmt.close();
            conn.close();
        }
        catch(SQLException e){
            System.out.println(e);
            try{
                conn.close();
            }
            catch(SQLException ex){
                System.out.println(ex);
            }
            return null;
        }

        return calculateTurn(diceRoll, location, username);
    }

    /**
     * calculateTurn method calculates the new location.
     * @param diceRoll Integer values of a dice.
     * @param initial current location of the player.
     * @param username username of user doing the dice roll.
     * @return player update with new location.
     */
    public PlayerUpdate calculateTurn(int diceRoll, int initial, String username){
        String msg = null;
        int newIndex = initial + diceRoll;

        if (newIndex >= BoardDefaults.getBoardTiles().length){
            newIndex = 0;
            msg = username + " has won the game!";
        }
        else {
            for(Point p : BoardDefaults.getDeadTiles()){
                if (p.equals(BoardDefaults.getBoardTiles()[newIndex])){
                    newIndex -= BoardDefaults.getSpecialTileMoveAmount();
                    msg = username + " landed on a dead tile and was moved back 3 tiles.";
                    break;
                }
            }
            for (Point p : BoardDefaults.getPowerupTiles()){
                if (p.equals(BoardDefaults.getBoardTiles()[newIndex])){
                    newIndex += BoardDefaults.getSpecialTileMoveAmount();
                    msg = username + " landed on a powerup tile and moved forward 3 tiles";
                    break;
                }
            }
        }

        return new PlayerUpdate(userID, username, diceRoll, newIndex, msg);
    }

    /**
     * sendPlayerUpdate method updates the database with new player update.
     * @param update object of PlayerUpdate class.
     * @return true if update was successfully transferred. False if something went wrong.
     */
    public boolean sendPlayerUpdate(PlayerUpdate update){
        String query = "UPDATE connected SET position = ? WHERE lobbyID = ? AND userID = ?";
        Connection conn = DBConnection.getConnection();
        try{
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, update.getLocation());
            stmt.setInt(2, lobbyID);
            stmt.setInt(3, userID);
            stmt.executeUpdate();
            stmt.close();

            query = "INSERT INTO turn (lobbyID, userID, diceRoll, location, UpdateMsg) VALUES(?, ?, ?, ?, ?) ON DUPLICATE KEY "+
                    "UPDATE userID = ?, diceRoll = ?, location = ?, updateMsg = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, lobbyID);
            stmt.setInt(2, userID);
            stmt.setInt(3, update.getDiceRoll());
            stmt.setInt(4, update.getLocation());
            stmt.setString(5, update.getMsg());
            stmt.setInt(6, userID);
            stmt.setInt(7, update.getDiceRoll());
            stmt.setInt(8, update.getLocation());
            stmt.setString(9, update.getMsg());
            stmt.execute();
            stmt.close();
            conn.close();

            previousID = userID;
            isRolling = false;
            return true;
        }
        catch (SQLException e){
            System.out.println(e);
            try{
                conn.close();

            }
            catch (SQLException ex){
                System.out.println(ex);
            }
            return false;
        }
    }

    /**
     * GetUpdate method fetches the new update of the game's progress.
     * @param parent reference to AppController.
     */
    public void getUpdate(AppController parent){
        if (isRolling) return;

        Integer[] order = parent.getPlayerOrder();
        if (parent.hasOrderChanged()){
            Integer[] newOrder = parent.getPlayerOrder();
            if (newOrder.length == 1){
                Platform.runLater(()->parent.gameOver(null));
                return;
            }
            for (int i = 0; i < newOrder.length; i++){
                if (order[i] != newOrder[i]){
                    if (order[i] == userID){
                        System.out.println("Got kicked");
                        Platform.runLater(()->parent.disconnect());
                    }
                    else {
                        System.out.println("UserID: " + order[i] + "has been kicked");
                        int tempuserID = order[i];
                        int tempIndex = i;
                        Platform.runLater(()->parent.removePlayer(tempuserID, tempIndex));
                    }
                    return;
                }
            }
            System.out.println("UserID: " + order[order.length - 1] + "has been kicked");
            int tempuserID = order[order.length - 1];
            int tempIndex = order.length - 1;
            Platform.runLater(()->parent.removePlayer(tempuserID, tempIndex));
            return;
        }

        Connection conn = DBConnection.getConnection();
        String query = "SELECT userID, diceRoll, location, updateMsg FROM turn WHERE lobbyID = ?";
        try{
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, lobbyID);
            ResultSet res = stmt.executeQuery();

            int currentID = 0;
            if (res.next()){
                if (res.getInt("location") == 0){
                    int temp = res.getInt("userID");
                    res.close();
                    stmt.close();
                    conn.close();
                    parent.gameOver(gameOver(temp));
                    return;
                }
                currentID = res.getInt("userID");
            }
            else {
                res.close();
                stmt.close();
                conn.close();
                return;
            }

            if (currentID != previousID){
                PlayerUpdate temp = new PlayerUpdate(res.getInt("userID"), "", res.getInt("diceRoll"),
                                                    res.getInt("location"), res.getString("UpdateMsg"));
                previousID = currentID;
                timeoutCounter = 0;
                res.close();
                stmt.close();
                conn.close();
                Platform.runLater(()->parent.movePlayer(temp));
            }
            else if (isMyTurn(order)){
                res.close();
                stmt.close();
                conn.close();

                isRolling = true;
                timeoutCounter = 0;

                Random rand = new Random();

                int roll = rand.nextInt(6) + 1;
                Platform.runLater(()->parent.roll(roll));
            }
            else {
                res.close();
                stmt.close();
                conn.close();
                timeoutCounter++;
            }
        }
        catch (SQLException e) {
            System.out.println(e);
            try{
                conn.close();
            }
            catch(SQLException ex){
                System.out.println(ex);
            }
        }

        System.out.println("Timeout counter = " + timeoutCounter);
        if (timeoutCounter >= 20){
            System.out.println("Above 20");
            int nextIndex = Arrays.asList(order).indexOf(previousID) + 2;
            if (nextIndex >= order.length){
                nextIndex -= order.length;
            }

            if (order[nextIndex] == userID){
                int indexToRemove = (nextIndex - 1 < 0) ? (order.length - 1) : (nextIndex - 1);
                removePlayer(order[indexToRemove]);
            }
            timeoutCounter = 0;
        }
    }

    /**
     * gameOver method ends the game and updates scores.
     * @param winUser userID to winning player.
     * @return array with users in winning order.
     */
    private Integer[] gameOver(int winUser){
        String query = "SELECT userID, position FROM connected WHERE lobbyID = ? ORDER BY position DESC";
        Connection conn = DBConnection.getConnection();
        Integer[] players = null;
        try{
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, lobbyID);
            ResultSet res = stmt.executeQuery();

            ArrayList<Integer> playersTemp = new ArrayList<Integer>();
            playersTemp.add(winUser);
            while(res.next()){
                if (res.getInt("userID") != winUser){
                    playersTemp.add(res.getInt("userID"));
                }
            }

            players = new Integer[playersTemp.size()];
            for (int i = 0; i < players.length; i++){
                players[i] = playersTemp.get(i);
            }

            res.close();
            stmt.close();

            query = "INSERT INTO scores (userID, gamesPlayed, gamesWon) VALUES(?, ?, ?) ON DUPLICATE KEY "+
                    "UPDATE gamesPlayed = (gamesPlayed + 1) ";
            if (winUser == userID){
                query += ", gamesWon = (gamesWon + 1) ";
            }
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, userID);
            stmt.setInt(2, 1);
            stmt.setInt(3, (winUser == userID) ? 1 : 0);

            stmt.execute();
            stmt.close();
        }
        catch (SQLException e){
            System.out.println(e);
        }
        finally {
            try{
                conn.close();
            }
            catch (SQLException e){
                System.out.println(e);
            }
        }

        return players;
    }

    /**
     * removePlayer method removes an player from the game.
     * @param userID userID to removed player.
     */
    private void removePlayer(int userID){
        String query = "DELETE FROM connected WHERE userID = ? AND lobbyID = ?";
        Connection conn = DBConnection.getConnection();
        try{
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userID);
            stmt.setInt(2, lobbyID);
            stmt.execute();
            stmt.close();
        }
        catch (SQLException e){
            System.out.println(e);
        }
        finally {
            try{
                conn.close();
            }
            catch (SQLException e){
                System.out.println(e);
            }
        }

        RoomModule.updatePlayerOrderRemote(lobbyID);
    }
}
