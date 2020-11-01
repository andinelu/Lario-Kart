import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

/**
 * This class handles game connection from the host side.
 * The host can start games.
 * It extends from the super class RoomModule.
 */
public class HostRoomModule extends RoomModule {

    /**
     * Class constructor.
     * @param lobbyID lobbyID.
     */
    public HostRoomModule(int lobbyID){ this.lobbyID = lobbyID; }
    public HostRoomModule(){ }

    /**
     * joinRoom method creates the game lobby, and joins the lobby as host.
     * @param lobby an object of the class lobby.
     * @param userID the host's userID.
     * @return true on successful creation of lobby. False on failure.
     */
    public boolean joinRoom (Lobby lobby, int userID){
        String query = "INSERT INTO lobby (lobbyName, maxPlayer, lobbyPassword, started) VALUES (?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, lobby.getName().replaceAll("\"", "").replace("\'", ""));
            stmt.setInt(2, lobby.getMax());
            if(!lobby.isPublic()) {
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                byte[] passHash = md.digest(lobby.getPassword().getBytes(StandardCharsets.UTF_8));
                stmt.setBlob(3, new javax.sql.rowset.serial.SerialBlob(passHash));
            }
            else{
                stmt.setNull(3, java.sql.Types.BLOB);
            }
            stmt.setBoolean(4, false);
            stmt.execute();

            ResultSet res = stmt.getGeneratedKeys();
            if (res.next())
                lobbyID = res.getInt(1);

            res.close();
            stmt.close();

            query = "INSERT INTO connected (lobbyID, userID, p_order, position) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(query);
            stmt.setInt (1, lobbyID);
            stmt.setInt(2, userID);
            stmt.setInt(3, 1);
            stmt.setInt(4, 0);
            stmt.execute();

            stmt.close();
            conn.close();

            hasOrderChanged();

            return true;
        }
        catch (SQLException | NoSuchAlgorithmException e){
            System.out.println(e);
            try {
                conn.close();
            }
            catch(SQLException ex){
                System.out.println(ex);
            }
            return false;
        }
    }
    /**
     * getUpdate method updates the player list inside the lobby if order has changed.
     * @param parent reference to AppController.
     */
    public void getUpdate(AppController parent){
        if(hasOrderChanged()){
            parent.updatePlayerList(false);
        }
    }
    /**
     * gameStart method starts the game and updates database.
     */

    public void gameStart(){
        String query = "UPDATE lobby SET started = ? WHERE lobbyID = ?";
        Connection conn = DBConnection.getConnection();
        try {
           PreparedStatement stmt = conn.prepareStatement(query);
           stmt.setBoolean(1, true);
           stmt.setInt(2, lobbyID);
           stmt.executeUpdate();

           stmt.close();
        }
        catch (SQLException e) {
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
    }

    /**
     * disconnect method disconnects the user from the lobby. If lobby is empty, it gets deleted.
     * @param userID player ID.
     */
    public void disconnect(int userID){
        String query = "SELECT COUNT(userID) FROM connected WHERE lobbyID = ?";
        Connection conn = DBConnection.getConnection();
        try{
            conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, lobbyID);
            ResultSet res = stmt.executeQuery();

            res.next();
            if(res.getInt(1) == 1) {
                res.close();
                stmt.close();

                query = "DELETE FROM lobby WHERE lobbyID = ?";
                stmt = conn.prepareStatement(query);
                stmt.setInt(1, lobbyID);
                stmt.execute();
                stmt.close();
                conn.close();
            }
            else{
                res.close();
                stmt.close();
                conn.close();

                super.disconnect(userID);
            }
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
    }
}
