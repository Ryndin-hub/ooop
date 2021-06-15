import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class View extends JFrame {

    private BufferedImage track_img;
    private final BufferedImage particles_img;
    private final BufferedImage cars_img;
    private final BufferedImage final_img;

    private final Graphics2D g2;
    private final Graphics2D g3;
    private final Graphics2D g4;

    private final Car[] cars;
    private final int main_car_i;

    private final boolean[] keyboard = new boolean[255];
    InputStreamReader fileInputStream = new InputStreamReader(System.in);
    BufferedReader bufferedReader = new BufferedReader(fileInputStream);

    private int window_width = 1280, window_height = 720;
    private final int track_width = 5999;
    private final int track_height = 2851;

    public View(Car[] _cars, int _main_car_i){
        cars = _cars;
        main_car_i = _main_car_i;

        try {
            track_img = ImageIO.read(getClass().getResourceAsStream("background.png"));
        } catch (IOException ignore) {
        }
        particles_img = new BufferedImage(track_width, track_height, BufferedImage.TYPE_INT_ARGB);
        cars_img = new BufferedImage(track_width, track_height, BufferedImage.TYPE_INT_ARGB);
        final_img = new BufferedImage(track_width, track_height, BufferedImage.TYPE_INT_ARGB);

        g2 = (Graphics2D) particles_img.getGraphics();
        g3 = (Graphics2D) cars_img.getGraphics();
        g4 = (Graphics2D) final_img.getGraphics();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(window_width,window_height);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        window_width = getWidth();
        window_height = getHeight();
        Vector pos = new Vector(cars[main_car_i].getPosition());
        int x_img_start = (int)pos.x - window_width / 2;
        int y_img_start = (int)pos.y - window_height / 2;
        Vector[] saved_positions = new Vector[10];
        for (int i = 0; i < 10; i++) {
            saved_positions[i] = new Vector(cars[i].getPosition());
        }
        if (x_img_start < 0) x_img_start = 0;
        if (y_img_start < 0) y_img_start = 0;
        if (x_img_start + window_width > track_width) x_img_start = track_width - window_width;
        if (y_img_start + window_height > track_height) y_img_start = track_height - window_height;
        g3.setBackground(new Color(0, 0, 0, 0));
        g3.clearRect(x_img_start, y_img_start, window_width, window_height);
        for (int i = 0; i < main_car_i; i++) {
            cars[i].draw(g3, g2, saved_positions[i], saved_positions[main_car_i], false);
        }
        for (int i = main_car_i + 1; i < 10; i++) {
            cars[i].draw(g3, g2, saved_positions[i],saved_positions[main_car_i], false);
        }
        cars[main_car_i].draw(g3, g2, saved_positions[main_car_i], saved_positions[main_car_i], true);
        g4.drawImage(track_img.getSubimage(x_img_start, y_img_start, window_width, window_height), 0, 0, null);
        g4.drawImage(particles_img.getSubimage(x_img_start, y_img_start, window_width, window_height), 0, 0, null);
        g4.drawImage(cars_img.getSubimage(x_img_start, y_img_start, window_width, window_height), 0, 0, null);
        g2d.drawImage(final_img.getSubimage(0, 0, window_width, window_height), 0, 0, null);
    }

    @Override
    protected void processKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
                keyboard[e.getKeyCode()] = true;
        }
        else if (e.getID() == KeyEvent.KEY_RELEASED) {
            keyboard[e.getKeyCode()] = false;
        }
    }

    public String readConsole() throws IOException {
        if (bufferedReader.ready()) {
            return bufferedReader.readLine();
        }
        return null;
    }

    public boolean[] getKeyboard(){
        return keyboard;
    }

    public void clear(){
        g2.setBackground(new Color(0, 0, 0, 0));
        g2.clearRect(0,0, track_width, track_height);
    }

    public void render(){
        repaint();
    }
}
