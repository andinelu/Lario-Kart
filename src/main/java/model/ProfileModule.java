import javax.sql.rowset.serial.SerialException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.sql.*;
import java.util.Arrays;

/**
 * This class contains the logic behind a player profile.
 * That includes creating a new profile and logging in.
 */
public class ProfileModule {

    private int userID;
    private final int HIGHSCORE_USER_COUNT;

    /**
     * The class constructor.
     */
    public ProfileModule() {
        userID = 0;
        HIGHSCORE_USER_COUNT = 10;
    }

    /**
     * getUserID method fetches the userID.
     * @return userID.
     */
    public int getUserID() {
        return userID;
    }

    private void setUserID(String username) {
        String query = "SELECT userID FROM users WHERE username = ?";
        Connection conn = DBConnection.getConnection();
        try{
           PreparedStatement stmt = conn.prepareStatement(query);
           stmt.setString(1, username);
           ResultSet res = stmt.executeQuery();

           if(res.next()){
               userID = res.getInt("userID");
           }
           res.close();
           stmt.close();
       }
       catch (SQLException e) {
            System.out.println(e);
       }
       finally{
            try {
                conn.close();
            }
            catch (SQLException e){
                System.out.println(e);
            }
        }
    }
    /**
     * getUsername method fetches players username.
     * @param userID an array with userIDs used to find usernames.
     * @return an array with usernames.
     */
    public String[] getUsername(Integer[] userID) {
        String[] usernames = new String[userID.length];
        int indexCounter = 0;

        String query = "SELECT username, userID FROM users WHERE userID = ?";
        for (int i = 1; i < userID.length; i++) {
            query += (" OR userID = ?");
        }

        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            for (int i = 0; i < userID.length; i++){
                stmt.setInt(i + 1, userID[i]);
            }
            ResultSet res = stmt.executeQuery();

            while (res.next()) {
                if (indexCounter < usernames.length) {
                    int userIndex = Arrays.asList(userID).indexOf(res.getInt("userID"));
                    usernames[userIndex] = res.getString("username");
                    indexCounter++;
                }
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
            catch (SQLException ex){
                System.out.println(ex);
            }
            return null;
        }
        return usernames;
    }

    private byte[] createRandomSalt(){
        SecureRandom rand = new SecureRandom();
        byte[] salt = new byte[16];
        rand.nextBytes(salt);

        return salt;
    }

