/**
 * This class controls the scores of the players.
 */
public class Score {
    private String username;
    private int score;

    /**
     * Class constructor with username and score.
     * @param username username.
     * @param score score.
     */
    public Score(String username, int score){
        this.username = username;
        this.score = score;
    }

    /**
     * getUsername method fetches the username.
     * @return username.
     */
    public String getUsername(){ return username; }

    /**
     * getGamesWon method fetches the score.
     * @return score.
     */
    public int getGamesWon(){ return score; }
}
