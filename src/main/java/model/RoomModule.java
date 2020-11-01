import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class is the super RoomModule class.
 */
public abstract class RoomModule {
    protected int lobbyID = 0;
    protected Integer[] playerOrder = null;

    public int getLobbyID() {
        return lobbyID;
    }

    public abstract boolean joinRoom(Lobby lobby, int userID);

    public abstract void getUpdate(AppController parent);

    /**
     * updatePlayerOrderRemote method updates the player order.
     * @param lobby lobbyID
     */
    public static void updatePlayerOrderRemote(int lobby){
        Connection conn = DBConnection.getConnection();

        String query = "SELECT * FROM connected WHERE lobbyID = ? ORDER BY p_order ASC";
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, lobby);
            ResultSet res = stmt.executeQuery();

            ArrayList<Integer> temp_order = new ArrayList<Integer>();
            while (res.next()) {
                temp_order.add(res.getInt("p_order"));
            }

            res.close();
            stmt.close();
            conn.close();

            int playerOrder = 1;
            for (Integer order : temp_order) {
                if (order != playerOrder) {
                    conn = DBConnection.getConnection();
                    query = "UPDATE connected SET p_order = ? WHERE p_order = ? AND lobbyID = ?";
                    stmt = conn.prepareStatement(query);
                    stmt.setInt(1, playerOrder);
                    stmt.setInt(2, (playerOrder + 1));
                    stmt.setInt(3, lobby);

                    stmt.executeUpdate();

                    stmt.close();
                    conn.close();
                }
                playerOrder++;
            }
        }
        catch (SQLException e){
            System.out.println(e);
            try {
                conn.close();
            }
            catch (SQLException ex){
                System.out.println(ex);
            }
        }
    }

    /**
     * disconnect method deletes an user from lobby.
     * @param userID user getting deleted.
     */
    public void disconnect(int userID){
        Connection conn = DBConnection.getConnection();
        String query = "DELETE FROM connected WHERE userID = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userID);
            stmt.execute();
            stmt.close();
            conn.close();

            updatePlayerOrderRemote(lobbyID);
        }
        catch (SQLException e){
            System.out.println(e);
            try {
                conn.close();
            }
            catch (SQLException ex){
                System.out.println(ex);
            }
        }
    }

    private Integer[] getOrderRemote() {
        Integer[] returnValue = null;
        String query = "SELECT userID, p_order, (SELECT COUNT(userID) FROM connected WHERE lobbyID = ?) AS length " +
                        "FROM connected WHERE lobbyID = ?";
        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, lobbyID);
            stmt.setInt(2, lobbyID);
            ResultSet res = stmt.executeQuery();
            boolean flag = false;

            while (res.next()) {
                if (!flag) {
                    returnValue = new Integer[res.getInt("length")];
                    flag = true;
                }
                returnValue[res.getInt("p_order") - 1] = res.getInt("userID");
            }

            res.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println(e);
            }
        }
        return returnValue;
    }

    protected boolean hasOrderChanged() {
        Integer[] newPlayerOrder = getOrderRemote();
        boolean returnValue = false;
        if (playerOrder == null || newPlayerOrder.length != playerOrder.length) {
            returnValue = true;
        }
        else {
            for (int i = 0; i < newPlayerOrder.length; i++) {
                if (newPlayerOrder[i] != playerOrder[i]) {
                    returnValue = true;
                }
            }
        }
        if (returnValue){
            playerOrder = new Integer[newPlayerOrder.length];
            for (int i = 0; i < newPlayerOrder.length; i++) {
                playerOrder[i] = newPlayerOrder[i];
            }
        }
        return returnValue;
    }

    /**
     * GetPlayerOrder fetches the player order in the game.
     * @return player order.
     */
    public Integer[] getPlayerOrder() {
        return playerOrder;
    }
}
