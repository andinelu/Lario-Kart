/**
 * This class creates the default game board for our game.
 */

public class BoardDefaults {
    private static BoardDefaults instance = null;
    private final Point[] BOARD_TILES;
    private final Point START_LOCATION;
    private final Point[] POWERUP_TILES;
    private final int SPECIAL_TILE_MOVE_AMOUNT;
    private final Point[] DEAD_TILES;

    private BoardDefaults(){
        BOARD_TILES = new Point[]{ new Point(16, 4),
            new Point(16, 5), new Point(16, 6), new Point(16, 7),
            new Point(16, 8), new Point(16, 9), new Point(16, 10),
            new Point(16, 11), new Point(15, 11), new Point(14, 11),
            new Point(13, 11), new Point(13, 10), new Point(13, 9),
            new Point(13, 8), new Point(13, 7), new Point(13, 6),
            new Point(13, 5), new Point(13, 4), new Point(12, 4),
            new Point(11, 4), new Point(10, 4), new Point(9, 4),
            new Point(8, 4), new Point(7, 4), new Point(6, 4),
            new Point(5, 4), new Point(4, 4), new Point(4, 5),
            new Point(4, 6), new Point(4, 7), new Point(4, 8),
            new Point(5, 8), new Point(6, 8), new Point(7, 8),
            new Point(8, 8), new Point(9, 8), new Point(10, 8),
            new Point(10, 9), new Point(10, 10), new Point(10, 11),
            new Point(9, 11), new Point(8, 11), new Point(7,11),
            new Point(6, 11), new Point(5, 11), new Point(4, 11),
            new Point(3, 11), new Point(2, 11), new Point(1, 11),
            new Point(1, 10), new Point(1, 9), new Point(1, 8),
            new Point(1, 7), new Point(1, 6), new Point(1, 5),
            new Point(1, 4), new Point(1, 3), new Point(1, 2),
            new Point(1, 1), new Point(2, 1), new Point(3, 1),
            new Point(4, 1), new Point(5, 1), new Point(6, 1),
            new Point(7,1), new Point(8,1 ), new Point(9,1),
            new Point(10, 1), new Point(11,1), new Point(12, 1),
            new Point(13, 1), new Point(14, 1), new Point(15, 1),
            new Point(16, 1), new Point(16, 2), new Point(16,3)
        };

        START_LOCATION = new Point(16, 4);
        POWERUP_TILES = new Point[]{
            new Point(14, 11), new Point(10, 4), new Point(10, 10),
            new Point(4, 6), new Point(3, 11), new Point(1, 4),
            new Point(8, 1)
        };

        DEAD_TILES = new Point[]{
            new Point(16, 2), new Point(13, 7), new Point(6, 11),
            new Point(4, 8), new Point(3, 1), new Point(13, 1)
        };
        SPECIAL_TILE_MOVE_AMOUNT = 3;
    }

    private static BoardDefaults getInstance(){
        if (instance == null) instance = new BoardDefaults();

        return instance;
    }

    /**
     * This class fetches the game's board tiles.
     * @return array with board tiles.
     */
    public static Point[] getBoardTiles(){
        return getInstance().BOARD_TILES;
    }

    /**
     * This class fetches the game's start location for players.
     * @return start location.
     */
    public static Point getStartLocation() {
        return getInstance().START_LOCATION;
    }

    /**
     * This class fetches the game's power-up tiles.
     * @return array with power-up tiles.
     */
    public static Point[] getPowerupTiles(){ return getInstance().POWERUP_TILES; }

    /**
     * This method fetches the game's dead tiles.
     * @return array with dead tiles.
     */
    public static Point[] getDeadTiles(){ return getInstance().DEAD_TILES; }

    /**
     * This method fetches the players' movement amount when landing on a special tile.
     * @return movement amount.
     */
    public static int getSpecialTileMoveAmount(){ return getInstance().SPECIAL_TILE_MOVE_AMOUNT; }
}
