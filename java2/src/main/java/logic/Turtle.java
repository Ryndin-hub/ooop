package logic;

public class Turtle {
    private int x;
    private int y;
    private final int maxX;
    private final int maxY;
    private boolean draw;
    private Field field;

    /**
     * Creates turtle with given coordinates, sets maximum size of field and draw to false
     */
    public Turtle(int _x, int _y, int _maxX, int _maxY){
        x = _x;
        y = _y;
        maxX = _maxX;
        maxY = _maxY;
        draw = false;
    }

    /**
     * Moves turtle in given direction, steps - how many moves it will do. If draw is true, paints all cells while moving
     */
    public void move(Direction direction, int steps){
        for (int i = 0; i < steps; i++){
            if (draw){
                field.paintCell(x,y);
            }
            switch (direction){
                case L: x-=1; break;
                case R: x+=1; break;
                case U: y+=1; break;
                case D: y-=1; break;
            }
            if (x >= maxX) x-=maxX;
            if (x < 0) x+=maxX;
            if (y >= maxY) y-=maxY;
            if (y < 0) y+=maxY;
        }
        if (draw){
            field.paintCell(x,y);
        }
    }

    /**
     * Teleports turtle to given coordinates
     */
    public boolean teleport(int _x, int _y) {
        if (_x < 0 || _y < 0 || _x >= maxX || _y >= maxY) return true;
        x = _x;
        y = _y;
        return false;
    }

    /**
     * Choose if turtle need to draw trail or not
     */
    public void setDrawState(boolean state){
        draw = state;
    }

    /**
     * Connects turtle to given field
     */
    public void setField(Field _field){
        field = _field;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }
}
