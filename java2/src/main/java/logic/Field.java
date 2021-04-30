package logic;

/**
 * Stores values of cells and size of field
 */
public class Field {
    private final int width;
    private final int height;
    private boolean[][] map;

    /**
     * Initializes field with given size
     */
    public Field(int _width, int _height){
        width = _width;
        height = _height;
        map = new boolean[width][height];
    }

    /**
     * Paint cell on given coordinates
     */
    public void paintCell(int x, int y){
        map[x][y] = true;
    }

    public boolean stateCell(int x, int y){
        return map[x][y];
    }
}
