import javafx.application.Platform;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * This class handles game connection from the client side.
 * It extends from the super class RoomModule.
 */

public class ClientRoomModule extends RoomModule {
    private static boolean isSearching = false;
    public ClientRoomModule(){
    }
    /**
     * getUpdate makes sure that the game is always updated.
     * If a player disconnects this method will be called.
     * It makes sure that each player is in the correct order.
     * @param parent AppController parent class.
     */

    public void getUpdate (AppController parent) {
        if(hasOrderChanged()){
            if (playerOrder[0] == parent.getUserID()){
                parent.updatePlayerList(true);
            }
            else{
                parent.updatePlayerList(false);
            }
        }
        Connection conn = DBConnection.getConnection();
        String query = "SELECT started FROM lobby WHERE lobbyID = ?";
        try{
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, lobbyID);
            ResultSet res = stmt.executeQuery();
            res.next();
            if(res.getBoolean("started")){
                Platform.runLater(()->parent.gameStart());
            }
            res.close();
            stmt.close();
        }
        catch (SQLException e){
            System.out.println(e);
        }
        finally {
            try {
                conn.close();
            }
            catch(SQLException e){
                System.out.println(e);
            }
        }
    }
    /**
     * findLobbies creates an ArrayList of the current game lobbies.
     * @param prv checks if the lobby is private or not.
     * @return an ArrayList of lobbies.
     */

    public static ArrayList<Lobby> findLobbies(boolean prv){
        if (isSearching) return null;

        isSearching = true;

        ArrayList<Lobby> lobbies = new ArrayList<Lobby>();
        Connection conn = DBConnection.getConnection();
        String query;
        try {
            if (prv) {
                query = "SELECT * FROM lobby JOIN (SELECT COUNT(connected.userID) AS p_count, lobbyID FROM connected GROUP BY lobbyID) AS connected " +
                        "ON lobby.lobbyID = connected.lobbyID WHERE lobbyPassword IS NOT NULL AND started = ?";
            }
            else {
                query = "SELECT * FROM lobby JOIN (SELECT COUNT(connected.userID) AS p_count, lobbyID FROM connected GROUP BY lobbyID) AS connected " +
                        "ON lobby.lobbyID = connected.lobbyID WHERE lobbyPassword IS NULL AND started = ?";
            }
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setBoolean(1, false);
            ResultSet res = stmt.executeQuery();

            while (res.next()){
                if(res.getInt("p_count") == res.getInt("maxPlayer")
                    || res.getInt("p_count") == 0) continue;

                lobbies.add(new Lobby(res.getInt("lobbyID"),
                        res.getString("lobbyName"),
                        res.getInt("maxPlayer"),
                        res.getInt("p_count"),
                        (prv ? "placeholder" : "")));
            }
            res.close();
            stmt.close();
        }
        catch (SQLException e){
            System.out.println(e);
        }
        finally {
            isSearching = false;
            try {
                conn.close();
            }
            catch(SQLException e){
                System.out.println(e);
            }
        }

        return lobbies;
    }
    /**
     * joinRoom method handles the process of joining a lobby.
     * @param lobby An object of the class Lobby.
     * @param userID a player.
     * @return true on success to join lobby. False on fail to join lobby.
     */

    public boolean joinRoom(Lobby lobby, int userID) {
        String query = "SELECT COUNT(userID) AS playerCount, lobbyPassword, maxPlayer FROM connected JOIN lobby ON lobby.lobbyID = connected.lobbyID " +
                        "WHERE lobby.lobbyID = ? AND lobbyName = ?";

        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, lobby.getID());
            stmt.setString(2, lobby.getName());
            ResultSet res = stmt.executeQuery();

            int maxOrder = 0;
            MessageDigest md;
            if (res.next()){
                if (res.getInt("playerCount") == res.getInt("maxPlayer")){
                    System.out.println("Lobby is full");
                    return false;
                }

                if (!lobby.isPublic()) {
                    md = MessageDigest.getInstance("SHA-512");
                    Blob tempBlob = res.getBlob("lobbyPassword");
                    byte[] passHash = tempBlob.getBytes(1, (int) tempBlob.length());
                    tempBlob.free();

                    if (!Arrays.equals(md.digest(lobby.getPassword().getBytes(StandardCharsets.UTF_8)), passHash)) {
                        System.out.println("Wrong password");
                        res.close();
                        stmt.close();
                        conn.close();
                        return false;
                    }
                }
                maxOrder = res.getInt("playerCount");
            }
            else {
                System.out.println("Couldn't find lobby");
                res.close();
                stmt.close();
                conn.close();
                return false;
            }
            res.close();
            stmt.close();

            query = "INSERT INTO connected (lobbyID, userID, p_order, position) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, lobby.getID());
            stmt.setInt(2, userID);
            stmt.setInt(3, (maxOrder+1));
            stmt.setInt(4, 0);
            stmt.execute();

            stmt.close();
        }
        catch(SQLException | NoSuchAlgorithmException e){
            System.out.println(e);
            try{
                conn.close();
            }
            catch(SQLException ex){
                System.out.println(ex);
            }
            return false;
        }

        lobbyID = lobby.getID();
        hasOrderChanged();

        return true;
    }
}