    /**
     * newUser method creates a new user.
     * @param username player username.
     * @param password player password.
     * @return true if account was successfully created. False if account creation was unsuccessful.
     */
    public boolean newUser(String username, String password) {
        byte[] hashedPass= null;
        Blob pass = null;

        byte[] saltBytes = createRandomSalt();
        Blob salt = null;

        if(!userExists(username)){
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                md.update(saltBytes);
                hashedPass = md.digest(password.getBytes(StandardCharsets.UTF_8));
                pass = new javax.sql.rowset.serial.SerialBlob(hashedPass);
                salt = new javax.sql.rowset.serial.SerialBlob(saltBytes);
            } catch(SerialException e){
                System.out.println(e);
                return false;
            } catch(NoSuchAlgorithmException | SQLException e){
                System.out.println(e);
                return false;
            }

            String query = "INSERT INTO users(password, salt, username) VALUES(?, ?, ?)";
            Connection conn = DBConnection.getConnection();

            try {
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setBlob(1, pass);
                stmt.setBlob(2, salt);
                stmt.setString(3, username);
                stmt.execute();

                setUserID(username);

                stmt.close();
                conn.close();
            }
            catch(SQLException e) {
                System.out.println(e);
                try {
                    conn.close();
                }
                catch(SQLException ex){
                    System.out.println(ex);
                }
                return false;
            }
            return true;
        }
        return false;
    }
    /**
     * login method logs a players in.
     * @param username player username.
     * @param password player password.
     * @return true if login was successful. False if login was unsuccessful.
     */
    public boolean login(String username, String password) {
        if(checkPassword(username, password)) {
            setUserID(username);
            removeCrashTracks();
            return true;
        }
        return false;
    }

    private void removeCrashTracks(){
        String query = "SELECT lobbyID, COUNT(userID) as p_count FROM connected where lobbyID = (SELECT lobbyID FROM connected WHERE userID = ?) GROUP BY lobbyID";
        Connection conn = DBConnection.getConnection();
        try{
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userID);
            ResultSet res = stmt.executeQuery();

            PreparedStatement stmt2 = null;
            while(res.next()){
                if (res.getInt("p_count") == 1){
                    query = "DELETE FROM lobby WHERE lobbyID = ?";
                    stmt2 = conn.prepareStatement(query);
                    stmt2.setInt(1, res.getInt("lobbyID"));
                }
                else if (res.getInt("p_count") > 1){
                    query = "DELETE FROM connected WHERE userID = ?";
                    stmt2 = conn.prepareStatement(query);
                    stmt2.setInt(1, userID);
                }
                stmt2.execute();

                stmt2.close();

                if (res.getInt("p_count") > 1){
                    RoomModule.updatePlayerOrderRemote(res.getInt("lobbyID"));
                }
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
            catch (SQLException e){
                System.out.println(e);
            }
        }
    }

    private boolean userExists(String username) {
        boolean returnValue = true;
        Connection conn = DBConnection.getConnection();

        try {
            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet res = stmt.executeQuery();

            if (!res.next()) returnValue = false;

            res.close();
            stmt.close();
            conn.close();
        }
        catch (SQLException e) {
            System.out.println(e);
            try {
                conn.close();
            }
            catch(SQLException ex){
                System.out.println(ex);
            }
            returnValue = false;
        }
        return returnValue;
    }

    private boolean checkPassword(String username, String password) {
        boolean returnValue = false;
        Blob tempBlob = null;
        if(userExists(username)) {
            String query = "SELECT password, salt FROM users WHERE username = ?";
            Connection conn = DBConnection.getConnection();

            try {
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1,username);
                ResultSet res = stmt.executeQuery();

                if (res.next()) {
                    tempBlob = res.getBlob("salt");
                    MessageDigest md = MessageDigest.getInstance("SHA-512");
                    byte[] salt = tempBlob.getBytes(1, (int) tempBlob.length());
                    md.update(salt);

                    tempBlob = res.getBlob("password");
                    byte[] passHash = tempBlob.getBytes(1, (int) tempBlob.length());

                    if (Arrays.equals(md.digest(password.getBytes(StandardCharsets.UTF_8)), passHash)) {
                        returnValue = true;
                    }
                    tempBlob.free();
                }
                res.close();
                stmt.close();
                conn.close();
            }
            catch(SQLException e) {
                System.out.println(e);
                try {
                    conn.close();
                }
                catch(SQLException ex){
                    System.out.println(ex);
                }
                returnValue = false;
            }
            catch(NoSuchAlgorithmException e){
                System.out.println(e);
                returnValue = false;
            }
        }
        return returnValue;
    }

    /**
     * checkStats method fetches the player stats.
     * @return an array with statistics.
     */

    public int[] checkStats() {
        int[] stats = new int[3];
        String query = "SELECT gamesPlayed, gamesWon FROM scores WHERE userID = ?";

        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userID);
            ResultSet res = stmt.executeQuery();
            if (res.next()) {
                stats[0] = res.getInt("gamesPlayed");
                stats[1] = res.getInt("gamesWon");
                stats[2] = (int) (((float) stats[1] / (float) stats[0]) * 100);
            }

            res.close();
            stmt.close();
            conn.close();
        }
        catch(SQLException e) {
            System.out.println(e);
            try {
                conn.close();
            }
            catch (SQLException ex){
                System.out.println(ex);
            }
            return new int[]{0, 0, 0};
        }
        return stats;
    }

    /**
     * checkHighscores method fetches the highscores.
     * @return an array with highscores.
     */    public Score[] checkHighscores() {
        Score[] highscores = new Score[HIGHSCORE_USER_COUNT];

        String query = "SELECT username, gamesWon FROM scores JOIN users ON scores.userID = users.userID ORDER BY gamesWon DESC LIMIT " + HIGHSCORE_USER_COUNT;
        Connection conn = DBConnection.getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet res = stmt.executeQuery();

            int indexCounter = 0;
            while (res.next()) {
                highscores[indexCounter] = new Score(res.getString("username"), res.getInt("gamesWon"));
                indexCounter++;
            }
            stmt.close();
            res.close();
            conn.close();
        }
        catch (SQLException e) {
            System.out.println(e);
            try {
                conn.close();
            }
            catch (SQLException ex){
                System.out.println(ex);
            }
            return null;
        }
        return highscores;
    }
}
