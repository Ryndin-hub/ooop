import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EventListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

public class Game extends JFrame {

    private BufferedImage track_img;
    private BufferedImage particles_img;
    private BufferedImage cars_img;
    private BufferedImage final_img;

    private Graphics2D g1;
    private Graphics2D g2;
    private Graphics2D g3;
    private Graphics2D g4;

    private Car car = new Car();

    private boolean[] keyboard = new boolean[255];

    private double x = 0, y = 0;
    private final int window_width = 1280, window_height = 740;
    int track_width = 5999;
    int track_height = 2851;

    public Game() throws IOException {

        try {
            track_img = ImageIO.read(getClass().getResourceAsStream("background.png")); //new BufferedImage(1280, 740, BufferedImage.TYPE_INT_ARGB);
        } catch (IOException ignore) {
        }
        particles_img = new BufferedImage(track_width, track_height, BufferedImage.TYPE_INT_ARGB);
        cars_img = new BufferedImage(track_width, track_height, BufferedImage.TYPE_INT_ARGB);
        final_img = new BufferedImage(window_width, window_height, BufferedImage.TYPE_INT_ARGB);

        g1 = (Graphics2D) track_img.getGraphics();
        g2 = (Graphics2D) particles_img.getGraphics();
        g3 = (Graphics2D) cars_img.getGraphics();
        g4 = (Graphics2D) final_img.getGraphics();

        //g1.drawImage(track_img, 0, 0, null);

        //g1.setColor(Color.WHITE);
        //g1.fillRect(0, 0, 1280, 740);
        //g1.translate(1280/2, 740/2);
        //g1.scale(-1, 1);

        //g2.setColor(Color.WHITE);
        //g2.fillRect(0, 0, 1280, 740);
        //g2.translate(1280/2, 740/2);
        //g2.scale(-1, 1);
        //g3.scale(-1,1);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(window_width,window_height);

        new Timer().schedule(new MainLoop(), 50, 30);
    }

    public void update() {
        car.update(keyboard);
        x = car.position.x;
        y = car.position.y;
    }

    int tick = 0;

    @Override
    public void paint(Graphics g) {
        //super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        //g2d.translate(1280/2, 740/2);
        //g2d.scale(1, -1);

        /*
        g3.setColor(Color.WHITE);
        g3.fillRect(0, 0, 1280, 740);

        g2.setBackground(new Color(0, 0, 0, 0));
        g2.clearRect(-1280/2, -740/2, 1280, 740);

        AffineTransform at = g1.getTransform();
        AffineTransform at2 = g2.getTransform();
        car.draw(g2, g1);
        g1.setTransform(at);
        g2.setTransform(at2);

        g3.drawImage(back1, (int)x, (int)y, null);
        g3.drawImage(back2, (int)x, (int)y, null);
        //g2d.drawImage(back1, 0, 0, null);

        g2d.drawImage(back3, (int)x, (int)y, null);
         */

        //g2d.drawImage(track_img, 0, 0 ,1280 , 740, (int)x, (int)y, (int)x + 1280, (int)y + 740, null);
        //g1.drawImage(track_img.getSubimage(Math.abs((int)x), Math.abs((int)y), 1280, 740), 0, 0, null);

        int x_img_start = (int)x - window_width / 2;
        int y_img_start = (int)y - window_height / 2;
        if (x_img_start < 0) x_img_start = 0;
        if (y_img_start < 0) y_img_start = 0;
        if (x_img_start + window_width > track_width) x_img_start = track_width - window_width;
        if (y_img_start + window_height > track_height) y_img_start = track_height - window_height;

        g3.setBackground(new Color(0, 0, 0, 0));
        g3.clearRect(x_img_start, y_img_start, window_width, window_height);
        car.draw(g3, g2);
        g4.drawImage(track_img.getSubimage(x_img_start, y_img_start, window_width, window_height), 0, 0, null);
        g4.drawImage(particles_img.getSubimage(x_img_start, y_img_start, window_width, window_height), 0, 0, null);
        g4.drawImage(cars_img.getSubimage(x_img_start, y_img_start, window_width, window_height), 0, 0, null);
        g2d.drawImage(final_img, 0, 0, null);
        //g2d.drawImage(particles_img.getSubimage(x_img_start, y_img_start, window_width, window_height), 0, 0, null);
        //g2d.drawImage(cars_img.getSubimage(x_img_start, y_img_start, window_width, window_height), 0, 0, null);

        //tick++;
        //System.out.println(tick);
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

    private class MainLoop extends TimerTask {
        @Override
        public void run() {
            long start = System.nanoTime();
            update();
            long finish = System.nanoTime();
            System.out.println("update: " + (finish - start));
            start = System.nanoTime();
            repaint();
            finish = System.nanoTime();
            System.out.println("repaint: " + (finish - start));
        }
    }

}
