/**
 * The Lobby class handles the logic behind game lobbies.
 */

public class Lobby {
    private final int ID;
    private final String name;
    private final int max;
    private int connected;
    private String password;
    /**
     * Class constructor.
     * @param ID lobby ID.
     * @param name lobby name.
     * @param max max number of players in the lobby.
     * @param connected number of connected players.
     * @param password lobby password.
     */

    public Lobby (int ID, String name, int max, int connected, String password){
        this.ID = ID;
        this.name = name;
        this.max = max;
        this.connected = connected;
        this.password = password;
    }
    /**
     * getName method fetches the lobby name.
     * @return name of lobby.
     */

    public String getName(){
        return name;
    }

    /**
     * getID method fetches the lobby ID.
     * @return lobby ID.
     */
    public int getID(){
        return ID;
    }

    /**
     * getMax method fetches the maximum amount of players.
     * @return max amount.
     */
    public int getMax(){
        return max;
    }

    /**
     * getConnected method fetches the amount of players connected to the lobby.
     * @return amount of players connected.
     */
    public String getConnected(){ return (connected + "/" + max); }
    /**
     * getPassword method fetches the lobby password.
     * @return lobby password.
     */

    public String getPassword() { return password; }

    /**
     * setPassword method sets the lobby password.
     * @param password lobby password.
     */
    public void setPassword(String password){
        this.password = password;
    }
    /**
     * isPublic method states if the lobby is public or not.
     * @return true if there is no password.
     */

    public boolean isPublic() { return password == ""; }
}
