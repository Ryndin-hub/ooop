package logic;
import org.apache.log4j.Logger;

import java.io.IOException;

public class Game {
    private final Parser parser = new Parser(this);
    private Field field = null;
    public Turtle turtle;
    private int width, height;

    private final Logger log = Logger.getLogger(Game.class);

    public Game() throws IOException {
    }

    /**
     * In infinity loop parses command from console and draws field
     * @see Parser#parse_command()
     * @see Game#draw()
     */
    public void play() throws ClassNotFoundException {
        while (true){
            parser.parse_command();
            if (field != null) {
                draw();
            }
        }
    }

    /**
     * Draws field in console
     * @see Field
     */
    public void draw(){
        System.out.print(' ');
        for (int i = 0; i < width; i++){
            System.out.print("---");
        }
        for (int j = height - 1; j >= 0; j--) {
            System.out.println();
            System.out.print('|');
            for (int i = 0; i < width; i++) {
                if (turtle.getX() == i && turtle.getY() == j) System.out.print(" R ");
                else if (field.stateCell(i,j)) System.out.print(" * ");
                else System.out.print("   ");
            }
            System.out.print('|');
        }
        System.out.println();
        System.out.print(' ');
        for (int i = 0; i < width; i++){
            System.out.print("---");
        }
        System.out.println();
    }

    /**
     * Initializes field and turtle, connects turtle to field
     * @see Field
     * @see Turtle
     */
    public void init(int _width, int _height, int x, int y){
        width = _width;
        height = _height;
        field = new Field(_width,_height);
        turtle = new Turtle(x,y,_width,_height);
        turtle.setField(field);
    }
}
