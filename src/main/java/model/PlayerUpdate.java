/**
 * PlayerUpdate class contains the player data.
 */
public class PlayerUpdate {
    private int user;
    private String username;
    private final int diceRoll;
    private final int location;
    private final String msg;

    /**
     * This is the class constructor.
     * @param user the player.
     * @param username the player's username.
     * @param diceRoll the dice roll value.
     * @param location location on game board.
     * @param msg update message.
     */
    public PlayerUpdate(int user, String username, int diceRoll, int location, String msg){
        this.user = user;
        this.username = username;
        this.diceRoll = diceRoll;
        this.location = location;
        this.msg = msg;
    }

    /**
     * getUser method fetches the user.
     * @return user.
     */
    public int getUser(){ return user; }

    /**
     * setUser method sets the user.
     * @param user the player.
     */
    public void setUser(int user){ this.user = user; }

    /**
     * getUsername fetches the player username.
     * @return username.
     */
    public String getUsername(){ return username; }

    /**
     * setUsername method sets the desired username for the player.
     * @param username the desired username.
     */
    public void setUsername(String username){ this.username = username; }

    /**
     * getDiceRoll fetches the last dice roll.
     * @return dice roll value.
     */
    public int getDiceRoll(){ return diceRoll; }

    /**
     * getLocation method fetches the location of the player.
     * @return location of player.
     */
    public int getLocation() {
        return location;
    }

    /**
     * getMsg fetches the update message.
     * @return message.
     */
    public String getMsg() {
        return msg;
    }

    /**
     * equals method compares two object.
     * @param obj object compared to.
     * @return true if same object. False if not.
     */
    @Override
    public boolean equals(Object obj) {
        PlayerUpdate other;
        try{
            other = (PlayerUpdate)obj;
        }
        catch (ClassCastException e){
            return false;
        }
        if (user == other.getUser() &&
            username.equals(other.getUsername()) &&
            diceRoll == other.getDiceRoll() &&
            location == other.getLocation() &&
            ((msg == null && other.getMsg() == null) || msg.equals(other.getMsg()))) {
            return true;
        }
        else{
            return false;
        }
    }
}
